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
import javafx.scene.layout.HBox
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.SVGPath

class TabContainer : GridPane() {
    companion object {
        private val currentTabProperty = SimpleIntegerProperty(0)

        fun currentTabProperty(): ReadOnlyIntegerProperty = currentTabProperty

        var currentTab: Int
            get() = currentTabProperty.get()
            set(value) = currentTabProperty.set(value)
    }

    init {
        background =
            Background(
                BackgroundFill(
                    Color.web(ColorPalette.secondaryColor),
                    CornerRadii(16.0, 0.0, 16.0, 16.0, false),
                    null,
                ),
            )

        setMaxSize(175.0, 425.0)
        setMinSize(175.0, 425.0)

        implementTabBox()
        implementInfoBox()
        implementStatisticsBox()

        hgap = 10.0
        vgap = 7.0
    }

    private fun implementTabBox() {
        val tabs =
            listOf(
                "General" to "M10 20v-6h4v6h5v-8h3L12 3 2 12h3v8z", // Home icon
                "Proxy" to "M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z", // Globe icon
                "Fortification" to "M12 1L3 5v6c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V5l-9-4zm0 10.99h7c-.53 4.12-3.28 7.79-7 8.94V12H5V6.3l7-3.11v8.8z", // Shield icon
                "Advanced" to "M19.14 12.94c.04-.3.06-.61.06-.94 0-.32-.02-.64-.07-.94l2.03-1.58c.18-.14.23-.41.12-.61l-1.92-3.32c-.12-.22-.37-.29-.59-.22l-2.39.96c-.5-.38-1.03-.7-1.62-.94l-.36-2.54c-.04-.24-.24-.41-.48-.41h-3.84c-.24 0-.43.17-.47.41l-.36 2.54c-.59.24-1.13.57-1.62.94l-2.39-.96c-.22-.08-.47 0-.59.22L2.74 8.87c-.12.21-.08.47.12.61l2.03 1.58c-.05.3-.09.63-.09.94s.02.64.07.94l-2.03 1.58c-.18.14-.23.41-.12.61l1.92 3.32c.12.22.37.29.59.22l2.39-.96c.5.38 1.03.7 1.62.94l.36 2.54c.05.24.24.41.48.41h3.84c.24 0 .44-.17.47-.41l.36-2.54c.59-.24 1.13-.56 1.62-.94l2.39.96c.22.08.47 0 .59-.22l1.92-3.32c.12-.22.07-.47-.12-.61l-2.01-1.58zM12 15.6c-1.98 0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6 1.62 3.6 3.6-1.62 3.6-3.6 3.6z", // Gear icon
                "Visuals" to "M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z", // Eye icon
            ).mapIndexed { index, (labelText, svgPath) ->
                createTab(labelText, index, svgPath)
            }

        fun switchTab(tabNumber: Int) {
            tabs[currentTab].background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(8.0, false), null))
            tabs[tabNumber].background = Background(BackgroundFill(Color.web(ColorPalette.controlColor), CornerRadii(8.0, false), null))
            currentTab = tabNumber

            Logger.printDebug("Current tab index: ${currentTabProperty.get()}")
        }

        add(
            GridPane().apply {
                background =
                    Background(BackgroundFill(Color.web(ColorPalette.menuColor), CornerRadii(16.0, false), null))
                setMaxSize(160.0, 280.0)
                setMinSize(160.0, 280.0)
                alignment = Pos.TOP_CENTER
                setMargin(this, Insets(0.0, 0.0, -5.0, 0.0))
                vgap = 10.0

                tabs.forEachIndexed { index, tab ->
                    add(tab, 0, index + 1)
                }
            },
            0,
            1,
        )

        switchTab(currentTab)

        tabs.forEachIndexed { index, tab ->
            tab.setOnMouseClicked {
                if (currentTab != index) {
                    switchTab(index)
                }
            }
        }
    }

    private fun createTab(
        labelText: String,
        tabNumber: Int,
        svgPath: String
    ): VBox =
        VBox().apply {
            setMaxSize(145.0, 35.0)
            setMinSize(145.0, 35.0)

            setOnMouseEntered {
                background =
                    Background(BackgroundFill(Color.web(ColorPalette.controlColor), CornerRadii(8.0, false), null))
            }

            setOnMouseExited {
                if (currentTab != tabNumber) {
                    background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii(8.0, false), null))
                }
            }

            children.add(
                HBox().apply {
                    setMaxSize(145.0, 35.0)
                    setMinSize(145.0, 35.0)
                    alignment = Pos.CENTER_LEFT
                    padding = Insets(0.0, 0.0, 0.0, 10.0)
                    spacing = 10.0

                    children.addAll(
                        SVGPath().apply {
                            content = svgPath
                            fill = Color.web(ColorPalette.textColor)
                            maxWidth(16.0)
                            maxHeight(16.0)
                        },
                        Label(labelText).apply {
                            style = "-fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 14;"
                        }
                    )
                }
            )
        }

    // todo: make this update regularly based on the generation delay
    private fun implementStatisticsBox() {
        add(
            Region().apply {
                background =
                    Background(BackgroundFill(Color.web(ColorPalette.menuColor), CornerRadii(16.0, false), null))
                setMaxSize(160.0, 75.0)
                setMinSize(160.0, 75.0)
                alignment = Pos.TOP_CENTER
                setMargin(this, Insets(7.5, 0.0, 0.0, 0.0))
            },
            0,
            0,
        )

        add(
            Label("Statistics").apply {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                style =
                    "-fx-text-fill: ${ColorPalette.accentColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
                setMaxSize(160.0, 75.0)
                setMinSize(160.0, 75.0)
                alignment = Pos.TOP_CENTER
                padding = Insets(7.5, 0.0, 0.0, 0.0)
            },
            0,
            0,
        )

        val validLabel =
            Label("Valid").apply {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                style =
                    "-fx-text-fill: ${ColorPalette.controlColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
                setMaxSize(160.0, 75.0)
                setMinSize(160.0, 75.0)
                alignment = Pos.TOP_CENTER
            }

        val validHitsLabel =
            Label(SessionStatistics.validNitroCodes.toString()).apply {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                style =
                    "-fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 12;"
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

        val defaultSuccessRateLabelPadding: Double =
            when {
                successRate == 0f -> -38.0
                successRate < 12 -> -40.0
                else -> -45.0
            }

        add(
            Label("Success Rate").apply {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                style =
                    "-fx-text-fill: ${ColorPalette.controlColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 13;"
                setMaxSize(160.0, 75.0)
                setMinSize(160.0, 75.0)
                alignment = Pos.TOP_CENTER
                padding = Insets(55.0, 0.0, 0.0, defaultSuccessRateLabelPadding)
            },
            0,
            0,
        )

        val successRateLabelText = "%.2f".format(successRate) + "%"
        val defaultPaddingValue =
            if (successRate != 0f) {
                105.0
            } else {
                93.0
            }
        val paddingValue =
            if (successRate < 12 && successRate != 0f) {
                90.0
            } else {
                defaultPaddingValue
            }

        val successRateLabel =
            Label(successRateLabelText).apply {
                background = Background(BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY))
                style =
                    "-fx-text-fill: ${ColorPalette.textColor}; -fx-font-family: '${ResourceHandler.comfortaaBold.family}'; -fx-font-size: 12;"
                setMaxSize(160.0, 75.0)
                setMinSize(160.0, 75.0)
                alignment = Pos.TOP_CENTER
                padding = Insets(54.0, 0.0, 0.0, paddingValue)
            }

        add(successRateLabel, 0, 0)
    }

    private fun implementInfoBox() {
        add(
            Region().apply {
                background =
                    Background(BackgroundFill(Color.web(ColorPalette.menuColor), CornerRadii(16.0, false), null))
                setMaxSize(160.0, 40.0)
                setMinSize(160.0, 40.0)
                alignment = Pos.BOTTOM_CENTER
                setMargin(this, Insets(0.0, 0.0, 5.0, 0.0))
            },
            0,
            2,
        )

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

        add(createLabel("Nitro Generator", Pos.TOP_CENTER, 0.0, ColorPalette.accentColor), 0, 2)
        add(createLabel("1.0.2", Pos.BOTTOM_CENTER, 15.0, ColorPalette.controlColor), 0, 2)
    }
}
