package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import com.spoiligaming.logging.Logger
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ColorPicker
import javafx.scene.layout.HBox

object ElementColorPicker {
    fun addColorPickerValue(
        initialValue: String,
        labelText: String,
        valueUpdater: (String) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0)
    ) = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        children.addAll(ColorPicker().apply {
            setMaxSize(142.0, 25.0)
            setMinSize(142.0, 25.0)
            style = "-fx-color-label-visible: false; -fx-background-color: ${ColorPalette.CONTROL_COLOR}; -fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 12; -fx-highlight-fill: ${ColorPalette.ACCENT_COLOR};"
            valueProperty().addListener { _, _, newValue ->
                valueUpdater("#" + newValue.toString().substring(2, 8))
            }
        }, CommonElement.createLabel(labelText))
    }
}
