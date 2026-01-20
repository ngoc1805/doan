import java.util.Properties
import java.io.File
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
}
val localProperties = Properties()
val localPropertiesFile = File(rootDir, "local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}
android {
    namespace = "com.example.dat_lich_kham_fe"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.dat_lich_kham_fe"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "API_KEY",
                "\"${localProperties.getProperty("API_KEY", "")}\"")
            buildConfigField("String", "ADDRESS",
                "\"${localProperties.getProperty("ADDRESS", "")}\"")
        }
        release {
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            isShrinkResources = true

            buildConfigField("String", "API_KEY",
                "\"${localProperties.getProperty("API_KEY", "")}\"")
            buildConfigField("String", "ADDRESS",
                "\"${localProperties.getProperty("ADDRESS", "")}\"")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation ("androidx.compose.foundation:foundation:1.4.3")
    implementation ("io.coil-kt:coil-compose:2.4.0")
    // CameraX - Thêm camera2 implementation
    implementation("androidx.camera:camera-core:1.3.4")
    implementation("androidx.camera:camera-camera2:1.3.4")
    implementation("androidx.camera:camera-lifecycle:1.3.4")
    implementation("androidx.camera:camera-view:1.3.4")

    // ML Kit barcode scanning
    implementation("com.google.mlkit:barcode-scanning:17.2.0")

    // Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")
    implementation("androidx.compose.runtime:runtime-livedata:1.9.0-beta03")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.9.1")

    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("androidx.activity:activity-compose:1.9.0")

    implementation("androidx.navigation:navigation-compose:2.7.5")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("androidx.datastore:datastore-preferences:1.0.0")
    implementation("com.google.code.gson:gson:2.10.1")

    implementation("com.google.guava:guava:31.1-android")

    //firebase
    implementation ("com.google.firebase:firebase-auth:22.3.1")
    implementation(platform("com.google.firebase:firebase-bom:34.2.0"))
    implementation("com.google.firebase:firebase-messaging")
    implementation ("com.google.firebase:firebase-inappmessaging")
    implementation ("com.google.firebase:firebase-inappmessaging-display")

    implementation("com.google.ai.client.generativeai:generativeai:0.9.0")

    implementation("androidx.glance:glance-appwidget:1.0.0")

    // build.gradle (Module)
    implementation("io.ktor:ktor-client-android:2.3.0")
    implementation("io.ktor:ktor-client-cio:2.3.0")
    implementation("io.ktor:ktor-client-content-negotiation:2.3.0")

    implementation("com.google.accompanist:accompanist-swiperefresh:0.32.0")

    //
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    //
    implementation("androidx.security:security-crypto:1.1.0-alpha06")
    //
    implementation("androidx.biometric:biometric:1.2.0-alpha05")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.ads.mobile.sdk)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
