package com.spoiligaming.generator.gui

import com.spoiligaming.generator.configuration.BaseConfigurationFactory

object ColorPalette {
    var textColor = BaseConfigurationFactory.getInstance().themeSettings.textColor
    var controlColor = BaseConfigurationFactory.getInstance().themeSettings.controlColor
    var accentColor = BaseConfigurationFactory.getInstance().themeSettings.accentColor
    var menuColor = BaseConfigurationFactory.getInstance().themeSettings.menuColor
    var secondaryColor = BaseConfigurationFactory.getInstance().themeSettings.secondaryColor
}
