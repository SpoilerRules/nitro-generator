plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.javafx)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.shadow)
    application
}

group = "com.spoiligaming"
version = "1.0.2"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlin.coroutines.javafx)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kaml) // YAML support for Kotlin Serialization

    testImplementation(libs.kotlin.test)
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

ktlint {
    debug.set(false)
    verbose.set(false)
    android.set(false)
    ignoreFailures.set(true)
    enableExperimentalRules.set(true)
    baseline.set(file("config/ktlint/baseline.xml"))

    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }

    filter {
        exclude("**/generated/**")
        include("**/kotlin/**")
    }
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("NitroGenerator.jar")
    manifest {
        attributes["Main-Class"] = "com.spoiligaming.generator.MainKt"
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("NitroGenerator-thin.jar")
    manifest {
        attributes["Main-Class"] = "com.spoiligaming.generator.MainKt"
    }
}

application {
    mainClass.set("com.spoiligaming.generator.MainKt")
}

kotlin {
    jvmToolchain(11)
}
