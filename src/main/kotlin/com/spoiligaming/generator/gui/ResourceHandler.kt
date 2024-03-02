package com.spoiligaming.generator.gui

import javafx.scene.text.Font

object ResourceHandler {
    private fun loadFont(fontPath: String): Font =
        Font.loadFont(this::class.java.getResourceAsStream(fontPath), 100.0)

    val comfortaaFont by lazy { loadFont("/fonts/Comfortaa-Bold.ttf") }
    val comfortaaRegular by lazy { loadFont("/fonts/Comfortaa-Regular.ttf") }
    val comfortaaLight by lazy { loadFont("/fonts/Comfortaa-Light.ttf") }
    val comfortaaMedium by lazy { loadFont("/fonts/Comfortaa-Medium.ttf") }
    val comfortaaSemiBold by lazy { loadFont("/fonts/Comfortaa-SemiBold.ttf") }
}