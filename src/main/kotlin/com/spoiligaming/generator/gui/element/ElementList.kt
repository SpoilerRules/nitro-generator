package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.configuration.BaseConfigurationFactory
import com.spoiligaming.generator.configuration.CustomProxy
import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.collections.ObservableList
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.ComboBox
import javafx.scene.control.ListCell
import javafx.scene.control.skin.ComboBoxListViewSkin
import javafx.scene.layout.HBox
import javafx.scene.paint.Color

// supports only proxy settings for now.
// TODO: make this more scalable and flexible
object ElementList {
    fun addListValue(
        items: ObservableList<String>,
        value: String,
        updateAction: CustomProxy.(String) -> Unit,
        labelText: String,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0),
    ) = HBox().apply {
        alignment = Pos.TOP_LEFT
        spacing = 5.0
        this.padding = padding

        children.addAll(
            ComboBox<String>().apply {
                style =
                    "-fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13; -fx-background-radius: 12; -fx-background-color: ${ColorPalette.controlColor};"
                setMinSize(142.0, 25.0)
                setMaxSize(142.0, 25.0)
                this.items = items
                this.value = value
                setCellFactory {
                    object : ListCell<String>() {
                        init {
                            style =
                                "-fx-background-color: ${ColorPalette.controlColor}; -fx-text-fill: ${ColorPalette.textColor};"
                            setOnMouseEntered {
                                style =
                                    "-fx-background-color: ${ColorPalette.controlColor}; -fx-text-fill: ${ColorPalette.accentColor};"
                                cursor = Cursor.HAND
                            }
                            setOnMouseExited {
                                style =
                                    "-fx-background-color: ${ColorPalette.controlColor}; -fx-text-fill: ${ColorPalette.textColor};"
                                cursor = Cursor.DEFAULT
                            }
                        }

                        override fun updateItem(
                            item: String?,
                            empty: Boolean,
                        ) {
                            super.updateItem(item, empty)
                            text = item
                        }
                    }
                }
                setOnAction {
                    val selectedValue = this.value
                    BaseConfigurationFactory.updateValue {
                        proxySettings.updateAction(selectedValue)
                    }
                }
                buttonCell =
                    object : ListCell<String>() {
                        init {
                            alignment = Pos.CENTER_LEFT
                            textFill = Color.web(ColorPalette.textColor)
                        }

                        override fun updateItem(
                            item: String?,
                            empty: Boolean,
                        ) {
                            super.updateItem(item, empty)
                            text = item
                        }
                    }

                skinProperty().addListener { _, _, newSkin ->
                    (newSkin as ComboBoxListViewSkin<*>).let { skin ->
                        (skin.children.find { it.styleClass.contains("arrow-button") })?.style =
                            "-fx-background-color: ${ColorPalette.textColor}; -fx-scale-x: 0.6; -fx-scale-y: 0.4; -fx-shape: 'M480-362q-8 0-15-2.5t-13-8.5L268-557q-11-11-11-28t11-28q11-11 28-11t28 11l156 156 156-156q11-11 28-11t28 11q11 11 11 28t-11 28L508-373q-6 6-13 8.5t-15 2.5Z';"
                    }
                }
            },
            CommonElement.createLabel(labelText),
        )
    }
}
