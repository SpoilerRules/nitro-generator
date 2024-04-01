package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.Authenticator
import java.net.ConnectException
import java.net.InetSocketAddress
import java.net.PasswordAuthentication
import java.net.Proxy
import java.net.URI
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Base64
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NitroValidationWrapper {
    fun setProperties(
        connectionInstance: HttpsURLConnection,
        config: BaseConfigurationFactory,
    ) {
        with(connectionInstance) {
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            )
            if (config.proxySettings.isAuthenticationRequired && config.proxySettings.enabled && config.proxySettings.mode == 1) {
                setRequestProperty(
                    "Proxy-Authorization",
                    "Basic ${
                        Base64.getEncoder()
                            .encodeToString("${config.proxySettings.username}:${config.proxySettings.password}".toByteArray())
                    }",
                )
            }
        }
    }

    fun reactToResponseCode(
        responseCode: Int,
        nitroCode: String,
        nitroValidationRetries: Int,
        config: BaseConfigurationFactory,
        threadIdentity: String?,
        retryBehaviour: () -> Unit,
    ) {
        val responseMessage =
            when (responseCode) {
                200, 204 -> {
                    SessionStatistics.validNitroCodes += 1
                    // this if-else if block is logically suspicious. please report any issues you encounter with it
                    if (config.autoClaimSettings.enabled && config.generalSettings.generatePromotionalGiftCode) {
                        claimValidNitro(nitroCode, false, config).also { result ->
                            config.generalSettings.alertWebhook.takeIf { it }?.let {
                                when (result) {
                                    0 -> alertWebhook(nitroCode, true)
                                    else -> alertWebhook(nitroCode, false)
                                }
                            }
                        }
                    } else if (config.generalSettings.alertWebhook) {
                        alertWebhook(nitroCode, null)
                    }
                    "The code $nitroCode is valid. " + if (nitroValidationRetries > 0) "Took $nitroValidationRetries retries." else ""
                }

                404 -> {
                    SessionStatistics.invalidNitroCodes += 1
                    "The code $nitroCode is invalid. " + if (nitroValidationRetries > 0) "Took $nitroValidationRetries retries." else ""
                }

                429 -> "The request for code $nitroCode was rate limited."

                else -> "Unexpected response while validating the code $nitroCode: $responseCode"
            }

        if (config.generalSettings.logGenerationInfo) {
            Logger.printSuccess(
                "${threadIdentity?.let { "${CEnum.RESET}[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$it${CEnum.RESET}] " } ?: ""}$responseMessage",
                true,
            )
        }

        if (responseCode !in listOf(200, 204, 404) && config.generalSettings.retryTillValid) {
            retryBehaviour()
        }
    }

    private fun alertWebhook(
        nitroCode: String,
        isAutoclaimSucceeded: Boolean?,
    ) {
        var connection: HttpsURLConnection? = null

        runCatching {
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            connection =
                URI.create(BaseConfigurationFactory.getInstance().generalSettings.discordWebhookURL).toURL()
                    .openConnection() as HttpsURLConnection
            connection?.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
                )
                doOutput = true
            }

            with(connection!!.outputStream) {
                this.write(
                    """
                        {
                          "embeds": [
                            {
                              "title": "Valid Nitro code: $nitroCode | <https://discord.gift/$nitroCode>",
                              "color": "${
                        when {
                            isAutoclaimSucceeded == null -> 16744192
                            isAutoclaimSucceeded -> 65280
                            else -> 16711680
                        }
                    }",
                              "description": "${
                        when {
                            isAutoclaimSucceeded == null -> ""
                            isAutoclaimSucceeded -> "The Nitro code was successfully claimed."
                            else -> "Failed to claim the Nitro code."
                        }
                    }",
                              "footer": {
                                "text": "Timestamp: $currentDateTime"
                              }
                            }
                          ]
                        }
                    """.trimIndent().toByteArray(),
                )
                this.flush()
            }

            connection?.responseCode?.takeIf { it != HttpsURLConnection.HTTP_OK && it != HttpsURLConnection.HTTP_NO_CONTENT }
                ?.let {
                    Logger.printError(
                        "Failed to send Discord webhook. Server responded with code $it: ${connection?.responseMessage ?: "No response message"}",
                    )
                }
        }.onFailure { error ->
            Logger.printError("Error occurred while connecting to the webhook: ${error.message}")
        }.also {
            connection?.disconnect()
        }
    }

    fun getConnection(
        nitroCode: String,
        threadIdentity: String?,
        config: BaseConfigurationFactory,
        proxyInavailabilityBehavior: (() -> Unit)? = null,
    ): HttpsURLConnection {
        val threadIdentityPrefix =
            threadIdentity?.let {
                "${CEnum.RESET}[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$it${CEnum.RESET}] "
            } ?: ""

        val proxy =
            when {
                !config.proxySettings.enabled -> Proxy.NO_PROXY
                config.proxySettings.mode == 1 -> {
                    val proxyType = config.proxySettings.getProxyType(config.proxySettings.protocol)
                    if (proxyType == Proxy.Type.SOCKS && config.proxySettings.isAuthenticationRequired) {
                        Authenticator.setDefault(
                            object : Authenticator() {
                                override fun getPasswordAuthentication() =
                                    PasswordAuthentication(
                                        config.proxySettings.username,
                                        config.proxySettings.password.toCharArray(),
                                    )
                            },
                        )
                    }
                    Proxy(proxyType, InetSocketAddress(config.proxySettings.host, config.proxySettings.port.toInt()))
                }
                else ->
                    ProxyHandler.getNextProxy()?.let { proxyInfo ->
                        Logger.printDebug("${threadIdentityPrefix}Using proxy: ${CEnum.CYAN}${proxyInfo.first}:${proxyInfo.second}${CEnum.RESET}")
                        Proxy(
                            config.proxySettings.getProxyType(config.proxySettings.protocol),
                            InetSocketAddress(proxyInfo.first, proxyInfo.second),
                        )
                    } ?: run {
                        threadIdentity?.let { proxyInavailabilityBehavior?.invoke() }
                        throw ConnectException("${threadIdentityPrefix}Failed to establish a connection to validate the nitro code because the next proxy is null.")
                    }
            }

        disableProxySecurity()

        return URI(
            "https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true",
        ).toURL().openConnection(proxy) as HttpsURLConnection
    }

    @Suppress("EmptyFunctionBlock")
    fun disableProxySecurity() {
        HttpsURLConnection.setDefaultSSLSocketFactory(
            SSLContext.getInstance("SSL").apply {
                init(
                    null,
                    arrayOf<TrustManager>(
                        object : X509TrustManager {
                            override fun checkClientTrusted(
                                chain: Array<X509Certificate>,
                                authType: String,
                            ) {
                            }

                            override fun checkServerTrusted(
                                chain: Array<X509Certificate>,
                                authType: String,
                            ) {
                            }

                            override fun getAcceptedIssuers(): Array<X509Certificate>? = null
                        },
                    ),
                    SecureRandom(),
                )
            }.socketFactory,
        )
        HttpsURLConnection.setDefaultHostnameVerifier { _, _ -> true }
    }

    private fun claimValidNitro(
        nitroCode: String,
        isTokenValidated: Boolean,
        config: BaseConfigurationFactory,
    ): Int {
        val setProperties: (HttpsURLConnection, BaseConfigurationFactory) -> Unit = { connection, configReference ->
            connection.setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36",
            )
            connection.setRequestProperty(
                "Authorization",
                configReference.autoClaimSettings.accountToken,
            )
        }

        if (!isTokenValidated) {
            with(URI("https://discordapp.com/api/v9/users/@me").toURL().openConnection() as HttpsURLConnection) {
                setRequestProperty("Content-Type", "application/json")
                setProperties(this, config)

                disconnect()
                when (responseCode) {
                    HttpsURLConnection.HTTP_OK, HttpsURLConnection.HTTP_NO_CONTENT -> {
                        val jsonResponse =
                            inputStream.bufferedReader().use { it.readText() }.split("[{},]".toRegex())
                                .filter { it.contains(":") }
                                .map { it.split(":", limit = 2) }
                                .associate { (key, value) -> key.trim(' ', '"') to value.trim(' ', '"') }
                        Logger.printDebug(
                            "Successfully authenticated on Discord as ${jsonResponse["global_name"]} (${jsonResponse["username"]}) using the provided token for Auto-Claim.",
                        )
                    }

                    HttpsURLConnection.HTTP_UNAUTHORIZED -> {
                        Logger.printWarning(
                            "The token you entered for Auto-Claim is invalid. Auto-Claim functionality will be disrupted until a valid token is provided.",
                        )
                        return 1
                    }

                    else -> {
                        Logger.printError("Failed to log in using the provided account token for Auto-Claim.")
                        return 1
                    }
                }
            }
        }

        with(
            URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode/redeem").toURL()
                .openConnection() as HttpsURLConnection,
        ) {
            requestMethod = "POST"
            setProperties(this, config)

            disconnect()
            responseCode.takeIf { it != HttpsURLConnection.HTTP_OK && it != HttpsURLConnection.HTTP_NO_CONTENT }
                ?.let {
                    return when (it) {
                        HttpsURLConnection.HTTP_UNAUTHORIZED -> { // should not be reachable but added, just in case
                            Logger.printWarning("The token you entered for Auto-Claim is potentially incorrect.")
                            1
                        }
                        HttpsURLConnection.HTTP_NOT_FOUND -> {
                            Logger.printError("The Nitro code ($nitroCode) that was attempted to be claimed has already been claimed.")
                            1
                        }
                        else -> claimValidNitro(nitroCode, true, config)
                    }
                }
        }
        return 0
    }

    inline fun retryValidation(
        nitroCode: String,
        configuration: BaseConfigurationFactory,
        retryCount: Int,
        threadIdentity: String?,
        crossinline validateFunction: (String, BaseConfigurationFactory, Int) -> Unit,
    ) {
        val threadIdentityPrefix =
            threadIdentity?.let {
                "${CEnum.RESET}[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$it${CEnum.RESET}] "
            } ?: ""

        val shouldDelay =
            configuration.generalSettings.retryDelay > 0 && !(configuration.proxySettings.enabled && configuration.proxySettings.mode in 2..3)
        val shouldRetryWithoutDelay =
            configuration.proxySettings.mode in 2..3 && configuration.proxySettings.enabled || configuration.generalSettings.retryDelay <= 0

        when {
            shouldDelay -> {
                repeat(configuration.generalSettings.retryDelay) { index ->
                    val message =
                        "${threadIdentityPrefix}Retrying validation of $nitroCode in ${CEnum.ORANGE}${configuration.generalSettings.retryDelay - index}${CEnum.RESET} seconds."

                    if (index == 0) {
                        Logger.printSuccess(message)
                    } else {
                        Logger.printDebug(message)
                    }
                    Thread.sleep(1000)
                }
            }
            shouldRetryWithoutDelay -> {
                Logger.printWarning(
                    "${threadIdentityPrefix}Retrying validation of Nitro code: $nitroCode.",
                )
            }
        }

        threadIdentity?.let { validateFunction(nitroCode, configuration, retryCount) }
    }
}
