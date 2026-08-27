package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.teavm.gradle.api.TeaVMExtension
import org.teavm.gradle.api.TeaVMJSConfiguration
import org.teavm.gradle.api.TeaVMWasmGCConfiguration
import java.io.File
import javax.inject.Inject

open class GdxTeaVMExtension @Inject constructor(
    private val objects: ObjectFactory,
    private val project: Project
) {
    private var teavm: TeaVMExtension? = null
    private val declaredTargets = linkedSetOf<GdxTeaVMTarget>()
    private val declaredDefaultTargets = linkedSetOf<GdxTeaVMTarget>()
    private val namedJsTargets = linkedMapOf<String, GdxTeaVMTargetRegistration<GdxTeaVMJsExtension>>()
    private val namedWasmTargets = linkedMapOf<String, GdxTeaVMTargetRegistration<GdxTeaVMWasmExtension>>()
    private val namedGlfwTargets = linkedMapOf<String, GdxTeaVMTargetRegistration<GdxTeaVMGlfwExtension>>()
    private val namedIosTargets = linkedMapOf<String, GdxTeaVMTargetRegistration<GdxTeaVMIosExtension>>()

    constructor(
        objects: ObjectFactory,
        project: Project,
        teavm: TeaVMExtension
    ) : this(objects, project) {
        this.teavm = teavm
    }

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
    val reflectionDebug: Property<Boolean> = objects.property(Boolean::class.javaObjectType).convention(
        project.providers.gradleProperty(REFLECTION_DEBUG)
            .map(String::toBoolean)
            .orElse(false)
    )

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

    /** Optional conventions inherited by every declared JavaScript and Wasm target. */
    val webDefaults: GdxTeaVMWebDefaults = objects.newInstance(GdxTeaVMWebDefaults::class.java)

    /** Optional conventions inherited by every declared TeaVM C/native target. */
    val nativeDefaults: GdxTeaVMNativeDefaults = objects.newInstance(GdxTeaVMNativeDefaults::class.java)

    /**
     * JavaScript web target configuration.
     *
     * Default: output directory `build/dist/js`, target file `app.js`.
     */
    val js: GdxTeaVMJsExtension by lazy {
        objects.newInstance(
            GdxTeaVMJsExtension::class.java,
            project,
            requireTeaVMExtension().getJs(),
            webDefaults,
            "dist/js"
        )
    }

    /**
     * Wasm web target configuration.
     *
     * Default: output directory `build/dist/wasm`, target file `app.wasm`.
     */
    val wasm: GdxTeaVMWasmExtension by lazy {
        objects.newInstance(
            GdxTeaVMWasmExtension::class.java,
            project,
            requireTeaVMExtension().getWasmGC(),
            webDefaults,
            "dist/wasm"
        )
    }

    /**
     * GLFW native target configuration.
     *
     * Default: output directory `build/dist/glfw`, target file `app`.
     */
    val glfw: GdxTeaVMGlfwExtension = objects.newInstance(
        GdxTeaVMGlfwExtension::class.java,
        project,
        "dist/glfw",
        "app",
        nativeDefaults
    )

    /**
     * Android native target configuration.
     *
     * Default: output directory `build/generated/gdx-teavm/android`, target file `app`.
     */
    val android: GdxTeaVMAndroidExtension = objects.newInstance(
        GdxTeaVMAndroidExtension::class.java,
        project,
        "generated/gdx-teavm/android",
        "app",
        nativeDefaults
    )

    /**
     * iOS native target configuration.
     *
     * Default: output directory `build/dist/ios`, target file `app`.
     */
    val ios: GdxTeaVMIosExtension = objects.newInstance(
        GdxTeaVMIosExtension::class.java,
        project,
        "dist/ios",
        "app",
        "xcode-derived/ios",
        nativeDefaults
    )

    /**
     * Configures and declares the JavaScript web target.
     *
     * The plugin creates JavaScript gdx-teavm tasks only when this block is declared.
     */
    fun js(action: Action<in GdxTeaVMJsExtension>) {
        declaredTargets.add(GdxTeaVMTarget.JS)
        declaredDefaultTargets.add(GdxTeaVMTarget.JS)
        action.execute(js)
    }

    /** Configures and declares an independently named JavaScript web target. */
    fun js(name: String, action: Action<in GdxTeaVMJsExtension>) {
        declaredTargets.add(GdxTeaVMTarget.JS)
        action.execute(namedJsTarget(name))
    }

    /**
     * Configures and declares the Wasm web target.
     *
     * The plugin creates Wasm gdx-teavm tasks only when this block is declared.
     */
    fun wasm(action: Action<in GdxTeaVMWasmExtension>) {
        declaredTargets.add(GdxTeaVMTarget.WASM)
        declaredDefaultTargets.add(GdxTeaVMTarget.WASM)
        action.execute(wasm)
    }

    /** Configures and declares an independently named Wasm web target. */
    fun wasm(name: String, action: Action<in GdxTeaVMWasmExtension>) {
        declaredTargets.add(GdxTeaVMTarget.WASM)
        action.execute(namedWasmTarget(name))
    }

    /**
     * Configures and declares the GLFW native target.
     *
     * The plugin creates GLFW gdx-teavm tasks only when this block is declared.
     */
    fun glfw(action: Action<in GdxTeaVMGlfwExtension>) {
        declaredTargets.add(GdxTeaVMTarget.GLFW)
        declaredDefaultTargets.add(GdxTeaVMTarget.GLFW)
        action.execute(glfw)
    }

    /** Configures and declares an independently named GLFW TeaVM C target. */
    fun glfw(name: String, action: Action<in GdxTeaVMGlfwExtension>) {
        declaredTargets.add(GdxTeaVMTarget.GLFW)
        action.execute(namedGlfwTarget(name))
    }

    /**
     * Configures and declares the Android native target.
     *
     * The plugin creates Android gdx-teavm tasks only when this block is declared.
     */
    fun android(action: Action<in GdxTeaVMAndroidExtension>) {
        declaredTargets.add(GdxTeaVMTarget.ANDROID)
        declaredDefaultTargets.add(GdxTeaVMTarget.ANDROID)
        action.execute(android)
    }

    /**
     * Configures and declares the experimental iOS native target.
     *
     * The plugin creates iOS gdx-teavm tasks only when this block is declared.
     */
    fun ios(action: Action<in GdxTeaVMIosExtension>) {
        declaredTargets.add(GdxTeaVMTarget.IOS)
        declaredDefaultTargets.add(GdxTeaVMTarget.IOS)
        action.execute(ios)
    }

    /** Configures and declares an independently named experimental iOS TeaVM C target. */
    fun ios(name: String, action: Action<in GdxTeaVMIosExtension>) {
        declaredTargets.add(GdxTeaVMTarget.IOS)
        action.execute(namedIosTarget(name))
    }

    /** Configures optional conventions inherited by JavaScript and Wasm targets. */
    fun webDefaults(action: Action<in GdxTeaVMWebDefaults>) {
        action.execute(webDefaults)
    }

    /** Configures optional conventions inherited by TeaVM C/native targets. */
    fun nativeDefaults(action: Action<in GdxTeaVMNativeDefaults>) {
        action.execute(nativeDefaults)
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

    private fun namedJsTarget(name: String): GdxTeaVMJsExtension {
        val identity = namedTargetIdentity(name, namedJsTargets, "JavaScript")
        identity.existing?.let { return it.target }
        val config = objects.newInstance(TeaVMJSConfiguration::class.java)
        val target = objects.newInstance(
            GdxTeaVMJsExtension::class.java,
            project,
            config,
            webDefaults,
            "dist/js/${identity.segment}"
        )
        namedJsTargets[identity.segment] = GdxTeaVMTargetRegistration(identity.name, identity.segment, target)
        return target
    }

    private fun namedWasmTarget(name: String): GdxTeaVMWasmExtension {
        val identity = namedTargetIdentity(name, namedWasmTargets, "Wasm")
        identity.existing?.let { return it.target }
        val config = objects.newInstance(TeaVMWasmGCConfiguration::class.java)
        val target = objects.newInstance(
            GdxTeaVMWasmExtension::class.java,
            project,
            config,
            webDefaults,
            "dist/wasm/${identity.segment}"
        )
        namedWasmTargets[identity.segment] = GdxTeaVMTargetRegistration(identity.name, identity.segment, target)
        return target
    }

    private fun namedGlfwTarget(name: String): GdxTeaVMGlfwExtension {
        val identity = namedTargetIdentity(name, namedGlfwTargets, "GLFW")
        identity.existing?.let { return it.target }
        val target = objects.newInstance(
            GdxTeaVMGlfwExtension::class.java,
            project,
            "dist/glfw/${identity.segment}",
            "app",
            nativeDefaults
        )
        namedGlfwTargets[identity.segment] = GdxTeaVMTargetRegistration(identity.name, identity.segment, target)
        return target
    }

    private fun namedIosTarget(name: String): GdxTeaVMIosExtension {
        val identity = namedTargetIdentity(name, namedIosTargets, "iOS")
        identity.existing?.let { return it.target }
        val target = objects.newInstance(
            GdxTeaVMIosExtension::class.java,
            project,
            "dist/ios/${identity.segment}",
            "app",
            "xcode-derived/ios/${identity.segment}",
            nativeDefaults
        )
        namedIosTargets[identity.segment] = GdxTeaVMTargetRegistration(identity.name, identity.segment, target)
        return target
    }

    private fun <T> namedTargetIdentity(
        name: String,
        targets: Map<String, GdxTeaVMTargetRegistration<T>>,
        targetType: String
    ): NamedTargetIdentity<T> {
        val trimmedName = name.trim()
        val segment = normalizeGdxTeaVMTargetName(trimmedName)
        val existing = targets[segment]
        if(existing != null && existing.name != trimmedName) {
            throw IllegalArgumentException(
                "$targetType target names '${existing.name}' and '$trimmedName' both normalize to '$segment'"
            )
        }
        return NamedTargetIdentity(trimmedName, segment, existing)
    }

    internal fun jsTargets(): List<GdxTeaVMTargetRegistration<GdxTeaVMJsExtension>> = buildList {
        if(isDefaultTargetDeclared(GdxTeaVMTarget.JS)) {
            add(GdxTeaVMTargetRegistration(null, null, js))
        }
        addAll(namedJsTargets.values)
    }

    internal fun wasmTargets(): List<GdxTeaVMTargetRegistration<GdxTeaVMWasmExtension>> = buildList {
        if(isDefaultTargetDeclared(GdxTeaVMTarget.WASM)) {
            add(GdxTeaVMTargetRegistration(null, null, wasm))
        }
        addAll(namedWasmTargets.values)
    }

    internal fun glfwTargets(): List<GdxTeaVMTargetRegistration<GdxTeaVMGlfwExtension>> = buildList {
        if(isDefaultTargetDeclared(GdxTeaVMTarget.GLFW)) {
            add(GdxTeaVMTargetRegistration(null, null, glfw))
        }
        addAll(namedGlfwTargets.values)
    }

    internal fun iosTargets(): List<GdxTeaVMTargetRegistration<GdxTeaVMIosExtension>> = buildList {
        if(isDefaultTargetDeclared(GdxTeaVMTarget.IOS)) {
            add(GdxTeaVMTargetRegistration(null, null, ios))
        }
        addAll(namedIosTargets.values)
    }

    private fun requireTeaVMExtension(): TeaVMExtension {
        return teavm ?: throw IllegalStateException(
            "This gdx-teavm project is configured for Android-only generation. " +
                "Move js/wasm/glfw/ios targets to a Java project or apply gdx-teavm to a non-Android module."
        )
    }

    private data class NamedTargetIdentity<T>(
        val name: String,
        val segment: String,
        val existing: GdxTeaVMTargetRegistration<T>?
    )

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
                properties[GENERATE_INDEX_HTML] = web.generateIndexHtml.get().toString()
                properties[ENTRY_POINT_NAME] = web.entryPointName.get()
                properties[MAIN_CLASS_ARGS] = web.mainClassArgs.get()
                properties[HTML_TITLE] = web.htmlTitle.get()
                properties[HTML_WIDTH] = web.htmlWidth.get().toString()
                properties[HTML_HEIGHT] = web.htmlHeight.get().toString()
                properties[COPY_ASSETS] = web.copyAssets.get().toString()
                properties[LOGO_PATH] = web.logoPath.get()
                properties[COPY_LOADING_ASSET] = web.copyLoadingAsset.get().toString()
            }
        }
    }

    internal fun toNativeProperties(project: Project, native: GdxTeaVMNativeTargetExtension): Provider<Map<String, String>> {
        return project.provider {
            linkedMapOf<String, String>().also { properties ->
                properties[NATIVE_BACKEND] = native.backendName
                properties[NATIVE_OUTPUT_ROOT] = native.outputDir.get().asFile.absolutePath
                properties[NATIVE_GENERATED_SOURCES] = native.generatedSourcesDir().get().asFile.absolutePath
                properties[NATIVE_RELEASE_PATH] = native.releasePath.get().asFile.absolutePath
                if(native is GdxTeaVMGlfwExtension) {
                    properties[NATIVE_BUILD_TYPE] = native.buildType.get().backendValue
                    properties[NATIVE_BUILD_EXECUTABLE] = native.buildExecutable.get().toString()
                    properties[NATIVE_RUN_EXECUTABLE] = native.runExecutable.get().toString()
                    properties[NATIVE_CONSOLE_LOG] = native.consoleLog.get().toString()
                    native.cmakeDefinitions.get().entries.forEachIndexed { index, definition ->
                        val definitionKey = "$NATIVE_CMAKE_DEFINITIONS.${index.toString().padStart(8, '0')}"
                        properties["$definitionKey.name"] = definition.key
                        properties["$definitionKey.value"] = definition.value
                    }
                }
                if(native is GdxTeaVMIosExtension) {
                    properties[IOS_XCODE_PROJECT_DIR] = native.xcodeProjectDir.get().asFile.absolutePath
                    properties[IOS_GRAPHICS_API] = normalizeIosGraphicsApi(native.graphicsApi.get())
                }
            }
        }
    }

    internal fun selectedNativeTargetOrNull(project: Project): GdxTeaVMNativeTargetExtension? {
        return nativeTargetForBackendName(selectedNativeBackendName(project))
    }

    internal fun nativeTargetForBackendName(backendName: String?): GdxTeaVMNativeTargetExtension? {
        return when(backendName) {
            "glfw" -> if(isDefaultTargetDeclared(GdxTeaVMTarget.GLFW)) glfw else null
            "android" -> if(isDefaultTargetDeclared(GdxTeaVMTarget.ANDROID)) android else null
            "ios" -> if(isDefaultTargetDeclared(GdxTeaVMTarget.IOS)) ios else null
            else -> null
        }
    }

    internal fun defaultNativeTargetOrNull(): GdxTeaVMNativeTargetExtension? {
        var firstNativeTarget: GdxTeaVMNativeTargetExtension? = null
        for(target in declaredDefaultTargets) {
            val nativeTarget = nativeTargetForDeclaredTarget(target) ?: continue
            if(firstNativeTarget == null) {
                firstNativeTarget = nativeTarget
            }
            if(nativeTarget.mainClass.isPresent) {
                return nativeTarget
            }
        }
        return firstNativeTarget
    }

    private fun nativeTargetForDeclaredTarget(target: GdxTeaVMTarget): GdxTeaVMNativeTargetExtension? {
        return when(target) {
            GdxTeaVMTarget.GLFW -> glfw
            GdxTeaVMTarget.ANDROID -> android
            GdxTeaVMTarget.IOS -> ios
            else -> null
        }
    }

    internal fun isTargetDeclared(target: GdxTeaVMTarget): Boolean {
        return declaredTargets.contains(target)
    }

    internal fun isDefaultTargetDeclared(target: GdxTeaVMTarget): Boolean {
        return declaredDefaultTargets.contains(target)
    }

    internal fun isWebTargetDeclared(): Boolean {
        return isTargetDeclared(GdxTeaVMTarget.JS) || isTargetDeclared(GdxTeaVMTarget.WASM)
    }

    internal fun selectedNativeBackendName(project: Project): String? {
        val requestedTasks = project.gradle.startParameter.taskNames
            .map { it.lowercase() }
            .map { it.substringAfterLast(':') }
        val glfwRequested = requestedTasks.any { it in DEFAULT_GLFW_TASK_NAMES }
        val androidRequested = requestedTasks.any { it in DEFAULT_ANDROID_TASK_NAMES }
        val iosRequested = requestedTasks.any { it in DEFAULT_IOS_TASK_NAMES }
        val requestedNativeTargets = listOf(glfwRequested, androidRequested, iosRequested).count { it }
        if(requestedNativeTargets > 1) {
            throw IllegalStateException("Only one gdx-teavm native backend can be selected in a single Gradle invocation")
        }
        return when {
            glfwRequested -> "glfw"
            androidRequested -> "android"
            iosRequested -> "ios"
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

    internal fun normalizeIosGraphicsApi(value: String): String {
        return when(value.trim().lowercase()) {
            "angle", "metalangle", "metal-angle" -> "angle"
            "gles", "opengles", "open-gles", "opengl-es" -> "gles"
            else -> throw IllegalArgumentException(
                "Unsupported iOS graphics API '$value'. Supported values are 'angle' and 'gles'."
            )
        }
    }

    private companion object {
        val DEFAULT_GLFW_TASK_NAMES = setOf(
            "gdx_teavm_glfw_generate",
            "gdx_teavm_glfw_build",
            "gdx_teavm_glfw_run"
        )
        val DEFAULT_ANDROID_TASK_NAMES = setOf("gdx_teavm_android_generate")
        val DEFAULT_IOS_TASK_NAMES = setOf(
            "gdx_teavm_ios_generate",
            "gdx_teavm_ios_prepare_angle",
            "gdx_teavm_ios_init_xcode",
            "gdx_teavm_ios_regenerate_xcode",
            "gdx_teavm_ios_open_xcode",
            "gdx_teavm_ios_build_simulator",
            "gdx_teavm_ios_run_simulator"
        )
        const val WEBAPP_ENABLED = "gdx.teavm.webapp.enabled"
        const val GENERATE_INDEX_HTML = "gdx.teavm.webapp.generateIndexHtml"
        const val ENTRY_POINT_NAME = "gdx.teavm.entryPointName"
        const val MAIN_CLASS_ARGS = "gdx.teavm.mainClassArgs"
        const val HTML_TITLE = "gdx.teavm.html.title"
        const val HTML_WIDTH = "gdx.teavm.html.width"
        const val HTML_HEIGHT = "gdx.teavm.html.height"
        const val COPY_ASSETS = "gdx.teavm.copyAssets"
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
        const val NATIVE_CMAKE_DEFINITIONS = "gdx.teavm.native.cmakeDefinitions"
        const val IOS_XCODE_PROJECT_DIR = "gdx.teavm.ios.xcode.projectDir"
        const val IOS_GRAPHICS_API = "gdx.teavm.ios.graphicsApi"
    }
}

internal enum class GdxTeaVMTarget {
    JS,
    WASM,
    GLFW,
    ANDROID,
    IOS
}
