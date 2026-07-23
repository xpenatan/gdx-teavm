package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.Sync
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault(because = "Copies Android runtime sources from resolved backend artifacts")
abstract class GdxTeaVMGenerateAndroidRuntimeTask : Sync() {
    @get:OutputDirectory
    abstract val outputDirectory: DirectoryProperty

    init {
        into(outputDirectory)
    }
}
