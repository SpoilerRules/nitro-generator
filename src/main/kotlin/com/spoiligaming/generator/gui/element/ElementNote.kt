package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.HBox

object ElementNote {
    fun addNote(noteText: String, textSize: String, padding: Insets = Insets(10.0, 0.0, 0.0, 10.0)): HBox =
        HBox().apply {
            alignment = Pos.TOP_LEFT
            spacing = 5.0
            this.padding = padding

            children.add(Label().apply {
                style =
                    "-fx-text-fill: ${ColorPalette.ACCENT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: $textSize; -fx-padding: 0;"
                text = noteText
            })
        }
}