package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.*
import java.util.concurrent.atomic.AtomicBoolean

object NitroValidatorAdvancedMt {
    var isNextProxyAvailable = AtomicBoolean(true)

    fun validateNitro(nitroCode: String, config: BaseConfigurationFactory, retryCount: Int, threadIdentity: String) {
        if (!isNextProxyAvailable.get()) {
            return
        }

        if (config.generalSettings.logGenerationInfo) {
            Logger.printSuccess(
                "[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Validating nitro code: $nitroCode",
                true
            )
        }

        var nitroValidationRetries = retryCount

        runCatching {
            with(getConnection(nitroCode, threadIdentity, config)) {
                NitroValidationWrapper.disableProxySecurity()
                NitroValidationWrapper.setProperties(this, config)

                NitroValidationWrapper.reactToResponseCode(
                    responseCode,
                    nitroCode,
                    nitroValidationRetries,
                    config,
                    threadIdentity
                ) {
                    nitroValidationRetries++
                    NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, threadIdentity) { code, _, count ->
                        validateNitro(
                            code,
                            BaseConfigurationFactory.getInstance(),
                            count,
                            threadIdentity
                        )
                    }
                }

                // explicitly disconnect to free resources as soon as possible
                disconnect()
            }
        }.onFailure {
            Logger.printError("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Occurred while validating a nitro code: ${it.message}")

            if (config.generalSettings.retryTillValid) {
                nitroValidationRetries++
                NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, threadIdentity) { code, _, count ->
                    validateNitro(
                        code,
                        BaseConfigurationFactory.getInstance(),
                        count,
                        threadIdentity
                    )
                }
            }
        }
    }

    private fun getConnection(
        nitroCode: String,
        threadIdentity: String,
        config: BaseConfigurationFactory
    ): HttpURLConnection {
        val proxy = when {
            !config.customProxy.enabled -> Proxy.NO_PROXY
            else -> when (config.customProxy.mode) {
                // keeping 1 case, we might merge advanced and simple mt validators.
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
                    Logger.printDebug("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Using proxy: ${CEnum.CYAN}${proxyInfo.first}:${proxyInfo.second}${CEnum.RESET}")
                    Proxy(
                        config.customProxy.getProxyType(config.customProxy.protocol),
                        InetSocketAddress(proxyInfo.first, proxyInfo.second)
                    )
                }
                    ?: run {
                        // stop other threads from executing when next proxy is null
                        // no need for a check because getNextProxy won't return null when the certain setting (recursive usage) is enabled.
                        isNextProxyAvailable.set(false)
                        throw RuntimeException("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Failed to establish a connection to validate the nitro code because the next proxy is null.")
                    }
            }
        }

        NitroValidationWrapper.disableProxySecurity()

        return URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true").toURL()
            .openConnection(proxy) as HttpURLConnection
    }
}