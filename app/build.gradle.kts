plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.mybrowser"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.mybrowser"
        minSdk = 23
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation("androidx.webkit:webkit:1.13.0")
}