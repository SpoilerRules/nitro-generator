package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import javafx.beans.property.SimpleBooleanProperty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Semaphore
import kotlin.concurrent.timer

object GeneratorBean {
    var isGenerationPaused = SimpleBooleanProperty(false)

    fun startGeneratingNitro() {
        timer(
            initialDelay = 0,
            daemon = true,
            period = BaseConfigurationFactory.getInstance().generalSettings.generationDelay.takeIf { it != 0L } ?: 1,
        ) {
            val config = BaseConfigurationFactory.getInstance()
            // reset isConfigUpdated to ensure concurrent operations work
            BaseConfigurationFactory.isConfigUpdated = false

            if (isGenerationPaused.get()) return@timer

            val nitroCode = generateNitroCode(config.generalSettings.generatePromotionalGiftCode)

            if (!config.generalSettings.validateNitroCode) {
                return@timer Logger.printSuccess("Generated nitro code: $nitroCode")
            }

            if ((config.proxySettings.proxyFilePath.isNotEmpty() && config.proxySettings.mode == 2 && config.proxySettings.enabled) ||
                config.proxySettings.enabled && config.proxySettings.mode != 2 ||
                !config.proxySettings.enabled
            ) { // very fragile, please do not touch this
                when {
                    config.proxySettings.mode in 1..3 && !config.multithreadingSettings.enabled ->
                        NitroValidatorSequential.validateNitro(nitroCode, 0, config)
                    else ->
                        handleConcurrentValidation(nitroCode, config)
                }
            } else if (config.proxySettings.proxyFilePath.isEmpty() && config.proxySettings.mode == 2 && config.proxySettings.enabled) {
                Logger.printWarning(
                    "Nitro generation was skipped because ${CEnum.UNDERLINE}the Proxy File path was empty${CEnum.RESET}, even though Custom Proxy mode was set to 'One File' and enabled. Please check your proxy settings.",
                )
            }
        }
    }

    private fun handleConcurrentValidation(
        initialNitroCode: String,
        config: BaseConfigurationFactory,
    ) {
        val semaphore = Semaphore(config.multithreadingSettings.threadLimit)

        runBlocking {
            var index = 0
            repeat(config.multithreadingSettings.threadLimit) {
                launch(Dispatchers.IO) {
                    while (isActive && !BaseConfigurationFactory.isConfigUpdated && !isGenerationPaused.get()) {
                        semaphore.acquire()
                        val nitroCode =
                            if (index++ == 0) initialNitroCode else generateNitroCode(config.generalSettings.generatePromotionalGiftCode)
                        NitroValidatorConcurrent.validateNitro(
                            nitroCode,
                            config,
                            0,
                            coroutineContext[Job]?.toString()?.substringAfter('@')
                                ?: "UnknownThread".substringBefore(']'),
                        )
                        semaphore.release()
                    }
                }
                delay(config.multithreadingSettings.threadLaunchDelay)
            }
        }
    }

    private fun generateNitroCode(promotionalGiftCode: Boolean): String =
        List(
            if (!promotionalGiftCode) 16 else 24,
        ) { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString(
            "",
        )
}
