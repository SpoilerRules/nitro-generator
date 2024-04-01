package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.input.ContextMenuEvent
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import java.io.OutputStream
import java.io.PrintStream

class TabConsole : AbstractTab(4, "Console") {
    override fun getContent(): GridPane {
        val textArea =
            TextArea().apply {
                isEditable = false
                isWrapText = true
                isMouseTransparent = false
                isFocusTraversable = false
                setMaxSize(395.0, 285.0)
                setMinSize(395.0, 285.0)
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(16.0), Insets.EMPTY))
                style =
                    "-fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14; -fx-text-fill: ${ColorPalette.textColor}; -fx-highlight-fill: ${ColorPalette.accentColor};"
                cursor = Cursor.DEFAULT

                addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) { it.consume() }
            }

        val originalOut = System.out
        System.setOut(
            PrintStream(
                object : OutputStream() {
                    override fun write(b: Int) {
                        textArea.appendText(b.toChar().toString())
                        originalOut.write(b)
                    }

                    override fun write(
                        b: ByteArray,
                        off: Int,
                        len: Int,
                    ) {
                        textArea.appendText(String(b, off, len).replace("\u001B\\[[;\\d]*m".toRegex(), ""))
                        originalOut.write(b, off, len)
                    }
                },
            ),
        )

        return pane.apply {
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
                    setMaxSize(410.0, 355.0)
                    setMinSize(410.0, 355.0)
                    GridPane.setMargin(this, Insets(-1.5, 0.0, 0.0, 4.4))
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
                            setMaxSize(410.0, 40.0)
                            setMinSize(410.0, 40.0)

                            children.add(
                                Label("Output Console").apply {
                                    style =
                                        "-fx-text-fill: ${ColorPalette.accentColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14;"
                                },
                            )
                        },
                    )
                    children.add(
                        VBox(textArea).apply {
                            alignment = Pos.CENTER
                            VBox.setMargin(textArea, Insets(10.0, 0.0, 0.0, 0.0))
                        },
                    )
                },
                0,
                0,
            )
        }
    }
}
