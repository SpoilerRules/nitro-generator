package com.spoiligaming.generator.gui.tabs

import javafx.scene.layout.GridPane

sealed interface ITab {
    enum class TabVisibility {
        VISIBLE,
        INVISIBLE
    }

    fun getContent(): GridPane

    fun setVisibility(visibility: TabVisibility)
}