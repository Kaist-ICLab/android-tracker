plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinCompose)

    id("com.google.devtools.ksp")
}

android {
    namespace = "kaist.iclab.wearabletracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "kaist.iclab.duiTracker"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.wear.compose.material)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.wear.tooling.preview)
    implementation(libs.compose.activity)
    implementation(libs.androidx.core.splashscreen)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    // RoomDB
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.gson) // for converter

    // Google Play Services
    implementation(libs.android.gms.wearable)
    implementation(libs.android.gms.location)

    // koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // icons
    implementation(libs.material.icons.extended)

    // tracker library
    implementation(project(":tracker-library"))
}