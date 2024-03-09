package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.CheckBox
import javafx.scene.layout.HBox

object ElementBoolean {
    fun addBooleanValue(
        initialValue: Boolean,
        labelText: String,
        valueUpdater: (Boolean) -> Unit,
        padding: Insets
    ): HBox = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        children.addAll(CheckBox().apply {
            alignment = Pos.CENTER
            styleClass.add("check-box")
            style =
                "-fx-text-fill: ${ColorPalette.ACCENT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-mark-color: ${ColorPalette.ACCENT_COLOR};"
            isSelected = initialValue


            setOnMouseEntered { cursor = Cursor.HAND }
            setOnMouseExited { cursor = Cursor.DEFAULT }

            selectedProperty().addListener { _, _, newValue ->
                valueUpdater(newValue)
            }
        }, CommonElement.createLabel(labelText))
    }
}
