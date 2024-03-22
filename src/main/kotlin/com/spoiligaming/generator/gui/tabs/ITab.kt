package com.spoiligaming.generator.gui.tabs

import javafx.scene.Node

sealed interface ITab {
    enum class TabVisibility {
        VISIBLE,
        INVISIBLE
    }

    fun getContent(): Node

    fun setVisibility(visibility: TabVisibility)
}
