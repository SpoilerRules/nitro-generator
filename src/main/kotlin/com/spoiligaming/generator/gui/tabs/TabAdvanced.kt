package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.NitroValidatorConcurrent
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.TooltipKeyAccessor
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementText
import com.spoiligaming.generator.gui.element.ElementValue
import com.spoiligaming.logging.Logger
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabAdvanced : ITab {
    private val advancedPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Advanced tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            advancedPane.isVisible = newValue == 3
        }
    }

    override fun getContent(): GridPane =
        advancedPane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@apply,
                    "Multi Threading",
                    150.0,
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().multithreadingSettings.enabled,
                        "Enabled",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                multithreadingSettings.enabled = newValue
                            }
                            if (newValue) {
                                NitroValidatorConcurrent.isNextProxyAvailable.set(true)
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0),
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().multithreadingSettings.threadLimit,
                        "Thread Amount",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                multithreadingSettings.threadLimit = newValue
                            }
                        },
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().multithreadingSettings.threadLaunchDelay,
                        "Start Delay (ms)",
                        TooltipKeyAccessor.getValue("thread.start.delay"),
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                multithreadingSettings.threadLaunchDelay = newValue
                            }
                        },
                    ),
                )
                createContentField(
                    this@apply,
                    "Auto Claim",
                    152.0,
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
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                autoClaimSettings.accountToken = newValue
                            }
                        },
                    ),
                )
            }
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        advancedPane.isVisible = (TabContainer.currentTab == 3 && visibility == ITab.TabVisibility.VISIBLE)
    }
}
