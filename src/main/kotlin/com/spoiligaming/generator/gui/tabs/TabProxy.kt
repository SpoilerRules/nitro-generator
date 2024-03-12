package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.element.*
import com.spoiligaming.logging.Logger
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy
import javafx.scene.layout.GridPane

class TabProxy : ITab {
    private val proxyPane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Proxy tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            proxyPane.isVisible = newValue == 1
        }
    }

    override fun getContent(): ScrollPane = ScrollPane().apply {
        maxWidth = 420.0
        minWidth = 420.0
        fitToWidthProperty().set(true)
        hbarPolicy = ScrollBarPolicy.NEVER
        vbarPolicy = ScrollBarPolicy.NEVER
        padding = Insets(-0.5, 0.0, 0.0, 0.0)
        style = "-fx-background-color: transparent; -fx-background: transparent; -fx-border-width: 0;"

        content = proxyPane.apply proxyPaneApply@ {
            alignment = Pos.TOP_CENTER
            hgap = 20.0
            vgap = 7.5
            CommonElement().run {
                createContentField(
                    this@proxyPaneApply,
                    "Custom Proxy",
                    225.0,
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().customProxy.enabled,
                        "Enabled",
                        null,
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
                    ),
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.host,
                        "Host",
                        "Only available in Static mode.",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.host = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.port,
                        "Port",
                        "Only available in Static mode.",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.port = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                )
                createContentField(
                    this@proxyPaneApply,
                    "Additional Authentication",
                    150.0, ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().customProxy.isAuthenticationRequired,
                        "Enabled",
                        "Only available in Static mode.",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.isAuthenticationRequired = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.username,
                        "Username",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.username = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.password,
                        "Password",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.password = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    )
                )
                createContentField(
                    this@proxyPaneApply,
                    "Mode Specific",
                    150.0,
                    ElementText.addTextValue(
                        if (BaseConfigurationFactory.getInstance().customProxy.rawContentSeparator == "\n") "\\n" else BaseConfigurationFactory.getInstance().customProxy.rawContentSeparator,
                        "Content Separator",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.rawContentSeparator = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementFilePicker.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.proxyFileName,
                        "Proxy File",
                        "This allows you to select a file containing a collection of proxies. The proxies should be in the 'host:port' format.",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.proxyFileName = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.rawContentLinks,
                        "Raw Content Link(s)",
                        "Links are separated by a comma. Only links pointing to raw content sources are supported.",
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.rawContentLinks = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    )
                )
            }
        }
    }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        proxyPane.isVisible = (TabContainer.currentTab == 1 && visibility == ITab.TabVisibility.VISIBLE)
    }
}