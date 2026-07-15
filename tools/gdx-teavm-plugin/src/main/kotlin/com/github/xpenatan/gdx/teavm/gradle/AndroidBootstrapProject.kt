package com.github.xpenatan.gdx.teavm.gradle

import java.io.File

internal object AndroidBootstrapProject {
    private const val CMAKE_FILE_NAME = "CMakeLists.txt"
    private const val GENERATED_ENTRYPOINT_NAME = "app_include.c"
    private const val PLACEHOLDER_SOURCE_NAME = "bootstrap.c"

    fun ensure(buildRoot: File, generatedSourcesDir: File, libraryName: String) {
        if(hasGeneratedProject(buildRoot, generatedSourcesDir)) {
            return
        }
        ensureDirectory(buildRoot, "Android bootstrap output root")
        ensureDirectory(generatedSourcesDir, "Android bootstrap generated sources")
        writeIfChanged(File(buildRoot, CMAKE_FILE_NAME), bootstrapCMake(libraryName))
        writeIfChanged(File(buildRoot, PLACEHOLDER_SOURCE_NAME), bootstrapSource())
    }

    internal fun bootstrapCMake(libraryName: String): String {
        return """
            cmake_minimum_required(VERSION 3.10)
            # Placeholder project written during Gradle configuration so Android Studio can sync
            # before the TeaVM-generated Android native project exists.
            project($libraryName C)

            set(CMAKE_C_STANDARD 11)

            add_library($libraryName SHARED "${"$"}{CMAKE_CURRENT_SOURCE_DIR}/$PLACEHOLDER_SOURCE_NAME")

            target_link_libraries($libraryName android log)
        """.trimIndent() + "\n"
    }

    internal fun bootstrapSource(): String {
        return """
            /* Placeholder source written during Gradle configuration so AGP CMake sync succeeds
             * before TeaVM generation replaces this bootstrap project. */
            void gdx_teavm_android_bootstrap_placeholder(void) {
            }
        """.trimIndent() + "\n"
    }

    private fun hasGeneratedProject(buildRoot: File, generatedSourcesDir: File): Boolean {
        val cmakeFile = File(buildRoot, CMAKE_FILE_NAME)
        val generatedEntrypoint = File(generatedSourcesDir, GENERATED_ENTRYPOINT_NAME)
        return cmakeFile.isFile && generatedEntrypoint.isFile
    }

    private fun writeIfChanged(file: File, content: String) {
        val parent = file.parentFile
        if(parent != null) {
            ensureDirectory(parent, "Android bootstrap output directory")
        }
        if(file.isFile && file.readText() == content) {
            return
        }
        file.writeText(content)
    }

    private fun ensureDirectory(directory: File, name: String) {
        if(directory.isDirectory) {
            return
        }
        if(directory.exists()) {
            throw IllegalStateException("$name is not a directory: ${directory.absolutePath}")
        }
        if(!directory.mkdirs()) {
            throw IllegalStateException("Unable to create $name: ${directory.absolutePath}")
        }
    }
}
