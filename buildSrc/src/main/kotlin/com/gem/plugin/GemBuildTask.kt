package com.gem.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import java.io.File

abstract class GemBuildTask : DefaultTask() {

    @get:InputFiles
    abstract val inputDirs: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    init {
        doFirst {
            val dir = outputDir.get().asFile
            if (!dir.exists()) {
                dir.mkdirs()
            }
        }
    }

    @TaskAction
    fun process() {

        val outFolder = outputDir.get().asFile

        outFolder.deleteRecursively()
        outFolder.mkdirs()

        inputDirs.files.forEach {
            dir ->
            if (dir.exists() && dir.isDirectory) {

                dir.walkTopDown()
                .filter {
                    it.isFile && it.extension == "kt"
                }
                .forEach {
                    file ->

                    var content = file.readText()

                    val hasSavable = "@Savable" in content
                    val hasDontSave = "@DontSave" in content

                    // Замены аннотаций
                    if (hasSavable) {
                        content = content.replace("@Savable", "@Serializable")
                    }

                    if (hasDontSave) {
                        content = content.replace("@DontSave", "@Transient")
                    }

                    // Добавление импортов
                    if (hasSavable || hasDontSave) {

                        val lines = content.lines().toMutableList()
                        val importsToAdd = mutableListOf<String>()

                        if (hasSavable) {
                            importsToAdd += "import kotlinx.serialization.Serializable"
                        }

                        if (hasDontSave) {
                            importsToAdd += "import kotlinx.serialization.Transient"
                        }

                        val packageIndex = lines.indexOfFirst {
                            it.startsWith("package ")
                        }

                        val insertIndex = if (packageIndex != -1) {
                            packageIndex + 1
                        } else {
                            0
                        }

                        lines.addAll(insertIndex, importsToAdd)

                        content = lines.joinToString("\n")
                    }

                    val outFile = outFolder.resolve(file.name)
                    outFile.writeText(content)
                }
            }
        }
    }
}