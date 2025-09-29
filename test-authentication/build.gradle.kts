plugins {
    id("dev.iclab.android.basic.application")
    id("dev.iclab.android.compose.application")
    alias(libs.plugins.googleServices)
}

android {
    namespace = "com.example.test_authentication"
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":tracker-library"))
}