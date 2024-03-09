package com.spoiligaming.generator.gui

import com.spoiligaming.generator.GeneratorBean
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.Logger
import javafx.application.Application
import javafx.application.Platform
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
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
    private var xOffset = 0.0
    private var yOffset = 0.0

    override fun start(primaryStage: Stage) {
        System.setProperty("prism.lcdtext", "false");
        System.setProperty("prism.text", "t2k");
        val borderPane = BorderPane()

        borderPane.left = TabContainer()
        TabHandler.allocatePane()
        borderPane.center = TabHandler.tabContentPane
        borderPane.style = "-fx-background-color: #4C4C4C; -fx-background-radius: 16;"
        val scene = Scene(borderPane, 600.0, 425.0)
        scene.fill = Color.TRANSPARENT

        primaryStage.scene = scene
        primaryStage.initStyle(StageStyle.TRANSPARENT)

        scene.setOnKeyPressed { event ->
            isShiftDown = event.isShiftDown
            isControlDown = event.isControlDown
        }
        scene.setOnKeyReleased { event ->
            isShiftDown = event.isShiftDown
            isControlDown = event.isControlDown
        }

        scene.setOnMousePressed { event ->
            xOffset = event.sceneX
            yOffset = event.sceneY
        }

        scene.setOnMouseDragged { event ->
            primaryStage.x = event.screenX - xOffset
            primaryStage.y = event.screenY - yOffset
        }

        scene.stylesheets.add(javaClass.getResource("/console-style.css")!!.toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/checkbox-style.css")!!.toExternalForm())

        primaryStage.title = "Spoili's Nitro Generator - 1.0.0"
        primaryStage.isResizable = false

        primaryStage.setOnCloseRequest {
            exitProcess(0)
        }

        borderPane.bottom = addFundamentalButtons(primaryStage)/*HBox().apply {
            alignment = Pos.BOTTOM_RIGHT
            padding = Insets(-35.0, 10.0, 10.0, 0.0)
            style = "-fx-background-color: transparent;"

            val exitButton = Button("Exit").apply {
                background = Background(BackgroundFill(Color.web("#282828"), CornerRadii(16.0, false), null))
                style = """
            -fx-text-fill: #FFFFFF; 
            -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; 
            -fx-font-size: 13; 
            -fx-background-radius: 16px;
            -fx-background-color: #282828;
            -fx-control-inner-background: #282828;
            -fx-focus-color: #282828;
            -fx-faint-focus-color: transparent;
            -fx-selection-bar-text: white;
        """
                setOnAction { Platform.exit() }
                setMaxSize(100.0, 25.0)
                setMinSize(100.0, 25.0)

                setOnMouseEntered {
                    style += "-fx-text-fill: #414141;"
                    scene.cursor = Cursor.HAND
                }
                setOnMouseExited {
                    style += "-fx-text-fill: #FFFFFF;"
                    scene.cursor = Cursor.DEFAULT
                }
            }
            children.add(exitButton)*/

        primaryStage.show()
        BaseConfigurationFactory.createConfig()
        GeneratorBean.startGeneratingNitro(false)
    }

    private fun addFundamentalButtons(stage: Stage): HBox {
        val buttonSize = 100.0 to 25.0
        val buttonStyle = { color: String ->
            "-fx-background-color: #282828; -fx-text-fill: $color; -fx-background-radius: 16px; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 13;"
        }

        val pauseButton = Button().apply {
            setMaxSize(buttonSize.first, buttonSize.second)
            setMinSize(buttonSize.first, buttonSize.second)
            styleProperty().bind(Bindings.`when`(GeneratorBean.isGenerationPaused)
                .then(buttonStyle("#E85D9B"))
                .otherwise(buttonStyle("#FFFFFF")))
            textProperty().bind(Bindings.`when`(GeneratorBean.isGenerationPaused)
                .then("Resume")
                .otherwise("Pause"))
            setOnAction {
                GeneratorBean.isGenerationPaused.set(!GeneratorBean.isGenerationPaused.get())
                if (GeneratorBean.isGenerationPaused.get()) {
                    Logger.printSuccess("Nitro generation has been paused.")
                } else {
                    Logger.printSuccess("Nitro generation has resumed.")
                }
            }

            setOnMouseEntered {
                styleProperty().bind(Bindings.`when`(GeneratorBean.isGenerationPaused)
                    .then(buttonStyle("#c84969"))
                    .otherwise(buttonStyle("#c8c8c8")))
                scene.cursor = Cursor.HAND
            }
            setOnMouseExited {
                styleProperty().bind(Bindings.`when`(GeneratorBean.isGenerationPaused)
                    .then(buttonStyle("#E85D9B"))
                    .otherwise(buttonStyle("#FFFFFF")))
                scene.cursor = Cursor.DEFAULT
            }
        }

        val minimizeButton = Button().apply {
            setMaxSize(buttonSize.first, buttonSize.second)
            setMinSize(buttonSize.first, buttonSize.second)
            styleProperty().bind(Bindings.`when`(stage.iconifiedProperty())
                .then(buttonStyle("#E85D9B"))
                .otherwise(buttonStyle("#FFFFFF")))
            textProperty().bind(Bindings.`when`(stage.iconifiedProperty())
                .then("Restore")
                .otherwise("Minimize"))
            setOnAction {
                if (stage.isIconified) {
                    stage.isIconified = false
                    Logger.printSuccess("Application has been restored.")
                } else {
                    stage.isIconified = true
                    Logger.printSuccess("Application has been minimized.")
                }
            }

            setOnMouseEntered {
                styleProperty().bind(Bindings.`when`(stage.iconifiedProperty())
                    .then(buttonStyle("#c84969"))
                    .otherwise(buttonStyle("#c8c8c8")))
                scene.cursor = Cursor.HAND
            }
            setOnMouseExited {
                styleProperty().bind(Bindings.`when`(stage.iconifiedProperty())
                    .then(buttonStyle("#E85D9B"))
                    .otherwise(buttonStyle("#FFFFFF")))
                scene.cursor = Cursor.DEFAULT
            }
        }

        val exitButton = Button("Exit").apply {
            setMaxSize(buttonSize.first, buttonSize.second)
            setMinSize(buttonSize.first, buttonSize.second)
            style = buttonStyle("#FFFFFF")

            setOnAction {
                Platform.exit()
            }

            setOnMouseEntered {
                style += "-fx-text-fill: #c8c8c8;"
                scene.cursor = Cursor.HAND
            }

            setOnMouseExited {
                style += "-fx-text-fill: #FFFFFF;"
                scene.cursor = Cursor.DEFAULT
            }
        }

        return HBox().apply {
            alignment = Pos.BOTTOM_RIGHT
            padding = Insets(-55.0, 10.0, 10.0, 0.0)
            style = "-fx-background-color: transparent;"

            children.add(BorderPane().apply {
                background = Background(BackgroundFill(Color.web("#414141"), CornerRadii(16.0), Insets.EMPTY))
                setMaxSize(325.0, 40.0)
                setMinSize(325.0, 40.0)

                center = HBox(pauseButton, minimizeButton, exitButton).apply {
                    alignment = Pos.CENTER
                    spacing = 5.0
                }
            })
        }
    }

    companion object {
        var isShiftDown = false
        var isControlDown = false

        fun addGeneralTab(tabPane: TabPane): GridPane {
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
                    valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        Int.MAX_VALUE,
                        generalSettings.generationDelay.toInt(),
                        1
                    )
                },
                Spinner<Int>().apply {
                    isEditable = true
                    valueFactory =
                        SpinnerValueFactory.IntegerSpinnerValueFactory(0, Int.MAX_VALUE, generalSettings.retryDelay, 1)
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

            return gridPane
        }
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