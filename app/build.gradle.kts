import org.gradle.api.tasks.compile.JavaCompile

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.whatsapponlylatest"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.whatsapponlylatest"
        minSdk = 26
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(17)
    sourceCompatibility = "17"
    targetCompatibility = "17"
}
