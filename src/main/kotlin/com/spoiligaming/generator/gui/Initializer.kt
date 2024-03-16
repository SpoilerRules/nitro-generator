package com.spoiligaming.generator.gui

import com.spoiligaming.generator.GeneratorBean
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.logging.Logger
import javafx.application.Application
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.stage.Screen
import javafx.stage.Stage
import javafx.stage.StageStyle
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
        borderPane.style = "-fx-background-color: ${ColorPalette.CONTROL_COLOR}; -fx-background-radius: 16;"
        val scene = Scene(borderPane, 600.0, 425.0)
        scene.fill = Color.TRANSPARENT

        primaryStage.scene = scene
        primaryStage.initStyle(StageStyle.TRANSPARENT)

        scene.setOnMousePressed { event ->
            xOffset = event.sceneX
            yOffset = event.sceneY
        }

        scene.setOnMouseDragged { event ->
            // prevents the user from dragging the window outside the visible area of the screen or behind the taskbar
            Screen.getPrimary().visualBounds.let { bounds ->
                primaryStage.apply {
                    x = (event.screenX - xOffset).coerceIn(bounds.minX, bounds.maxX - width)
                    y = (event.screenY - yOffset).coerceIn(bounds.minY, bounds.maxY - height)
                }
            }
        }

        scene.stylesheets.add(javaClass.getResource("/console-style.css")!!.toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/checkbox-style.css")!!.toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/combobox-style.css")!!.toExternalForm())
        scene.stylesheets.add(javaClass.getResource("/textarea-style.css")!!.toExternalForm())

        primaryStage.title = "Spoili's Nitro Generator - 1.0.0"
        primaryStage.isResizable = false

        primaryStage.setOnCloseRequest {
            exitProcess(0)
        }

        borderPane.bottom = addFundamentalButtons(primaryStage)

        primaryStage.show()
        BaseConfigurationFactory.createConfig()
        GeneratorBean.startGeneratingNitro()
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
                    Logger.printSuccess("Nitro generation has been paused. Any ongoing nitro validation process will now exit.")
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
                exitProcess(0)
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
}