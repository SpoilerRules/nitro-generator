package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI

object NitroValidatorSimple : NitroValidationWrapper() {
    fun validateNitro(nitroCode: String, configuration: BaseConfigurationFactory, retryCount: Int) {
        if (GeneratorBean.isGenerationPaused.get()) {
            return
        }

        if (configuration.generalSettings.logGenerationInfo) {
            Logger.printSuccess("Validating nitro code: $nitroCode", true)
        }

        var nitroValidationRetries = retryCount

        runCatching {
            with(
                URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true").toURL()
                    .openConnection(
                        if (configuration.customProxy.enabled && configuration.customProxy.mode == 1) {
                            Proxy(
                                configuration.customProxy.getProxyType(configuration.customProxy.protocol),
                                InetSocketAddress(
                                    configuration.customProxy.host,
                                    configuration.customProxy.port.toInt()
                                )
                            )
                        } else {
                            Proxy.NO_PROXY
                        }
                    ) as HttpURLConnection
            ) {
                setProperties(this)

                if (configuration.generalSettings.logGenerationInfo) {
                    Logger.printSuccess(when (responseCode) {
                        200 -> "The code $nitroCode is valid. " + if (nitroValidationRetries > 0) "Took $retryCount retries." else "".also {
                            SessionStatistics.validNitroCodes += 1
                            if (BaseConfigurationFactory.getInstance().generalSettings.alertWebhook) {
                                alertWebhook(nitroCode)
                            }
                        }

                        404 -> "The code $nitroCode is invalid. " + if (nitroValidationRetries > 0) "Took $nitroValidationRetries retries." else "".also { SessionStatistics.invalidNitroCodes += 1 }
                        429 -> "The request for code $nitroCode was rate limited."
                        else -> "Unexpected response while validating the code $nitroCode: $responseCode"
                    }, true)
                }

                disconnect()

                if (responseCode !in listOf(200, 404)) {
                    nitroValidationRetries++

                    if (configuration.generalSettings.retryTillValid) {
                        retryValidation(nitroCode, configuration, nitroValidationRetries)
                    }
                }
            }
        }.onFailure {
            Logger.printError("Occurred while validating a nitro code: ${it.message}")
            nitroValidationRetries++

            if (configuration.generalSettings.retryTillValid) {
                retryValidation(nitroCode, configuration, nitroValidationRetries)
            }
        }
    }

    private fun retryValidation(nitroCode: String, configuration: BaseConfigurationFactory, retryCount: Int) {
        if (configuration.generalSettings.retryDelay > 0) {
            for (index in (configuration.generalSettings.retryDelay - 1) downTo 0) {
                Logger.printWarning("Retrying validation of $nitroCode in ${CEnum.ORANGE}${index + 1}${CEnum.RESET} seconds.")
                Thread.sleep(1000)
            }
        }

        validateNitro(nitroCode, BaseConfigurationFactory.getInstance(), retryCount)
    }
}