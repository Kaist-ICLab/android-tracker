plugins {
    id("dev.iclab.android.basic.application")
    id("dev.iclab.android.compose.application")
}

android {
    namespace = "dev.iclab.test_controller"
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":tracker-library"))
}