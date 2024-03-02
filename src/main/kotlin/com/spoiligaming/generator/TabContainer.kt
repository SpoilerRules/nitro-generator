package com.spoiligaming.generator

import javafx.geometry.Side
import javafx.scene.control.TabPane

class TabContainer : TabPane() {
    init {
        stylesheets.add(javaClass.getResource("/test.css")!!.toExternalForm())
        style = "-fx-background-color: #1e1e1e; -fx-background-radius: 24; -fx-tab-min-height: 128px; -fx-tab-max-height: 128px; -fx-tab-min-width: 16px; -fx-tab-max-width: 16px; -fx-tab-background: #1e1e1e;"
        tabClosingPolicy = TabClosingPolicy.UNAVAILABLE
        side = Side.LEFT
    }
}