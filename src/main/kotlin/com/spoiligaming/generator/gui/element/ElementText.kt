package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox

object ElementText {
    fun addTextValue(
        initialValue: String,
        labelText: String,
        inputRegex: Regex = Regex("^[a-zA-Z0-9\\\\]*$"),
        tooltipText: String?,
        valueUpdater: (String) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0),
    ) = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        children.addAll(
            TextField().apply textField@{
                setMaxSize(142.0, 25.0)
                setMinSize(142.0, 25.0)
                style =
                    "-fx-background-color: ${ColorPalette.controlColor}; -fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 12; -fx-highlight-fill: ${ColorPalette.accentColor}; -fx-padding: 0 5 0 5;"
                text = initialValue

                textProperty().addListener { _, oldValue, newValue ->
                    if (!newValue.matches(inputRegex)) {
                        text = oldValue
                    }
                }

                setOnKeyPressed { event ->
                    if (event.code == KeyCode.ENTER) {
                        Platform.runLater {
                            valueUpdater(text)
                            scene?.focusOwner?.requestFocus()
                        }
                    }
                }

                setOnMouseClicked { event ->
                    if (event.button == MouseButton.PRIMARY) {
                        Platform.runLater {
                            valueUpdater(text)
                            scene?.focusOwner?.requestFocus()
                        }
                    }
                }
            },
            CommonElement.createLabel(labelText),
        )
        tooltipText?.let {
            children.add(CommonElement.createTooltip(it))
        }

        Platform.runLater { requestFocus() }
    }
}
