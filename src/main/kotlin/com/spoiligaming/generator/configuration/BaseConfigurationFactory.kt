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
    @SerialName("retry") var retryTillValid: Boolean = true,
    var retryDelay: Int = 5,
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
    var rawContentSeparator: String = "\n",
) {
    fun getProxyType(protocol: Int): Proxy.Type =
        when (protocol) {
            1 -> Proxy.Type.HTTP
            2 -> Proxy.Type.SOCKS
            else -> throw IllegalArgumentException("Invalid protocol type")
        }
}

@Serializable
data class DiscordWebhookAlert(
    @SerialName("alertWebhookForValidNitroCode") var alertWebhook: Boolean = true,
    var discordWebhookURL: String = "https://dummylink.com/suspicious-webhook/",
)

@Serializable
data class AutoClaim(
    var enabled: Boolean = true,
    var retryTillSuccess: Boolean = true,
    var accountToken: String = "dGhpcyBpcyBhIGR1bW15IHRva2Vu",
)

@Serializable
data class AutoRetention(
    var enabled: Boolean = true,
    var contentSeparator: String = "\n",
    var informationalFile: Boolean = true,
)

@Serializable
data class Theme(
    var accentColor: String = "#E85D9B",
    var menuColor: String = "#282828",
    var controlColor: String = "#4C4C4C",
    var secondaryColor: String = "#414141",
    var textColor: String = "#FFFFFF",
)

@Serializable
data class BaseConfigurationFactory(
    @SerialName("General") var generalSettings: General = General(),
    @SerialName("Custom Proxy") var proxySettings: CustomProxy = CustomProxy(),
    @SerialName("Multi Threading") var multithreadingSettings: Multithreading = Multithreading(),
    @SerialName("Discord Webhook Alert") var discordWebhookAlertSettings: DiscordWebhookAlert = DiscordWebhookAlert(),
    @SerialName("Auto Claim") var autoClaimSettings: AutoClaim = AutoClaim(),
    @SerialName("Auto Retention") var autoRetentionSettings: AutoRetention = AutoRetention(),
    @SerialName("Theme") var themeSettings: Theme = Theme(),
) {
    companion object {
        var isConfigUpdated: Boolean = false

        private var configFactoryInstance: BaseConfigurationFactory? = null
        private val configFile = File("configuration.json")
        private val jsonFormatter =
            Json {
                encodeDefaults = true
                prettyPrint = true
            }

        @Synchronized
        fun getInstance(): BaseConfigurationFactory {
            configFactoryInstance = configFactoryInstance ?: if (configFile.exists()) {
                jsonFormatter.decodeFromString(serializer(), configFile.readText())
            } else {
                BaseConfigurationFactory().also {
                    runCatching {
                        configFile.writeText(jsonFormatter.encodeToString(serializer(), it))
                    }.onFailure {
                        Logger.printError("Failed to create configuration file: ${it.message}")
                    }.onSuccess {
                        Logger.printSuccess("Created configuration file.")
                    }
                }
            }
            return configFactoryInstance!!
        }

        fun updateValue(updateFunction: BaseConfigurationFactory.() -> Unit) {
            isConfigUpdated = true
            getInstance().apply(updateFunction).also {
                configFile.writeText(jsonFormatter.encodeToString(serializer(), it))
            }
        }
    }
}
