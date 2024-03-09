package com.spoiligaming.generator.gui.controller

import com.spoiligaming.logging.Logger
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.StackPane

class TabController {
    private lateinit var tabPane: TabPane

    fun handleTabs() {
        tabPane.tabs.forEach { tab ->
            when (tab.text) {
                "General" -> {
                    tab.content = createGeneralTabContent()
                    Logger.printSuccess("Current tab is general")
                }
                "Proxy" -> tab.content = createProxyTabContent()
                // Add similar cases for other tabs
            }
        }
    }

    private fun createGeneralTabContent(): StackPane {
        Logger.printSuccess("cre")
        return StackPane().apply {
            setMaxSize(600.0, 400.0)
            setMinSize(600.0, 400.0)
            alignment = Pos.CENTER
            children.add(Label("This is General Tab"))
        }
    }

    private fun createProxyTabContent(): StackPane {
        return StackPane().apply {
            children.add(Label("This is Proxy Tab"))
        }
    }

    // Add similar functions for other tab contents if needed

    fun setTabPane(tabPane: TabPane) {
        this.tabPane = tabPane
    }
}