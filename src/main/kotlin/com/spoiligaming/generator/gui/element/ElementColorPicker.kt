package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.beans.binding.Bindings
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.ColorPicker
import javafx.scene.layout.HBox
import javafx.scene.paint.Color
import java.util.Locale

object ElementColorPicker {
    fun addColorPickerValue(
        initialValue: String,
        labelText: String,
        valueUpdater: (String) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0),
    ) = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        val colorPicker =
            ColorPicker().apply {
                value = Color.web(initialValue)
                valueProperty().addListener { _, _, newValue ->
                    valueUpdater("#" + newValue.toString().substring(2, 8))
                }
                isVisible = false
            }

        children.addAll(
            Button().apply {
                setMaxSize(25.0, 25.0)
                setMinSize(25.0, 25.0)
                styleProperty().bind(
                    Bindings.concat(
                        "-fx-background-color: ",
                        String.format(
                            Locale.US,
                            "#%02X%02X%02X",
                            (colorPicker.value.red * 255).toInt(),
                            (colorPicker.value.green * 255).toInt(),
                            (colorPicker.value.blue * 255).toInt(),
                        ),
                        "; -fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 6; -fx-highlight-fill: ${ColorPalette.accentColor};",
                    ),
                )
                setOnAction { colorPicker.show() }
            },
            CommonElement.createLabel(labelText),
            colorPicker,
        )
    }
}
