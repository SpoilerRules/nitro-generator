package com.spoiligaming.generator.configuration

import com.spoiligaming.logging.Logger
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File
import java.net.Proxy

@Serializable
data class General(
    var logGenerationInfo: Boolean = true,
    var generatePromotionalGiftCode: Boolean = false,
    var generationDelay: Long = 6000,
    var validateNitroCode: Boolean = true,
    @SerialName("alertWebhookForValidNitroCode") var alertWebhook: Boolean = true,
    var discordWebhookURL: String = "https://dummylink.com/suspicious-webhook/",
    @SerialName("retry") var retryTillValid: Boolean = true,
    var retryDelay: Int = 3
)

@Serializable
data class Multithreading(
   // var mode: String = "Normal",
    var enabled: Boolean = false,
    var threadLimit: Int = 3,
    var threadLaunchDelay: Long = 3000,

   // var waitDelay: Long = 10000
)

@Serializable
data class CustomProxy(
    var enabled: Boolean = false,
    var mode: Int = 1,
    var protocol: Int = 2,
    var recursiveUsaqe: Boolean = true,
    var isAuthenticationRequired: Boolean = false,
    var host: String = "162.0.220.211",
    var port: String = "46148",
    var username: String = "Dummy Internet Suspect",
    var password: String = "123Dummy\$Password!",
    var proxyFilePath: String = "",
    var rawContentLinks: String = "https://my-epic-proxy-api/proxies.txt, https://dummy-proxy-api/suspicious-http/proxies.txt",
    var rawContentSeparator: String = "\n"
) {
    fun getProxyType(protocol: Int): Proxy.Type = when (protocol) {
        1 -> Proxy.Type.HTTP
        2 -> Proxy.Type.SOCKS
        else -> throw IllegalArgumentException("Invalid protocol type")
    }
}


// experimental and undone.
@Serializable
data class Theme(
    var accentColor: String = "#E85D9B",
    var menuColor: String = "#282828",
    var controlColor: String = "#4C4C4C",
    var secondaryColor: String = "#414141",
    var textColor: String = "#FFFFFF"
)

@Serializable
data class BaseConfigurationFactory(
    @SerialName("General") var generalSettings: General = General(),
    @SerialName("Multi Threading") var multithreading: Multithreading = Multithreading(),
    @SerialName("Custom Proxy") var customProxy: CustomProxy = CustomProxy(),
    @SerialName("Theme") var themeSettings: Theme = Theme()
) {
    companion object {
        private var pcFactoryInstance: BaseConfigurationFactory? = null
        private val configFile = File("configuration.json")
        private val jsonFormatter = Json {
            encodeDefaults = true
            prettyPrint = true
        }

        @Synchronized
        fun getInstance(): BaseConfigurationFactory {
            pcFactoryInstance = pcFactoryInstance ?: if (configFile.exists()) {
                jsonFormatter.decodeFromString(serializer(), configFile.readText())
            } else {
                BaseConfigurationFactory().also {
                    configFile.writeText(jsonFormatter.encodeToString(serializer(), it))
                }
            }
            return pcFactoryInstance!!
        }

        fun createConfig() {
            if (!configFile.exists()) {
                runCatching {
                    configFile.writeText(jsonFormatter.encodeToString(serializer(), getInstance()))
                }.onFailure {
                    Logger.printError("Failed to create configuration file: ${it.message}")
                }.onSuccess {
                    Logger.printSuccess("Created configuration file.")
                }
            }
        }

        fun updateValue(updateFunction: BaseConfigurationFactory.() -> Unit) {
            getInstance().apply(updateFunction)
            configFile.run {
                if (exists()) {
                    writeText(jsonFormatter.encodeToString(serializer(), getInstance()))
                } else createConfig()
            }
        }
    }
}
