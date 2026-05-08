package com.gem.plugin

import com.android.build.api.variant.AndroidComponentsExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

class GemBuildPlugin : Plugin<Project> {

    override fun apply(project: Project) {

        val androidComponents = project.extensions
            .findByType(AndroidComponentsExtension::class.java)
            ?: return

        project.dependencies.add(
            "implementation",
            project.findProperty("gem.kryo.version")
                ?.let { "com.esotericsoftware:kryo:$it" }
                ?: "com.esotericsoftware:kryo:5.6.2"
        )

        androidComponents.onVariants { variant ->

            val taskProvider = project.tasks.register(
                "${variant.name}Gem",
                GemBuildTask::class.java
            ) {
                val inputFolders = listOf(
                    "workspace",
                    "src/main/kotlin_gpp"
                )

                inputFolders.forEach { path ->
                    val dir = project.layout.projectDirectory.dir(path)
                    if (dir.asFile.exists()) {
                        inputDirs.from(dir)
                    }
                }

                outputDir.convention(
                    project.layout.buildDirectory.dir("generated/gem/${variant.name}")
                )
            }

            variant.sources.kotlin?.addGeneratedSourceDirectory(
                taskProvider,
                GemBuildTask::outputDir
            )

            project.tasks.withType(KotlinCompile::class.java).configureEach {
                if (name.contains(variant.name, ignoreCase = true)) {
                    dependsOn(taskProvider)
                    source(taskProvider.flatMap { it.outputDir })
                }
            }
        }
    }
}