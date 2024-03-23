package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TooltipKeyAccessor
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementText
import com.spoiligaming.generator.gui.element.ElementValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane

class TabGeneral : AbstractTab(0, "General") {
    override fun getContent(): ScrollPane =
        ScrollPane().apply {
            maxWidth = 420.0
            minWidth = 420.0
            fitToWidthProperty().set(true)
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            padding = Insets(-0.5, 0.0, 0.0, 0.0)
            style = "-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;"

            content =
                pane.apply generalPaneApply@{
                    alignment = Pos.TOP_CENTER
                    hgap = 20.0
                    vgap = 7.5
                    CommonElement().run {
                        createContentField(
                            this@generalPaneApply, "General", 260.0,
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

                        createContentField(
                            this@generalPaneApply, "Discord Webhook", 115.0,
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().generalSettings.alertWebhook,
                                "Alert Webhook",
                                TooltipKeyAccessor.getValue("alert.webhook"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        generalSettings.alertWebhook = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().generalSettings.discordWebhookURL,
                                "Webhook Link",
                                null,
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        generalSettings.discordWebhookURL = newValue
                                    }
                                }, padding = Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                        )
                    }
                }
        }
}
