package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementColorPicker
import com.spoiligaming.generator.gui.element.ElementNote
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabVisuals : AbstractTab(4, "Advanced") {
    override fun getContent(): GridPane =
        pane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@apply,
                    "User Interface",
                    245.0,
                    ElementNote.addNote("Will take effect after restarting the software.", "13"),
                    ElementColorPicker.addColorPickerValue(
                        BaseConfigurationFactory.getInstance().themeSettings.accentColor,
                        "Accent color",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                themeSettings.accentColor = newValue
                            }
                        },
                    ),
                    ElementColorPicker.addColorPickerValue(
                        BaseConfigurationFactory.getInstance().themeSettings.menuColor,
                        "Menu color",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                themeSettings.menuColor = newValue
                            }
                        },
                    ),
                    ElementColorPicker.addColorPickerValue(
                        BaseConfigurationFactory.getInstance().themeSettings.controlColor,
                        "Control color",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                themeSettings.controlColor = newValue
                            }
                        },
                    ),
                    ElementColorPicker.addColorPickerValue(
                        BaseConfigurationFactory.getInstance().themeSettings.secondaryColor,
                        "Secondary color",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                themeSettings.secondaryColor = newValue
                            }
                        },
                    ),
                    ElementColorPicker.addColorPickerValue(
                        BaseConfigurationFactory.getInstance().themeSettings.textColor,
                        "Text color",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                themeSettings.textColor = newValue
                            }
                        },
                    ),
                )
            }
        }
}
