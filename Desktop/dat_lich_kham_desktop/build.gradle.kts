import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
    id("org.openjfx.javafxplugin") version "0.0.13"
}
javafx {
    version = "17"
    modules = listOf("javafx.controls", "javafx.swing", "javafx.web")
}

group = "com.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}
// Cấu hình JavaFX


dependencies {
    implementation("org.jetbrains.compose.material:material-icons-extended:1.5.0")

    implementation("cafe.adriel.voyager:voyager-navigator:1.0.0")
    implementation("cafe.adriel.voyager:voyager-core:1.0.0")

    implementation("io.ktor:ktor-client-core:2.3.6")
    implementation("io.ktor:ktor-client-cio:2.3.6") // cho JVM/Desktop
    implementation("io.ktor:ktor-client-content-negotiation:2.3.6")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.6")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("com.github.kittinunf.fuel:fuel:2.3.1")
    implementation("com.github.kittinunf.fuel:fuel-coroutines:2.3.1")

    implementation("com.squareup.okhttp3:okhttp:4.9.3")


    // Option 2: Voyager ViewModel (Easier integration với Voyager)
    implementation("cafe.adriel.voyager:voyager-screenmodel:1.0.0")

    // Option 3: Koin Dependency Injection (nếu cần DI)
    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-compose:1.1.0")

    implementation ("org.json:json:20240303")


    implementation("io.insert-koin:koin-core:3.5.0")
    implementation("io.insert-koin:koin-compose:1.1.0")

    // Voyager Koin integration
    implementation("cafe.adriel.voyager:voyager-koin:1.0.0")

    // Coroutines for Swing (for file chooser)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")

    implementation("media.kamel:kamel-image:0.7.0") // check latest version

    implementation("org.jetbrains.compose.ui:ui-desktop:1.5.0")
    implementation("org.jetbrains.compose.runtime:runtime:1.5.0")
    implementation("org.jetbrains.compose.foundation:foundation:1.5.0")
    implementation("org.jetbrains.compose.material:material:1.5.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("ch.qos.logback:logback-classic:1.4.14")
    //
    val javaFxVersion = "17"
    implementation("org.openjfx:javafx-controls:$javaFxVersion")
    implementation("org.openjfx:javafx-swing:$javaFxVersion")
    implementation("org.openjfx:javafx-web:$javaFxVersion")
    implementation("org.openjfx:javafx-base:$javaFxVersion")
    implementation("org.openjfx:javafx-graphics:$javaFxVersion")

    // Platform-specific
    val os = System.getProperty("os.name").lowercase()
    val platform = when {
        os.contains("win") -> "win"
        os.contains("mac") -> "mac"
        os.contains("linux") -> "linux"
        else -> "linux"
    }

    implementation("org.openjfx:javafx-controls:$javaFxVersion:$platform")
    implementation("org.openjfx:javafx-swing:$javaFxVersion:$platform")
    implementation("org.openjfx:javafx-web:$javaFxVersion:$platform")

    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "dat_lich_kham_desktop"
            packageVersion = "1.0.0"
        }
    }
}
