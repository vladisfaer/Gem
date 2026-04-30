plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.gem.framework"
    compileSdk = 33

    defaultConfig {
        minSdk = 24
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
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    // Если framework использует корутины или jbox2d, раскомментируйте:
    // implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    // implementation("org.jbox2d:jbox2d-library:2.2.1.1")
}
