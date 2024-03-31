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
import java.util.concurrent.atomic.AtomicBoolean
import javax.net.ssl.HttpsURLConnection

object NitroValidatorConcurrent {
    var isNextProxyAvailable = AtomicBoolean(true)

    fun validateNitro(
        nitroCode: String,
        config: BaseConfigurationFactory,
        retryCount: Int,
        threadIdentity: String,
    ) {
        if (!isNextProxyAvailable.get() && config.proxySettings.mode in 2..3) {
            return
        }

        if (config.generalSettings.logGenerationInfo) {
            Logger.printSuccess(
                "[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Validating nitro code: $nitroCode",
                true,
            )
        }

        var nitroValidationRetries = retryCount

        runCatching {
            with(getConnection(nitroCode, threadIdentity, config)) {
                NitroValidationWrapper.disableProxySecurity()
                NitroValidationWrapper.setProperties(this, config)

                disconnect()
                NitroValidationWrapper.reactToResponseCode(
                    responseCode,
                    nitroCode,
                    nitroValidationRetries,
                    config,
                    threadIdentity,
                ) {
                    nitroValidationRetries++
                    NitroValidationWrapper.retryValidation(
                        nitroCode,
                        config,
                        retryCount,
                        threadIdentity,
                    ) { code, _, _ ->
                        validateNitro(
                            code,
                            BaseConfigurationFactory.getInstance(),
                            nitroValidationRetries,
                            threadIdentity,
                        )
                    }
                }
            }
        }.onFailure {
            Logger.printError(
                "[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] An unknown error occurred while validating a nitro code.",
            )

            if (config.generalSettings.retryTillValid) {
                nitroValidationRetries++
                NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, threadIdentity) { code, _, _ ->
                    validateNitro(
                        code,
                        BaseConfigurationFactory.getInstance(),
                        nitroValidationRetries,
                        threadIdentity,
                    )
                }
            }
        }
    }

    private fun getConnection(
        nitroCode: String,
        threadIdentity: String,
        config: BaseConfigurationFactory,
    ): HttpsURLConnection {
        val proxy =
            when {
                !config.proxySettings.enabled -> Proxy.NO_PROXY
                else ->
                    when (config.proxySettings.mode) {
                        // keeping 1 case, we might merge advanced and simple mt validators.
                        1 ->
                            Proxy(
                                config.proxySettings.getProxyType(config.proxySettings.protocol).also {
                                    if (it == Proxy.Type.SOCKS && config.proxySettings.isAuthenticationRequired) {
                                        Authenticator.setDefault(
                                            object : Authenticator() {
                                                override fun getPasswordAuthentication(): PasswordAuthentication {
                                                    return PasswordAuthentication(
                                                        config.proxySettings.username,
                                                        config.proxySettings.password.toCharArray(),
                                                    )
                                                }
                                            },
                                        )
                                    }
                                },
                                InetSocketAddress(config.proxySettings.host, config.proxySettings.port.toInt()),
                            )

                        else ->
                            ProxyHandler.getNextProxy()?.let { proxyInfo ->
                                Logger.printDebug("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Using proxy: ${CEnum.CYAN}${proxyInfo.first}:${proxyInfo.second}${CEnum.RESET}")
                                Proxy(
                                    config.proxySettings.getProxyType(config.proxySettings.protocol),
                                    InetSocketAddress(proxyInfo.first, proxyInfo.second),
                                )
                            }
                                ?: run {
                                    // stop other threads from executing when next proxy is null
                                    // no need for a check because getNextProxy won't return null when the certain setting (recursive usage) is enabled.
                                    isNextProxyAvailable.set(false)
                                    throw ConnectException("[${CEnum.BLUE}THREAD: ${CEnum.RESET}${CEnum.CYAN}$threadIdentity${CEnum.RESET}] Failed to establish a connection to validate the nitro code because the next proxy is null.")
                                }
                    }
            }

        NitroValidationWrapper.disableProxySecurity()

        return URI(
            "https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true",
        ).toURL()
            .openConnection(proxy) as HttpsURLConnection
    }
}
