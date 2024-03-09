package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.scene.control.Label

object CommonElement {
    fun createLabel(text: String) = Label(text).apply {
        setMaxSize(150.0, 25.0)
        setMinSize(150.0, 25.0)
        style = "-fx-background-color: transparent; -fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 13;"
    }
}