plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    implementation("com.android.tools.build:gradle:8.0.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
}
