
plugins {
    id("com.android.application")
    id("kotlin-android")
    //kotlin("plugin.power-assert") version "2.0.0"
}

android {
    namespace = "com.example.myapplication3"
    compileSdk = 33
    
    defaultConfig {
        applicationId = "com.example.myapplication3"
        minSdk = 21
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
        
        vectorDrawables { 
            useSupportLibrary = true
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    buildFeatures {
        viewBinding = true
        
    }
    
}

dependencies{
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")
    implementation("org.jbox2d:jbox2d-library:2.2.1.1")
    implementation("org.jetbrains.kotlin:kotlin-reflect:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-test:2.0.21")

}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    kotlinOptions.jvmTarget = "11"
}


