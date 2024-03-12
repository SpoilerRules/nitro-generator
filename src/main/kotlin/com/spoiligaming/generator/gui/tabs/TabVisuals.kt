package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.element.*
import com.spoiligaming.logging.Logger
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabVisuals : ITab {
    private val visualsPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Visuals tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            visualsPane.isVisible = newValue == 4
        }
    }

    override fun getContent(): GridPane =
        visualsPane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            if (Logger.showDebug) {
                CommonElement().run {
                    createContentField(
                        this@apply, "Theme", 225.0, ElementColorPicker.addColorPickerValue(
                            BaseConfigurationFactory.getInstance().themeSettings.accentColor,
                            "Accent Color",
                            { newValue ->
                                BaseConfigurationFactory.updateValue {
                                    themeSettings.accentColor = newValue
                                }
                            },
                            Insets(10.0, 0.0, 0.0, 10.0)
                        ),
                    )
                }
            } else {
                add(CommonElement.createLabel("Not stable yet.", "15"), 0, 0)
            }
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        visualsPane.isVisible = (TabContainer.currentTab == 4 && visibility == ITab.TabVisibility.VISIBLE)
    }
}