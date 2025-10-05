pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
    }
}

plugins {
    id("org.gradle.experimental.android-ecosystem").version("0.1.43")
}

rootProject.name = "example-android-app"

include("app")
// Removed sample library modules to avoid unnecessary dependencies: list, utilities

defaults {
    androidApplication {
        jdkVersion = 17
        compileSdk = 34
        // Lower minSdk to 23 to meet requirement for runtime permission handling support
        minSdk = 23

        versionCode = 1
        versionName = "1.0"
        applicationId = "org.example.app"

        testing {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }

    androidLibrary {
        jdkVersion = 17
        compileSdk = 34
        minSdk = 23

        testing {
            dependencies {
                implementation("org.junit.jupiter:junit-jupiter:5.10.2")
                runtimeOnly("org.junit.platform:junit-platform-launcher")
            }
        }
    }
}
