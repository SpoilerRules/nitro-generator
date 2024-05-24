package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.autoretention.AutoRetentionHandler
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TooltipKeyAccessor
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementText
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy

class TabFortification : AbstractTab(2, "Fortification") {
    override fun getContent(): ScrollPane =
        ScrollPane().apply {
            maxWidth = 420.0
            minWidth = 420.0
            fitToWidthProperty().set(true)
            hbarPolicy = ScrollBarPolicy.NEVER
            vbarPolicy = ScrollBarPolicy.NEVER
            padding = Insets(-0.5, 0.0, 0.0, 0.0)
            style = "-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;"

            content =
                pane.apply fortificationPaneApply@{
                    alignment = Pos.TOP_CENTER
                    hgap = 20.0
                    vgap = 7.5
                    CommonElement().run {
                        createContentField(
                            this@fortificationPaneApply, "Discord Webhook",
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().discordWebhookAlertSettings.alertWebhook,
                                "Alert Webhook",
                                TooltipKeyAccessor.getValue("alert.webhook"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        discordWebhookAlertSettings.alertWebhook = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().discordWebhookAlertSettings.discordWebhookURL,
                                "Webhook Link",
                                inputRegex = Regex("^[a-zA-Z0-9:/._\\-]+\$"),
                                tooltipText = null,
                                valueUpdater = { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        discordWebhookAlertSettings.discordWebhookURL = newValue
                                    }
                                },
                                padding = Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                        )
                        createContentField(
                            this@fortificationPaneApply,
                            "Auto Claim",
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().autoClaimSettings.enabled,
                                "Enabled",
                                TooltipKeyAccessor.getValue("auto.claim.description"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        autoClaimSettings.enabled = newValue
                                    }
                                },
                            ),
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().autoClaimSettings.retryTillSuccess,
                                "Retry",
                                TooltipKeyAccessor.getValue("auto.claim.retry"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        autoClaimSettings.retryTillSuccess = newValue
                                    }
                                },
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().autoClaimSettings.accountToken,
                                "Discord Account Token",
                                inputRegex = Regex("^[a-zA-Z0-9.]*\$"),
                                tooltipText = null,
                                valueUpdater = { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        autoClaimSettings.accountToken = newValue
                                    }
                                },
                            ),
                        )
                        createContentField(
                            this@fortificationPaneApply,
                            "Auto Retention",
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().autoRetentionSettings.enabled,
                                "Enabled",
                                TooltipKeyAccessor.getValue("auto.retention.description"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        autoRetentionSettings.enabled = newValue
                                    }
                                    if (newValue) {
                                        AutoRetentionHandler.initialize(
                                            if (!BaseConfigurationFactory.getInstance().autoRetentionSettings.informationalFile) {
                                                AutoRetentionHandler.InitializationType.BASIC
                                            } else {
                                                AutoRetentionHandler.InitializationType.INFORMATIONAL_YAML
                                            },
                                        )
                                    }
                                },
                            ),
                            ElementText.addTextValue(
                                if (BaseConfigurationFactory.getInstance().autoRetentionSettings.contentSeparator == "\n") {
                                    "\\n"
                                } else {
                                    BaseConfigurationFactory.getInstance().autoRetentionSettings.contentSeparator
                                },
                                "Content Separator",
                                tooltipText = TooltipKeyAccessor.getValue("auto.retention.contentseperator"),
                                valueUpdater = { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        autoRetentionSettings.contentSeparator = newValue
                                    }
                                },
                            ),
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().autoRetentionSettings.informationalFile,
                                "Additional Information File",
                                TooltipKeyAccessor.getValue("auto.retention.informationalfile"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        autoRetentionSettings.informationalFile = newValue
                                    }
                                    if (BaseConfigurationFactory.getInstance().autoRetentionSettings.enabled) {
                                        AutoRetentionHandler.initialize(if (newValue) AutoRetentionHandler.InitializationType.BASIC else AutoRetentionHandler.InitializationType.INFORMATIONAL_YAML)
                                    }
                                },
                            ),
                        )
                    }
                }
        }
}
