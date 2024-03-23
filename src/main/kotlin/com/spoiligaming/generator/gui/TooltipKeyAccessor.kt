package com.spoiligaming.generator.gui

import com.spoiligaming.generator.gui.TooltipKeyAccessor.properties
import com.spoiligaming.logging.Logger
import java.util.Properties

/**
 * `TooltipKeyAccessor` is a singleton object that provides an interface to access tooltip descriptions.
 *
 * It loads a properties file containing key-value pairs of tooltip descriptions. The properties file is expected to be
 * located in the root of the classpath with the name `tooltip_descriptions.properties`.
 *
 * In case of any failure during the loading of the properties file, an error message is logged and tooltips won't be available.
 *
 * @property properties The loaded properties from the `tooltip_descriptions.properties` file. It's nullable and would be null if the properties file failed to load.
 */
object TooltipKeyAccessor {
    private val properties: Properties? =
        runCatching {
            Properties().apply {
                load(TooltipKeyAccessor::class.java.getResourceAsStream("/tooltip_descriptions.properties"))
            }
        }.onFailure {
            Logger.printError("Failed to load properties file: ${it.message}. Tooltips won't be available.")
        }.getOrNull()

    /**
     * Retrieves the tooltip description corresponding to the provided key.
     *
     * @param key The key for which the tooltip description is to be retrieved.
     * @return The tooltip description if the key is present in the properties; `null` otherwise.
     */
    fun getValue(key: String) = properties?.getProperty(key)
}
