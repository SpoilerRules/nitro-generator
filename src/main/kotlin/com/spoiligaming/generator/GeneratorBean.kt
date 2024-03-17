package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import kotlinx.coroutines.*
import kotlin.concurrent.timer

object GeneratorBean {
    var isGenerationPaused: BooleanProperty = SimpleBooleanProperty(false)
    var fakeValidation = false

    fun startGeneratingNitro() {
        timer(initialDelay = 0, period = BaseConfigurationFactory.getInstance().generalSettings.generationDelay.takeIf { it != 0L } ?: 1) {
            val config = BaseConfigurationFactory.getInstance()
            if (isGenerationPaused.get()) return@timer

            val nitroCode = generateNitroCode(config.generalSettings.generatePromotionalGiftCode)

            if (!config.generalSettings.validateNitroCode) {
                return@timer Logger.printSuccess("Generated nitro code: $nitroCode")
            }

            when (fakeValidation) {
                true -> {
                    Logger.printSuccess("The code $nitroCode is valid.")
                    SessionStatistics.validNitroCodes += 1
                    NitroValidationWrapper.alertWebhook(nitroCode)
                }
                false -> {
                    val proxy = config.customProxy
                    val multithreading = config.multithreading.enabled
                    if ((proxy.proxyFilePath.isNotEmpty() && proxy.mode == 2 && proxy.enabled) || proxy.enabled && proxy.mode != 2 || !proxy.enabled) {
                        when {
                            proxy.mode in 1..3 && !multithreading -> NitroValidatorOrdinary.validateNitro(nitroCode, config, 0)
                            else -> handleConcurrentValidation(nitroCode, config)
                        }
                    } else if (proxy.proxyFilePath.isEmpty() && proxy.mode == 2 && proxy.enabled) {
                        Logger.printWarning("Nitro generation was skipped because ${CEnum.UNDERLINE}the Proxy File path was empty${CEnum.RESET}, even though Custom Proxy mode was set to 'One File' and enabled. Please check your proxy settings.")
                    }
                }
            }
        }
    }

    private fun handleConcurrentValidation(initialNitroCode: String, config: BaseConfigurationFactory) {
        runBlocking {
            List(config.multithreading.threadLimit) { index ->
                val nitroCode =
                    if (index == 0) initialNitroCode else generateNitroCode(config.generalSettings.generatePromotionalGiftCode)
                launch(Dispatchers.IO) {
                    when (config.customProxy.mode) {
                        1 -> {
                            NitroValidatorSimpleMt.validateNitro(nitroCode, config, 0, "${index + 1}")
                        }
                        2, 3 -> {
                            NitroValidatorAdvancedMt.validateNitro(nitroCode, config, 0, "${index + 1}")
                        }
                    }
                }
                delay(config.multithreading.threadLaunchDelay)
            }.run { joinAll() }
        }
    }

    private fun generateNitroCode(promotionalGiftCode: Boolean): String = List(if (!promotionalGiftCode) 16 else 24) { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString("")
}