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
import org.teavm.gradle.api.JSModuleType
import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy
import org.teavm.gradle.api.TeaVMConfiguration
import org.teavm.gradle.api.TeaVMDevServerConfiguration
import org.teavm.gradle.api.TeaVMJSConfiguration
import org.teavm.gradle.api.TeaVMWasmGCConfiguration
import org.teavm.gradle.api.WasmDebugInfoLevel
import org.teavm.gradle.api.WasmDebugInfoLocation
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
open class GdxTeaVMTargetExtension internal constructor(
    internal val teavmConfig: TeaVMConfiguration,
    defaults: GdxTeaVMWebDefaults
) {
    init {
        mainClass.convention(defaults.mainClass)
        relativePathInOutputDir.convention(defaults.relativePathInOutputDir.orElse("webapp"))
        optimization.convention(defaults.optimization.orElse(OptimizationLevel.BALANCED))
        debugInformation.convention(defaults.debugInformation.orElse(false))
        fastGlobalAnalysis.convention(defaults.fastGlobalAnalysis.orElse(false))
        outOfProcess.convention(defaults.outOfProcess.orElse(true))
        processMemory.convention(defaults.processMemory.orElse(1024))
        preservedClasses.convention(defaults.preservedClasses.orElse(emptyList()))
        teavmConfig.properties.convention(emptyMap())
        teavmConfig.skip.convention(false)
    }

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
     * Default: JS `BALANCED`, Wasm `BALANCED`.
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
     * Default: `true`.
     */
    val outOfProcess: Property<Boolean>
        get() = teavmConfig.outOfProcess as Property<Boolean>

    /**
     * Memory limit in megabytes for out-of-process TeaVM compilation.
     *
     * Default: `1024`.
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
    internal val jsConfig: TeaVMJSConfiguration,
    defaults: GdxTeaVMWebDefaults,
    outputDirName: String
) : GdxTeaVMWebExtension(objects, project, jsConfig, jsConfig.devServer, defaults) {
    init {
        outputDir.convention(project.layout.buildDirectory.dir(outputDirName))
        targetFileName.convention("app.js")
        entryPointName.convention(defaults.entryPointName.orElse("main"))
        obfuscated.convention(defaults.obfuscated.orElse(true))
        strict.convention(defaults.strict.orElse(false))
        sourceMap.convention(defaults.sourceMap.orElse(false))
        sourceFilePolicy.convention(defaults.sourceFilePolicy.orElse(SourceFilePolicy.LINK_LOCAL_FILES))
        jsConfig.moduleType.convention(JSModuleType.UMD)
        jsConfig.addedToWebApp.convention(false)
        jsConfig.devServer.stackDeobfuscated.convention(false)
        jsConfig.devServer.indicator.convention(false)
        jsConfig.devServer.resourceRoots.convention(emptyList())
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
    internal val wasmConfig: TeaVMWasmGCConfiguration,
    defaults: GdxTeaVMWebDefaults,
    outputDirName: String
) : GdxTeaVMWebExtension(objects, project, wasmConfig, wasmConfig.devServer, defaults) {
    init {
        outputDir.convention(project.layout.buildDirectory.dir(outputDirName))
        targetFileName.convention("app.wasm")
        obfuscated.convention(defaults.obfuscated.orElse(true))
        strict.convention(defaults.strict.orElse(false))
        copyRuntime.convention(true)
        modularRuntime.convention(false)
        sourceMap.convention(defaults.sourceMap.orElse(false))
        sourceFilePolicy.convention(defaults.sourceFilePolicy.orElse(SourceFilePolicy.LINK_LOCAL_FILES))
        wasmConfig.addedToWebApp.convention(false)
        wasmConfig.disassembly.convention(false)
        wasmConfig.debugInfoLocation.convention(WasmDebugInfoLocation.EXTERNAL)
        wasmConfig.debugInfoLevel.convention(WasmDebugInfoLevel.DEOBFUSCATION)
        wasmConfig.minDirectBuffersSize.convention(2)
        @Suppress("DEPRECATION")
        wasmConfig.maxDirectBuffersSize.convention(32)
        @Suppress("DEPRECATION")
        wasmConfig.importedWasmMemory.convention(false)
        wasmConfig.sharedBuffer.convention(false)
        wasmConfig.emscripten.enabled.convention(false)
        wasmConfig.emscripten.compilerArgs.convention(emptyList())
        wasmConfig.emscripten.exportedFunctions.convention(emptyList())
        wasmConfig.devServer.resourceRoots.convention(emptyList())
    }

    /**
     * JavaScript export function name called by the generated Wasm web app.
     *
     * Default: `main`.
     */
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
    teavmDevServerConfig: TeaVMDevServerConfiguration,
    defaults: GdxTeaVMWebDefaults
) : GdxTeaVMTargetExtension(teavmConfig, defaults) {
    /**
     * JavaScript entry point function name called by the generated web app.
     *
     * Default: `main`.
     */
    open val entryPointName: Property<String> = objects.property(String::class.java)
        .convention(defaults.entryPointName.orElse("main"))

    /**
     * Arguments passed by the generated web app to the TeaVM entry point.
     *
     * Use JavaScript array element syntax without the surrounding brackets, for example `"foo", "bar"`.
     *
     * Default: empty string.
     */
    val mainClassArgs: Property<String> = objects.property(String::class.java)
        .convention(defaults.mainClassArgs.orElse(""))

    /**
     * Enables generation of the gdx-teavm web app files around the TeaVM output.
     *
     * When enabled, the backend writes files such as `index.html`, `WEB-INF/web.xml`, assets,
     * preload manifest, and support scripts.
     *
     * Default: `true`.
     */
    val webappEnabled: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.webappEnabled.orElse(true))

    /**
     * Generates the web app `index.html` entry page when true.
     *
     * When false, an existing `index.html` is left untouched. Other web app files and TeaVM output are still
     * generated according to their own settings.
     *
     * Default: `true`.
     */
    val generateIndexHtml: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.generateIndexHtml.orElse(true))

    /**
     * Browser document title used by the generated `index.html`.
     *
     * Default: `gdx-teavm`.
     */
    val htmlTitle: Property<String> = objects.property(String::class.java)
        .convention(defaults.htmlTitle.orElse("gdx-teavm"))

    /**
     * Initial canvas width written to the generated `index.html`.
     *
     * Default: `800`.
     */
    val htmlWidth: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(defaults.htmlWidth.orElse(800))

    /**
     * Initial canvas height written to the generated `index.html`.
     *
     * Default: `600`.
     */
    val htmlHeight: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(defaults.htmlHeight.orElse(600))

    /**
     * Copies configured assets, contributed classpath assets, and support scripts into the generated web app.
     *
     * This does not control the loading logo; use [copyLoadingAsset] for that resource.
     *
     * Default: `true`.
     */
    val copyAssets: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.copyAssets.orElse(true))

    /**
     * Default startup-logo asset path compiled into `WebPreloadApplicationListener`.
     *
     * When [copyLoadingAsset] is enabled, the same path is copied from the build classpath into the web app assets
     * folder. A custom preload listener can override its public `startupLogo` field at runtime.
     *
     * Default: `startup-logo.png`.
     */
    val logoPath: Property<String> = objects.property(String::class.java)
        .convention(defaults.logoPath.orElse("startup-logo.png"))

    /**
     * Copies [logoPath] into the generated web app when true.
     *
     * Default: `true`.
     */
    val copyLoadingAsset: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.copyLoadingAsset.orElse(true))

    /**
     * Port used by this target's plugin web run task.
     *
     * Default: Gradle property `teavmPluginPort` when present, otherwise `8080`.
     */
    val serverPort: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(
            defaults.serverPort.orElse(
                project.providers.gradleProperty("teavmPluginPort").map(String::toInt).orElse(8080)
            )
        )

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
    val backendName: String,
    defaults: GdxTeaVMNativeDefaults
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
        .convention(defaults.mainClass)

    /**
     * Path inside [outputDir] where TeaVM writes generated C source files.
     *
     * Default: `c/src`.
     */
    val relativePathInOutputDir: Property<String> = objects.property(String::class.java)
        .convention(defaults.relativePathInOutputDir.orElse("c/src"))

    /**
     * Name of the generated native target.
     *
     * Default: `app`.
     */
    val targetFileName: Property<String> = objects.property(String::class.java)
        .convention(defaults.targetFileName.orElse(targetFileNameValue))

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
     * Default: `BALANCED`.
     */
    val optimization: Property<OptimizationLevel> = objects.property(OptimizationLevel::class.java)
        .convention(defaults.optimization.orElse(OptimizationLevel.BALANCED))

    /**
     * Includes TeaVM C debug information in generated output when supported.
     *
     * Default: `false`.
     */
    val debugInformation: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.debugInformation.orElse(false))

    /**
     * Enables TeaVM fast global analysis, trading precision for faster native compilation.
     *
     * Default: `false`.
     */
    val fastGlobalAnalysis: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.fastGlobalAnalysis.orElse(false))

    /**
     * Runs TeaVM C compilation out of the Gradle process when supported by TeaVM.
     *
     * Default: `false`.
     */
    val outOfProcess: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.outOfProcess.orElse(false))

    /**
     * Memory limit in megabytes for out-of-process TeaVM C compilation.
     *
     * Default: `512`.
     */
    val processMemory: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(defaults.processMemory.orElse(512))

    /**
     * Classes TeaVM should preserve from aggressive removal or renaming for this native target.
     *
     * Default: empty list.
     */
    val preservedClasses: ListProperty<String> = objects.listProperty(String::class.java)
        .convention(defaults.preservedClasses.orElse(emptyList()))

    /**
     * Initial native heap size in megabytes.
     *
     * Default: `4`.
     */
    val minHeapSizeMb: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(defaults.minHeapSizeMb.orElse(4))

    /**
     * Maximum native heap size in megabytes.
     *
     * Default: `128`.
     */
    val maxHeapSizeMb: Property<Int> = objects.property(Int::class.javaObjectType)
        .convention(defaults.maxHeapSizeMb.orElse(128))

    /**
     * Enables TeaVM heap dump support for native output when supported by TeaVM.
     *
     * Default: `false`.
     */
    val heapDump: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.heapDump.orElse(false))

    /**
     * Asks TeaVM to generate shorter C file names.
     *
     * Useful for native toolchains with path limits.
     *
     * Default: `true`.
     */
    val shortFileNames: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.shortFileNames.orElse(true))

    /**
     * Obfuscates generated native C symbols when true.
     *
     * Default: `true`.
     */
    val obfuscated: Property<Boolean> = objects.property(Boolean::class.javaObjectType)
        .convention(defaults.obfuscated.orElse(true))

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
    targetFileNameValue: String,
    defaults: GdxTeaVMNativeDefaults
) : GdxTeaVMNativeTargetExtension(objects, project, outputDirName, targetFileNameValue, "glfw", defaults) {
    /**
     * Native build type used by generated GLFW build scripts.
     *
     * Default: [GlfwBuildType.DEBUG].
     */
    val buildType: Property<GlfwBuildType> = objects.property(GlfwBuildType::class.java)
        .convention(GlfwBuildType.DEBUG)

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
    targetFileNameValue: String,
    defaults: GdxTeaVMNativeDefaults
) : GdxTeaVMNativeTargetExtension(objects, project, outputDirName, targetFileNameValue, "android", defaults)

open class GdxTeaVMIosExtension @Inject constructor(
    objects: ObjectFactory,
    project: Project,
    outputDirName: String,
    targetFileNameValue: String,
    xcodeDerivedDataDirName: String,
    defaults: GdxTeaVMNativeDefaults
) : GdxTeaVMNativeTargetExtension(objects, project, outputDirName, targetFileNameValue, "ios", defaults) {
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
        .convention(project.layout.buildDirectory.dir(xcodeDerivedDataDirName))

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
