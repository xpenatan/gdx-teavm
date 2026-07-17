package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.teavm.gradle.api.TeaVMDevServerConfiguration

/**
 * Common TeaVM development-server settings shared by JavaScript and WasmGC targets.
 *
 * The containing web target's `serverPort` property controls the HTTP port for both the regular
 * gdx-teavm server and TeaVM's development server.
 */
open class GdxTeaVMDevServerExtension internal constructor(
    objects: ObjectFactory,
    private val teavmConfig: TeaVMDevServerConfiguration
) {
    /**
     * Makes the target's existing `gdx_teavm_web_*_run` task use TeaVM's development server.
     *
     * Default: `false`, preserving the normal build-and-serve workflow.
     */
    val enabled: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /** Automatically recompiles project changes while the run task remains active. */
    val autoBuild: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /** Reloads the served page after a successful TeaVM development-server rebuild. */
    val autoReload: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /** Maximum heap size, in megabytes, for the TeaVM development-server process. */
    val processMemory: Property<Int>
        get() = teavmConfig.processMemory

    /** Additional directories served as static files. */
    val staticDirs: ConfigurableFileCollection
        get() = teavmConfig.staticDirs

    /** URL path prefix under which [staticDirs] are served. */
    val staticServePath: Property<String>
        get() = teavmConfig.staticServePath

    /** Classpath resource roots served as static resources. */
    val resourceRoots: ListProperty<String>
        get() = teavmConfig.resourceRoots

    /** URL path prefix under which [resourceRoots] are served. */
    val resourceServePath: Property<String>
        get() = teavmConfig.resourceServePath
}
