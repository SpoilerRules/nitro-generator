package com.spoiligaming.generator.gui

import com.spoiligaming.generator.SessionStatistics
import com.spoiligaming.logging.Logger
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.SimpleIntegerProperty
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.Label
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.scene.layout.GridPane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color

class TabContainer : GridPane() {
    companion object {
        private val currentTabProperty = SimpleIntegerProperty(0)

        fun currentTabProperty(): ReadOnlyIntegerProperty = currentTabProperty

        var currentTab: Int
            get() = currentTabProperty.get()
            set(value) = currentTabProperty.set(value)
    }

    init {
        background = Background(
            BackgroundFill(
                Color.web(ColorPalette.SECONDARY_COLOR),
                CornerRadii(16.0, 0.0, 16.0, 16.0, false),
                null
            )
        )

        setMaxSize(175.0, 425.0)
        setMinSize(175.0, 425.0)

        implementTabBox()
        implementInfoBox()
        implementStatisticsBox()

        hgap = 10.0
        vgap = 7.5
    }

    private fun implementTabBox() {
        val tabs = listOf("General", "Proxy", "Advanced", /*"Visuals",*/ "Console").mapIndexed { index, labelText ->
            createTab(labelText, index)
        }

        fun switchTab(tabNumber: Int) {
            tabs[currentTab].background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(8.0, false), null))
            tabs[tabNumber].background = Background(BackgroundFill(Color.web("#4C4C4C"), CornerRadii(8.0, false), null))
            currentTab = tabNumber

            Logger.printDebug("Current tab index: ${currentTabProperty.get()}")
        }

        add(GridPane().apply {
            background = Background(BackgroundFill(Color.web(ColorPalette.MENU_COLOR), CornerRadii(16.0, false), null))
            setMaxSize(160.0, 275.0)
            setMinSize(160.0, 275.0)
            alignment = Pos.TOP_CENTER
            setMargin(this, Insets(0.0, 0.0, -5.0, 0.0))
            vgap = 10.0

            tabs.forEachIndexed { index, tab ->
                add(tab, 0, index + 1)
            }
        }, 0, 1)

        switchTab(currentTab)

        tabs.forEachIndexed { index, tab ->
            tab.setOnMouseClicked {
                if (currentTab != index) {
                    switchTab(index)
                }
            }
        }
    }

    private fun createTab(labelText: String, tabNumber: Int): VBox = VBox().apply {
        setMaxSize(145.0, 35.0)
        setMinSize(145.0, 35.0)

        setOnMouseEntered {
            background =
                Background(BackgroundFill(Color.web(ColorPalette.CONTROL_COLOR), CornerRadii(8.0, false), null))
        }

        setOnMouseExited {
            if (currentTab != tabNumber) {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(8.0, false), null))
            }
        }

        children.add(Label(labelText).apply {
            style =
                "-fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14;"
            setMaxSize(145.0, 35.0)
            setMinSize(145.0, 35.0)
            alignment = Pos.CENTER_LEFT
            padding = Insets(0.0, 0.0, 0.0, 20.0)
        })
    }

    //todo: make this update regularly based on the generation delay
    private fun implementStatisticsBox() {
        add(Region().apply {
            background = Background(BackgroundFill(Color.web(ColorPalette.MENU_COLOR), CornerRadii(16.0, false), null))
            setMaxSize(160.0, 75.0)
            setMinSize(160.0, 75.0)
            alignment = Pos.TOP_CENTER
            setMargin(this, Insets(10.0, 0.0, 0.0, 0.0))
        }, 0, 0)

        add(Label("Statistics").apply {
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
            style =
                "-fx-text-fill: ${ColorPalette.ACCENT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
            setMaxSize(160.0, 75.0)
            setMinSize(160.0, 75.0)
            alignment = Pos.TOP_CENTER
            padding = Insets(10.0, 0.0, 0.0, 0.0)
        }, 0, 0)

        val validLabel = Label("Valid").apply {
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
            style =
                "-fx-text-fill: ${ColorPalette.CONTROL_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
            setMaxSize(160.0, 75.0)
            setMinSize(160.0, 75.0)
            alignment = Pos.TOP_CENTER
        }

        val validHitsLabel = Label(SessionStatistics.validNitroCodes.toString()).apply {
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
            style =
                "-fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 12;"
            setMaxSize(160.0, 75.0)
            setMinSize(160.0, 75.0)
            alignment = Pos.TOP_CENTER
        }

        val labelPadding = 25.0

        validLabel.padding = Insets(35.0, 0.0, 0.0, -(labelPadding + -10))
        validHitsLabel.padding = Insets(34.0, 0.0, 0.0, labelPadding + 10)

        add(validLabel, 0, 0)
        add(validHitsLabel, 0, 0)

        val successRate = SessionStatistics.successRate

        val defaultSuccessRateLabelPadding: Double = when {
            successRate == 0f -> -38.0
            successRate < 12 -> -40.0
            else -> -45.0
        }

        add(Label("Success Rate").apply {
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
            style =
                "-fx-text-fill: ${ColorPalette.CONTROL_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
            setMaxSize(160.0, 75.0)
            setMinSize(160.0, 75.0)
            alignment = Pos.TOP_CENTER
            padding = Insets(55.0, 0.0, 0.0, defaultSuccessRateLabelPadding)
        }, 0, 0)

        val successRateLabelText = "%.2f".format(successRate) + "%"
        val defaultPaddingValue = if (successRate != 0f) {
            105.0
        } else {
            93.0
        }
        val paddingValue = if (successRate < 12 && successRate != 0f) {
            90.0
        } else {
            defaultPaddingValue
        }

        val successRateLabel = Label(successRateLabelText).apply {
            background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
            style =
                "-fx-text-fill: ${ColorPalette.TEXT_COLOR}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 12;"
            setMaxSize(160.0, 75.0)
            setMinSize(160.0, 75.0)
            alignment = Pos.TOP_CENTER
            padding = Insets(54.0, 0.0, 0.0, paddingValue)
        }

        add(successRateLabel, 0, 0)
    }

    private fun implementInfoBox() {
        add(Region().apply {
            background = Background(BackgroundFill(Color.web(ColorPalette.MENU_COLOR), CornerRadii(16.0, false), null))
            setMaxSize(160.0, 40.0)
            setMinSize(160.0, 40.0)
            alignment = Pos.BOTTOM_CENTER
            setMargin(this, Insets(0.0, 0.0, 5.0, 0.0))
        }, 0, 2)

        val createLabel: (String, Pos, Double, String) -> Label = { text, pos, bottomMargin, textColor ->
            Label(text).apply {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                setMaxSize(160.0, 40.0)
                setMinSize(160.0, 40.0)
                alignment = pos
                setMargin(this, Insets(0.0, 0.0, bottomMargin, 0.0))
                style =
                    "-fx-text-fill: $textColor; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 11;"
            }
        }

        add(createLabel("Nitro Generator", Pos.TOP_CENTER, 0.0, ColorPalette.ACCENT_COLOR), 0, 2)
        add(createLabel("1.0.0", Pos.BOTTOM_CENTER, 15.0, ColorPalette.CONTROL_COLOR), 0, 2)
    }
}
