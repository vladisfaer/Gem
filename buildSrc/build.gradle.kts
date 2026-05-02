plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        register("gemBuildPlugin") {
            id = "gem-build"
            implementationClass = "com.gem.plugin.GemBuildPlugin"
        }
    }
}

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {
    //implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
    // Android Gradle Plugin, чтобы видеть его API
    implementation("com.android.tools.build:gradle:8.0.0")
    // Kotlin Plugin нужен для kotlin-dsl, но можно и без него,
    // оставляем как у вас
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.23")
}