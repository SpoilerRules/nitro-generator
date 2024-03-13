package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.Logger
import java.net.HttpURLConnection
import java.net.URI
import java.util.*

object NitroValidationWrapper {
    fun retryRecursively() {}

    fun retrySimple() {}

    fun setProperties(connectionInstance: HttpURLConnection) {
        with(connectionInstance) {
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

        }.onFailure { error ->
            Logger.printError("Error occurred while connecting to the webhook: ${error.message}")
            connection?.responseCode?.let {
                Logger.printDebug("Discord Webhook response code: $it")
            } ?: Logger.printError("Failed to get Discord Webhook response code.")
        }.also {
            connection?.disconnect()
        }
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