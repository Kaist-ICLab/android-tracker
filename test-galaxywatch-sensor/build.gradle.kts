plugins {
    id("dev.iclab.android.basic.application")
    id("dev.iclab.android.compose.application")
}

android {
    namespace = "dev.iclab.test_galaxywatch_sensor"
    defaultConfig {
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(project(":tracker-library"))

    implementation(libs.wear.compose.material)
    implementation(libs.wear.compose.ui.tooling)
    implementation(libs.wear.tooling.preview)
}
