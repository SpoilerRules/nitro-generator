package com.spoiligaming.generator.autoretention

import com.charleskorn.kaml.Yaml
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import java.io.File

@Serializable
data class InformationalFileFactory(
    @SerialName("NitroCode") val nitroCode: String,
    @SerialName("ValidationDate") val validationDate: String,
    @SerialName("isPromotionalCode") val promotionalCode: Boolean,
    @SerialName("Retries") val retries: Int,
    @SerialName("WasClaimed") val wasClaimed: Boolean,
    @SerialName("Proxy") val proxyUsed: String,
    @SerialName("ThreadIdentity") val threadIdentity: String,
) {
    companion object {
        @Suppress("unused")
        fun deserialize(yamlFile: File): List<InformationalFileFactory> =
            Yaml.default.decodeFromString(
                ListSerializer(serializer()),
                yamlFile.readText(),
            )
    }

    fun append(yamlFile: File) =
        yamlFile.appendText(
            buildString {
                mapOf(
                    "NitroCode" to nitroCode,
                    "ValidationDate" to validationDate,
                    "isPromotionalCode" to promotionalCode,
                    "Retries" to retries,
                    "WasClaimed" to wasClaimed,
                    "Proxy" to proxyUsed,
                    "ThreadIdentity" to threadIdentity,
                ).forEach { (key, value) ->
                    appendLine("${if (key == "NitroCode") "- " else "  "}$key: \"$value\"")
                }
            },
        )
}
