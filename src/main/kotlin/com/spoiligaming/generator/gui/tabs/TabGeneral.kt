package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementText
import com.spoiligaming.generator.gui.element.ElementValue
import com.spoiligaming.logging.Logger
import javafx.application.Platform
import javafx.beans.property.SimpleLongProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import java.text.DecimalFormat

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
            add(VBox().apply {
                background = Background(
                    BackgroundFill(
                        Color.web(ColorPalette.SECONDARY_COLOR),
                        CornerRadii(16.0, false),
                        null
                    )
                )
                setMaxSize(410.0, 225.0)
                setMinSize(410.0, 225.0)
                GridPane.setMargin(this, Insets(0.0, 0.0, 0.0, -2.4))

                val hbox = HBox().apply {
                    alignment = Pos.CENTER
                    background = Background(
                        BackgroundFill(
                            Color.web(ColorPalette.PRIMARY_COLOR),
                            CornerRadii(16.0, 16.0, 0.0, 0.0, false),
                            null
                        )
                    )
                    setMaxSize(410.0, 35.0)
                    setMinSize(410.0, 35.0)
                    children.add(Label("General").apply {
                        style = "-fx-text-fill: ${ColorPalette.ACCENT_COLOR}; " +
                                "-fx-font-family: '${ResourceHandler.comfortaaBold.family}'; " +
                                "-fx-font-size: 14;"
                    })
                }

                children.addAll(
                    hbox,
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.logGenerationInfo,
                        labelText = "Log Generation Info",
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
                    ),
                )
            }, 0, 0)
            add(VBox().apply {
                background = Background(
                    BackgroundFill(
                        Color.web(ColorPalette.SECONDARY_COLOR),
                        CornerRadii(16.0, false),
                        null
                    )
                )
                setMaxSize(410.0, 120.0)
                setMinSize(410.0, 120.0)
                GridPane.setMargin(this, Insets(0.0, 0.0, 0.0, -2.4))

                children.addAll(
                    HBox().apply {
                        alignment = Pos.CENTER
                        background = Background(
                            BackgroundFill(
                                Color.web(ColorPalette.PRIMARY_COLOR),
                                CornerRadii(16.0, 16.0, 0.0, 0.0, false),
                                null
                            )
                        )
                        setMaxSize(410.0, 35.0)
                        setMinSize(410.0, 35.0)
                        children.add(Label("Discord Webhook").apply {
                            style = "-fx-text-fill: ${ColorPalette.ACCENT_COLOR}; " +
                                    "-fx-font-family: '${ResourceHandler.comfortaaBold.family}'; " +
                                    "-fx-font-size: 14;"
                        })
                    },
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().generalSettings.alertWebhook,
                        "Alert Webhook",
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
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                generalSettings.discordWebhookURL = newValue
                            }
                        })
                )
            }, 0, 1)
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        generalPane.isVisible = (TabContainer.currentTab == 0 && visibility == ITab.TabVisibility.VISIBLE)
    }
}
