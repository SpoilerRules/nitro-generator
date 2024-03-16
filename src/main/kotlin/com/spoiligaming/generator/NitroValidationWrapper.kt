package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.net.HttpURLConnection
import java.net.URI
import java.util.*

object NitroValidationWrapper {
    fun retryRecursively() {}

    fun retrySimple() {}

    fun setProperties(connectionInstance: HttpURLConnection, config: BaseConfigurationFactory) {
        with(connectionInstance) {
            setRequestProperty(
                "User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/121.0.0.0 Safari/537.36"
            )
            if (config.customProxy.isAuthenticationRequired && config.customProxy.enabled && config.customProxy.mode == 1) {
                setRequestProperty(
                    "Proxy-Authorization",
                    "Basic ${
                        Base64.getEncoder()
                            .encodeToString("${config.customProxy.username}:${config.customProxy.password}".toByteArray())
                    }"
                )
            }
        }
    }


    //TODO: broken, fix it.
    fun alertWebhook(nitroCode: String) {
        var connection: HttpURLConnection? = null

        runCatching {
            connection = URI.create(BaseConfigurationFactory.getInstance().generalSettings.discordWebhookURL).toURL()
                .openConnection() as HttpURLConnection
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

            connection?.responseCode?.takeIf { it != HttpURLConnection.HTTP_OK && it != HttpURLConnection.HTTP_NO_CONTENT }?.let {
                Logger.printError("Failed to send Discord webhook. Server responded with code $it: ${connection?.responseMessage ?: "No response message"}")
            }
        }.onFailure { error ->
            Logger.printError("Error occurred while connecting to the webhook: ${error.message}")
        }.also {
            connection?.disconnect()
        }
    }

    inline fun retryValidation(
        nitroCode: String,
        configuration: BaseConfigurationFactory,
        retryCount: Int,
        crossinline validateFunction: (String, BaseConfigurationFactory, Int) -> Unit
    ) {
        if (configuration.generalSettings.retryDelay > 0 && configuration.customProxy.mode !in 2..3) {
            for (index in (configuration.generalSettings.retryDelay - 1) downTo 0) {
                Logger.printWarning("Retrying validation of $nitroCode in ${CEnum.ORANGE}${index + 1}${CEnum.RESET} seconds.")
                Thread.sleep(1000)
            }
        } else if (configuration.customProxy.mode in 2..3) {
            Logger.printWarning("Retrying validation of nitro code: $nitroCode.")
        }

        validateFunction(nitroCode, configuration, retryCount)
    }
}

  /*  private fun getProxy(): Proxy = when {
        BaseConfigurationFactory.getInstance().customProxy.enabled && BaseConfigurationFactory.getInstance().customProxy.mode == 1 -> {
            Proxy(
                BaseConfigurationFactory.getInstance().customProxy.getProxyType(
                    BaseConfigurationFactory.getInstance().customProxy.protocol
                ), InetSocketAddress(
                    BaseConfigurationFactory.getInstance().customProxy.host,
                    BaseConfigurationFactory.getInstance().customProxy.port.toInt()
                )
            )
        }
        BaseConfigurationFactory.getInstance().customProxy.enabled && (BaseConfigurationFactory.getInstance().customProxy.mode == 2 || BaseConfigurationFactory.getInstance().customProxy.mode == 3) -> {
            val proxyInfo = ProxyHandler.getNextProxy()
            if (proxyInfo != null) {
                Logger.printDebug("Using proxy: ${proxyInfo.first}:${proxyInfo.second}")
                Proxy(Proxy.Type.HTTP, InetSocketAddress(proxyInfo.first, proxyInfo.second))
            } else {
                throw RuntimeException("Cannot find any proxy.")
            }
        }
    }*/