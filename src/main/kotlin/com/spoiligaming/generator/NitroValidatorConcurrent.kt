package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.util.concurrent.atomic.AtomicBoolean

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
            with(NitroValidationWrapper.getConnection(nitroCode, threadIdentity, config)) {
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
}
