package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import java.net.Proxy
import java.net.URI
import java.util.*
import kotlin.concurrent.timer

object GeneratorBean {
    var isGenerationPaused: BooleanProperty = SimpleBooleanProperty(false)
    var fakeValidation = false

    private val checkDelay: Long
        get() = BaseConfigurationFactory.getInstance().generalSettings.generationDelay

    private val DISCORD_WEBHOOK_URL: String
        get() = BaseConfigurationFactory.getInstance().generalSettings.discordWebhookURL

    fun startGeneratingNitro(promotionalGiftCode: Boolean = false) {
        timer(initialDelay = 0, period = checkDelay) {
            if (!BaseConfigurationFactory.getInstance().generalSettings.validateNitroCode) return@timer

            if (!isGenerationPaused.get()) {
                val nitroCode =
                    List(if (!promotionalGiftCode) 16 else 24) { "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".random() }.joinToString(
                        ""
                    )
                when (fakeValidation) {
                    true -> Logger.printSuccess("The code $nitroCode is valid.")
                        .also {
                            SessionStatistics.validNitroCodes += 1
                            NitroValidationWrapper.alertWebhook(nitroCode)
                        }

                    false -> if (BaseConfigurationFactory.getInstance().customProxy.proxyFilePath != "" || BaseConfigurationFactory.getInstance().customProxy.enabled) {
                        when {
                            BaseConfigurationFactory.getInstance().customProxy.mode == 1 && !BaseConfigurationFactory.getInstance().multithreading.enabled -> NitroValidatorSimple.validateNitro(nitroCode, BaseConfigurationFactory.getInstance(), 0)
                            else -> validateNitro(nitroCode)
                        }
                    } else if (BaseConfigurationFactory.getInstance().customProxy.proxyFilePath != "" && BaseConfigurationFactory.getInstance().customProxy.enabled) {
                        Logger.printWarning("Nitro generation was skipped because the Proxy File path was empty, even though Custom Proxy mode was set to 'One File' and enabled. Please check your proxy settings.")                    }
                }
            }
        }
    }

    //todo: handle multi threaded versions of the validators when multi threading is enabled. e.g: NitroValidatorAdvancedMt.kt
    private fun handleConcurrentValidation(nitroCode: String) {
    }

    //todo: when there are multiple proxy files to index through, merge all of them in a temp txt file and iterate through the proxies there.
    private fun validateNitro(nitroCode: String) {
        val shouldRetry = BaseConfigurationFactory.getInstance().generalSettings.retryTillValid
        var retry = shouldRetry
        var nitroValidationRetries = 0

        while (retry) {
            if (BaseConfigurationFactory.getInstance().generalSettings.logGenerationInfo) {
                Logger.printSuccess("Validating nitro code: $nitroCode", true)
            }

            if (isGenerationPaused.get()) {
                return
            }

            var connection: HttpURLConnection? = null

            runCatching {
                val discordValidatorURL =
                    URI("https://discordapp.com/api/v9/entitlements/gift-codes/$nitroCode?with_application=false&with_subscription_plan=true").toURL()

                connection = when {
                    BaseConfigurationFactory.getInstance().customProxy.enabled && BaseConfigurationFactory.getInstance().customProxy.mode == 1 -> {
                        val proxy = Proxy(
                            BaseConfigurationFactory.getInstance().customProxy.getProxyType(
                                BaseConfigurationFactory.getInstance().customProxy.protocol
                            ), InetSocketAddress(
                                BaseConfigurationFactory.getInstance().customProxy.host,
                                BaseConfigurationFactory.getInstance().customProxy.port.toInt()
                            )
                        )

                        discordValidatorURL.openConnection(proxy) as HttpURLConnection
                    }

                    BaseConfigurationFactory.getInstance().customProxy.enabled && (BaseConfigurationFactory.getInstance().customProxy.mode == 2 || BaseConfigurationFactory.getInstance().customProxy.mode == 3) -> {
                        val proxyInfo = ProxyHandler.getNextProxy()
                        if (proxyInfo != null) {
                            Logger.printDebug("Using proxy: ${proxyInfo.first}:${proxyInfo.second}")
                            val address = InetSocketAddress(proxyInfo.first, proxyInfo.second)
                            val proxy = Proxy(Proxy.Type.HTTP, address)
                            discordValidatorURL.openConnection(proxy) as HttpURLConnection
                        } else {
                            throw RuntimeException("Cannot find any proxy.")
                        }
                    }

                    BaseConfigurationFactory.getInstance().customProxy.mode > 3 -> throw IllegalArgumentException("Invalid custom proxy mode. The mode value must be either 1 or 2.")
                    else -> discordValidatorURL.openConnection() as HttpURLConnection
                }

                with(connection!!) {
                    requestMethod = "GET"
                    setRequestProperty(
                        "User-Agent",
                        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
                    )
                    if (BaseConfigurationFactory.getInstance().customProxy.isAuthenticationRequired) {
                        setRequestProperty(
                            "Proxy-Authorization",
                            "Basic ${
                                Base64.getEncoder()
                                    .encodeToString("${BaseConfigurationFactory.getInstance().customProxy.username}:${BaseConfigurationFactory.getInstance().customProxy.password}".toByteArray())
                            }"
                        )
                    }
                    if (BaseConfigurationFactory.getInstance().generalSettings.logGenerationInfo) {
                        Logger.printSuccess(when (responseCode) {
                            200 -> "The code $nitroCode is valid. " + if (nitroValidationRetries > 0) "Took $nitroValidationRetries retries." else "".also {
                                if (BaseConfigurationFactory.getInstance().generalSettings.alertWebhook) {
                                    alertWebhook(nitroCode, DISCORD_WEBHOOK_URL)
                                }
                                SessionStatistics.validNitroCodes += 1
                            }

                            404 -> "The code $nitroCode is invalid. " + if (nitroValidationRetries > 0) "Took $nitroValidationRetries retries." else "".also { SessionStatistics.invalidNitroCodes += 1 }
                            429 -> "The request for code $nitroCode was rate limited.".also {
                                if (BaseConfigurationFactory.getInstance().customProxy.enabled && BaseConfigurationFactory.getInstance().customProxy.mode == 2) {
                                    for (index in 2 downTo 0) {
                                        print("\r${CEnum.RESET}[${CEnum.YELLOW}WARNING${CEnum.RESET}] Retrying validation of $nitroCode in ${CEnum.ORANGE}${index + 1}${CEnum.RESET} seconds.")
                                        Thread.sleep(1000)
                                    }
                                    print("\r")
                                } else {
                                    Thread.sleep(checkDelay)
                                }
                            }

                            else -> {
                                "Unexpected response while validating the code $nitroCode: $responseCode".also {
                                    if (BaseConfigurationFactory.getInstance().customProxy.enabled && BaseConfigurationFactory.getInstance().customProxy.mode == 2) {
                                        for (index in 2 downTo 0) {
                                            print("\r${CEnum.RESET}[${CEnum.YELLOW}WARNING${CEnum.RESET}] Retrying validation of $nitroCode in ${CEnum.ORANGE}${index + 1}${CEnum.RESET} seconds.")
                                            Thread.sleep(1000)
                                        }
                                        print("\r")
                                    } else {
                                        Thread.sleep(checkDelay)
                                    }
                                }
                            }
                        }, true)
                    }
                    retry = responseCode !in listOf(200, 404) && shouldRetry
                    nitroValidationRetries++
                }
            }.onFailure { exception ->
                Logger.printError("Occurred while validating a nitro code: ${exception.message}")

                if (shouldRetry) {
                    if (BaseConfigurationFactory.getInstance().customProxy.enabled && BaseConfigurationFactory.getInstance().customProxy.mode == 2) {
                        retry = true
                    } else {
                        for (index in 2 downTo 0) {
                            print("\r${CEnum.RESET}[${CEnum.YELLOW}WARNING${CEnum.RESET}] Retrying in ${CEnum.ORANGE}${index + 1}${CEnum.RESET} seconds.")
                            Thread.sleep(1000)
                        }
                        print("\r")
                        nitroValidationRetries++
                    }
                }
            }.also {
                connection?.disconnect()
            }
        }
    }

    private fun alertWebhook(nitroCode: String, webhookUrl: String) {
        var connection: HttpURLConnection? = null

        runCatching {
            connection = URI.create(webhookUrl).toURL().openConnection() as HttpURLConnection
            connection?.apply {
                requestMethod = "POST"
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty(
                    "User-Agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
                )
                doOutput = true
            }

            with(connection!!.outputStream) {
                this.write("{\"content\":\"Valid nitro code: $nitroCode\"}".toByteArray())
                this.flush()
            }
        }.onFailure {
            Logger.printError("Occurred while connecting to the webhook: ${it.message}")
        }.also {
            connection?.disconnect()
        }
    }
}