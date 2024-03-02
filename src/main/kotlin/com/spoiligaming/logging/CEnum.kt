package com.spoiligaming.logging

/**
 * `CEnum` is an enumeration representing various color codes.
 *
 * Each enum constant corresponds to a specific color code used for console output. These color codes are defined by the ANSI Escape sequences, which are used to control the formatting, color, and other output options on the terminal.
 *
 * The enum constants are:
 * - `RESET`: Resets the color. It is used to bring the console color back to the default after it has been changed.
 * - `GREEN`: Represents the color green.
 * - `ERROR_RED`: Represents the color red. This is typically used to display error messages.
 * - `WHITE`: Represents the color white.
 * - `YELLOW`: Represents the color yellow.
 * - `ORANGE`: Represents the color orange.
 *
 * Each enum constant holds an escape code, which is a `String` that contains the ANSI escape sequence for the corresponding color.
 *
 * The `toString()` method is overridden to return the escape code when an enum constant is printed.
 */
enum class CEnum(private val escapeCode: String) {
    RESET("\u001B[0m"),
    GREEN("\u001B[32m"),
    ERROR_RED("\u001B[31m"),
    WHITE("\u001B[97m"),
    YELLOW("\u001B[33m"),
    ORANGE("\u001B[38;5;208m");

    override fun toString(): String = escapeCode
}