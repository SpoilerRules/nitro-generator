package com.spoiligaming.generator.gui

import javafx.scene.text.Font

object ResourceHandler {
    private fun loadFont(fontPath: String, weight: Double): Font =
        Font.loadFont(this::class.java.getResourceAsStream(fontPath), weight)

    val comfortaaLight by lazy { loadFont("/fonts/Comfortaa-Light.ttf", 300.0) }
    val comfortaaRegular by lazy { loadFont("/fonts/Comfortaa-Regular.ttf", 400.0) }
    val comfortaaMedium by lazy { loadFont("/fonts/Comfortaa-Medium.ttf", 500.0) }
    val comfortaaSemiBold by lazy { loadFont("/fonts/Comfortaa-SemiBold.ttf", 600.0) }
    val comfortaaBold by lazy { loadFont("/fonts/Comfortaa-Bold.ttf", 700.0) }
}
