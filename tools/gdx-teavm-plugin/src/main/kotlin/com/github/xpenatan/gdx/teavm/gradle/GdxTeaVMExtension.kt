package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import java.io.File
import javax.inject.Inject

open class GdxTeaVMExtension @Inject constructor(
    objects: ObjectFactory,
    private val project: Project
) {
    private val declaredTargets = linkedSetOf<GdxTeaVMTarget>()

    /**
     * Enables gdx-teavm reflection metadata generation.
     *
     * Keep this enabled when code or libraries use reflection at runtime.
     *
     * Default: `true`.
     */
    val reflectionEnabled: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Adds the default reflection configuration required by common libGDX runtime types.
     *
     * Default: `true`.
     */
    val reflectionDefaults: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Scans reachable classes and configured packages for reflection metadata when enabled.
     *
     * Default: `true`.
     */
    val reflectionScan: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(true)

    /**
     * Prints extra reflection metadata diagnostics during TeaVM generation when enabled.
     *
     * Default: `false`.
     */
    val reflectionDebug: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(false)

    /**
     * Local files or directories copied as libGDX internal assets.
     *
     * Directories are copied recursively and included in the generated preload manifest.
     *
     * Default: empty file collection.
     */
    val assets: ConfigurableFileCollection = project.files()

    /**
     * Classpath resource roots copied as libGDX classpath assets.
     *
     * Use this for bundled resources that should be loaded with `FileType.Classpath`.
     *
     * Default: empty list.
     */
    val classpathAssets: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    /**
     * Reflection class names or package patterns to preserve.
     *
     * Values are passed to the gdx-teavm reflection support during TeaVM generation.
     *
     * Default: empty list.
     */
    val reflection: ListProperty<String> = objects.listProperty(String::class.java).convention(emptyList())

    /**
     * JavaScript web target configuration.
     *
     * Default: output directory `build/dist/web`, target file `app.js`.
     */
    val js: GdxTeaVMJsExtension = objects.newInstance(
        GdxTeaVMJsExtension::class.java,
        project,
        "dist/web",
        "app.js"
    )

    /**
     * Wasm web target configuration.
     *
     * Default: output directory `build/dist/wasm`, target file `app.wasm`.
     */
    val wasm: GdxTeaVMWasmExtension = objects.newInstance(
        GdxTeaVMWasmExtension::class.java,
        project,
        "dist/wasm",
        "app.wasm"
    )

    /**
     * GLFW native target configuration.
     *
     * Default: output directory `build/dist/glfw`, target file `app`.
     */
    val glfw: GdxTeaVMGlfwExtension = objects.newInstance(
        GdxTeaVMGlfwExtension::class.java,
        project,
        "dist/glfw",
        "app"
    )

    /**
     * PSP native target configuration.
     *
     * Default: output directory `build/dist/psp`, target file `app`.
     */
    val psp: GdxTeaVMPspExtension = objects.newInstance(
        GdxTeaVMPspExtension::class.java,
        project,
        "dist/psp",
        "app"
    )

    /**
     * Configures and declares the JavaScript web target.
     *
     * The plugin creates JavaScript gdx-teavm tasks only when this block is declared.
     */
    fun js(action: Action<in GdxTeaVMJsExtension>) {
        declaredTargets.add(GdxTeaVMTarget.JS)
        action.execute(js)
    }

    /**
     * Configures and declares the Wasm web target.
     *
     * The plugin creates Wasm gdx-teavm tasks only when this block is declared.
     */
    fun wasm(action: Action<in GdxTeaVMWasmExtension>) {
        declaredTargets.add(GdxTeaVMTarget.WASM)
        action.execute(wasm)
    }

    /**
     * Configures and declares the GLFW native target.
     *
     * The plugin creates GLFW gdx-teavm tasks only when this block is declared.
     */
    fun glfw(action: Action<in GdxTeaVMGlfwExtension>) {
        declaredTargets.add(GdxTeaVMTarget.GLFW)
        action.execute(glfw)
    }

    /**
     * Configures and declares the PSP native target.
     *
     * The plugin creates PSP gdx-teavm tasks only when this block is declared.
     */
    fun psp(action: Action<in GdxTeaVMPspExtension>) {
        declaredTargets.add(GdxTeaVMTarget.PSP)
        action.execute(psp)
    }

    /** Adds local asset files or directories to [assets]. */
    fun assets(vararg paths: Any) {
        assets.from(*paths)
    }

    /** Adds classpath resource roots to [classpathAssets]. */
    fun classpathAssets(vararg paths: String) {
        classpathAssets.addAll(paths.toList())
    }

    /** Adds reflection class names or package patterns to [reflection]. */
    fun reflection(vararg patterns: String) {
        reflection.addAll(patterns.toList())
    }

    internal fun toGlobalProperties(project: Project): Provider<Map<String, String>> {
        return project.provider {
            linkedMapOf<String, String>().also { properties ->
                properties[REFLECTION_ENABLED] = reflectionEnabled.get().toString()
                properties[REFLECTION_DEFAULTS] = reflectionDefaults.get().toString()
                properties[REFLECTION_SCAN] = reflectionScan.get().toString()
                properties[REFLECTION_DEBUG] = reflectionDebug.get().toString()
                putPathList(properties, ASSETS, assets.files.map(File::getAbsolutePath))
                putTokenList(properties, CLASSPATH_ASSETS, classpathAssets.get())
                putTokenList(properties, REFLECTION, reflection.get())
            }
        }
    }

    internal fun toWebProperties(project: Project, web: GdxTeaVMWebExtension): Provider<Map<String, String>> {
        return project.provider {
            linkedMapOf<String, String>().also { properties ->
                properties[WEBAPP_ENABLED] = web.webappEnabled.get().toString()
                properties[ENTRY_POINT_NAME] = web.entryPointName.get()
                properties[MAIN_CLASS_ARGS] = web.mainClassArgs.get()
                properties[HTML_TITLE] = web.htmlTitle.get()
                properties[HTML_WIDTH] = web.htmlWidth.get().toString()
                properties[HTML_HEIGHT] = web.htmlHeight.get().toString()
                properties[LOGO_PATH] = web.logoPath.get()
                properties[COPY_LOADING_ASSET] = web.copyLoadingAsset.get().toString()
            }
        }
    }

    internal fun toNativeProperties(project: Project, native: GdxTeaVMNativeExtension): Provider<Map<String, String>> {
        return project.provider {
            linkedMapOf<String, String>().also { properties ->
                properties[NATIVE_BACKEND] = native.backendName
                properties[NATIVE_OUTPUT_ROOT] = native.outputDir.get().asFile.absolutePath
                properties[NATIVE_GENERATED_SOURCES] = native.generatedSourcesDir().get().asFile.absolutePath
                properties[NATIVE_RELEASE_PATH] = native.releasePath.get().asFile.absolutePath
                if(native is GdxTeaVMGlfwExtension) {
                    properties[NATIVE_BUILD_TYPE] = native.buildType.get()
                    properties[NATIVE_BUILD_EXECUTABLE] = native.buildExecutable.get().toString()
                    properties[NATIVE_RUN_EXECUTABLE] = native.runExecutable.get().toString()
                    properties[NATIVE_CONSOLE_LOG] = native.consoleLog.get().toString()
                }
                if(native is GdxTeaVMPspExtension) {
                    properties[PSP_DEBUG_MEMORY] = native.debugMemory.get().toString()
                    properties[PSP_AUTO_EXECUTE_BUILD] = native.autoExecuteBuild.get().toString()
                }
            }
        }
    }

    internal fun selectedNativeTargetOrNull(project: Project): GdxTeaVMNativeExtension? {
        return when(selectedNativeBackendName(project)) {
            "glfw" -> if(isTargetDeclared(GdxTeaVMTarget.GLFW)) glfw else null
            "psp" -> if(isTargetDeclared(GdxTeaVMTarget.PSP)) psp else null
            else -> null
        }
    }

    internal fun isTargetDeclared(target: GdxTeaVMTarget): Boolean {
        return declaredTargets.contains(target)
    }

    internal fun isWebTargetDeclared(): Boolean {
        return isTargetDeclared(GdxTeaVMTarget.JS) || isTargetDeclared(GdxTeaVMTarget.WASM)
    }

    internal fun selectedNativeBackendName(project: Project): String? {
        val requestedTasks = project.gradle.startParameter.taskNames
            .map { it.substringAfterLast(':').lowercase() }
        val glfwRequested = requestedTasks.any { it.contains("glfw") }
        val pspRequested = requestedTasks.any { it.contains("psp") }
        if(glfwRequested && pspRequested) {
            throw IllegalStateException("Only one gdx-teavm native backend can be selected in a single Gradle invocation")
        }
        return when {
            glfwRequested -> "glfw"
            pspRequested -> "psp"
            else -> null
        }
    }

    private fun putPathList(properties: MutableMap<String, String>, key: String, values: Iterable<String>) {
        val filtered = values.map(String::trim).filter(String::isNotEmpty)
        if(filtered.isNotEmpty()) {
            properties[key] = filtered.joinToString(File.pathSeparator)
        }
    }

    private fun putTokenList(properties: MutableMap<String, String>, key: String, values: Iterable<String>) {
        val filtered = values.map(String::trim).filter(String::isNotEmpty)
        if(filtered.isNotEmpty()) {
            properties[key] = filtered.joinToString(",")
        }
    }

    private companion object {
        const val WEBAPP_ENABLED = "gdx.teavm.webapp.enabled"
        const val ENTRY_POINT_NAME = "gdx.teavm.entryPointName"
        const val MAIN_CLASS_ARGS = "gdx.teavm.mainClassArgs"
        const val HTML_TITLE = "gdx.teavm.html.title"
        const val HTML_WIDTH = "gdx.teavm.html.width"
        const val HTML_HEIGHT = "gdx.teavm.html.height"
        const val LOGO_PATH = "gdx.teavm.logoPath"
        const val COPY_LOADING_ASSET = "gdx.teavm.copyLoadingAsset"
        const val ASSETS = "gdx.teavm.assets"
        const val CLASSPATH_ASSETS = "gdx.teavm.classpathAssets"
        const val REFLECTION_ENABLED = "gdx.teavm.reflection.enabled"
        const val REFLECTION_DEFAULTS = "gdx.teavm.reflection.defaults"
        const val REFLECTION_SCAN = "gdx.teavm.reflection.scan"
        const val REFLECTION = "gdx.teavm.reflection"
        const val REFLECTION_DEBUG = "gdx.teavm.reflection.debug"
        const val NATIVE_BACKEND = "gdx.teavm.native.backend"
        const val NATIVE_OUTPUT_ROOT = "gdx.teavm.native.outputRoot"
        const val NATIVE_RELEASE_PATH = "gdx.teavm.native.releasePath"
        const val NATIVE_GENERATED_SOURCES = "gdx.teavm.native.generatedSources"
        const val NATIVE_BUILD_TYPE = "gdx.teavm.native.buildType"
        const val NATIVE_BUILD_EXECUTABLE = "gdx.teavm.native.buildExecutable"
        const val NATIVE_RUN_EXECUTABLE = "gdx.teavm.native.runExecutable"
        const val NATIVE_CONSOLE_LOG = "gdx.teavm.native.consoleLog"
        const val PSP_DEBUG_MEMORY = "gdx.teavm.psp.debugMemory"
        const val PSP_AUTO_EXECUTE_BUILD = "gdx.teavm.psp.autoExecuteBuild"
    }
}

internal enum class GdxTeaVMTarget {
    JS,
    WASM,
    GLFW,
    PSP
}
