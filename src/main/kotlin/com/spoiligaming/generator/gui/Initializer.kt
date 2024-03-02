package com.spoiligaming.generator.gui

import com.spoiligaming.generator.GeneratorBean
import com.spoiligaming.generator.TabContainer
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import javafx.application.Application
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle
import java.io.OutputStream
import java.io.PrintStream
import kotlin.system.exitProcess

class Initializer : Application() {
    override fun start(primaryStage: Stage) {
        val tabPane = TabContainer()

        val scene = Scene(tabPane, 600.0, 225.0)
        scene.fill = Color.TRANSPARENT

        primaryStage.scene = scene
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        primaryStage.title = "Spoili's Nitro Generator - 1.0.0"
        primaryStage.isResizable = false

        addGeneralTab(tabPane)
        with(TabHandler(tabPane)) {
            addProxyTab()
            addAdvancedTab()
        }
        setupConsoleOutput(tabPane)
        primaryStage.setOnCloseRequest {
            exitProcess(0)
        }

        primaryStage.show()
        BaseConfigurationFactory.createConfig()
        GeneratorBean.startGeneratingNitro(false)
    }

    private fun addGeneralTab(tabPane: TabPane) {
        val generalSettings = BaseConfigurationFactory.getInstance().generalSettings

        val gridPane = GridPane().apply {
            vgap = 10.0
            hgap = 10.0
            alignment = Pos.CENTER
        }

        val cssStyle = """
    -fx-text-fill: #FFFFFF; 
    -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; 
    -fx-font-size: 13; 
    -fx-background-radius: 12px;
    -fx-control-inner-background: #4C4C4C;
    -fx-focus-color: white;
    -fx-faint-focus-color: transparent;
    """

        val spinnerStyle = """
    -fx-text-fill: #FFFFFF; 
    -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; 
    -fx-font-size: 13; 
    -fx-control-inner-background: #4C4C4C;
    -fx-focus-color: white;
    -fx-faint-focus-color: transparent;
    """

        val checkboxes = listOf(
            CheckBox("Log Generation Info").apply { isSelected = generalSettings.logGenerationInfo },
            CheckBox("Validate Nitro Code").apply { isSelected = generalSettings.validateNitroCode },
            CheckBox("Alert Webhook for Valid Nitro Code").apply { isSelected = generalSettings.alertWebhook }
        )

        val spinners = listOf(
            Spinner<Int>().apply {
                isEditable = true
                valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Int.MAX_VALUE, generalSettings.generationDelay.toInt(), 1)
            },
            Spinner<Int>().apply {
                isEditable = true
                valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, Int.MAX_VALUE, generalSettings.retryDelay, 1)
            }
        )

        val textFieldDiscordWebhookURL = TextField(generalSettings.discordWebhookURL).apply {
            style = cssStyle
            textProperty().addListener { _, _, newValue ->
                BaseConfigurationFactory.updateValue {
                    generalSettings.discordWebhookURL = newValue
                }
            }
        }

        val labels = listOf(
            Label("Generation Delay (ms)").also { it.style = cssStyle },
            Label("Retry Delay (s)").also { it.style = cssStyle },
            Label("Discord Webhook URL").also { it.style = cssStyle }
        )

        checkboxes.forEachIndexed { index, checkBox ->
            checkBox.style = cssStyle
            checkBox.setOnAction {
                BaseConfigurationFactory.updateValue {
                    when (index) {
                        0 -> generalSettings.logGenerationInfo = checkBox.isSelected
                        1 -> generalSettings.validateNitroCode = checkBox.isSelected
                        2 -> generalSettings.alertWebhook = checkBox.isSelected
                    }
                }
            }
            gridPane.addRow(index, checkBox)
        }

        spinners.forEachIndexed { index, spinner ->
            spinner.style = spinnerStyle
            spinner.valueProperty().addListener { _, _, newValue ->
                BaseConfigurationFactory.updateValue {
                    when (index) {
                        0 -> generalSettings.generationDelay = newValue.toLong()
                        1 -> generalSettings.retryDelay = newValue.toInt()
                    }
                }
            }
            gridPane.addRow(index + checkboxes.size, labels[index], spinner)
        }

        gridPane.addRow(checkboxes.size + spinners.size, labels[2], textFieldDiscordWebhookURL)

        val generalTab = Tab("General", gridPane)
        generalTab.isClosable = false
        tabPane.tabs.add(0, generalTab)
    }

    private fun setupConsoleOutput(tabPane: TabPane) {
        val console = TextArea().apply {
            isEditable = false
            isMouseTransparent = true
            isFocusTraversable = false
            style = "-fx-text-fill: black; -fx-padding: 28px; -fx-focus-color: transparent; -fx-faint-focus-color: transparent; -fx-control-inner-background: transparent;"
            font = Font.font("Roboto", 12.0)
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
            border = Border(BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.EMPTY))
        }

        val outputStream = object : OutputStream() {
            override fun write(b: Int) {
                Platform.runLater {
                    console.appendText(b.toChar().toString())
                }
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                val filteredStr = String(b, off, len).replace("\u001B\\[[;\\d]*m".toRegex(), "")
                Platform.runLater {
                    console.appendText(filteredStr)
                }
            }
        }

        System.setOut(PrintStream(outputStream, true))

        val consoleTab = Tab("Console", console)
        consoleTab.isClosable = false
        tabPane.tabs?.add(3, consoleTab)
    }
}