plugins {
    kotlin("jvm") version "1.9.23"
    kotlin("plugin.serialization") version "1.9.23"
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("io.gitlab.arturbosch.detekt") version "1.23.5"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    application
}

group = "com.spoiligaming"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
   // implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.23")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-javafx:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
    }

    implementation("org.openjfx:javafx-controls:11.0.2")
    implementation("org.openjfx:javafx-graphics:11.0.2")
    implementation("org.openjfx:javafx-base:11.0.2")
 //   implementation("org.controlsfx:controlsfx:11.2.0")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

javafx {
    version = "11"
    modules("javafx.controls", "javafx.graphics", "javafx.base")
}

detekt {
    toolVersion = "1.23.5"
    source.setFrom("src/main/kotlin")

    parallel = true

    config.setFrom("/config/detekt/detekt.yml")
    baseline = file("/config/detekt/detekt-baseline.xml")

    allRules = false
    disableDefaultRuleSets = false
    debug = false
    ignoreFailures = false
    basePath = projectDir.absolutePath
}

tasks.test {
    useJUnitPlatform()
}

tasks.register<Copy>("copyDependencies") {
    from(configurations.runtimeClasspath)
    into("libs")
}

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("NitroGenerator.jar")
    manifest {
        attributes["Main-Class"] = "${group}.generator.MainKt"
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("NitroGenerator-thin.jar")
    manifest {
        attributes["Main-Class"] = "com.spoiligaming.generator.MainKt"
    }
    dependsOn("copyDependencies")
}

application {
    mainClass.set("${group}.generator.MainKt")
}

kotlin {
    jvmToolchain(11)
}