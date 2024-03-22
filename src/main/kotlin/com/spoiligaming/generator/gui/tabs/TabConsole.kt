package com.spoiligaming.generator.gui.tabs

import com.spoiligaming.generator.gui.ColorPalette
import com.spoiligaming.generator.gui.ResourceHandler
import com.spoiligaming.generator.gui.TabContainer
import com.spoiligaming.logging.Logger
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

class TabConsole : ITab {
    private val consolePane: GridPane = GridPane()

    init {
        Logger.printDebug("Created an instance of GridPane for Console tab.")

        TabContainer.currentTabProperty().addListener { _, _, newValue ->
            consolePane.isVisible = newValue == 4
        }
    }

    override fun getContent(): GridPane {
        val textArea = TextArea().apply {
            isEditable = false
            isWrapText = true
            isMouseTransparent = false
            isFocusTraversable = false
            setMaxSize(395.0, 285.0)
            setMinSize(395.0, 285.0)
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(16.0), Insets.EMPTY))
            style =
                "-fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14; -fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-highlight-fill: ${ColorPalette.ACCENT_COLOR};"
            cursor = Cursor.DEFAULT

            addEventFilter(ContextMenuEvent.CONTEXT_MENU_REQUESTED) { it.consume() }
        }

        val originalOut = System.out
        System.setOut(PrintStream(object : OutputStream() {
            override fun write(b: Int) {
                textArea.appendText(b.toChar().toString())
                originalOut.write(b)
            }

            override fun write(b: ByteArray, off: Int, len: Int) {
                textArea.appendText(String(b, off, len).replace("\u001B\\[[;\\d]*m".toRegex(), ""))
                originalOut.write(b, off, len)
            }
        }))

        return consolePane.apply {
            add(VBox().apply {
                background =
                    Background(BackgroundFill(Color.web(ColorPalette.SECONDARY_COLOR), CornerRadii(16.0, false), null))
                setMaxSize(410.0, 355.0)
                setMinSize(410.0, 355.0)
                GridPane.setMargin(this, Insets(0.0, 0.0, 0.0, 4.4))
                children.add(HBox().apply {
                    alignment = Pos.CENTER
                    background = Background(
                        BackgroundFill(
                            Color.web(ColorPalette.MENU_COLOR),
                            CornerRadii(16.0, 16.0, 0.0, 0.0, false),
                            null
                        )
                    )
                    setMaxSize(410.0, 40.0)
                    setMinSize(410.0, 40.0)

                    children.add(Label("Output Console").apply {
                        style =
                            "-fx-text-fill: ${ColorPalette.ACCENT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14;"
                    })
                })
                children.add(VBox(textArea).apply {
                    alignment = Pos.CENTER
                    VBox.setMargin(textArea, Insets(10.0, 0.0, 0.0, 0.0))
                })
            }, 0, 0)
        }
    }

    override fun setVisibility(visibility: ITab.TabVisibility) {
        consolePane.isVisible = (TabContainer.currentTab == 4 && visibility == ITab.TabVisibility.VISIBLE)
    }
}
