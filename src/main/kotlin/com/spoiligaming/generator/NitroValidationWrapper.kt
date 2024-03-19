package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.HttpURLConnection
import java.net.URI
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

object NitroValidationWrapper {
    fun setProperties(connectionInstance: HttpURLConnection, config: BaseConfigurationFactory) {
        with(connectionInstance) {
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
            )
            if (config.customProxy.isAuthenticationRequired && config.customProxy.enabled && config.customProxy.mode == 1) {
                setRequestProperty(
                    "Proxy-Authorization",
                    "Basic ${
                        Base64.getEncoder()
                            .encodeToString("${config.customProxy.username}:${config.customProxy.password}".toByteArray())
                    }"
                )
            }
        }
    }

    fun reactToResponseCode(responseCode: Int, nitroCode: String, nitroValidationRetries: Int, config: BaseConfigurationFactory, threadIdentity: String?, retryBehaviour: () -> Unit) {
        val responseMessage = when (responseCode) {
            200, 204 -> {
                SessionStatistics.validNitroCodes += 1
                if (config.autoClaimSettings.enabled) {
                    runCatching {
                        claimValidNitro(nitroCode)
                    }.onFailure {
                        alertWebhook(nitroCode, false)
                    }.onSuccess {
                        alertWebhook(nitroCode, true)
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
                true
            )
        }

        if (responseCode !in listOf(200, 204, 404) && config.generalSettings.retryTillValid) {
            retryBehaviour()
        }
    }

    fun alertWebhook(nitroCode: String, isAutoclaimSucceeded: Boolean?) {
        var connection: HttpURLConnection? = null

        runCatching {
            connection = URI.create(BaseConfigurationFactory.getInstance().generalSettings.discordWebhookURL).toURL()
                .openConnection() as HttpURLConnection
            connection?.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
                )
                doOutput = true
            }

            with(connection!!.outputStream) {
                this.write(if (isAutoclaimSucceeded != null) {
                    """
        {
          "embeds": [
            {
              "title": "Valid nitro code: $nitroCode | <https://discord.gift/$nitroCode>",
              "color": "${if (isAutoclaimSucceeded == true) 65280 else 16711680}",
              "footer": {
                "text": "${if (isAutoclaimSucceeded == true) "The nitro code was successfully claimed." else "Failed to claim the nitro code."}"
              }
            }
          ]
        }
        """.trimIndent()
                } else {
                    "{\"content\":\"Valid nitro code: $nitroCode | [Claim here](https://discord.gift/$nitroCode>)\"}"
                }.toByteArray())
                this.flush()
            }

            connection?.responseCode?.takeIf { it != HttpURLConnection.HTTP_OK && it != HttpURLConnection.HTTP_NO_CONTENT }
                ?.let {
                    Logger.printError("Failed to send Discord webhook. Server responded with code $it: ${connection?.responseMessage ?: "No response message"}")
                }
        }.onFailure { error ->
            Logger.printError("Error occurred while connecting to the webhook: ${error.message}")
        }.also {
            connection?.disconnect()
        }
    }

    fun disableProxySecurity() {
        HttpsURLConnection.setDefaultSSLSocketFactory(SSLContext.getInstance("SSL").apply {
            init(null, arrayOf<TrustManager>(object : X509TrustManager {
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
                override fun getAcceptedIssuers(): Array<X509Certificate>? = null
            }), SecureRandom())
        }.socketFactory)
    }

    private fun claimValidNitro(nitroCode: String) {
        with(URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode/redeem").toURL().openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
            )
            setRequestProperty(
                "Authorization",
                BaseConfigurationFactory.getInstance().autoClaimSettings.accountToken
            )

            responseCode.takeIf { it != HttpURLConnection.HTTP_OK && it != HttpURLConnection.HTTP_NO_CONTENT }?.let {
                Logger.printError("Failed to send Discord webhook. Server responded with code $it: ${responseMessage ?: "No response message"}")
                disconnect()
                claimValidNitro(nitroCode)
            }

            disconnect()
        }
    }

    inline fun retryValidation(
        nitroCode: String,
        configuration: BaseConfigurationFactory,
        retryCount: Int,
        threadIdentity: String?,
        crossinline validateFunction: (String, BaseConfigurationFactory, Int) -> Unit
    ) {
        // no need for delay between retries when retry delay is <= 0 or custom proxy is enabled and custom proxy mode is in the range 2 to 3.
        if (configuration.generalSettings.retryDelay > 0 && !(configuration.customProxy.enabled && configuration.customProxy.mode in 2..3)) {
            for (index in (configuration.generalSettings.retryDelay - 1) downTo 0) {
                Logger.printWarning("${threadIdentity?.let { "${CEnum.RESET}[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$it${CEnum.RESET}] " } ?: ""}Retrying validation of $nitroCode in ${CEnum.ORANGE}${index + 1}${CEnum.RESET} seconds.")
                Thread.sleep(1000)
            }
        } else if (configuration.customProxy.mode in 2..3 && configuration.customProxy.enabled  || configuration.generalSettings.retryDelay <= 0) {
            Logger.printWarning("${threadIdentity?.let { "${CEnum.RESET}[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$it${CEnum.RESET}] " } ?: ""}Retrying validation of nitro code: $nitroCode.")
        }

        validateFunction(nitroCode, configuration, retryCount)
    }
}