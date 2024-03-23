package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.logging.Logger
import javafx.scene.Node
import javafx.scene.layout.GridPane

abstract class AbstractTab(private val tabIndex: Int, tabName: String) {
    enum class TabVisibility {
        VISIBLE,

        @Suppress("unused")
        INVISIBLE,
    }

    protected val pane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for $tabName tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            pane.isVisible = newValue == tabIndex
        }
    }

    open fun getContent(): Node = pane

    @Suppress("unused")
    fun setVisibility(visibility: TabVisibility) {
        pane.isVisible = (visibility == TabVisibility.VISIBLE)
    }
}
