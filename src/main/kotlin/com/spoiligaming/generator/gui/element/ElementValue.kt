package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.application.Platform
import javafx.beans.property.Property
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleLongProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Button
import javafx.scene.control.Slider
import javafx.scene.control.TextField
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import java.text.DecimalFormat

object ElementValue {
    fun <T : Number> addUnitValue(
        initialValue: T,
        labelText: String,
        tooltipText: String? = null,
        valueUpdater: (T) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0),
    ): HBox {
        val property = initialValue.toSimpleProperty()

        return HBox().apply {
            alignment = Pos.TOP_LEFT
            spacing = 5.0
            this.padding = padding

            children.addAll(
//                createSlider(property, valueUpdater), // Slider added before text field
                createTextField(property, valueUpdater),
                createButton("-", 30.0, 25.0) { updateValue(property, -1, valueUpdater as (Number) -> Unit) },
                createButton("+", 30.0, 25.0) { updateValue(property, 1, valueUpdater as (Number) -> Unit) },
                CommonElement.createLabel(labelText),
            )
            tooltipText?.let {
                children.add(CommonElement.createTooltip(it))
            }

            Platform.runLater { requestFocus() }
        }
    }

    private fun <T : Number> createTextField(
        property: Any,
        valueUpdater: (T) -> Unit,
    ) = TextField().apply {
        setMaxSize(70.0, 25.0)
        setMinSize(70.0, 25.0)
        if (property is SimpleLongProperty || property is SimpleIntegerProperty) {
            textProperty().bindBidirectional(property as Property<out Number>, DecimalFormat("#"))
        }
        style =
            "-fx-background-color: ${ColorPalette.controlColor}; -fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 14; -fx-background-radius: 12; -fx-highlight-fill: ${ColorPalette.accentColor};"
        alignment = Pos.CENTER

        var oldValue: String? = null

        textProperty().addListener { _, oldVal, newVal ->
            if (newVal.matches("\\d*".toRegex())) {
                if (oldValue == null) {
                    oldValue = oldVal
                }
            } else {
                text = oldValue
            }
        }

        setOnKeyPressed { event ->
            if (event.code == KeyCode.ENTER) {
                Platform.runLater {
                    if (text.isEmpty()) {
                        text = oldValue
                    }
                    scene?.focusOwner?.requestFocus()
                }
            }
        }

        setOnMouseClicked { event ->
            if (event.button == MouseButton.PRIMARY) {
                Platform.runLater {
                    if (text.isEmpty()) {
                        text = oldValue
                    }
                    scene?.focusOwner?.requestFocus()
                }
            }
        }

        focusedProperty().addListener { _, _, isFocused ->
            if (!isFocused) {
                oldValue = null
                if (text.isNotEmpty()) {
                    when (property) {
                        is SimpleIntegerProperty -> valueUpdater(text.toInt() as T)
                        is SimpleLongProperty -> valueUpdater(text.toLong() as T)
                    }
                }
            }
        }

        addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) { it.consume() }
    }

    private fun createButton(
        text: String,
        maxWidth: Double,
        maxHeight: Double,
        onClick: () -> Unit,
    ) = Button(text).apply {
        alignment = Pos.CENTER
        style =
            "-fx-background-color: ${ColorPalette.controlColor}; -fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 15; -fx-background-radius: 12;"
        setMaxSize(maxWidth, maxHeight)
        setMinSize(maxWidth, maxHeight)

        setOnMouseEntered { cursor = Cursor.HAND }
        setOnMouseExited { cursor = Cursor.DEFAULT }
        setOnAction { onClick() }
    }

    private fun <T : Number> createSlider(
        property: Any,
        valueUpdater: (T) -> Unit
    ) = Slider().apply {
        min = 0.0
        max = 100.0

        if (property is SimpleIntegerProperty) {
            value = property.get().toDouble()
        } else if (property is SimpleLongProperty) {
            value = property.get().toDouble()
        }

        style = """
            .track {
                -fx-background-color: gray;
            }
            .track:filled {
                -fx-background-color: darkgray;
            }
            .thumb {
                -fx-background-color: ${ColorPalette.accentColor};
            }
        """.trimIndent()

        isShowTickMarks = false
        isShowTickLabels = false
        isSnapToTicks = true

        valueProperty().addListener { _, _, newValue ->
            if (newValue.toInt() >= 0) {
                when (property) {
                    is SimpleIntegerProperty -> {
                        property.set(newValue.toInt())
                        valueUpdater(newValue as T)
                    }
                    is SimpleLongProperty -> {
                        property.set(newValue.toLong())
                        valueUpdater(newValue as T)
                    }
                }
            }
        }

        HBox.setHgrow(this, Priority.ALWAYS)
    }

    private fun updateValue(
        property: Any,
        increment: Long,
        valueUpdater: (Number) -> Unit,
    ) {
        when (property) {
            is SimpleIntegerProperty -> {
                val newValue = property.get().toLong() + increment
                if (newValue >= 0) {
                    property.set(newValue.toInt())
                    valueUpdater(newValue.toInt())
                }
            }

            is SimpleLongProperty -> {
                val newValue = property.get() + increment
                if (newValue >= 0) {
                    property.set(newValue)
                    valueUpdater(newValue)
                }
            }

            else -> throw IllegalArgumentException("Unsupported property type")
        }
    }

    private fun <T : Number> T.toSimpleProperty(): Any =
        when (this) {
            is Long -> SimpleLongProperty(this)
            is Int -> SimpleIntegerProperty(this)
            else -> throw IllegalArgumentException("Unsupported type")
        }
}
