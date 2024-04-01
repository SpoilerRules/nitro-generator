<!--suppress HtmlDeprecatedAttribute -->
<h1 align="center">
  <b>Spoili's Nitro Generator</b>
</h1>

<p align="center">
  This Nitro generator is created with the user's experience as the top priority, making it an exceptional application with a wide variety of features. It is equipped with a dependable and easy-to-navigate graphical user interface, simplifying the setup process for users. Although the chances of getting Nitro for free through this generator are quite low, the application itself is a clear indication of the commitment to quality and user satisfaction. The project was initially developed to kill time and spend more time with Kotlin/JVM while providing a valuable tool for the Discordian community.
</p>

<p align="center">
  <a><img src="https://img.shields.io/github/stars/SpoilerRules/nitro-generator?style=for-the-badge&color=slategray" alt="Stars"></a>
  <a href="LICENSE"><img src="https://img.shields.io/badge/license-GPL--3.0-blue.svg?style=for-the-badge&color=pink" alt="GPL-3.0 License"></a>
</p>

<p align="center">
  <a href="https://kotlinlang.org/"><img src="https://img.shields.io/badge/Kotlin-1.9.23-blue.svg?style=flat-square&logo=kotlin" alt="Kotlin"></a>
  <a href="https://www.oracle.com/java/technologies/javase-downloads.html"><img src="https://img.shields.io/badge/Java-11-blue.svg?style=flat-square&logo=java&logoColor=white" alt="Java"></a>
  <a href="https://gradle.org/"><img src="https://img.shields.io/badge/Gradle-8.7-blue.svg?style=flat-square&logo=gradle&logoColor=white" alt="Gradle"></a>
</p>

## Table of Contents
- [Key Features](#key-features)
- [Getting Started and Requirements](#getting-started)
- [GUI Inspiration](#gui-inspiration)
- [GUI Preview](#gui-preview)
- [Building the Project](#building-the-project)
- [Contributor Assistance Required](#contributor-assistance-required)
- [Contributing to the Project](#contributing-to-the-project)

## Key Features

- **Configurations Auto-Save**: Your settings are saved automatically using the Kotlin Serialization library, so you can pick up right where you left off every time.
- **Nitro Code Retry Mechanism**: If a nitro code fails to be validated, the system will retry it automatically.
- **Auto-Claim for Valid Codes**: When a valid nitro code is detected, it's claimed on your behalf.
  - **Retry Mechanism**: If the claim fails, the system will retry until successful.
- **Discord Webhook Notifications**: Get instant alerts via Discord webhook when a valid nitro code is found.
- **Concurrent Nitro Code Generation and Validation**: Generate and validate nitro codes simultaneously, maximizing efficiency. We achieve this using the Kotlin Coroutines Core library, which sets up a dedicated thread pool.
- **Advanced Proxy Support**:
    - **Proxy Modes**: Depending on your proxy list, choose from "Static", "One File" or "Online API" modes.
        - **Static Mode**: Use a single proxy with the option for additional authorization.
        - **One File Mode**: Select one proxy file and cycle through proxies, with a custom separator for content.
        - **Online API Mode**: Enter multiple links to raw content and iterate through them, with a custom separator for content.
    - **Full Proxy Protocol Support**: Works with both HTTP/HTTPS and SOCKS4/SOCKS5 proxies.
- **Customizable Options**: Adjust features like the Generation Delay to suit your needs.

## Getting Started

To use this Nitro generator, you'll need Java 11 or newer. We suggest the latest version that's supported for a long time (LTS version) for the best experience. Don't worry, setting up Java is straightforward:
1. Search online for "Java LTS version download".
2. Follow the instructions to install it on your computer. Once Java is set up, you're all set to start using the Nitro generator!

## GUI Inspiration

Our user interface is a tribute to the exceptional design of [Maple Lite](https://maple.software/), known for its dependability and user-friendly approach. As a dedicated fan of [Maple Lite](https://maple.software/), I was inspired to model our GUI on its organized, visually pleasing, and straightforward design, which aligns perfectly with our program's spirit. For those who play osu! or simply appreciate thoughtful design, I enthusiastically recommend exploring [Maple Lite](https://maple.software/).

## GUI Preview
<div style="display: flex; justify-content: center;">
  <img src="https://i.ibb.co/DM7mN7r/java-qhu-Cp-TR9z9.png" alt="GUI Preview 1" width="410" style="margin-right: 10px;">
  <img src="https://i.ibb.co/p2tTpYZ/java-79m-U1-BBNe2.png" alt="GUI Preview 2" width="410">
</div>

### Building the Project

Building this project is no sweat, even if you're new to development or just a casual user. Here's how to do it:

1. Open your terminal or command prompt.
2. Navigate to the project's root directory.
3. Run the command `./gradlew shadowJar`.

That's it! After the process completes, you'll find the build output in the `build/libs` folder. This single file will contain everything you need to run the project.

When you build the project, you may notice two types of `.jar` files in the `build/libs` folder:

- **NitroGenerator.jar (Fat Jar)**: This file is like a packed suitcase, it has everything you need inside. It includes all the necessary dependencies, so you can run the project right away without worrying about adding anything else.

- **NitroGenerator-thin.jar (Thin Jar)**: Think of this as an empty suitcase. It's just the core of the project and doesn't include the extra dependencies. It's smaller, but you'll need to gather and add the required dependencies yourself before you can use it.

Both files serve the same purpose but cater to different needs. Choose the fat jar for convenience or the thin jar if you prefer to manage dependencies manually.

### Contributor Assistance Required

We're seeking skilled contributors to improve this project with the following features:

- **SVG Icons**: Integrate SVG icons adjacent to tab labels, akin to those in Maple Lite, to enrich the UI's visual appeal.
- **ComboBox Styling**: Implement a ComboBox popup that mirrors the aesthetic and functionality of Maple Lite's design.
- **UI Element Replacement**: Swap out the CheckBox for a toggle switch element, styled after Maple Lite's interface.
- **Value Slider Addition**: Add a value slider next to text fields, modeled after Maple Lite's design. Reference implementation can be found in [ElementValue.kt](https://github.com/SpoilerRules/nitro-generator/blob/master/src/main/kotlin/com/spoiligaming/generator/gui/element/ElementValue.kt).

If you're interested in contributing to any of these areas, please reach out to `spoilerrules` on Discord.

### Contributing to the Project

We welcome contributions, but we like to keep things organized. If you're considering a pull request, please touch base with `spoilerrules` on Discord first. While we appreciate the use of Kotlin's idiomatic features, clarity and conciseness are our top priorities. To maintain high-quality standards, our team conducts regular code reviews and refactoring sessions, guided by the community's engagement and feedback.
