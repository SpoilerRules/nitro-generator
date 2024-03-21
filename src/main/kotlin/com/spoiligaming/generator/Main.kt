package com.spoiligaming.generator

import com.spoiligaming.generator.gui.Initializer
import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import javafx.application.Application
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.net.URI

fun main(args: Array<String>) {
    Logger.showDebug = "-debug" in args
    checkForUpdate()
    Application.launch(Initializer::class.java)
}

private fun checkForUpdate() = runBlocking(Dispatchers.IO) {
    val localVersion = Thread.currentThread().contextClassLoader.getResourceAsStream("version")?.bufferedReader()?.use { it.readText().trim() }
    Logger.printDebug("Local version of the software: $localVersion")

    val remoteVersion = withTimeoutOrNull(10000) {
        URI("https://raw.githubusercontent.com/SpoilerRules/nitro-generator/master/build.gradle.kts").toURL()
            .readText()
            .lineSequence()
            .map { it.trim() }
            .firstOrNull { it.startsWith("version =") }
            ?.split("\"")?.get(1)
    } ?: return@runBlocking Logger.printDebug("Failed to fetch remote version to check update")

    Logger.printDebug("Remote version of the software: $remoteVersion")

    when {
        localVersion != remoteVersion -> Logger.printWarning("${CEnum.RED}An update is available!${CEnum.RESET} Please visit https://github.com/SpoilerRules/nitro-generator/releases/latest to download the latest version (${CEnum.BRIGHT_PURPLE}$remoteVersion${CEnum.RESET}).")
        else -> Logger.printDebug("You're using the latest version of this software.")
    }
}
