package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.gui.ResourceHandler
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.logging.Logger
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.scene.paint.Color

class TabAdvanced : ITab {
    private val advancedPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Advanced tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            advancedPane.isVisible = newValue == 4
        }
    }

    override fun getContent(): GridPane =
        advancedPane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            add(VBox().apply {
                background = Background(BackgroundFill(Color.web("#414141"), CornerRadii(16.0, false), null))
                setMaxSize(410.0, 200.0)
                setMinSize(410.0, 200.0)
                GridPane.setMargin(this, Insets(0.0, 0.0, 0.0, -2.4))
                val hbox = HBox().apply {
                    alignment = Pos.CENTER
                    background = Background(BackgroundFill(Color.web("#282828"), CornerRadii(16.0, 16.0, 0.0, 0.0, false), null))
                    setMaxSize(410.0, 35.0)
                    setMinSize(410.0, 35.0)

                    children.add(Label("Multi Threading").apply {
                        style = "-fx-text-fill: #E85D9B; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14;"
                    })
                }

                children.add(hbox)
            }, 0, 0)
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        advancedPane.isVisible = (TabContainer.currentTab == 4 && visibility == ITab.TabVisibility.VISIBLE)
    }
}