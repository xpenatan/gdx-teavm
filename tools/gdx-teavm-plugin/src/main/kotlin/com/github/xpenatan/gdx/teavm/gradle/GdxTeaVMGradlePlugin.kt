package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.teavm.gradle.TeaVMPlugin
import org.teavm.gradle.api.TeaVMConfiguration
import org.teavm.gradle.api.TeaVMCommonConfiguration
import org.teavm.gradle.api.TeaVMExtension
import org.teavm.gradle.tasks.GenerateCTask
import org.teavm.gradle.tasks.TeaVMTask
import java.io.File
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.PathMatcher
import java.nio.file.Paths
import java.util.zip.ZipFile

class GdxTeaVMGradlePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        project.pluginManager.apply(JavaPlugin::class.java)
        project.pluginManager.apply(TeaVMPlugin::class.java)

        val extension = project.extensions.create<GdxTeaVMExtension>("gdxTeaVM", project)
        project.afterEvaluate {
            configureBackendDependencies(project, extension)
            configureTeaVM(project, extension)
            configureNativeTeaVM(project, extension)
            configureTeaVMTaskClasspaths(project, extension)
            forceTeaVMGenerationTasksToRun(project, extension)
            hideTeaVMTasks(project)
            registerTasks(project, extension)
        }
    }

    private fun hideTeaVMTasks(project: Project) {
        project.tasks.configureEach {
            if(isTeaVMTask(name, group)) {
                group = null
            }
        }
    }

    private fun isTeaVMTask(taskName: String, taskGroup: String?): Boolean {
        return taskGroup?.equals(TEAVM_TASK_GROUP, ignoreCase = true) == true ||
            taskName in TEAVM_SOURCE_SET_TASK_NAMES
    }

    private fun forceTeaVMGenerationTasksToRun(project: Project, extension: GdxTeaVMExtension) {
        if(extension.isTargetDeclared(GdxTeaVMTarget.JS)) {
            forceTaskToRun(project, TeaVMPlugin.JS_TASK_NAME)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            forceTaskToRun(project, TeaVMPlugin.WASM_GC_TASK_NAME)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.GLFW) || extension.isTargetDeclared(GdxTeaVMTarget.PSP)) {
            forceTaskToRun(project, TeaVMPlugin.C_TASK_NAME)
        }
    }

    private fun forceTaskToRun(project: Project, taskName: String) {
        project.tasks.named(taskName).configure {
            outputs.upToDateWhen { false }
        }
    }

    private fun configureBackendDependencies(project: Project, extension: GdxTeaVMExtension) {
        if(extension.isWebTargetDeclared()) {
            addBackendDependency(project, BACKEND_WEB_MODULE)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.GLFW)) {
            addBackendDependency(project, BACKEND_GLFW_MODULE)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.PSP)) {
            addBackendDependency(project, BACKEND_PSP_MODULE)
        }
    }

    private fun addBackendDependency(project: Project, moduleName: String) {
        project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, backendDependency(project, moduleName))
        project.dependencies.add(TeaVMPlugin.CONFIGURATION_NAME, backendDependency(project, moduleName))
    }

    private fun backendDependency(project: Project, moduleName: String): Any {
        val localProject = project.rootProject.findProject(":backends:$moduleName")
        if(localProject != null) {
            return project.dependencies.project(mapOf("path" to localProject.path))
        }
        return "${GdxTeaVMPluginInfo.GROUP}:$moduleName:${GdxTeaVMPluginInfo.VERSION}"
    }

    private fun configureTeaVM(project: Project, extension: GdxTeaVMExtension) {
        val teavm = project.extensions.getByType<TeaVMExtension>()
        val globalProperties = extension.toGlobalProperties(project)

        if(extension.isTargetDeclared(GdxTeaVMTarget.JS)) {
            val js = teavm.getJs()
            val reflectionClasses = reflectionClasses(project, extension, WEB_BACKEND)
            configureCommonTarget(js, extension.js, reflectionClasses)
            js.properties.putAll(globalProperties)
            js.properties.putAll(extension.toWebProperties(project, extension.js))
            js.properties.put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
            js.entryPointName.convention(extension.js.entryPointName)
            js.targetFileName.convention(extension.js.targetFileName)
            js.obfuscated.convention(extension.js.obfuscated)
            js.strict.convention(extension.js.strict)
        }

        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            val wasm = teavm.getWasmGC()
            val reflectionClasses = reflectionClasses(project, extension, WEB_BACKEND)
            configureCommonTarget(wasm, extension.wasm, reflectionClasses)
            wasm.properties.putAll(globalProperties)
            wasm.properties.putAll(extension.toWebProperties(project, extension.wasm))
            wasm.properties.put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
            wasm.targetFileName.convention(extension.wasm.targetFileName)
            wasm.obfuscated.convention(extension.wasm.obfuscated)
            wasm.strict.convention(extension.wasm.strict)
            wasm.copyRuntime.convention(extension.wasm.copyRuntime)
            wasm.modularRuntime.convention(extension.wasm.modularRuntime)
        }
    }

    private fun configureNativeTeaVM(project: Project, extension: GdxTeaVMExtension) {
        val nativeTarget = extension.selectedNativeTargetOrNull(project) ?: return
        val teavm = project.extensions.getByType<TeaVMExtension>()
        val c = teavm.getC()
        val reflectionClasses = reflectionClasses(project, extension, nativeTarget.backendName)
        configureCommonTarget(c, nativeTarget, reflectionClasses)
        c.properties.putAll(extension.toGlobalProperties(project))
        c.properties.putAll(extension.toNativeProperties(project, nativeTarget))
        c.properties.put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
        c.minHeapSize.convention(nativeTarget.minHeapSizeMb)
        c.maxHeapSize.convention(nativeTarget.maxHeapSizeMb)
        c.heapDump.convention(nativeTarget.heapDump)
        c.shortFileNames.convention(nativeTarget.shortFileNames)
        c.obfuscated.convention(nativeTarget.obfuscated)
        project.tasks.named(TeaVMPlugin.C_TASK_NAME, GenerateCTask::class.java).configure {
            getTargetFileName().convention(nativeTarget.targetFileName)
        }
    }

    private fun configureTeaVMTaskClasspaths(project: Project, extension: GdxTeaVMExtension) {
        if(extension.isTargetDeclared(GdxTeaVMTarget.JS)) {
            project.tasks.named(TeaVMPlugin.JS_TASK_NAME, TeaVMTask::class.java).configure {
                filterBackendClasspath(WEB_BACKEND)
            }
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            project.tasks.named(TeaVMPlugin.WASM_GC_TASK_NAME, TeaVMTask::class.java).configure {
                filterBackendClasspath(WEB_BACKEND)
            }
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.GLFW) || extension.isTargetDeclared(GdxTeaVMTarget.PSP)) {
            project.tasks.named(TeaVMPlugin.C_TASK_NAME, TeaVMTask::class.java).configure {
                filterBackendClasspath(extension.selectedNativeBackendName(project))
            }
        }
    }

    private fun TeaVMTask.filterBackendClasspath(targetBackend: String?) {
        if(targetBackend == null) {
            return
        }
        val classpath = targetClasspath(project, targetBackend)
        getClasspath().setFrom(classpath)
        getProperties().put(PLUGIN_CLASSPATH, project.provider {
            classpath.files.joinToString(File.pathSeparator) { file -> file.absolutePath }
        })
    }

    private fun targetClasspath(project: Project, targetBackend: String): FileCollection {
        val sourceSets = project.extensions.findByType(SourceSetContainer::class.java)
            ?: return project.files()
        val mainRuntimeClasspath = sourceSets.getByName("main").runtimeClasspath
        val teavmRuntimeClasspath = sourceSets.getByName(TeaVMPlugin.SOURCE_SET_NAME).runtimeClasspath
        val teavmConfiguration = project.configurations.getByName(TeaVMPlugin.CONFIGURATION_NAME)

        return project.files(mainRuntimeClasspath, teavmRuntimeClasspath, teavmConfiguration).filter { file ->
            isAllowedBackendClasspathEntry(file, targetBackend)
        }
    }

    private fun configureCommonTarget(
        teavmConfig: TeaVMCommonConfiguration,
        target: GdxTeaVMTargetExtension,
        reflectionClasses: Provider<List<String>>
    ) {
        teavmConfig.mainClass.convention(target.mainClass)
        teavmConfig.outputDir.convention(target.outputDir)
        teavmConfig.optimization.convention(target.optimization)
        teavmConfig.debugInformation.convention(target.debugInformation)
        teavmConfig.fastGlobalAnalysis.convention(target.fastGlobalAnalysis)
        teavmConfig.outOfProcess.convention(target.outOfProcess)
        teavmConfig.processMemory.convention(target.processMemory)
        teavmConfig.preservedClasses.addAll(target.preservedClasses)
        teavmConfig.preservedClasses.addAll(reflectionClasses)
        if(teavmConfig is TeaVMConfiguration) {
            teavmConfig.relativePathInOutputDir.convention(target.relativePathInOutputDir)
        }
    }

    private fun reflectionClasses(
        project: Project,
        extension: GdxTeaVMExtension,
        targetBackend: String
    ) = project.provider {
        if(!extension.reflectionEnabled.get()) {
            return@provider emptyList()
        }

        val patterns = reflectionPatterns(extension)
        if(patterns.isEmpty()) {
            return@provider emptyList()
        }

        val classes = linkedSetOf<String>()
        if(extension.reflectionScan.get()) {
            val matchers = patterns.map { pattern ->
                FileSystems.getDefault().getPathMatcher("glob:" + pattern.replace('.', '/'))
            }
            for(file in targetClasspath(project, targetBackend).files) {
                scanReflectionClasses(file, matchers, classes)
            }
        }
        else {
            classes.addAll(patterns.filter(::isExactClassName))
        }
        classes.toList()
    }

    private fun reflectionPatterns(extension: GdxTeaVMExtension): List<String> {
        val patterns = linkedSetOf<String>()
        if(extension.reflectionDefaults.get()) {
            patterns.addAll(DEFAULT_REFLECTION_PATTERNS)
        }
        patterns.addAll(extension.reflection.get().map(String::trim).filter(String::isNotEmpty))
        return patterns.toList()
    }

    private fun scanReflectionClasses(
        file: File,
        matchers: List<PathMatcher>,
        out: MutableSet<String>
    ) {
        if(!file.exists()) {
            return
        }
        if(file.isDirectory) {
            scanReflectionDirectory(file.toPath(), matchers, out)
        }
        else if(file.isFile && file.name.endsWith(".jar")) {
            scanReflectionJar(file, matchers, out)
        }
    }

    private fun scanReflectionDirectory(
        root: Path,
        matchers: List<PathMatcher>,
        out: MutableSet<String>
    ) {
        try {
            Files.walk(root).use { stream ->
                stream.filter { path -> Files.isRegularFile(path) }
                    .filter { path -> path.fileName.toString().endsWith(".class") }
                    .forEach { path ->
                        addReflectionClass(root.relativize(path).toString(), matchers, out)
                    }
            }
        }
        catch(ignored: IOException) {
        }
    }

    private fun scanReflectionJar(
        file: File,
        matchers: List<PathMatcher>,
        out: MutableSet<String>
    ) {
        try {
            ZipFile(file).use { zipFile ->
                val entries = zipFile.entries()
                while(entries.hasMoreElements()) {
                    val entry = entries.nextElement()
                    if(!entry.isDirectory && entry.name.endsWith(".class")) {
                        addReflectionClass(entry.name, matchers, out)
                    }
                }
            }
        }
        catch(ignored: IOException) {
        }
    }

    private fun addReflectionClass(
        classFileName: String,
        matchers: List<PathMatcher>,
        out: MutableSet<String>
    ) {
        val className = classFileName
            .removeSuffix(".class")
            .replace('\\', '.')
            .replace('/', '.')
        if(className == "module-info" || className.endsWith(".package-info")) {
            return
        }
        if(matchesReflectionPattern(className, matchers)) {
            out.add(className)
        }
    }

    private fun matchesReflectionPattern(className: String, matchers: List<PathMatcher>): Boolean {
        var currentClassName = className
        while(true) {
            val path = Paths.get(currentClassName.replace('.', '/'))
            if(matchers.any { matcher -> matcher.matches(path) }) {
                return true
            }
            val nestedIndex = currentClassName.lastIndexOf('$')
            if(nestedIndex < 0) {
                return false
            }
            currentClassName = currentClassName.substring(0, nestedIndex)
        }
    }

    private fun isExactClassName(pattern: String): Boolean {
        return pattern.none { char -> char == '*' || char == '?' || char == '[' || char == '{' }
    }

    private fun joinTokenList(values: Iterable<String>): String {
        return values.map(String::trim).filter(String::isNotEmpty).joinToString(",")
    }

    private fun registerTasks(project: Project, extension: GdxTeaVMExtension) {
        if(extension.isTargetDeclared(GdxTeaVMTarget.JS)) {
            registerJsTasks(project, extension)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            registerWasmTasks(project, extension)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.GLFW)) {
            registerGlfwTasks(project, extension)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.PSP)) {
            registerPspTasks(project, extension)
        }
    }

    private fun registerJsTasks(project: Project, extension: GdxTeaVMExtension) {
        val jsBuild = project.tasks.register("gdx_teavm_web_js_build") {
            group = TASK_GROUP
            description = "Build the gdx-teavm JavaScript web application."
            dependsOn(project.tasks.named(TeaVMPlugin.JS_TASK_NAME))
        }
        project.tasks.register<GdxTeaVMRunWebTask>("gdx_teavm_web_js_run") {
            group = TASK_GROUP
            description = "Build and serve the gdx-teavm JavaScript web application."
            dependsOn(jsBuild)
            webappDir.convention(extension.js.webappDir())
            port.convention(extension.js.serverPort)
            serverClasspath.from(targetClasspath(project, WEB_BACKEND))
        }
    }

    private fun registerWasmTasks(project: Project, extension: GdxTeaVMExtension) {
        val wasmBuild = project.tasks.register("gdx_teavm_web_wasm_build") {
            group = TASK_GROUP
            description = "Build the gdx-teavm Wasm web application."
            dependsOn(project.tasks.named(TeaVMPlugin.BUILD_WASM_GC_TASK_NAME))
        }
        project.tasks.register<GdxTeaVMRunWebTask>("gdx_teavm_web_wasm_run") {
            group = TASK_GROUP
            description = "Build and serve the gdx-teavm Wasm web application."
            dependsOn(wasmBuild)
            webappDir.convention(extension.wasm.webappDir())
            port.convention(extension.wasm.serverPort)
            serverClasspath.from(targetClasspath(project, WEB_BACKEND))
        }
    }

    private fun registerGlfwTasks(project: Project, extension: GdxTeaVMExtension) {
        val glfwGenerate = project.tasks.register("gdx_teavm_glfw_generate") {
            group = TASK_GROUP
            description = "Generate the gdx-teavm GLFW native C project."
            dependsOn(project.tasks.named(TeaVMPlugin.C_TASK_NAME))
        }
        val glfwBuild = project.tasks.register<GdxTeaVMNativeBuildTask>("gdx_teavm_glfw_build") {
            group = TASK_GROUP
            description = "Generate and build the GLFW executable for glfw.buildType."
            dependsOn(glfwGenerate)
            buildRoot.convention(extension.glfw.outputDir)
            scriptBaseName.convention(extension.glfw.buildType.map { buildType ->
                glfwBuildScriptBaseName(buildType)
            })
        }
        project.tasks.register<GdxTeaVMGlfwRunTask>("gdx_teavm_glfw_run") {
            group = TASK_GROUP
            description = "Generate, build, and run the GLFW executable for glfw.buildType."
            dependsOn(glfwBuild)
            buildRoot.convention(extension.glfw.outputDir)
            generatedSourcesDir.convention(extension.glfw.generatedSourcesDir())
            releaseDir.convention(extension.glfw.releasePath)
            projectName.convention(extension.glfw.targetFileName)
            buildType.convention(extension.glfw.buildType)
            consoleLog.convention(extension.glfw.consoleLog)
            backendClasspath.from(targetClasspath(project, GLFW_BACKEND))
        }
    }

    private fun registerPspTasks(project: Project, extension: GdxTeaVMExtension) {
        val pspGenerate = project.tasks.register("gdx_teavm_psp_generate") {
            group = TASK_GROUP
            description = "Generate the gdx-teavm PSP native C project."
            dependsOn(project.tasks.named(TeaVMPlugin.C_TASK_NAME))
        }
        project.tasks.register<GdxTeaVMNativeBuildTask>("gdx_teavm_psp_build") {
            group = TASK_GROUP
            description = "Generate and build the PSP native project."
            dependsOn(pspGenerate)
            buildRoot.convention(extension.psp.outputDir)
            scriptBaseName.set("build")
        }
    }

    private fun isAllowedBackendClasspathEntry(file: File, targetBackend: String): Boolean {
        val backendName = backendNameFromClasspathEntry(file) ?: return true
        return backendName == targetBackend
    }

    private fun backendNameFromClasspathEntry(file: File): String? {
        val path = file.absolutePath.replace('\\', '/').lowercase()
        return when {
            path.contains("/backends/$BACKEND_WEB_MODULE/") || path.contains("/$BACKEND_WEB_MODULE/") || path.contains("$BACKEND_WEB_MODULE-") -> WEB_BACKEND
            path.contains("/backends/$BACKEND_GLFW_MODULE/") || path.contains("/$BACKEND_GLFW_MODULE/") || path.contains("$BACKEND_GLFW_MODULE-") -> GLFW_BACKEND
            path.contains("/backends/$BACKEND_PSP_MODULE/") || path.contains("/$BACKEND_PSP_MODULE/") || path.contains("$BACKEND_PSP_MODULE-") -> PSP_BACKEND
            else -> null
        }
    }

    private fun glfwBuildScriptBaseName(buildType: String): String {
        return when(buildType.trim().lowercase()) {
            "debug" -> "app_debug"
            "release" -> "app_release"
            else -> throw IllegalArgumentException("Unsupported GLFW native build type: $buildType")
        }
    }

    private companion object {
        const val TASK_GROUP = "gdx-teavm"
        const val TEAVM_TASK_GROUP = "teavm"
        val TEAVM_SOURCE_SET_TASK_NAMES = setOf(
            "teavmClasses",
            "compileTeavmJava",
            "processTeavmResources"
        )
        const val BACKEND_WEB_MODULE = "backend-web"
        const val BACKEND_GLFW_MODULE = "backend-glfw"
        const val BACKEND_PSP_MODULE = "backend-psp"
        const val WEB_BACKEND = "web"
        const val GLFW_BACKEND = "glfw"
        const val PSP_BACKEND = "psp"
        const val PLUGIN_CLASSPATH = "gdx.teavm.classpath"
        const val REFLECTION_CLASSES = "gdx.teavm.reflection.classes"
        val DEFAULT_REFLECTION_PATTERNS = listOf(
            "com.badlogic.gdx.scenes.scene2d.**",
            "net.mgsx.gltf.data.**",
            "com.badlogic.gdx.utils.Array",
            "com.badlogic.gdx.utils.ArrayMap",
            "com.badlogic.gdx.utils.IntIntMap",
            "com.badlogic.gdx.utils.IntMap",
            "com.badlogic.gdx.utils.IntSet",
            "com.badlogic.gdx.utils.LongMap",
            "com.badlogic.gdx.utils.ObjectFloatMap",
            "com.badlogic.gdx.utils.ObjectIntMap",
            "com.badlogic.gdx.utils.ObjectMap",
            "com.badlogic.gdx.utils.ObjectSet",
            "com.badlogic.gdx.utils.Queue"
        )
    }
}
