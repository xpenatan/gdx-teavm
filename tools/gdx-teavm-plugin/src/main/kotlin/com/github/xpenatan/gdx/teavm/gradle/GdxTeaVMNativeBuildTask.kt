package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File

@DisableCachingByDefault(because = "Runs external native build scripts")
abstract class GdxTeaVMNativeBuildTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildRoot: DirectoryProperty

    @get:Input
    abstract val scriptBaseName: Property<String>

    @TaskAction
    fun build() {
        val root = buildRoot.get().asFile
        val script = File(root, scriptBaseName.get() + scriptExtension())
        if(!script.isFile) {
            throw IllegalStateException("Native build script was not generated: ${script.absolutePath}")
        }
        val command = if(isWindows()) {
            listOf("cmd", "/c", script.absolutePath)
        }
        else {
            listOf("bash", script.absolutePath)
        }
        val process = ProcessBuilder(command)
            .directory(root)
            .redirectErrorStream(true)
            .start()
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { logger.lifecycle(it) }
        }
        val exitCode = process.waitFor()
        if(exitCode != 0) {
            throw IllegalStateException("Native build failed with exit code $exitCode")
        }
    }

    private fun scriptExtension(): String {
        return if(isWindows()) ".bat" else ".sh"
    }

    private fun isWindows(): Boolean {
        return System.getProperty("os.name").lowercase().contains("windows")
    }
}
