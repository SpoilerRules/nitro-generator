package com.spoiligaming.generator

import com.spoiligaming.generator.NitroValidatorSequential.validateNitro
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.Logger
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * The standard nitro validator. This validator does not support multithreading
 * and is designed to work with all proxy modes specified in the configuration.
 *
 * @property validateNitro This function validates a nitro code against the Discord API.
 */
object NitroValidatorSequential {
    /**
     * Validates a nitro code against the Discord API.
     *
     * @param nitroCode The nitro code to validate.
     * @param retryCount The number of retry attempts for validation.
     * @param config The base configuration factory containing configuration settings.
     */
    fun validateNitro(
        nitroCode: String,
        retryCount: Int,
        config: BaseConfigurationFactory,
    ) {
        if (GeneratorBean.isGenerationPaused.get()) {
            return
        }

        if (config.generalSettings.logGenerationInfo) {
            Logger.printSuccess("Validating nitro code: $nitroCode", true)
        }

        var nitroValidationRetries = retryCount

        while (true) {
            val (connection, proxy) = NitroValidationWrapper.getConnection(nitroCode, null, config)

            try {
                with(connection) {
                    NitroValidationWrapper.setProperties(this, config)

                    disconnect()
                    NitroValidationWrapper.reactToResponseCode(
                        responseCode,
                        nitroCode,
                        nitroValidationRetries,
                        LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                        config,
                        proxy,
                        null,
                    ) {
                        nitroValidationRetries++
                        NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, null) { code, _, _ ->
                            validateNitro(
                                code,
                                nitroValidationRetries,
                                BaseConfigurationFactory.getInstance(),
                            )
                        }
                    }
                }
                break
            } catch (error: Exception) {
                Logger.printError("Occurred while validating a nitro code: ${error.message}")

                if (config.generalSettings.retryTillValid) {
                    nitroValidationRetries++
                    NitroValidationWrapper.retryValidation(nitroCode, config, retryCount, null) { _, _, _ ->
                        nitroValidationRetries++
                    }
                } else {
                    break
                }
            }
        }
    }
}
