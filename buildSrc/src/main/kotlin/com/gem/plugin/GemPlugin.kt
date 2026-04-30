package com.gem.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class GemPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("GemPlugin applied to ${project.name}")
        
        project.tasks.withType(KotlinCompile::class.java).configureEach {
            doFirst {
                println("GemPlugin: preparing Kotlin compilation for ${project.name}")
                // Здесь будет ваша логика: обработка аннотаций, подмена импортов и т.д.
            }
        }
    }
}
