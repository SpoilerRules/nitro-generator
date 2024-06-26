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
        tooltipText: String? = null,
        valueUpdater: (Boolean) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0),
    ): HBox =
        HBox().apply {
            alignment = Pos.TOP_LEFT
            spacing = 5.0
            this.padding = padding

            children.addAll(
                CheckBox().apply {
                    alignment = Pos.CENTER
                    style =
                        "-fx-text-fill: ${ColorPalette.accentColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-mark-color: ${ColorPalette.accentColor};"
                    isSelected = initialValue

                    setOnMouseEntered { cursor = Cursor.HAND }
                    setOnMouseExited { cursor = Cursor.DEFAULT }

                    selectedProperty().addListener { _, _, newValue ->
                        valueUpdater(newValue)
                    }
                },
                CommonElement.createLabel(labelText),
            )
            tooltipText?.let {
                children.add(CommonElement.createTooltip(it))
            }
        }
}
