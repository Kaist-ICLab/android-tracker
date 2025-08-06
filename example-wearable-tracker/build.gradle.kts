plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.kotlinCompose)
}

android {
    namespace = "kaist.iclab.wearabletracker"
    compileSdk = 36

    defaultConfig {
        applicationId = "kaist.iclab.wearableTracker"
        minSdk = 30
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    implementation(libs.android.gms.wearable)
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

    // koin
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // icons
    implementation(libs.material.icons.extended)

    // tracker library
    implementation(project(":tracker-library"))
}