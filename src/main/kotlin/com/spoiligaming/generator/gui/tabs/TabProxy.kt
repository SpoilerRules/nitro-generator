package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.NitroValidatorConcurrent
import com.spoiligaming.generator.ProxyHandler
import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.gui.TooltipKeyAccessor
import com.spoiligaming.generator.gui.element.CommonElement
import com.spoiligaming.generator.gui.element.ElementBoolean
import com.spoiligaming.generator.gui.element.ElementFilePicker
import com.spoiligaming.generator.gui.element.ElementList
import com.spoiligaming.generator.gui.element.ElementNote
import com.spoiligaming.generator.gui.element.ElementText
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.control.ScrollPane.ScrollBarPolicy

class TabProxy : AbstractTab(1, "Proxy") {
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
                pane.apply proxyPaneApply@{
                    alignment = Pos.TOP_CENTER
                    hgap = 20.0
                    vgap = 7.5
                    CommonElement().run {
                        createContentField(
                            this@proxyPaneApply,
                            "Custom Proxy",
                            257.0,
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().proxySettings.enabled,
                                "Enabled",
                                null,
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.enabled = newValue
                                    }
                                    // explicitly load proxies only when multi threading is enabled. the non-mt-supported simple validators will automatically load proxies on validation.
                                    if (newValue &&
                                        BaseConfigurationFactory.getInstance().multithreadingSettings.enabled &&
                                        BaseConfigurationFactory.getInstance().proxySettings.mode in 2..3
                                    ) {
                                        ProxyHandler.loadProxies()
                                    }
                                    if (newValue && BaseConfigurationFactory.getInstance().proxySettings.mode == 1) ProxyHandler.unloadProxies()
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().proxySettings.recursiveUsaqe,
                                "Recursive Usage",
                                TooltipKeyAccessor.getValue("recursive.usage"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.recursiveUsaqe = newValue
                                    }
                                    if (newValue) NitroValidatorConcurrent.isNextProxyAvailable.set(true)
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementList.addListValue(
                                FXCollections.observableArrayList("Static", "One File", "Online API"),
                                when (BaseConfigurationFactory.getInstance().proxySettings.mode) {
                                    1 -> "Static"
                                    2 -> "One File"
                                    3 -> "Online API"
                                    else -> "Static"
                                },
                                { newValue: String ->
                                    val previousMode = BaseConfigurationFactory.getInstance().proxySettings.mode
                                    val mode =
                                        when (newValue) {
                                            "Static" -> 1
                                            "One File" -> 2
                                            "Online API" -> 3
                                            else -> 1
                                        }

                                    BaseConfigurationFactory.getInstance().proxySettings.mode = mode
                                    if (previousMode != mode &&
                                        newValue != "Static" &&
                                        BaseConfigurationFactory.getInstance().multithreadingSettings.enabled &&
                                        BaseConfigurationFactory.getInstance().proxySettings.enabled
                                    ) {
                                        ProxyHandler.loadProxies()
                                    }
                                    // free resources when the new mode is static
                                    if (previousMode in 2..3 && mode == 1) {
                                        ProxyHandler.unloadProxies()
                                    }
                                },
                                "Mode",
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementList.addListValue(
                                FXCollections.observableArrayList("HTTP", "SOCKS"),
                                when (BaseConfigurationFactory.getInstance().proxySettings.protocol) {
                                    1 -> "HTTP"
                                    2 -> "SOCKS"
                                    else -> "HTTP"
                                },
                                { newValue: String ->
                                    when (newValue) {
                                        "HTTP" -> BaseConfigurationFactory.getInstance().proxySettings.protocol = 1
                                        "SOCKS" -> BaseConfigurationFactory.getInstance().proxySettings.protocol = 2
                                        else -> BaseConfigurationFactory.getInstance().proxySettings.protocol = 1
                                    }
                                },
                                "Protocol",
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().proxySettings.host,
                                "Host",
                                TooltipKeyAccessor.getValue("available.static.mode"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.host = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().proxySettings.port,
                                "Port",
                                TooltipKeyAccessor.getValue("available.static.mode"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.port = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                        )
                        createContentField(
                            this@proxyPaneApply,
                            "Additional Authentication",
                            150.0,
                            ElementBoolean.addBooleanValue(
                                BaseConfigurationFactory.getInstance().proxySettings.isAuthenticationRequired,
                                "Enabled",
                                TooltipKeyAccessor.getValue("available.static.mode"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.isAuthenticationRequired = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().proxySettings.username,
                                "Username",
                                null,
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.username = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().proxySettings.password,
                                "Password",
                                null,
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.password = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                        )
                        createContentField(
                            this@proxyPaneApply,
                            "Mode Specific",
                            195.0,
                            ElementFilePicker.addTextValue(
                                this@proxyPaneApply,
                                BaseConfigurationFactory.getInstance().proxySettings.proxyFilePath,
                                TooltipKeyAccessor.getValue("proxy.file.path"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.proxyFilePath = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementNote.addNote("Settings below will not take effect dynamically.", "13"),
                            ElementText.addTextValue(
                                if (BaseConfigurationFactory.getInstance().proxySettings.rawContentSeparator == "\n") "\\n" else BaseConfigurationFactory.getInstance().proxySettings.rawContentSeparator,
                                "Content Separator",
                                null,
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.rawContentSeparator = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                            ElementText.addTextValue(
                                BaseConfigurationFactory.getInstance().proxySettings.rawContentLinks,
                                "Raw Content Link(s)",
                                TooltipKeyAccessor.getValue("raw.content.description"),
                                { newValue ->
                                    BaseConfigurationFactory.updateValue {
                                        proxySettings.rawContentLinks = newValue
                                    }
                                },
                                Insets(10.0, 0.0, 0.0, 10.0),
                            ),
                        )
                    }
                }
        }
}
