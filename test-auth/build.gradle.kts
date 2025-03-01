plugins {
    id("dev.iclab.android.basic.application")
    id("dev.iclab.android.compose.application")
    alias(libs.plugins.googleServices)
}

android {
    namespace = "dev.iclab.test_auth"
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":tracker-library"))
}