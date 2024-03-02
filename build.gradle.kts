plugins {
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.serialization") version "1.9.22"
    id("org.openjfx.javafxplugin") version "0.1.0"
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
   // implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.22")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3") {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-stdlib-common")
    }

    implementation("org.openjfx:javafx-controls:11.0.2")
    implementation("org.openjfx:javafx-fxml:11.0.2")
    implementation("org.openjfx:javafx-graphics:11.0.2")
    implementation("org.openjfx:javafx-base:11.0.2")

    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

javafx {
    version = "11"
    modules("javafx.controls", "javafx.fxml", "javafx.graphics", "javafx.base")
}

tasks.test {
    useJUnitPlatform()
}

tasks.shadowJar {
    mergeServiceFiles()
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("NitroGenerator-$version.jar")
    manifest {
        attributes["Main-Class"] = "${group}.generator.MainKt"
    }
}

tasks.jar {
    duplicatesStrategy = DuplicatesStrategy.FAIL
    archiveFileName.set("NitroGenerator-$version-thin.jar")
    manifest {
        attributes["Main-Class"] = "${group}.generator.MainKt"
    }
}

tasks.register("sbuild") {
    dependsOn("build", "shadowJar")
}

application {
    mainClass.set("${group}.generator.MainKt")
}

kotlin {
    jvmToolchain(11)
}