package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.NitroValidatorConcurrent
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TooltipKeyAccessor
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementValue
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabAdvanced : AbstractTab(3, "Advanced") {
    override fun getContent(): GridPane =
        pane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@apply,
                    "Multi Threading",
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
            }
        }
}
