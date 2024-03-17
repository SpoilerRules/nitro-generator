package com.spoiligaming.generator

import com.spoiligaming.generator.NitroValidatorOrdinary.getConnection
import com.spoiligaming.generator.NitroValidatorOrdinary.validateNitro
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.*

/**
 * The standard nitro validator. This validator does not support multithreading
 * and is designed to work with all proxy modes specified in the configuration.
 *
 * @property validateNitro This function validates a nitro code against the Discord API.
 * @property getConnection This function establishes a connection to the Discord API
 *                         for validating nitro codes, utilizing configured proxies if enabled.
 */
object NitroValidatorOrdinary {
    /**
     * Validates a nitro code against the Discord API.
     *
     * @param nitroCode The nitro code to validate.
     * @param config The base configuration factory containing configuration settings.
     * @param retryCount The number of retry attempts for validation.
     */
    fun validateNitro(nitroCode: String, config: BaseConfigurationFactory, retryCount: Int) {
       /* Exception("Debugging Stack Trace").apply {
            stackTrace.forEach { println(it) }
        }*/
        if (GeneratorBean.isGenerationPaused.get()) {
            return
        }

        if (config.generalSettings.logGenerationInfo) {
            Logger.printSuccess("Validating nitro code: $nitroCode", true)
        }

        var nitroValidationRetries = retryCount

        runCatching {
            with(getConnection(nitroCode, config)) {
                NitroValidationWrapper.setProperties(this, config)

                val responseMessage = when (responseCode) {
                    200, 204 -> {
                        SessionStatistics.validNitroCodes += 1
                        if (config.generalSettings.alertWebhook) {
                            NitroValidationWrapper.alertWebhook(nitroCode)
                        }
                        "The code $nitroCode is valid. " + if (nitroValidationRetries > 0) "Took $retryCount retries." else ""
                    }

                    404 -> {
                        SessionStatistics.invalidNitroCodes += 1
                        "The code $nitroCode is invalid. " + if (nitroValidationRetries > 0) "Took $nitroValidationRetries retries." else ""
                    }

                    429 -> "The request for code $nitroCode was rate limited."

                    else -> "Unexpected response while validating the code $nitroCode: $responseCode"
                }

                if (config.generalSettings.logGenerationInfo) {
                    Logger.printSuccess(responseMessage, true)
                }

                // explicitly disconnect to free resources as soon as possible
                disconnect()

                if (responseCode !in listOf(200, 204, 404) && config.generalSettings.retryTillValid) {
                    nitroValidationRetries++
                    NitroValidationWrapper.retryValidation(nitroCode, config, retryCount) { code, _, count ->
                        validateNitro(code, BaseConfigurationFactory.getInstance(), count)
                    }
                }
            }
        }.onFailure {
            Logger.printError("Occurred while validating a nitro code: ${it.message}")

            if (config.generalSettings.retryTillValid) {
                nitroValidationRetries++
                NitroValidationWrapper.retryValidation(nitroCode, config, retryCount) { code, _, count ->
                    validateNitro(code, BaseConfigurationFactory.getInstance(), count)
                }
            }
        }
    }

    /**
     * Establishes a connection to the Discord API for validating nitro codes.
     *
     * @param nitroCode The nitro code to validate.
     * @param config The base configuration factory containing configuration settings.
     * @return An HttpURLConnection object representing the connection to the Discord API.
     */
    private fun getConnection(nitroCode: String, config: BaseConfigurationFactory): HttpURLConnection {
        val proxy = when {
            !config.customProxy.enabled -> Proxy.NO_PROXY
            else -> when (config.customProxy.mode) {
                1 -> Proxy(
                    config.customProxy.getProxyType(config.customProxy.protocol).also {
                        if (it == Proxy.Type.SOCKS && config.customProxy.isAuthenticationRequired) {
                            Authenticator.setDefault(object : Authenticator() {
                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                    return PasswordAuthentication(
                                        config.customProxy.username,
                                        config.customProxy.password.toCharArray()
                                    )
                                }
                            })
                        }
                    },
                    InetSocketAddress(config.customProxy.host, config.customProxy.port.toInt())
                )

                else -> ProxyHandler.getNextProxy()?.let { proxyInfo ->
                    Logger.printDebug("Using proxy: ${CEnum.CYAN}${proxyInfo.first}:${proxyInfo.second}${CEnum.RESET}")
                    Proxy(
                        config.customProxy.getProxyType(config.customProxy.protocol),
                        InetSocketAddress(proxyInfo.first, proxyInfo.second)
                    )
                }
                    ?: throw RuntimeException("Failed to establish a connection to validate the nitro code because the next proxy is null.")
            }
        }

        NitroValidationWrapper.disableProxySecurity()

        return URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true").toURL()
            .openConnection(proxy) as HttpURLConnection
    }
}