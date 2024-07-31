plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("maven-publish")
}

android {
    namespace = "dev.iclab.tracker"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.permission.flow)
//    implementation(platform(libs.koin.bom))
//    implementation(libs.koin.android)
//    implementation(libs.koin.core)


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

}
val libraryVersion: String by project

publishing{
    publications{
        register<MavenPublication>("release") {
        groupId = "dev.iclab"
        artifactId = "tracker"
        version = libraryVersion

        afterEvaluate {
            from(components["release"])
        }
    }
    }
    repositories {
        maven {
            url = uri("https://jitpack.io")
        }
    }
}

tasks.register("printVersion") {
    doLast {
        println(libraryVersion)
    }
}


//Github Package requires authentication also for user, for importing library...
//publishing{
//    repositories{
//        maven{
//            name="GitHubPackages"
//            url = uri("https://maven.pkg.github.com/Kaist-ICLab/dev.iclab.collector")
//            credentials{
//                username = System.getenv("GITHUB_ACTOR")
//                password = System.getenv("GITHUB_TOKEN")
//            }
//        }
//    }
//
//    publications {
//        create<MavenPublication>("maven") {
//            groupId = "dev.iclab"
//            artifactId = "collector"
//            version = "0.0.1-alpha"
//            artifact("${buildDir}/outputs/aar/${project.name}-release.aar")
//        }
//    }
//}