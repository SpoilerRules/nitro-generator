package com.spoiligaming.generator.gui

import com.spoiligaming.logging.CEnum
import com.spoiligaming.logging.Logger
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class StylesheetVisualizationManager {
    private val checkBoxStylesheet = javaClass.getResource("/checkbox-style.css")!!.readText() // accent, control
    private val consoleStylesheet = javaClass.getResource("/console-style.css")!!.readText() // text, secondary

    private var newStylesheetResource: MutableSet<String> = mutableSetOf()
    private lateinit var newCheckboxStyle: String
    private lateinit var newConsoleStyle: String

    init {
        if (!checkBoxStylesheet.contains(ColorPalette.accentColor) ||
            !checkBoxStylesheet.contains(ColorPalette.controlColor)
        ) {
            newCheckboxStyle =
                checkBoxStylesheet.replace("#E85D9B", ColorPalette.accentColor)
                    .replace("#4C4C4C", ColorPalette.controlColor)

            newStylesheetResource.add("modifiedCheckboxStylesheet")
        }

        if (!consoleStylesheet.contains(ColorPalette.textColor) ||
            !consoleStylesheet.contains(ColorPalette.secondaryColor)
        ) {
            newConsoleStyle =
                consoleStylesheet.replace("#FFFFFF", ColorPalette.textColor)
                    .replace("#414141", ColorPalette.secondaryColor)

            newStylesheetResource.add("modifiedConsoleStylesheet")
        }
    }

    private fun getTempStylesheets(element: Stylesheet): File {
        if (newStylesheetResource.isEmpty()) {
            throw IllegalArgumentException(
                "No modified stylesheets found for element ${element.fileName}. Please contact your developer for assistance.",
            )
        }

        val tempFile = File(System.getProperty("java.io.tmpdir"), "${element.fileName}.css")

        Files.write(
            Paths.get(tempFile.absolutePath),
            when (element) {
                Stylesheet.CONSOLE -> newConsoleStyle
                Stylesheet.CHECKBOX -> newCheckboxStyle
            }.toByteArray(),
        )

        Logger.printDebug(
            "Created temporary stylesheet file for ${CEnum.BRIGHT_PINK}$element${CEnum.RESET} element: ${CEnum.BRIGHT_PURPLE}${tempFile.absolutePath}${CEnum.RESET}",
        )

        return tempFile
    }

    fun getModifiedStylesheet(element: Stylesheet): String =
        when (element) {
            Stylesheet.CONSOLE ->
                if (::newConsoleStyle.isInitialized) {
                    getTempStylesheets(element).toURI().toURL().toExternalForm()
                } else {
                    javaClass.getResource("/console-style.css")!!.toExternalForm()
                }
            Stylesheet.CHECKBOX ->
                if (::newCheckboxStyle.isInitialized) {
                    getTempStylesheets(element).toURI().toURL().toExternalForm()
                } else {
                    javaClass.getResource("/checkbox-style.css")!!.toExternalForm()
                }
        }

    enum class Stylesheet(val fileName: String) {
        CHECKBOX("modifiedCheckboxStylesheet"),
        CONSOLE("modifiedConsoleStylesheet"),
    }
}
