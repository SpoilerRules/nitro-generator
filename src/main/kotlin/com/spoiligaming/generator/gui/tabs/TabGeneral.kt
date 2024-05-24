package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TooltipKeyAccessor
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabGeneral : AbstractTab(0, "General") {
    override fun getContent(): GridPane =
        pane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@apply,
                    "General",
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.logGenerationInfo,
                        "Log Generation Info",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.logGenerationInfo = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.validateNitroCode,
                        "Validate Nitro Code",
                        TooltipKeyAccessor.getValue("validate.nitro.code"),
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.validateNitroCode = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.generatePromotionalGiftCode,
                        "Promotional Nitro",
                        TooltipKeyAccessor.getValue("promotional.nitro"),
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.generatePromotionalGiftCode = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.retryTillValid,
                        "Retry",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.retryTillValid = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().generalSettings.retryDelay,
                        "Retry Delay (s)",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.retryDelay = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().generalSettings.generationDelay,
                        "Generation Delay (ms)",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.generationDelay = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                )
            }
        }
}
