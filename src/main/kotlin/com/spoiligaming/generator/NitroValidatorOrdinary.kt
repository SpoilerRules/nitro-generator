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

                NitroValidationWrapper.reactToResponseCode(
                    responseCode,
                    nitroCode,
                    nitroValidationRetries,
                    config,
                    null
                ) {
                    nitroValidationRetries++
                    NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, null) { code, _, _ ->
                        validateNitro(
                            code,
                            BaseConfigurationFactory.getInstance(),
                            nitroValidationRetries,
                        )
                    }
                }

                // explicitly disconnect to free resources as soon as possible
                disconnect()
            }
        }.onFailure {
            Logger.printError("Occurred while validating a nitro code: ${it.message}")

            if (config.generalSettings.retryTillValid) {
                NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, null) { code, _, _ ->
                    nitroValidationRetries++
                    validateNitro(code, BaseConfigurationFactory.getInstance(), nitroValidationRetries)
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
            !config.proxySettings.enabled -> Proxy.NO_PROXY
            else -> when (config.proxySettings.mode) {
                1 -> Proxy(
                    config.proxySettings.getProxyType(config.proxySettings.protocol).also {
                        if (it == Proxy.Type.SOCKS && config.proxySettings.isAuthenticationRequired) {
                            Authenticator.setDefault(object : Authenticator() {
                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                    return PasswordAuthentication(
                                        config.proxySettings.username,
                                        config.proxySettings.password.toCharArray()
                                    )
                                }
                            })
                        }
                    },
                    InetSocketAddress(config.proxySettings.host, config.proxySettings.port.toInt())
                )

                else -> ProxyHandler.getNextProxy()?.let { proxyInfo ->
                    Logger.printDebug("Using proxy: ${CEnum.CYAN}${proxyInfo.first}:${proxyInfo.second}${CEnum.RESET}")
                    Proxy(
                        config.proxySettings.getProxyType(config.proxySettings.protocol),
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