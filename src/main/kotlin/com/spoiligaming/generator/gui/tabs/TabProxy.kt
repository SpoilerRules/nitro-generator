package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.logging.Logger
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.GridPane

class TabProxy : ITab {
    private val proxyPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Proxy tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            proxyPane.isVisible = newValue == 1
        }
    }

    override fun getContent(): GridPane =
        proxyPane.apply {
            alignment = Pos.CENTER
            add(Label("This is Proxy tab.").apply { style = "-fx-text-fill: black;" }, 0, 0)
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        proxyPane.isVisible = (TabContainer.currentTab == 1 && visibility == ITab.TabVisibility.VISIBLE)
    }
}