package com.spoiligaming.generator.gui

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.configuration.CustomProxy
import com.spoiligaming.generator.gui.tabs.*
import com.spoiligaming.logging.Logger
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.*
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.BorderPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.FileChooser
import java.io.File

class TabHandler(private val tabPane: TabPane) {
    private val customProxy = BaseConfigurationFactory.getInstance().customProxy

    private val objectStyle = """
        -fx-text-fill: #FFFFFF; 
        -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; 
        -fx-font-size: 13; 
        -fx-background-radius: 12px;
        -fx-background-color: #4C4C4C;
        -fx-control-inner-background: #4C4C4C;
        -fx-focus-color: #4C4C4C;
        -fx-faint-focus-color: transparent;
        -fx-selection-bar-text: white;
    """

    private val tooltipStyle = """
        -fx-text-fill: #E85D9B; 
        -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; 
        -fx-font-size: 13; 
        -fx-background-radius: 12px;
        -fx-background-color: #414141;
        -fx-control-inner-background: #414141;
        -fx-focus-color: #414141;
        -fx-faint-focus-color: transparent;
        -fx-selection-bar-text: white;
    """

    companion object {
        lateinit var tabContentPane: BorderPane

        fun allocatePane() {
            tabContentPane = BorderPane().also { Logger.printDebug("Allocating BorderPane for tab content.") }
            tabContentPane.apply {
                this.translateY = -25.0
                setMinSize(410.0, 350.0)
                setMaxSize(410.0, 350.0)
                style = "-fx-background-color: transparent; -fx-background-radius: 16;"

                val generalContent = TabGeneral().getContent()
                val proxyContent = TabProxy().getContent()
                val advancedContent = TabAdvanced().getContent()
           //     val visualsContent = TabVisuals().getContent()
                val consoleContent = TabConsole().getContent()

                val stackPane = StackPane(generalContent, proxyContent, advancedContent, consoleContent)
                stackPane.alignment = Pos.CENTER

                center = stackPane

                TabContainer.currentTabProperty().addListener { _, _, newValue ->
                    stackPane.children.forEachIndexed { index, node ->
                        node.isVisible = index == newValue
                    }
                }

                stackPane.children.forEachIndexed { index, node ->
                    node.isVisible = index == TabContainer.currentTab
                }
            }
        }
    }

    fun addProxyTab() {
        val modeOptions = FXCollections.observableArrayList("Static", "One File", "Online API")

        fun createComboBox(
            items: ObservableList<String>,
            value: String,
            updateAction: CustomProxy.(String) -> Unit
        ): ComboBox<String> {
            val comboBox = ComboBox<String>()
            comboBox.apply {
                style = objectStyle
                setMinSize(120.0, 25.0)
                setMaxSize(120.0, 25.0)
                this.items = items
                this.value = value
                setCellFactory {
                    object : ListCell<String>() {
                        override fun updateItem(item: String?, empty: Boolean) {
                            super.updateItem(item, empty)
                            text = item
                            // Disable iterate button
                            if (item == "Iterate") {
                                isDisable = true
                            }
                        }
                    }
                }
                setOnAction {
                    val selectedValue = this.value
                    BaseConfigurationFactory.updateValue {
                        customProxy.updateAction(selectedValue)
                    }
                }
                buttonCell = object : ListCell<String>() {
                    init {
                        alignment = Pos.CENTER
                        padding = Insets(0.0, 0.0, 0.0, 20.0)
                        textFill = Color.WHITE
                    }

                    override fun updateItem(item: String?, empty: Boolean) {
                        super.updateItem(item, empty)
                        text = item
                    }
                }
            }

            comboBox.setOnMouseEntered {
                comboBox.style = "-fx-background-color: #606060; -fx-cursor: hand; -fx-background-radius: 12px; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 13;"
            }

            comboBox.setOnMouseExited {
                comboBox.style = objectStyle
            }

            return comboBox
        }

        val currentGridPane = GridPane().apply {
            if (customProxy.mode == 2) implementOneFileSettings(this)
        }
        val additionalGridPane = GridPane().apply {
            when (customProxy.mode) {
                1 -> implementStaticSettings(this)
                3 -> implementScrapSettings(this)
            }
        }

        val modeComboBox = createComboBox(
            items = modeOptions,
            value = when (customProxy.mode) {
                1 -> "Static"
                2 -> "One File"
                3 -> "Online API"
                else -> "Static"
            }
        ) { newValue ->
            mode = when (newValue) {
                "Static" -> 1
                "One File" -> 2
                "Online API" -> 3
                else -> 1
            }
            val previousActiveTabIndex = tabPane.selectionModel.selectedIndex
            tabPane.tabs.removeAt(1)
            addProxyTab()
            when (newValue) {
                "Static", "One File", "Online API" -> {
                    val settingsFunc = when (newValue) {
                        "Static" -> ::implementStaticSettings
                        "One File" -> ::implementOneFileSettings
                        "Online API" -> ::implementScrapSettings
                        else -> ::implementStaticSettings
                    }
                    settingsFunc(additionalGridPane)
                }
            }
            tabPane.selectionModel.select(previousActiveTabIndex)
        }

        val protocolComboBox = createComboBox(
            FXCollections.observableArrayList("HTTP", "SOCKS"),
            if (customProxy.protocol == 1) "HTTP" else "SOCKS"
        ) { newValue -> protocol = if (newValue == "HTTP") 1 else 2 }

        val isAuthRequiredCheckBox = CheckBox("Extra Authentication").apply {
            isSelected = customProxy.isAuthenticationRequired
            style =
                "-fx-text-fill: #FFFFFF; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 13; -fx-background-radius: 12px; -fx-control-inner-background: #4C4C4C; -fx-focus-color: white; -fx-faint-focus-color: transparent;"
            setOnAction {
                BaseConfigurationFactory.updateValue { customProxy.isAuthenticationRequired = isSelected }
            }
            setOnMouseEntered {
                style += "-fx-cursor: hand;"
            }
        }

        val labels = listOf("Protocol", "Mode").map {
            Label(it).apply {
                style =
                    "-fx-text-fill: #FFFFFF; -fx-font-family: '${ResourceHandler.comfortaaRegular.family}'; -fx-font-size: 13;"
            }
        }

        currentGridPane.apply {
            vgap = 10.0
            hgap = 10.0
            alignment = Pos.TOP_LEFT
            padding = Insets(10.0)
            addRow(0, HBox(10.0, modeComboBox, labels[1].apply { padding = Insets(5.0, 0.0, 0.0, -2.5) }))
            addRow(1, HBox(10.0, protocolComboBox, labels[0].apply { padding = Insets(5.0, 0.0, 0.0, -2.5) }))
            if (customProxy.mode == 1) addRow(
                2,
                HBox(isAuthRequiredCheckBox).apply { padding = Insets(0.0, 0.0, 0.0, 3.0) })
        }

        with(tabPane) {
            tabs.add(1, Tab("Proxy", HBox(currentGridPane, additionalGridPane)).apply { isClosable = false })
        }
    }

    //todo: add setting to change raw content separator and file name for one file mode
    //   Pair("File Text Separator", TextField(customProxy.fileTextSeparator))
    private fun implementStaticSettings(gridPane: GridPane) {
        val fields = listOf("Host" to { customProxy.host }, "Port" to { customProxy.port })
        val authFields = listOf(
            "Username" to { customProxy.username },
            "Password" to { customProxy.password }
        )

        val textFields = (fields + authFields).mapIndexed { _, (field, getter) ->
            val label = Label(field).apply {
                style = objectStyle
                padding = Insets(0.0, 0.0, 0.0, 75.0)
                if (field in authFields.map { it.first }) {
                    tooltip = Tooltip("This setting is only applicable when extra authentication is enabled.").apply {
                        style = tooltipStyle
                    }
                }
            }
            val textField = TextField(getter()).apply {
                style = objectStyle
                promptText = field
                setMinSize(120.0, 25.0)
                setMaxSize(120.0, 25.0)
                alignment = Pos.CENTER
                textProperty().addListener { _, _, newValue ->
                    BaseConfigurationFactory.updateValue {
                        when (field) {
                            "Host" -> customProxy.host = newValue
                            "Port" -> customProxy.port = newValue
                            "Username" -> customProxy.username = newValue
                            "Password" -> customProxy.password = newValue
                        }
                    }
                }
            }
            Pair(label, textField)
        }

        gridPane.apply {
            vgap = 10.0
            hgap = 10.0
            alignment = Pos.TOP_RIGHT
            padding = Insets(10.0)
            textFields.forEachIndexed { index, (label, textField) ->
                label.style = "-fx-text-fill: #FFFFFF; -fx-font-family: '${ResourceHandler.comfortaaRegular.family}'; -fx-font-size: 13;"
                addRow(index, label, textField)
            }
        }
    }

    private fun implementOneFileSettings(gridPane: GridPane) {
        val fileChooser = FileChooser().apply {
            extensionFilters.add(FileChooser.ExtensionFilter("Text Files", "*.txt"))
        }

        val selectedFileLabel = Label("").apply {
            style = "-fx-text-fill: #FFFFFF; -fx-font-family: '${ResourceHandler.comfortaaRegular.family}'; -fx-font-size: 13;"
        }

        val openButton = Button("Select file").apply {
            setOnAction {
                val selectedFile: File? = fileChooser.showOpenDialog(gridPane.scene.window)
                if (selectedFile != null) {
                    selectedFileLabel.text = "Selected proxy file: ${selectedFile.name}"
                } else {
                    selectedFileLabel.text = ""
                }
            }

            setMinSize(120.0, 25.0)
            setMaxSize(120.0, 25.0)
            cursor = Cursor.HAND
            style = objectStyle
            tooltip =
                Tooltip("This allows you to select a file containing a collection of proxies. The proxies should be in the 'host:port' format.").apply {
                    style = tooltipStyle
                }

            setOnMouseEntered {
                style = "-fx-text-fill: #FFFFFF; -fx-background-color: #606060; -fx-cursor: hand; -fx-background-radius: 12; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 13;"
            }

            setOnMouseExited {
                style = objectStyle
            }
        }

        val proxySeparatorLabel = Label("Proxy Separator").apply {
            style = "-fx-text-fill: #FFFFFF; -fx-font-family: '${ResourceHandler.comfortaaRegular.family}'; -fx-font-size: 13;"
            tooltip = Tooltip("Enter the character or string that separates the host and port in your proxy file.").apply {
                style = tooltipStyle
            }
        }

        val proxySeparatorTextField = TextField().apply {
            setMinSize(120.0, 25.0)
            setMaxSize(120.0, 25.0)
            style = objectStyle
            tooltip = Tooltip("Enter the character or string that separates the host and port in your proxy file.").apply {
                style = tooltipStyle
            }
            text = "\\n"
            promptText = "e.g; newline"
        }

        gridPane.apply {
            val proxySeparatorRow = HBox(proxySeparatorTextField, proxySeparatorLabel.apply { padding = Insets(0.0, 0.0, 0.0, 8.0) }).apply {
                alignment = Pos.CENTER_LEFT
            }
            val openButtonRow = HBox(openButton).apply {
                alignment = Pos.CENTER_LEFT
            }
            val selectedFileLabelRow = HBox(selectedFileLabel).apply {
                alignment = Pos.CENTER_LEFT
            }

            addRow(2, proxySeparatorRow)
            addRow(3, openButtonRow)
            addRow(4, selectedFileLabelRow)
        }
    }

    private fun implementScrapSettings(gridPane: GridPane) {
        //TODO
    }

    fun addAdvancedTab() {
        with(tabPane) {
            tabs.add(2, Tab("Advanced").apply { isClosable = false })
        }
    }
}