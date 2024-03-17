package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.NitroValidatorAdvancedMt
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
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
                    this@apply, "Multi Threading (experimental)", 150.0, ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().multithreading.enabled,
                        "Enabled",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                multithreading.enabled = newValue
                            }
                            if (newValue) {
                                NitroValidatorAdvancedMt.isNextProxyAvailable.set(true)
                                Logger.printWarning("When multi-threading is enabled, only 'Static' proxy mode or no proxy can be used. Some features may be unavailable due to stability issues.")
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().multithreading.threadLimit,
                        "Thread Amount",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                multithreading.threadLimit = newValue
                            }
                        }
                    ),
                    ElementValue.addUnitValue(
                        BaseConfigurationFactory.getInstance().multithreading.threadLaunchDelay,
                        "Start Delay (ms)",
                        "Specifies the delay between the initiation of threads.\nFor instance, the second thread will commence after the specified milliseconds following the start of the first thread.",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                multithreading.threadLaunchDelay = newValue
                            }
                        }
                    )
                )
            }
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        advancedPane.isVisible = (TabContainer.currentTab == 3 && visibility == ITab.TabVisibility.VISIBLE)
    }
}