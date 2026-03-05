import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    google()
}

dependencies {
    // Note, if you develop a library, you should use compose.desktop.common.
    // compose.desktop.currentOs should be used in launcher-sourceSet
    // (in a separate module for demo project and in testMain).
    // With compose.desktop.common you will also lose @Preview functionality
    implementation(compose.desktop.currentOs)
    implementation(compose.materialIconsExtended)
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.skiko:skiko-awt-runtime-windows-x64:0.7.93")
    implementation("io.ktor:ktor-client-core:2.3.3")
    implementation("io.ktor:ktor-client-cio:2.3.3")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.3")
    implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.3")
    // Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
// Converter (Gson)
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    implementation("cafe.adriel.voyager:voyager-screenmodel:1.0.0")
    implementation("cafe.adriel.voyager:voyager-koin:1.0.0")
    implementation("cafe.adriel.voyager:voyager-navigator:1.0.0")
    implementation("cafe.adriel.voyager:voyager-core:1.0.0")

    implementation("io.insert-koin:koin-compose:1.0.4")

    implementation("media.kamel:kamel-image:0.9.5")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:1.7.3")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs += listOf(
        "-Xadd-modules=java.smartcardio"
    )
}

compose.desktop {
    application {
        mainClass = "MainKt"

        jvmArgs += listOf(
            "--add-modules=java.smartcardio"
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "the_datlichkham_fe"
            packageVersion = "1.0.0"
        }
    }
}
