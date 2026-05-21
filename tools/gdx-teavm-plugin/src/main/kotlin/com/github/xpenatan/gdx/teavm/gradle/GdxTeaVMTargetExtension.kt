package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.teavm.gradle.api.OptimizationLevel
import javax.inject.Inject

open class GdxTeaVMTargetExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project,
    outputDirName: String,
    targetFileNameValue: String
) {
    /**
     * Root directory for generated files for this target.
     *
     * Default: JS `build/dist/web`, Wasm `build/dist/wasm`, GLFW `build/dist/glfw`, PSP `build/dist/psp`.
     */
    val outputDir: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir(outputDirName))

    /**
     * Fully qualified launcher class used as this target's TeaVM main class.
     *
     * JS and Wasm usually share a web launcher, while GLFW and PSP usually need native-specific launchers.
     *
     * Default: none. This must be set for every target you build.
     */
    val mainClass: Property<String> = objects.property(String::class.java)

    /**
     * Path inside [outputDir] where TeaVM writes generated target files.
     *
     * Web targets default to `webapp`; native targets override this to `c/src`.
     *
     * Default: JS and Wasm `webapp`, GLFW and PSP `c/src`.
     */
    val relativePathInOutputDir: Property<String> = objects.property(String::class.java).convention("webapp")

    /**
     * Name of the main generated output file, such as `app.js`, `app.wasm`, or `app`.
     *
     * Default: JS `app.js`, Wasm `app.wasm`, GLFW `app`, PSP `app`.
     */
    val targetFileName: Property<String> = objects.property(String::class.java).convention(targetFileNameValue)

    /**
     * TeaVM optimization level used for this target.
     *
     * Default: JS `BALANCED`, Wasm `AGGRESSIVE`, GLFW `AGGRESSIVE`, PSP `AGGRESSIVE`.
     */
    val optimization: Property<OptimizationLevel> = objects.property(OptimizationLevel::class.java)
        .convention(OptimizationLevel.BALANCED)

    /**
     * Includes TeaVM debug information in generated output when supported by the target.
     *
     * Default: `false`.
     */
    val debugInformation: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Enables TeaVM fast global analysis, trading precision for faster compilation.
     *
     * Default: `false`.
     */
    val fastGlobalAnalysis: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Runs TeaVM compilation out of the Gradle process when supported by TeaVM.
     *
     * Default: `false`.
     */
    val outOfProcess: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Memory limit in megabytes for out-of-process TeaVM compilation.
     *
     * Default: `512`.
     */
    val processMemory: Property<Int> = objects.property(Int::class.javaObjectType).convention(512)

    /**
     * Classes TeaVM should preserve from aggressive removal or renaming.
     *
     * Default: empty list.
     */
    val preservedClasses: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    internal fun webappDir(): Provider<Directory> {
        return outputDir.flatMap { output ->
            relativePathInOutputDir.map { relativePath ->
                output.dir(relativePath)
            }
        }
    }
}

open class GdxTeaVMJsExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMWebExtension(objects, project, outputDirName, targetFileNameValue) {
    /**
     * Minifies and renames generated JavaScript output when true.
     *
     * Default: `true`.
     */
    val obfuscated: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Enables TeaVM strict JavaScript generation checks.
     *
     * Default: `false`.
     */
    val strict: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)
}

open class GdxTeaVMWasmExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMWebExtension(objects, project, outputDirName, targetFileNameValue) {
    init {
        optimization.convention(OptimizationLevel.AGGRESSIVE)
    }

    /**
     * Minifies and renames generated Wasm runtime support output when true.
     *
     * Default: `true`.
     */
    val obfuscated: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Enables TeaVM strict Wasm generation checks.
     *
     * Default: `true`.
     */
    val strict: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Copies TeaVM's Wasm runtime JavaScript next to the generated `.wasm` file.
     *
     * Keep this enabled unless you provide the matching runtime script yourself.
     *
     * Default: `true`.
     */
    val copyRuntime: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Copies TeaVM's ES module Wasm runtime instead of the global script runtime.
     *
     * The generated gdx-teavm web app expects the default global runtime, so this normally stays false.
     *
     * Default: `false`.
     */
    val modularRuntime: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)
}

open class GdxTeaVMWebExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMTargetExtension(objects, project, outputDirName, targetFileNameValue) {
    /**
     * JavaScript entry point function name emitted by TeaVM and called by the generated web app.
     *
     * Default: `main`.
     */
    val entryPointName: Property<String> = objects.property(String::class.java).convention("main")

    /**
     * Arguments passed by the generated web app to the TeaVM entry point.
     *
     * Use JavaScript array element syntax without the surrounding brackets, for example `"foo", "bar"`.
     *
     * Default: empty string.
     */
    val mainClassArgs: Property<String> = objects.property(String::class.java).convention("")

    /**
     * Enables generation of the gdx-teavm web app files around the TeaVM output.
     *
     * When enabled, the backend writes files such as `index.html`, `WEB-INF/web.xml`, assets,
     * preload manifest, and support scripts.
     *
     * Default: `true`.
     */
    val webappEnabled: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Browser document title used by the generated `index.html`.
     *
     * Default: `gdx-teavm`.
     */
    val htmlTitle: Property<String> = objects.property(String::class.java).convention("gdx-teavm")

    /**
     * Initial canvas width written to the generated `index.html`.
     *
     * Default: `800`.
     */
    val htmlWidth: Property<Int> = objects.property(Int::class.javaObjectType).convention(800)

    /**
     * Initial canvas height written to the generated `index.html`.
     *
     * Default: `600`.
     */
    val htmlHeight: Property<Int> = objects.property(Int::class.javaObjectType).convention(600)

    /**
     * Classpath resource path for the loading logo copied into the web app assets folder.
     *
     * Default: `startup-logo.png`.
     */
    val logoPath: Property<String> = objects.property(String::class.java).convention("startup-logo.png")

    /**
     * Copies [logoPath] into the generated web app when true.
     *
     * Default: `true`.
     */
    val copyLoadingAsset: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Port used by this target's plugin web run task.
     *
     * Default: Gradle property `teavmPluginPort` when present, otherwise `8080`.
     */
    val serverPort: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(project.providers.gradleProperty("teavmPluginPort").map(String::toInt).orElse(8080))
}

open class GdxTeaVMNativeExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String,
    val backendName: String
) : GdxTeaVMTargetExtension(objects, project, outputDirName, targetFileNameValue) {
    /**
     * Initial native heap size in megabytes.
     *
     * Default: `4`.
     */
    val minHeapSizeMb: Property<Int> = objects.property(Int::class.javaObjectType).convention(4)

    /**
     * Maximum native heap size in megabytes.
     *
     * Default: `128`.
     */
    val maxHeapSizeMb: Property<Int> = objects.property(Int::class.javaObjectType).convention(128)

    /**
     * Enables TeaVM heap dump support for native output when supported by TeaVM.
     *
     * Default: `false`.
     */
    val heapDump: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Asks TeaVM to generate shorter C file names. Useful for native toolchains with path limits.
     *
     * Default: `true`.
     */
    val shortFileNames: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Obfuscates generated native C symbols when true.
     *
     * Default: `true`.
     */
    val obfuscated: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Directory where native runtime assets and build output support files are prepared.
     *
     * Default: `[outputDir]/c/release`.
     */
    val releasePath: DirectoryProperty = objects.directoryProperty()
        .convention(outputDir.map { it.dir("c/release") })

    init {
        relativePathInOutputDir.convention("c/src")
        optimization.convention(OptimizationLevel.AGGRESSIVE)
    }

    internal fun generatedSourcesDir(): Provider<Directory> {
        return webappDir()
    }
}

open class GdxTeaVMGlfwExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMNativeExtension(objects, project, outputDirName, targetFileNameValue, "glfw") {
    /**
     * Native build type used by generated GLFW build scripts, typically `Debug` or `Release`.
     *
     * Default: `Debug`.
     */
    val buildType: Property<String> = objects.property(String::class.java).convention("Debug")

    /**
     * Lets the backend invoke the generated GLFW build script when true.
     *
     * Default: `false`.
     */
    val buildExecutable: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Lets the backend run the generated GLFW executable after building when true.
     *
     * Default: `false`.
     */
    val runExecutable: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Opens or attaches native console logging for GLFW run tasks when supported by the platform.
     *
     * Default: `false`.
     */
    val consoleLog: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)
}

open class GdxTeaVMPspExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMNativeExtension(objects, project, outputDirName, targetFileNameValue, "psp") {
    /**
     * Enables PSP memory debug support in generated native glue code.
     *
     * Default: `false`.
     */
    val debugMemory: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Lets the backend execute the generated PSP build script after generating sources when true.
     *
     * Default: `false`.
     */
    val autoExecuteBuild: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)
}
