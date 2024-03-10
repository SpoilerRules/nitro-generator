package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.configuration.CustomProxy
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementList
import com.spoiligaming.logging.Logger
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.layout.GridPane

class TabProxy : ITab {
    private val proxyPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Proxy tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            proxyPane.isVisible = newValue == 1
        }
    }

    override fun getContent(): GridPane =
        proxyPane.apply {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@apply,
                    "Custom Proxy",
                    225.0,
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().customProxy.enabled,
                        "Enabled",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.enabled = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementList.addListValue(
                        FXCollections.observableArrayList("Static", "One File", "Online API"),
                        when (BaseConfigurationFactory.getInstance().customProxy.mode) {
                            1 -> "Static"
                            2 -> "One File"
                            3 -> "Online API"
                            else -> "Static"
                        },
                        { newValue: String ->
                            when (newValue) {
                                "Static" -> BaseConfigurationFactory.getInstance().customProxy.mode = 1
                                "One File" -> BaseConfigurationFactory.getInstance().customProxy.mode = 2
                                "Online API" -> BaseConfigurationFactory.getInstance().customProxy.mode = 3
                                else -> BaseConfigurationFactory.getInstance().customProxy.mode = 1
                            }
                        },
                        "Mode",
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementList.addListValue(
                        FXCollections.observableArrayList("HTTP", "SOCKS"),
                        when (BaseConfigurationFactory.getInstance().customProxy.protocol) {
                            1 -> "HTTP"
                            2 -> "SOCKS"
                            else -> "HTTP"
                        },
                        { newValue: String ->
                            when (newValue) {
                                "HTTP" -> BaseConfigurationFactory.getInstance().customProxy.protocol = 1
                                "SOCKS" -> BaseConfigurationFactory.getInstance().customProxy.protocol = 2
                                else -> BaseConfigurationFactory.getInstance().customProxy.protocol = 1
                            }
                        },
                        "Protocol",
                        Insets(10.0, 0.0, 0.0, 10.0)
                    )
                )
            }
        }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        proxyPane.isVisible = (TabContainer.currentTab == 1 && visibility == ITab.TabVisibility.VISIBLE)
    }
}