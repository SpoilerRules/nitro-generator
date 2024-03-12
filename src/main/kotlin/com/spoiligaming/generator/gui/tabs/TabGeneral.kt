package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementText
import com.spoiligaming.generator.gui.element.ElementValue
import com.spoiligaming.logging.Logger
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabGeneral : ITab {
    private val generalPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for General tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            generalPane.isVisible = newValue == 0
        }
    }

    override fun getContent(): GridPane =
        generalPane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@apply, "General", 225.0, ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.logGenerationInfo,
                        "Log Generation Info",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.logGenerationInfo = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.validateNitroCode,
                        "Validate Nitro Code",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.validateNitroCode = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
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
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().generalSettings.retryDelay,
                        "Retry Delay (s)",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.retryDelay = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().generalSettings.generationDelay,
                        "Generation Delay (ms)",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.generationDelay = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    )
                )

                createContentField(
                    this@apply, "Discord Webhook", 120.0,
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.alertWebhook,
                        "Alert Webhook",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.alertWebhook = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().generalSettings.discordWebhookURL,
                        "Webhook Link",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.discordWebhookURL = newValue
                            }
                        }, padding = Insets(10.0, 0.0, 0.0, 10.0))
                )
            }
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        generalPane.isVisible = (TabContainer.currentTab == 0 && visibility == ITab.TabVisibility.VISIBLE)
    }
}
