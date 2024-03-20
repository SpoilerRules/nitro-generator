package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.*

object NitroValidatorSimpleMt {
    fun validateNitro(nitroCode: String, config: BaseConfigurationFactory, retryCount: Int, threadIdentity: String) {
        if (GeneratorBean.isGenerationPaused.get()) {
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
            with(
                URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true").toURL()
                    .openConnection(
                        if (config.proxySettings.enabled && config.proxySettings.mode == 1) {
                            Proxy(
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
                        } else {
                            Proxy.NO_PROXY
                        }
                    ) as HttpURLConnection
            ) {
                NitroValidationWrapper.disableProxySecurity()
                NitroValidationWrapper.setProperties(this, config)

                disconnect()
                NitroValidationWrapper.reactToResponseCode(
                    responseCode,
                    nitroCode,
                    nitroValidationRetries,
                    config,
                    threadIdentity
                ) {
                    nitroValidationRetries++
                    NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, threadIdentity) { code, _, _ ->
                        validateNitro(
                            code,
                            BaseConfigurationFactory.getInstance(),
                            nitroValidationRetries,
                            threadIdentity
                        )
                    }
                }
            }
        }.onFailure {
            Logger.printError("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Occurred while validating a nitro code: ${it.message}")

            if (config.generalSettings.retryTillValid) {
                nitroValidationRetries++
                NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, threadIdentity) { code, _, _ ->
                    validateNitro(code, BaseConfigurationFactory.getInstance(), nitroValidationRetries, threadIdentity)
                }
            }
        }.onSuccess {
            Logger.printDebug("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Safely exiting the thread.")
        }
    }
}