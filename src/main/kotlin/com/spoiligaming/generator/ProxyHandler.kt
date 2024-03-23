package com.spoiligaming.generator

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.util.concurrent.atomic.AtomicInteger
import kotlin.io.path.absolutePathString
import kotlin.io.path.bufferedWriter
import kotlin.io.path.deleteIfExists

object ProxyHandler {
    private val proxyIndex = AtomicInteger(0)
    private var proxies: List<Pair<String, Int>> = emptyList()

    private val numericRegex = Regex("[^0-9]")

    init {
        loadProxies()
    }

    private fun loadProxiesFromFile(fileName: String) {
        val file = File(fileName)

        if (!file.exists()) {
            Logger.printError("Proxy file not found: $fileName")
            return
        }

        proxies =
            file.readLines().mapNotNull { line ->
                line.split(":").takeIf { it.size == 2 }?.let { (host, port) ->
                    runCatching {
                        Pair(host, port.trim().replace(numericRegex, "").toInt())
                    }.onFailure {
                        Logger.printError("Invalid port number in proxy file: $line due to $it.message")
                    }.getOrNull()
                }
            }
        Logger.printDebug(
            "Loaded ${CEnum.BRIGHT_PINK}${proxies.size} proxies${CEnum.RESET} from file: ${CEnum.BRIGHT_PURPLE}$fileName${CEnum.RESET}",
        )
    }

    private fun loadProxiesFromURL(
        rawContentLink: List<String>,
        rawContentSeparator: String,
    ) {
        Files.createTempFile("temp_proxies", ".txt").also { tempFile ->
            tempFile.bufferedWriter().use { writer ->
                rawContentLink.forEach { url ->
                    val startTime = System.currentTimeMillis()
                    runCatching {
                        URI(url).toURL().readText()
                    }.onSuccess { content ->
                        Logger.printDebug(
                            "Successfully read proxy content from $url in ${CEnum.BRIGHT_PINK}${System.currentTimeMillis() - startTime}ms${CEnum.RESET}. The size of the raw content is ${CEnum.BRIGHT_PINK}${
                                "%.2f".format(
                                    content.toByteArray().size / 1024.0,
                                )
                            }KB${CEnum.RESET}.",
                        )
                        writer.write(content)
                        writer.write(rawContentSeparator)
                    }.onFailure {
                        Logger.printError("Failed to read proxy content from $url due to ${it.message}")
                    }
                }
            }
            Logger.printDebug("Temporary proxy file created: ${CEnum.BRIGHT_PURPLE}${tempFile.absolutePathString()}${CEnum.RESET}")
            loadProxiesFromFile(tempFile.absolutePathString())

            runCatching {
                tempFile.deleteIfExists()
            }.onFailure {
                Logger.printError(
                    "Failed to delete temporary proxy file located at ${tempFile.absolutePathString()} due to ${it::class.simpleName}: ${it.message}",
                )
            }.onSuccess {
                Logger.printDebug("Temporary proxy file deleted after saving the content to random access memory.")
            }
        }
    }

    // use when new mode is static or custom proxy is disabled
    fun unloadProxies() {
        if (proxies.isNotEmpty()) {
            proxies = emptyList()
            proxyIndex.set(0)
            Logger.printSuccess("Proxies have been unloaded to free up system memory.")
        }
    }

    @Synchronized
    fun loadProxies() {
        /* Exception("Debugging Stack Trace").apply {
             stackTrace.forEach { println(it) }
         }*/

        when (BaseConfigurationFactory.getInstance().proxySettings.mode) {
            1 -> {
                "Proxy Mode was set to 'Static', and ProxyHandler class cannot be used for Static mode. Please contact your developer for assistance.".run {
                    Logger.printError(this)
                    throw UnsupportedOperationException(this)
                }
            }

            2 -> loadProxiesFromFile(BaseConfigurationFactory.getInstance().proxySettings.proxyFilePath)
            3 ->
                loadProxiesFromURL(
                    BaseConfigurationFactory.getInstance().proxySettings.rawContentLinks.split(",").map { it.trim() },
                    BaseConfigurationFactory.getInstance().proxySettings.rawContentSeparator,
                )
        }
    }

    @Synchronized
    fun getNextProxy(): Pair<String, Int>? {
        if (proxies.isEmpty()) {
            Logger.printWarning("All proxies are exhausted. Consider inserting new set of proxies.")
            return null
        }

        if (proxyIndex.get() >= proxies.size) {
            if (BaseConfigurationFactory.getInstance().proxySettings.recursiveUsaqe) {
                Logger.printWarning("All proxies are exhausted. Starting from the beginning of the proxy list.")
                proxyIndex.set(0)
            } else {
                Logger.printWarning("All proxies are exhausted. Consider inserting new set of proxies.")
                return null
            }
        }

        val proxy = proxies[proxyIndex.get()]
        proxyIndex.set((proxyIndex.get() + 1) % proxies.size)
        return proxy
    }
}
