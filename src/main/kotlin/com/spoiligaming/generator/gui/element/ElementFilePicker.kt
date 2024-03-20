package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.ProxyHandler
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import com.spoiligaming.logging.Logger
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.stage.FileChooser
import java.io.File


//TODO: make this more flexible and scalable.
object ElementFilePicker {
    fun addTextValue(
        gridPane: GridPane,
        initialValue: String,
        tooltipText: String? = null,
        valueUpdater: (String) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0)
    ): VBox {
        val selectedFileLabel = Label().apply {
            style = "-fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
            text = if (BaseConfigurationFactory.getInstance().proxySettings.proxyFilePath.isEmpty()) "No proxy file is selected." else "Selected proxy file: ${File(initialValue).name}"
        }

        return VBox().apply {
            alignment = Pos.TOP_LEFT
            spacing = 5.0
            this.padding = padding

            children.addAll(
                HBox().apply {
                    spacing = 5.0
                    children.add(Button("Select proxy file").apply {
                        alignment = Pos.CENTER
                        setMaxSize(142.0, 25.0)
                        setMinSize(142.0, 25.0)
                        style = "-fx-background-color: ${ColorPalette.CONTROL_COLOR}; -fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 12; -fx-highlight-fill: ${ColorPalette.ACCENT_COLOR}; -fx-padding: 0;"
                        setOnAction {
                            FileChooser().apply {
                                extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
                                showOpenDialog(gridPane.scene.window)?.let { selectedFile ->
                                    selectedFileLabel.text = "Selected proxy file: ${selectedFile.name}"
                                    valueUpdater(selectedFile.toPath().toString())
                                    ProxyHandler.loadProxies().also { Logger.printDebug("Reloading proxies...") }
                                } ?: run {
                                    Logger.printError("Failed to select a proxy file because no file was chosen.")
                                    valueUpdater("")
                                    selectedFileLabel.text = "No proxy file was selected."
                                }
                            }
                        }
                        setOnMouseEntered { scene.cursor = Cursor.HAND }
                        setOnMouseExited { scene.cursor = Cursor.DEFAULT }
                    })
                    tooltipText?.let {
                        children.add(CommonElement.createTooltip(it))
                    }
                },
                HBox().apply {
                    alignment = Pos.TOP_LEFT
                    children.add(selectedFileLabel)
                }
            )
        }
    }
}