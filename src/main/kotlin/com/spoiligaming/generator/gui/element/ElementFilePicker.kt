package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Button
import javafx.scene.control.TextField
import javafx.scene.control.Tooltip
import javafx.scene.layout.HBox

//todo: make this a real file picker using file chooser.
object ElementFilePicker {
    fun addTextValue(
        initialValue: String,
        labelText: String,
        tooltipText: String? = null,
        valueUpdater: (String) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0)
    ) = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        children.addAll(TextField().apply {
            setMaxSize(142.0, 25.0)
            setMinSize(142.0, 25.0)
            style =
                "-fx-background-color: ${ColorPalette.CONTROL_COLOR}; -fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 12; -fx-highlight-fill: ${ColorPalette.ACCENT_COLOR};"
            text = initialValue

            focusedProperty().addListener { _, _, isFocused ->
                if (!isFocused) {
                    valueUpdater(text)
                }
            }
        }, CommonElement.createLabel(labelText))
        tooltipText?.let {
            children.add(CommonElement.createTooltip(it))
        }
    }
}