package com.spoiligaming.generator.gui

import com.spoiligaming.generator.gui.tabs.TabAdvanced
import com.spoiligaming.generator.gui.tabs.TabConsole
import com.spoiligaming.generator.gui.tabs.TabGeneral
import com.spoiligaming.generator.gui.tabs.TabProxy
import com.spoiligaming.logging.Logger
import javafx.geometry.Pos
import javafx.scene.layout.BorderPane
import javafx.scene.layout.StackPane

object TabHandler {
    lateinit var tabContentPane: BorderPane

    fun allocatePane() {
        tabContentPane = BorderPane().also { Logger.printDebug("Allocating BorderPane for tab content.") }
        tabContentPane.apply {
            this.translateY = -25.0
            setMinSize(410.0, 350.0)
            setMaxSize(410.0, 350.0)
            style = "-fx-background-color: transparent; -fx-background-radius: 16;"

            val generalContent = TabGeneral().getContent()
            val proxyContent = TabProxy().getContent()
            val advancedContent = TabAdvanced().getContent()
            //     val visualsContent = TabVisuals().getContent()
            val consoleContent = TabConsole().getContent()

            val stackPane = StackPane(generalContent, proxyContent, advancedContent, consoleContent)
            stackPane.alignment = Pos.CENTER

            center = stackPane

            TabContainer.currentTabProperty().addListener { _, _, newValue ->
                stackPane.children.forEachIndexed { index, node ->
                    node.isVisible = index == newValue
                }
            }

            stackPane.children.forEachIndexed { index, node ->
                node.isVisible = index == TabContainer.currentTab
            }
        }
    }
}
