package com.spoiligaming.generator.gui.element

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.util.Duration

class CommonElement {
    private var contentFieldIndex: Int = 0

    fun createContentField(
        gridPane: GridPane,
        contentFieldTitle: String,
        vararg content: Node?,
    ) {

        gridPane.apply {
            add(
                VBox().apply {
                    background =
                        Background(
                            BackgroundFill(
                                Color.web(ColorPalette.secondaryColor),
                                CornerRadii(16.0, false),
                                null,
                            ),
                        )
                    maxWidth = 410.0
                    minWidth = 410.0
                    prefHeight = Region.USE_COMPUTED_SIZE
                    GridPane.setMargin(this, Insets(-0.5, 0.0, 0.0, -2.4))

                    children.add(
                        HBox().apply {
                            alignment = Pos.CENTER
                            background =
                                Background(
                                    BackgroundFill(
                                        Color.web(ColorPalette.menuColor),
                                        CornerRadii(16.0, 16.0, 0.0, 0.0, false),
                                        null,
                                    ),
                                )
                            setMaxSize(410.0, 35.0)
                            setMinSize(410.0, 35.0)
                            children.add(
                                Label(contentFieldTitle).apply {
                                    style = "-fx-text-fill: ${ColorPalette.accentColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14;"
                                },
                            )
                        },
                    )
                    children.addAll(content)
                    children.add(
                        Region().apply {
                            minHeight = 7.5
                            maxHeight = 7.5
                        },
                    )
                },
                0,
                contentFieldIndex++,
            )
        }
    }

    companion object {
        fun createLabel(
            text: String,
            textSize: String = "13",
        ) = Label(text).apply {
            minWidthProperty().bind(prefWidthProperty())
            maxWidthProperty().bind(prefWidthProperty())
            maxHeight = 25.0
            minHeight = 25.0
            prefHeight = 25.0

            style =
                "-fx-background-color: transparent; -fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: $textSize;"
        }

        fun createTooltip(tooltipText: String): HBox =
            HBox().apply {
                setMaxSize(15.0, 25.0)
                setMinSize(15.0, 25.0)
                alignment = Pos.CENTER
                children.add(
                    Button("?").apply buttonNode@{
                        setMaxSize(18.0, 20.0)
                        setMinSize(18.0, 20.0)
                        alignment = Pos.CENTER
                        tooltip =
                            Tooltip(tooltipText).apply {
                                showDelay = Duration.ZERO
                                hideDelay = Duration.ZERO
                                style =
                                    "-fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 13; -fx-background-radius: 12; -fx-background-color: ${ColorPalette.secondaryColor};"
                            }
                        style =
                            "-fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaSemiBold.family}'; -fx-font-size: 12; -fx-background-radius: 12; -fx-background-color: ${ColorPalette.controlColor}; -fx-focus-color: transparent; -fx-padding: 0;"
                    },
                )
            }
    }
}
