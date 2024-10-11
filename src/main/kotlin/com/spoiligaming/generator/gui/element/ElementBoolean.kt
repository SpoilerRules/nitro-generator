package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import javafx.animation.TranslateTransition
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.layout.HBox
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Rectangle
import javafx.util.Duration

object ElementBoolean {
    fun addBooleanValue(
        initialValue: Boolean,
        labelText: String,
        tooltipText: String? = null,
        valueUpdater: (Boolean) -> Unit,
        padding: Insets = Insets(10.0, 0.0, 0.0, 10.0),
    ): HBox =
        HBox().apply {
            alignment = Pos.TOP_LEFT
            spacing = 10.0
            this.padding = padding

            children.addAll(
                createToggleSwitch(initialValue, valueUpdater),
                CommonElement.createLabel(labelText),
            )
            tooltipText?.let {
                children.add(CommonElement.createTooltip(it))
            }
        }

    private fun createToggleSwitch(initialValue: Boolean, valueUpdater: (Boolean) -> Unit): StackPane {
        val width = 50.0
        val height = 20.0
        val thumbSizeMultiplier = 1.2

        // Background rectangle
        val background = Rectangle(width, height).apply {
            arcWidth = height
            arcHeight = height
            style =
                "-fx-fill: ${if (initialValue) "-fx-on-color" else "-fx-off-color"};"
        }

        // Thumb circle with larger size
        val thumbSize = height * thumbSizeMultiplier
        val thumb = Circle(thumbSize / 2).apply {
            style = """
                -fx-fill: -fx-thumb-color;
                -fx-stroke-width: 0;
            """
            translateX = if (initialValue) (width - thumbSize) / 2 else -(width - thumbSize) / 2
        }

        val transition = TranslateTransition(Duration.millis(100.0), thumb)

        return StackPane(background, thumb).apply {
            cursor = Cursor.HAND

            // custom style variables
            style = """
                -fx-on-color: ${ColorPalette.controlColor};
                -fx-off-color: #383839;
                -fx-thumb-color: gray;
            """

            setOnMouseClicked {
                val newValue = !background.style.contains("-fx-fill: -fx-on-color")
                background.style =
                    "-fx-fill: ${if (newValue) "-fx-on-color" else "-fx-off-color"};"
                transition.toX = if (newValue) (width - thumbSize) / 2 else -(width - thumbSize) / 2
                transition.play()
                valueUpdater(newValue)
            }

            setOnMouseEntered { cursor = Cursor.HAND }
            setOnMouseExited { cursor = Cursor.DEFAULT }
        }
    }
}
