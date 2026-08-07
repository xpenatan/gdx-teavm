package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.bundling.Jar

internal abstract class GdxTeaVMDevServerClasspathJar : Jar() {
    @get:Classpath
    abstract val classpath: ConfigurableFileCollection

    @TaskAction
    override fun copy() {
        manifest.attributes["Class-Path"] = classpath.files.joinToString(" ") { file ->
            file.toURI().toASCIIString()
        }
        super.copy()
    }
}
