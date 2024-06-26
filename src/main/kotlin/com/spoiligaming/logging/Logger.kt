package com.spoiligaming.logging

import javafx.application.Platform
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object Logger {
    var showDebug = false
    var uiEnabled = true

    private val dateTimeFormatter by lazy { DateTimeFormatter.ofPattern("HH:mm:ss") }
    private val dateFormatter by lazy { DateTimeFormatter.ofPattern("yyyy-MM-dd_HH.mm.ss") }

    private val logFile: File by lazy {
        File("logs/logs-${LocalDateTime.now().format(dateFormatter)}.txt").apply {
            parentFile.mkdirs()
        }
    }

    fun <V> printError(error: V) = log("ERROR", error, CEnum.ERROR_RED)

    fun <V> printSuccess(
        message: V,
        nitroGenerationLog: Boolean = false,
    ) = if (!nitroGenerationLog) {
        log("OK", message, CEnum.GREEN)
    } else {
        if (uiEnabled) {
            Platform.runLater {
                println(
                    "${
                        createStatus(
                            CEnum.GREEN,
                            "OK",
                        )
                    } $message",
                )
            }
        } else {
            println(
                "${
                    createStatus(
                        CEnum.GREEN,
                        "OK",
                    )
                } $message",
            )
        }
    }

    fun <V> printWarning(warning: V) = log("WARNING", warning, CEnum.YELLOW)

    fun <V> printDebug(information: V) = log("DEBUG", information, CEnum.ORANGE)

    private fun <V> log(
        level: String,
        message: V,
        color: CEnum,
    ) {
        if (level != "DEBUG" || showDebug) {
            if (uiEnabled) {
                Platform.runLater {
                    println("${createStatus(color, level)} $message")
                }
            } else {
                println("${createStatus(color, level)} $message")
            }
        }
        logFile.appendText(
            "${
                "[${LocalDateTime.now().format(dateTimeFormatter)}] ${
                    createStatus(
                        color,
                        level,
                    )
                } $message".replace("\u001B\\[[;\\d]*m".toRegex(), "")
            }\n",
        )
    }

    private fun createStatus(
        primaryColor: CEnum,
        status: String,
    ): String = "${CEnum.RESET}[$primaryColor$status${CEnum.RESET}]${CEnum.RESET}"
}
