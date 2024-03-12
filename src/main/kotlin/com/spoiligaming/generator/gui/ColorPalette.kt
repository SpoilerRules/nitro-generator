package com.spoiligaming.generator.gui

import com.spoiligaming.generator.configuration.BaseConfigurationFactory

object ColorPalette {
    var TEXT_COLOR = BaseConfigurationFactory.getInstance().themeSettings.textColor
    var CONTROL_COLOR = BaseConfigurationFactory.getInstance().themeSettings.controlColor
    var ACCENT_COLOR = BaseConfigurationFactory.getInstance().themeSettings.accentColor
    var MENU_COLOR = BaseConfigurationFactory.getInstance().themeSettings.menuColor
    var SECONDARY_COLOR = BaseConfigurationFactory.getInstance().themeSettings.secondaryColor
}
