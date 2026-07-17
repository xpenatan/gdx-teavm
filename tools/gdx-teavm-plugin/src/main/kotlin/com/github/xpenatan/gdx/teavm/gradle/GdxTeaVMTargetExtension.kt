package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy
import org.teavm.gradle.api.TeaVMConfiguration
import org.teavm.gradle.api.TeaVMDevServerConfiguration
import org.teavm.gradle.api.TeaVMJSConfiguration
import org.teavm.gradle.api.TeaVMWasmGCConfiguration
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
open class GdxTeaVMTargetExtension internal constructor(
    internal val teavmConfig: TeaVMConfiguration
) {
    /**
     * Root directory for generated files for this target.
     *
     * Default: JS `build/dist/js`, Wasm `build/dist/wasm`.
     */
    val outputDir: DirectoryProperty
        get() = teavmConfig.outputDir

    /**
     * Fully qualified launcher class used as this target's TeaVM main class.
     *
     * JS and Wasm usually share a web launcher.
     *
     * Default: none. This must be set for every target you build.
     */
    val mainClass: Property<String>
        get() = teavmConfig.mainClass as Property<String>

    /**
     * Path inside [outputDir] where TeaVM writes generated target files.
     *
     * Default: `webapp`.
     */
    val relativePathInOutputDir: Property<String>
        get() = teavmConfig.relativePathInOutputDir as Property<String>

    /**
     * TeaVM optimization level used for this target.
     *
     * Default: JS `BALANCED`, Wasm `AGGRESSIVE`.
     */
    val optimization: Property<OptimizationLevel>
        get() = teavmConfig.optimization as Property<OptimizationLevel>

    /**
     * Includes TeaVM debug information in generated output when supported by the target.
     *
     * Default: `false`.
     */
    val debugInformation: Property<Boolean>
        get() = teavmConfig.debugInformation as Property<Boolean>

    /**
     * Enables TeaVM fast global analysis, trading precision for faster compilation.
     *
     * Default: `false`.
     */
    val fastGlobalAnalysis: Property<Boolean>
        get() = teavmConfig.fastGlobalAnalysis as Property<Boolean>

    /**
     * Runs TeaVM compilation out of the Gradle process when supported by TeaVM.
     *
     * Default: `false`.
     */
    val outOfProcess: Property<Boolean>
        get() = teavmConfig.outOfProcess as Property<Boolean>

    /**
     * Memory limit in megabytes for out-of-process TeaVM compilation.
     *
     * Default: `512`.
     */
    val processMemory: Property<Int>
        get() = teavmConfig.processMemory as Property<Int>

    /**
     * Classes TeaVM should preserve from aggressive removal or renaming.
     *
     * Default: empty list.
     */
    val preservedClasses: ListProperty<String>
        get() = teavmConfig.preservedClasses as ListProperty<String>

    internal fun outputSubDir(): Provider<Directory> {
        return outputDir.flatMap { output ->
            relativePathInOutputDir.map { relativePath ->
                output.dir(relativePath)
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
open class GdxTeaVMJsExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    private val jsConfig: TeaVMJSConfiguration
) : GdxTeaVMWebExtension(objects, project, jsConfig, jsConfig.devServer) {
    init {
        outputDir.convention(project.layout.buildDirectory.dir("dist/js"))
        relativePathInOutputDir.convention("webapp")
        targetFileName.convention("app.js")
        optimization.convention(OptimizationLevel.BALANCED)
        debugInformation.convention(false)
        fastGlobalAnalysis.convention(false)
        outOfProcess.convention(false)
        processMemory.convention(512)
        entryPointName.convention("main")
        obfuscated.convention(true)
        strict.convention(false)
        sourceMap.convention(false)
        sourceFilePolicy.convention(SourceFilePolicy.LINK_LOCAL_FILES)
    }

    /**
     * JavaScript entry point function name emitted by TeaVM and called by the generated web app.
     *
     * Default: `main`.
     */
    override val entryPointName: Property<String>
        get() = jsConfig.entryPointName as Property<String>

    /**
     * Name of the generated JavaScript output file.
     *
     * Default: `app.js`.
     */
    val targetFileName: Property<String>
        get() = jsConfig.targetFileName as Property<String>

    /**
     * Minifies and renames generated JavaScript output when true.
     *
     * Default: `true`.
     */
    val obfuscated: Property<Boolean>
        get() = jsConfig.obfuscated as Property<Boolean>

    /**
     * Enables TeaVM strict JavaScript generation checks.
     *
     * Default: `false`.
     */
    val strict: Property<Boolean>
        get() = jsConfig.strict as Property<Boolean>

    /**
     * Generates browser source maps for TeaVM web output.
     *
     * Default: `false`.
     */
    val sourceMap: Property<Boolean>
        get() = jsConfig.sourceMap as Property<Boolean>

    /**
     * Controls how Java source files referenced by source maps are exposed to the browser.
     *
     * Use [SourceFilePolicy.COPY] for browser DevTools validation, [SourceFilePolicy.LINK_LOCAL_FILES]
     * for local IDE-oriented paths, or [SourceFilePolicy.DO_NOTHING] to leave sources out.
     *
     * Default: [SourceFilePolicy.LINK_LOCAL_FILES].
     */
    val sourceFilePolicy: Property<SourceFilePolicy>
        get() = jsConfig.sourceFilePolicy as Property<SourceFilePolicy>
}

@Suppress("UNCHECKED_CAST")
open class GdxTeaVMWasmExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    private val wasmConfig: TeaVMWasmGCConfiguration
) : GdxTeaVMWebExtension(objects, project, wasmConfig, wasmConfig.devServer) {
    init {
        outputDir.convention(project.layout.buildDirectory.dir("dist/wasm"))
        relativePathInOutputDir.convention("webapp")
        targetFileName.convention("app.wasm")
        optimization.convention(OptimizationLevel.AGGRESSIVE)
        debugInformation.convention(false)
        fastGlobalAnalysis.convention(false)
        outOfProcess.convention(false)
        processMemory.convention(512)
        obfuscated.convention(true)
        strict.convention(false)
        copyRuntime.convention(true)
        modularRuntime.convention(false)
        sourceMap.convention(false)
        sourceFilePolicy.convention(SourceFilePolicy.LINK_LOCAL_FILES)
    }

    /**
     * JavaScript export function name called by the generated Wasm web app.
     *
     * Default: `main`.
     */
    override val entryPointName: Property<String> = objects.property(String::class.java).convention("main")

    /**
     * Name of the generated Wasm output file.
     *
     * Default: `app.wasm`.
     */
    val targetFileName: Property<String>
        get() = wasmConfig.targetFileName as Property<String>

    /**
     * Minifies and renames generated Wasm runtime support output when true.
     *
     * Default: `true`.
     */
    val obfuscated: Property<Boolean>
        get() = wasmConfig.obfuscated as Property<Boolean>

    /**
     * Enables TeaVM strict Wasm generation checks.
     *
     * Default: `false`.
     */
    val strict: Property<Boolean>
        get() = wasmConfig.strict as Property<Boolean>

    /**
     * Copies TeaVM's Wasm runtime JavaScript next to the generated `.wasm` file.
     *
     * Keep this enabled unless you provide the matching runtime script yourself.
     *
     * Default: `true`.
     */
    val copyRuntime: Property<Boolean>
        get() = wasmConfig.copyRuntime as Property<Boolean>

    /**
     * Copies TeaVM's ES module Wasm runtime instead of the global script runtime.
     *
     * The generated gdx-teavm web app expects the default global runtime, so this normally stays false.
     *
     * Default: `false`.
     */
    val modularRuntime: Property<Boolean>
        get() = wasmConfig.modularRuntime as Property<Boolean>

    /**
     * Generates browser source maps for TeaVM web output.
     *
     * Default: `false`.
     */
    val sourceMap: Property<Boolean>
        get() = wasmConfig.sourceMap as Property<Boolean>

    /**
     * Controls how Java source files referenced by source maps are exposed to the browser.
     *
     * Use [SourceFilePolicy.COPY] for browser DevTools validation, [SourceFilePolicy.LINK_LOCAL_FILES]
     * for local IDE-oriented paths, or [SourceFilePolicy.DO_NOTHING] to leave sources out.
     *
     * Default: [SourceFilePolicy.LINK_LOCAL_FILES].
     */
    val sourceFilePolicy: Property<SourceFilePolicy>
        get() = wasmConfig.sourceFilePolicy as Property<SourceFilePolicy>
}

open class GdxTeaVMWebExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    teavmConfig: TeaVMConfiguration,
    teavmDevServerConfig: TeaVMDevServerConfiguration
) : GdxTeaVMTargetExtension(teavmConfig) {
    /**
     * JavaScript entry point function name called by the generated web app.
     *
     * Default: `main`.
     */
    open val entryPointName: Property<String> = objects.property(String::class.java).convention("main")

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

    /**
     * Configures TeaVM's persistent development server for this web target.
     *
     * When enabled, the target's existing `gdx_teavm_web_*_run` task delegates to TeaVM's
     * development server instead of performing the normal build-and-serve workflow.
     */
    val devServer: GdxTeaVMDevServerExtension = GdxTeaVMDevServerExtension(objects, teavmDevServerConfig)

    init {
        teavmDevServerConfig.port.convention(serverPort)
        teavmDevServerConfig.processMemory.convention(processMemory)
    }

    /** Configures [devServer]. */
    fun devServer(action: Action<in GdxTeaVMDevServerExtension>) {
        action.execute(devServer)
    }

    internal fun webappDir(): Provider<Directory> {
        return outputSubDir()
    }
}

open class GdxTeaVMNativeTargetExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String,
    val backendName: String
) {
    /**
     * Root directory for generated files for this native target.
     *
     * Default: native target specific.
     */
    val outputDir: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir(outputDirName))

    /**
     * Fully qualified launcher class used as this native target's TeaVM main class.
     *
     * Native targets usually need target-specific launchers.
     *
     * Default: none. This must be set for every native target you build.
     */
    val mainClass: Property<String> = objects.property(String::class.java)

    /**
     * Path inside [outputDir] where TeaVM writes generated C source files.
     *
     * Default: `c/src`.
     */
    val relativePathInOutputDir: Property<String> = objects.property(String::class.java).convention("c/src")

    /**
     * Name of the generated native target.
     *
     * Default: `app`.
     */
    val targetFileName: Property<String> = objects.property(String::class.java).convention(targetFileNameValue)

    /**
     * Directory where native runtime assets and build output support files are prepared.
     *
     * Default: `[outputDir]/c/release`.
     */
    val releasePath: DirectoryProperty = objects.directoryProperty()
        .convention(outputDir.map { it.dir("c/release") })

    /**
     * TeaVM C optimization level used for this native target.
     *
     * Default: `AGGRESSIVE`.
     */
    val optimization: Property<OptimizationLevel> = objects.property(OptimizationLevel::class.java)
        .convention(OptimizationLevel.AGGRESSIVE)

    /**
     * Includes TeaVM C debug information in generated output when supported.
     *
     * Default: `false`.
     */
    val debugInformation: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Enables TeaVM fast global analysis, trading precision for faster native compilation.
     *
     * Default: `false`.
     */
    val fastGlobalAnalysis: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Runs TeaVM C compilation out of the Gradle process when supported by TeaVM.
     *
     * Default: `false`.
     */
    val outOfProcess: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Memory limit in megabytes for out-of-process TeaVM C compilation.
     *
     * Default: `512`.
     */
    val processMemory: Property<Int> = objects.property(Int::class.javaObjectType).convention(512)

    /**
     * Classes TeaVM should preserve from aggressive removal or renaming for this native target.
     *
     * Default: empty list.
     */
    val preservedClasses: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

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
     * Asks TeaVM to generate shorter C file names.
     *
     * Useful for native toolchains with path limits.
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

    internal fun generatedSourcesDir(): Provider<Directory> {
        return outputDir.flatMap { output ->
            relativePathInOutputDir.map { relativePath ->
                output.dir(relativePath)
            }
        }
    }
}

open class GdxTeaVMGlfwExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMNativeTargetExtension(objects, project, outputDirName, targetFileNameValue, "glfw") {
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

    /**
     * CMake cache definitions passed to the generated GLFW configure scripts.
     *
     * Definitions retain their declaration order.
     *
     * Default: empty map.
     */
    val cmakeDefinitions: MapProperty<String, String> =
        objects.mapProperty(String::class.java, String::class.java).convention(emptyMap())

    /** Adds or replaces one CMake cache definition. */
    fun cmakeDefinition(name: String?, value: String?) {
        require(!name.isNullOrBlank()) { "CMake definition name cannot be blank" }
        require(value != null) { "CMake definition value cannot be null" }
        cmakeDefinitions.put(name.trim(), value)
    }
}

open class GdxTeaVMAndroidExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMNativeTargetExtension(objects, project, outputDirName, targetFileNameValue, "android")

open class GdxTeaVMIosExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String
) : GdxTeaVMNativeTargetExtension(objects, project, outputDirName, targetFileNameValue, "ios") {
    /**
     * Generated Xcode project name.
     *
     * Default: `GdxTeaVMIOSSpike`.
     */
    val xcodeProjectName: Property<String> = objects.property(String::class.java).convention("GdxTeaVMIOSSpike")

    /**
     * Directory containing the generated Xcode project and Swift sources.
     *
     * Set this to a source-controlled directory, such as `layout.projectDirectory.dir("xcode")`,
     * when the project should be committed and reused by other developers.
     *
     * Default: `[outputDir]/xcode`.
     */
    val xcodeProjectDir: DirectoryProperty = objects.directoryProperty()
        .convention(outputDir.map { it.dir("xcode") })

    /**
     * Graphics implementation used by the generated Xcode project.
     *
     * Supported values:
     * - `angle`: MetalANGLEKit-backed GLES over Metal.
     * - `gles`: Apple's native OpenGL ES / GLKit path.
     *
     * Default: `angle`.
     */
    val graphicsApi: Property<String> = objects.property(String::class.java).convention(
        project.providers.gradleProperty("gdx.teavm.ios.graphicsApi")
            .orElse("angle")
    )

    /**
     * Xcode scheme used by simulator build tasks.
     *
     * Default: `GdxTeaVMIOSSpike`.
     */
    val xcodeScheme: Property<String> = objects.property(String::class.java).convention("GdxTeaVMIOSSpike")

    /**
     * Xcode build configuration used by simulator build tasks.
     *
     * Default: `Debug`.
     */
    val xcodeConfiguration: Property<String> = objects.property(String::class.java).convention("Debug")

    /**
     * Simulator device name or UDID used by `gdx_teavm_ios_run_simulator`.
     *
     * Default: `iPhone 12 Pro`.
     */
    val simulatorDevice: Property<String> = objects.property(String::class.java).convention("iPhone 12 Pro")

    /**
     * App bundle identifier used by `gdx_teavm_ios_run_simulator`.
     *
     * Default: `com.github.xpenatan.gdxteavm.ios.spike`.
     */
    val bundleIdentifier: Property<String> = objects.property(String::class.java)
        .convention("com.github.xpenatan.gdxteavm.ios.spike")

    /**
     * Derived data directory used by simulator build and run tasks.
     *
     * Default: `build/xcode-derived/ios`.
     */
    val xcodeDerivedDataPath: DirectoryProperty = objects.directoryProperty()
        .convention(project.layout.buildDirectory.dir("xcode-derived/ios"))

    /**
     * Opens Simulator.app when running the simulator task.
     *
     * Default: `true`.
     */
    val openSimulator: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Rewrites the generated Xcode project during Xcode initialization when true.
     *
     * Keep this disabled for normal development so Xcode signing, teams, capabilities, and other
     * manual project settings survive. Enable only when intentionally refreshing the generated
     * Xcode template, or use `gdx_teavm_ios_regenerate_xcode`.
     *
     * Default: `false`.
     */
    val overwriteXcodeProject: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(
        project.providers.gradleProperty("gdx.teavm.ios.xcode.overwrite")
            .map(String::toBoolean)
            .orElse(false)
    )
}
