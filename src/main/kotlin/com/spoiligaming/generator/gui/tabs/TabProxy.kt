package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.NitroValidatorAdvancedMt
import com.spoiligaming.generator.ProxyHandler
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.generator.gui.TooltipKeyAccessor
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
                    257.0,
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().customProxy.enabled,
                        "Enabled",
                        null,
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.enabled = newValue
                            }
                            // explicitly load proxies only when multi threading is enabled. the non-mt-supported simple validators will automatically load proxies on validation.
                            if (newValue && BaseConfigurationFactory.getInstance().multithreading.enabled && BaseConfigurationFactory.getInstance().customProxy.mode in 2..3) ProxyHandler.loadProxies()
                            if (newValue && BaseConfigurationFactory.getInstance().customProxy.mode == 1) ProxyHandler.unloadProxies()
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementBoolean.addBooleanValue(
                        BaseConfigurationFactory.getInstance().customProxy.recursiveUsaqe,
                        "Recursive Usage",
                        TooltipKeyAccessor.getValue("recursive.usage"),
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.recursiveUsaqe = newValue
                            }
                            if (newValue) NitroValidatorAdvancedMt.isNextProxyAvailable.set(true)
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
                            val previousMode = BaseConfigurationFactory.getInstance().customProxy.mode
                            val mode = when (newValue) {
                                "Static" -> 1
                                "One File" -> 2
                                "Online API" -> 3
                                else -> 1
                            }

                            BaseConfigurationFactory.getInstance().customProxy.mode = mode
                            if (previousMode != mode && mode != 1 && BaseConfigurationFactory.getInstance().multithreading.enabled && BaseConfigurationFactory.getInstance().customProxy.enabled) {
                                ProxyHandler.loadProxies()
                            }
                            // free resources when the new mode is static
                            if (previousMode in 2..3 && mode == 1) {
                                ProxyHandler.unloadProxies()
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
                        TooltipKeyAccessor.getValue("available.static.mode"),
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
                        TooltipKeyAccessor.getValue("available.static.mode"),
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
                        TooltipKeyAccessor.getValue("available.static.mode"),
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
                    195.0,
                    ElementFilePicker.addTextValue(
                        this@proxyPaneApply,
                        BaseConfigurationFactory.getInstance().customProxy.proxyFilePath,
                        TooltipKeyAccessor.getValue("proxy.file.path"),
                        { newValue ->
                            BaseConfigurationFactory.updateValue {
                                customProxy.proxyFilePath = newValue
                            }
                        },
                        Insets(10.0, 0.0, 0.0, 10.0)
                    ),
                    ElementNote.addNote("Settings below will not take effect dynamically.", "13"),
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
                    ElementText.addTextValue(
                        BaseConfigurationFactory.getInstance().customProxy.rawContentLinks,
                        "Raw Content Link(s)",
                        TooltipKeyAccessor.getValue("raw.content.description"),
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