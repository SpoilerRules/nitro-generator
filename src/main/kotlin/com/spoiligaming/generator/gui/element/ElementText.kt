package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.TextField
import javafx.scene.layout.HBox

object ElementText {
    fun addTextValue(
        initialValue: String,
        labelText: String,
        tooltipText: String?,
        valueUpdater: (String) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0)
    ) = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        children.addAll(TextField().apply {
            setMaxSize(142.0, 25.0)
            setMinSize(142.0, 25.0)
            style = "-fx-background-color: ${ColorPalette.CONTROL_COLOR}; -fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 12; -fx-highlight-fill: ${ColorPalette.ACCENT_COLOR}; -fx-padding: 0 5 0 5;"
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

        Platform.runLater { requestFocus() }
    }
}