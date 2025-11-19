plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinCompose)
    alias(libs.plugins.googleServices)
    
    kotlin("plugin.serialization") version "2.2.10"
}

android {
    namespace = "kaist.iclab.mobiletracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "kaist.iclab.trackerSystem"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        jvmToolchain(17)
    }

    buildFeatures {
        compose = true
    }
}

dependencies {
    /* Android Tracker Library */
    implementation(project(":tracker-library"))

    /* Android Compose */
    implementation(libs.compose.activity)
    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(platform(libs.compose.bom))
    debugImplementation(libs.compose.ui.tooling)

    /* Androidx */
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.compose.lifecycle.viewmodel)
    
    /* Navigation */
    implementation(libs.compose.navigation)

    /* Supabase Related */
    implementation(libs.supabase.kt)
    implementation(libs.postgrest.kt)
    implementation(libs.realtime.kt)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.core)
    
    /* Kotlin Serialization */
    implementation(libs.kotlinx.serialization.json)
    
    /* Koin Dependency Injection */
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
