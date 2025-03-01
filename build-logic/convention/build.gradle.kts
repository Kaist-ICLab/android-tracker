import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "dev.iclab.convention.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(libs.ksp.gradle.plugin)
}

gradlePlugin {
    plugins {
        register("androidComposeApplication") {
            id = "dev.iclab.android.compose.application"
            implementationClass = "AndroidComposeApplicationPlugin"
        }
        register("androidBasicApplication") {
            id = "dev.iclab.android.basic.application"
            implementationClass = "AndroidBasicApplicationPlugin"
        }
        register("androidBasicLibrary") {
            id = "dev.iclab.android.basic.library"
            implementationClass = "AndroidBasicLibraryPlugin"
        }
    }
}