package com.spoiligaming.generator.autoretention

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.io.File

object AutoRetentionHandler {
    private lateinit var validNitroCodesFile: File
    private lateinit var validNitroCodesFileYAML: File

    fun initialize(initializationType: InitializationType) {
        val (fileName, fileReference) =
            mapOf(
                InitializationType.BASIC to
                    Pair(
                        "valid-nitro-codes.txt",
                        ::validNitroCodesFile,
                    ),
                InitializationType.INFORMATIONAL_YAML to
                    Pair(
                        "valid-nitro-codes_informational.yml",
                        ::validNitroCodesFileYAML,
                    ),
            )[initializationType] ?: return

        val file = File(fileName)

        runCatching {
            if (!file.exists()) {
                file.createNewFile()
                Logger.printSuccess(
                    "Created '$fileName' file to store valid Nitro codes at: ${CEnum.BRIGHT_PURPLE}${file.absolutePath}${CEnum.RESET}",
                )
            }
        }.onFailure {
            Logger.printError("Failed to create '$fileName' file: ${it.message}")
        }

        fileReference.set(file)
    }

    /**
     * This method saves a valid Nitro code to a text file.
     *
     * @param nitroCode The valid Nitro code to be saved.
     * @param config The configuration factory instance.
     *
     * @synchronized This method is synchronized to prevent concurrent access.
     */
    @Synchronized
    fun saveValidNitroCode(
        nitroCode: String,
        config: BaseConfigurationFactory,
    ) {
        if (!::validNitroCodesFile.isInitialized) {
            Logger.printDebug(
                "The 'validNitroCodesFile' variable was not initialized when attempting to save a valid Nitro code. Invoking the 'initialize' function.",
            )
            initialize(InitializationType.BASIC)
        }

        val currentPosition = validNitroCodesFile.readLines().size to validNitroCodesFile.readText().length

        validNitroCodesFile.appendText(nitroCode + config.autoRetentionSettings.contentSeparator)

        val newPosition = validNitroCodesFile.readLines().size to validNitroCodesFile.readText().length

        Logger.printDebug(
            "Appended text '${CEnum.BLUE}$nitroCode${CEnum.RESET}' at ${CEnum.BRIGHT_PINK}line ${currentPosition.first + 1}${CEnum.RESET}, taking ${CEnum.BRIGHT_PINK}${newPosition.second - currentPosition.second} columns${CEnum.RESET}.",
        )
    }

    /**
     * Appends a valid Nitro code to a YAML file with additional information.
     *
     * @param nitroCode The Nitro code to be appended.
     * @param validationDate The validation date in ISO 8601 format.
     * @param isPromotionalCode Indicates if the Nitro code is promotional.
     * @param retries The number of retries.
     * @param proxy The proxy used (optional, default is "none").
     * @param threadIdentity The thread identity (optional, default is "none").
     *
     * @synchronized This method is synchronized to prevent concurrent access.
     */
    @Synchronized
    fun appendValidNitroCode(
        nitroCode: String,
        validationDate: String,
        isPromotionalCode: Boolean,
        retries: Int,
        wasClaimed: Boolean,
        proxy: String? = null,
        threadIdentity: String? = null,
    ) {
        if (!::validNitroCodesFileYAML.isInitialized) {
            Logger.printDebug(
                "The 'validNitroCodesFileYAML' variable was not initialized when attempting to save a valid Nitro code. Invoking the 'initialize' function.",
            )
            initialize(InitializationType.INFORMATIONAL_YAML)
        }

        InformationalFileFactory(
            nitroCode,
            validationDate,
            isPromotionalCode,
            retries,
            wasClaimed,
            proxy ?: "none",
            threadIdentity ?: "none",
        ).append(validNitroCodesFileYAML)
    }

    enum class InitializationType {
        BASIC,
        INFORMATIONAL_YAML,
    }
}
