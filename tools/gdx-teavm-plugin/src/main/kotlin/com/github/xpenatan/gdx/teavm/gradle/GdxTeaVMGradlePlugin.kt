package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.JavaVersion
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RelativePath
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.named
import org.gradle.kotlin.dsl.register
import org.teavm.gradle.TeaVMPlugin
import org.teavm.gradle.api.TeaVMCConfiguration
import org.teavm.gradle.api.TeaVMExtension
import org.teavm.gradle.config.ArtifactCoordinates
import org.teavm.gradle.tasks.GenerateCTask
import org.teavm.gradle.tasks.GenerateJavaScriptTask
import org.teavm.gradle.tasks.GenerateWasmGCTask
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
        if(isAndroidProject(project)) {
            applyAndroidProject(project)
            return
        }
        applyJavaProject(project)
    }

    private fun applyJavaProject(project: Project) {
        project.pluginManager.apply(JavaPlugin::class.java)
        project.pluginManager.apply(TeaVMPlugin::class.java)

        val teavm = project.extensions.getByType<TeaVMExtension>()
        val extension = project.extensions.create<GdxTeaVMExtension>("gdxTeaVM", project, teavm)
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

    private fun applyAndroidProject(project: Project) {
        registerAndroidTeaVMConfigurations(project)
        configureAndroidRuntimeSourceRoot(project)
        configureAndroidNativeSourceRoot(project)
        configureAndroidIdeaModel(project)
        val extension = project.extensions.create<GdxTeaVMExtension>("gdxTeaVM", project)
        project.afterEvaluate {
            validateAndroidTargets(extension)
            addAndroidBackendDependency(project, extension)
            registerAndroidProjectTasks(project, extension)
        }
    }

    private fun isAndroidProject(project: Project): Boolean {
        return project.pluginManager.hasPlugin(ANDROID_APPLICATION_PLUGIN) ||
            project.pluginManager.hasPlugin(ANDROID_LIBRARY_PLUGIN)
    }

    private fun registerAndroidTeaVMConfigurations(project: Project) {
        val teavm = project.configurations.maybeCreate(TeaVMPlugin.CONFIGURATION_NAME)
        teavm.isCanBeConsumed = false
        teavm.isCanBeResolved = true
        project.dependencies.add(TeaVMPlugin.CONFIGURATION_NAME, ArtifactCoordinates.CLASSLIB)

        val ideClasspath = project.configurations.maybeCreate(ANDROID_IDE_CONFIGURATION_NAME)
        ideClasspath.isCanBeConsumed = false
        ideClasspath.isCanBeResolved = false
        ideClasspath.extendsFrom(teavm)
        ideClasspath.exclude(mapOf("group" to TEAVM_GROUP, "module" to TEAVM_CLASSLIB_MODULE))
        project.configurations.getByName(JavaPlugin.COMPILE_ONLY_CONFIGURATION_NAME).extendsFrom(ideClasspath)
    }

    private fun configureAndroidRuntimeSourceRoot(project: Project) {
        val runtimeSourceDir = project.layout.buildDirectory.dir(ANDROID_RUNTIME_GENERATED_SOURCE_DIR)
        addAndroidMainJavaSourceDir(project, runtimeSourceDir)
        val generateTask = project.tasks.register<Copy>("generateGdxTeaVMAndroidRuntimeJava") {
            description = "Internal task used by Android javac to compile the gdx-teavm Android runtime bridge."
            into(runtimeSourceDir)
            val localRuntimeSourceDir = localAndroidRuntimeSourceDir(project)
            if(localRuntimeSourceDir != null) {
                from(localRuntimeSourceDir)
            }
            else {
                from(androidRuntimeSourceArchive(project)) {
                    eachFile {
                        stripAndroidRuntimeResourcePrefix()
                    }
                    includeEmptyDirs = false
                }
            }
        }

        project.tasks.withType(JavaCompile::class.java).configureEach {
            if(isAndroidJavacTask(name)) {
                dependsOn(generateTask)
            }
        }
    }

    private fun localAndroidRuntimeSourceDir(project: Project): File? {
        val localBackendProject = project.rootProject.findProject(":backends:$BACKEND_ANDROID_MODULE")
            ?: return null
        val sourceDir = localBackendProject.layout.projectDirectory.dir(ANDROID_RUNTIME_SOURCE_DIR).asFile
        return if(sourceDir.isDirectory) sourceDir else null
    }

    private fun androidRuntimeSourceArchive(project: Project): Provider<Any> {
        return project.provider {
            val backendFile = project.configurations.getByName(TeaVMPlugin.CONFIGURATION_NAME)
                .resolve()
                .firstOrNull { file -> backendNameFromClasspathEntry(file) == ANDROID_BACKEND }
                ?: throw IllegalStateException("Unable to find backend-android runtime sources on the gdx-teavm Android classpath")

            if(backendFile.isDirectory) {
                File(backendFile, ANDROID_RUNTIME_RESOURCE_DIR)
            }
            else {
                project.zipTree(backendFile).matching {
                    include("$ANDROID_RUNTIME_RESOURCE_DIR/**")
                }
            }
        }
    }

    private fun org.gradle.api.file.FileCopyDetails.stripAndroidRuntimeResourcePrefix() {
        val segments = relativePath.segments
        if(segments.size < ANDROID_RUNTIME_RESOURCE_PREFIX_SEGMENTS.size) {
            return
        }
        for(i in ANDROID_RUNTIME_RESOURCE_PREFIX_SEGMENTS.indices) {
            if(segments[i] != ANDROID_RUNTIME_RESOURCE_PREFIX_SEGMENTS[i]) {
                return
            }
        }
        relativePath = RelativePath(true, *segments.drop(ANDROID_RUNTIME_RESOURCE_PREFIX_SEGMENTS.size).toTypedArray())
    }

    private fun configureAndroidNativeSourceRoot(project: Project) {
        val nativeSourceDir = project.layout.projectDirectory.dir(ANDROID_NATIVE_SOURCE_DIR).asFile
        addAndroidMainJavaSourceDir(project, nativeSourceDir)

        val nativeSourcePath = nativeSourceDir.toPath().toAbsolutePath().normalize()
        project.tasks.withType(JavaCompile::class.java).configureEach {
            if(isAndroidJavacTask(name)) {
                exclude { element ->
                    element.file.toPath().toAbsolutePath().normalize().startsWith(nativeSourcePath)
                }
            }
        }
    }

    private fun addAndroidMainJavaSourceDir(project: Project, sourceDir: Any) {
        val android = project.extensions.findByName("android") ?: return
        val sourceSets = android.javaClass.getMethod("getSourceSets").invoke(android)
        val mainSourceSet = sourceSets.javaClass.getMethod("getByName", String::class.java).invoke(sourceSets, "main")
        val javaSourceSet = mainSourceSet.javaClass.getMethod("getJava").invoke(mainSourceSet)
        javaSourceSet.javaClass.getMethod("srcDir", Any::class.java).invoke(javaSourceSet, sourceDir)
    }

    private fun isAndroidJavacTask(taskName: String): Boolean {
        return taskName.startsWith("compile") && taskName.endsWith("JavaWithJavac")
    }

    private fun configureAndroidIdeaModel(project: Project) {
        project.pluginManager.apply("idea")
        project.plugins.withId("idea") {
            val ideaModel = project.extensions.findByName("idea") ?: return@withId
            val module = ideaModel.javaClass.getMethod("getModule").invoke(ideaModel)
            val nativeSourceDir = project.layout.projectDirectory.dir(ANDROID_NATIVE_SOURCE_DIR).asFile

            @Suppress("UNCHECKED_CAST")
            val sourceDirs = module.javaClass.getMethod("getSourceDirs").invoke(module) as Set<File>
            val updatedSourceDirs = LinkedHashSet(sourceDirs)
            updatedSourceDirs.add(nativeSourceDir)
            module.javaClass.getMethod("setSourceDirs", Set::class.java).invoke(module, updatedSourceDirs)

            addIdeaScopeConfiguration(
                module,
                "PROVIDED",
                "plus",
                project.configurations.getByName(TeaVMPlugin.CONFIGURATION_NAME)
            )
        }
    }

    private fun addIdeaScopeConfiguration(
        module: Any,
        scopeName: String,
        mappingName: String,
        configuration: Configuration
    ) {
        @Suppress("UNCHECKED_CAST")
        val scopes = module.javaClass.getMethod("getScopes").invoke(module)
            as Map<String, Map<String, Collection<Configuration>>>
        val updatedScopes = linkedMapOf<String, MutableMap<String, MutableCollection<Configuration>>>()
        for((name, mappings) in scopes) {
            val updatedMappings = linkedMapOf<String, MutableCollection<Configuration>>()
            for((mapping, configurations) in mappings) {
                updatedMappings[mapping] = LinkedHashSet(configurations)
            }
            updatedScopes[name] = updatedMappings
        }
        val scope = updatedScopes.getOrPut(scopeName) { linkedMapOf() }
        val configurations = scope.getOrPut(mappingName) { linkedSetOf() }
        configurations.add(configuration)
        module.javaClass.getMethod("setScopes", Map::class.java).invoke(module, updatedScopes)
    }

    private fun validateAndroidTargets(extension: GdxTeaVMExtension) {
        val supported = extension.isTargetDeclared(GdxTeaVMTarget.ANDROID)
        val unsupported = extension.isTargetDeclared(GdxTeaVMTarget.JS)
            || extension.isTargetDeclared(GdxTeaVMTarget.WASM)
            || extension.isTargetDeclared(GdxTeaVMTarget.GLFW)
            || extension.isTargetDeclared(GdxTeaVMTarget.PSP)
        if(unsupported) {
            throw IllegalStateException("Android Gradle projects currently support only the gdxTeaVM android target")
        }
        if(!supported) {
            return
        }
        if(!extension.android.mainClass.isPresent) {
            throw IllegalStateException("gdxTeaVM.android.mainClass must be set to generate Android TeaVM C code")
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
        if(extension.isNativeTargetDeclared()) {
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
        if(extension.isTargetDeclared(GdxTeaVMTarget.ANDROID)) {
            addBackendDependency(project, BACKEND_ANDROID_MODULE)
        }
    }

    private fun addBackendDependency(project: Project, moduleName: String) {
        project.dependencies.add(JavaPlugin.IMPLEMENTATION_CONFIGURATION_NAME, backendDependency(project, moduleName))
        project.dependencies.add(TeaVMPlugin.CONFIGURATION_NAME, backendDependency(project, moduleName))
    }

    private fun addAndroidBackendDependency(project: Project, extension: GdxTeaVMExtension) {
        if(extension.isTargetDeclared(GdxTeaVMTarget.ANDROID)) {
            project.dependencies.add(TeaVMPlugin.CONFIGURATION_NAME, backendDependency(project, BACKEND_ANDROID_MODULE))
        }
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
            js.preservedClasses.addAll(reflectionClasses)
            js.properties.putAll(globalProperties)
            js.properties.putAll(extension.toWebProperties(project, extension.js))
            js.properties.put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
        }

        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            val wasm = teavm.getWasmGC()
            val reflectionClasses = reflectionClasses(project, extension, WEB_BACKEND)
            wasm.preservedClasses.addAll(reflectionClasses)
            wasm.properties.putAll(globalProperties)
            wasm.properties.putAll(extension.toWebProperties(project, extension.wasm))
            wasm.properties.put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
        }
    }

    private fun configureNativeTeaVM(project: Project, extension: GdxTeaVMExtension) {
        val selectedNativeBackend = extension.selectedNativeBackendName(project)
        val nativeTarget = extension.nativeTargetForBackendName(selectedNativeBackend)
            ?: extension.defaultNativeTargetOrNull()
            ?: return
        val teavm = project.extensions.getByType<TeaVMExtension>()
        val c = teavm.getC()
        val reflectionClasses = reflectionClasses(project, extension, nativeTarget.backendName)
        applyNativeTargetConfiguration(c, nativeTarget)
        if(!nativeTarget.mainClass.isPresent) {
            c.mainClass.set(VALIDATION_ONLY_MAIN_CLASS)
        }
        c.preservedClasses.addAll(reflectionClasses)
        c.properties.putAll(extension.toGlobalProperties(project))
        c.properties.putAll(extension.toNativeProperties(project, nativeTarget))
        c.properties.put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
        project.tasks.named(TeaVMPlugin.C_TASK_NAME, GenerateCTask::class.java).configure {
            getTargetFileName().set(nativeTarget.targetFileName)
            if(selectedNativeBackend == null) {
                onlyIf("a gdx-teavm native backend task is selected") { false }
            }
        }
    }

    private fun applyNativeTargetConfiguration(
        c: TeaVMCConfiguration,
        nativeTarget: GdxTeaVMNativeTargetExtension
    ) {
        if(nativeTarget.mainClass.isPresent) {
            c.mainClass.set(nativeTarget.mainClass)
        }
        c.outputDir.set(nativeTarget.outputDir)
        c.relativePathInOutputDir.set(nativeTarget.relativePathInOutputDir)
        c.optimization.set(nativeTarget.optimization)
        c.debugInformation.set(nativeTarget.debugInformation)
        c.fastGlobalAnalysis.set(nativeTarget.fastGlobalAnalysis)
        c.outOfProcess.set(nativeTarget.outOfProcess)
        c.processMemory.set(nativeTarget.processMemory)
        c.preservedClasses.addAll(nativeTarget.preservedClasses)
        c.minHeapSize.set(nativeTarget.minHeapSizeMb)
        c.maxHeapSize.set(nativeTarget.maxHeapSizeMb)
        c.heapDump.set(nativeTarget.heapDump)
        c.shortFileNames.set(nativeTarget.shortFileNames)
        c.obfuscated.set(nativeTarget.obfuscated)
    }

    private fun configureTeaVMTaskClasspaths(project: Project, extension: GdxTeaVMExtension) {
        if(extension.isTargetDeclared(GdxTeaVMTarget.JS)) {
            project.tasks.named(TeaVMPlugin.JS_TASK_NAME, GenerateJavaScriptTask::class.java).configure {
                filterBackendClasspath(WEB_BACKEND)
                getSourceFiles().from(runtimeProjectSourceDirs(project))
            }
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            project.tasks.named(TeaVMPlugin.WASM_GC_TASK_NAME, GenerateWasmGCTask::class.java).configure {
                filterBackendClasspath(WEB_BACKEND)
                getSourceFiles().from(runtimeProjectSourceDirs(project))
            }
        }
        if(extension.isNativeTargetDeclared()) {
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

    private fun androidTeaVMClasspath(
        project: Project,
        compileTask: Provider<JavaCompile>
    ): FileCollection {
        val teavmConfiguration = project.configurations.getByName(TeaVMPlugin.CONFIGURATION_NAME)
        return project.files(
            compileTask.flatMap { it.destinationDirectory },
            teavmConfiguration
        ).filter { file ->
            isAllowedBackendClasspathEntry(file, ANDROID_BACKEND)
        }
    }

    private fun runtimeProjectSourceDirs(project: Project): Provider<List<File>> {
        return project.provider {
            val result = linkedSetOf<File>()
            val runtimeClasspath = project.configurations.getByName(JavaPlugin.RUNTIME_CLASSPATH_CONFIGURATION_NAME)
            for(dependencyResult in runtimeClasspath.incoming.resolutionResult.allDependencies) {
                if(dependencyResult !is ResolvedDependencyResult) {
                    continue
                }
                val componentId = dependencyResult.selected.id
                if(componentId is ProjectComponentIdentifier) {
                    val dependencyProject = project.rootProject.findProject(componentId.projectPath)
                    if(dependencyProject != null) {
                        addSourceDirs(dependencyProject, result)
                    }
                }
            }
            result.toList()
        }
    }

    private fun addSourceDirs(project: Project, result: MutableSet<File>) {
        val sourceSets = project.extensions.findByType(SourceSetContainer::class.java) ?: return
        for(sourceSet in sourceSets) {
            result.addAll(sourceSet.allJava.sourceDirectories.files)
        }
    }

    private fun reflectionClasses(
        project: Project,
        extension: GdxTeaVMExtension,
        targetBackend: String
    ): Provider<List<String>> {
        return reflectionClasses(project, extension, targetBackend, project.provider {
            targetClasspath(project, targetBackend).files
        })
    }

    private fun reflectionClasses(
        project: Project,
        extension: GdxTeaVMExtension,
        targetBackend: String,
        classpathProvider: Provider<Set<File>>
    ) = project.provider {
        val debug = extension.reflectionDebug.get()
        if(!extension.reflectionEnabled.get()) {
            if(debug) {
                project.logger.lifecycle("[gdx-teavm] Reflection disabled for target '$targetBackend'")
            }
            return@provider emptyList()
        }

        val patterns = reflectionPatterns(extension)
        if(patterns.isEmpty()) {
            if(debug) {
                project.logger.lifecycle("[gdx-teavm] Reflection enabled for target '$targetBackend', but no Gradle reflection patterns were configured")
            }
            return@provider emptyList()
        }

        val classes = linkedSetOf<String>()
        val classpathFiles = if(extension.reflectionScan.get()) {
            classpathProvider.get()
        }
        else {
            emptySet()
        }
        if(extension.reflectionScan.get()) {
            val matchers = patterns.map { pattern ->
                FileSystems.getDefault().getPathMatcher("glob:" + pattern.replace('.', '/'))
            }
            for(file in classpathFiles) {
                scanReflectionClasses(file, matchers, classes)
            }
        }
        else {
            classes.addAll(patterns.filter(::isExactClassName))
        }
        if(debug) {
            logReflectionClasses(project, targetBackend, extension.reflectionScan.get(), patterns, classpathFiles, classes)
        }
        classes.toList()
    }

    private fun logReflectionClasses(
        project: Project,
        targetBackend: String,
        scanEnabled: Boolean,
        patterns: List<String>,
        classpathFiles: Collection<File>,
        classes: Collection<String>
    ) {
        project.logger.lifecycle("[gdx-teavm] Reflection debug for target '$targetBackend'")
        project.logger.lifecycle("[gdx-teavm]   scan: $scanEnabled")
        project.logger.lifecycle("[gdx-teavm]   patterns (${patterns.size}):")
        for(pattern in patterns) {
            project.logger.lifecycle("[gdx-teavm]     pattern: $pattern")
        }
        if(scanEnabled) {
            project.logger.lifecycle("[gdx-teavm]   scanned classpath entries (${classpathFiles.size}):")
            for(file in classpathFiles.sortedBy(File::getAbsolutePath)) {
                project.logger.lifecycle("[gdx-teavm]     classpath: ${file.absolutePath}")
            }
        }
        project.logger.lifecycle("[gdx-teavm]   classes added to TeaVM preservedClasses (${classes.size}):")
        for(className in classes.sorted()) {
            project.logger.lifecycle("[gdx-teavm]     class: $className")
        }
    }

    private fun reflectionPatterns(extension: GdxTeaVMExtension): List<String> {
        return extension.reflection.get()
            .map(String::trim)
            .filter(String::isNotEmpty)
            .distinct()
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
        if(extension.isTargetDeclared(GdxTeaVMTarget.ANDROID)) {
            registerAndroidTasks(project)
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

    private fun registerAndroidTasks(project: Project) {
        project.tasks.register("gdx_teavm_android_generate") {
            group = TASK_GROUP
            description = "Generate the gdx-teavm Android native C/CMake payload."
            dependsOn(project.tasks.named(TeaVMPlugin.C_TASK_NAME))
        }
    }

    private fun registerAndroidProjectTasks(project: Project, extension: GdxTeaVMExtension) {
        if(!extension.isTargetDeclared(GdxTeaVMTarget.ANDROID)) {
            return
        }
        val compilerConfiguration = project.configurations.detachedConfiguration(
            project.dependencies.create(ArtifactCoordinates.TOOLS)
        )
        val compileTask = project.tasks.register<JavaCompile>("compileGdxTeaVMAndroidJava") {
            description = "Internal task used by gdx_teavm_android_generate to compile Android TeaVM launcher sources."
            source(project.layout.projectDirectory.dir(ANDROID_NATIVE_SOURCE_DIR))
            classpath = project.configurations.getByName(TeaVMPlugin.CONFIGURATION_NAME)
            destinationDirectory.set(project.layout.buildDirectory.dir("classes/java/gdxTeaVMAndroid"))
            sourceCompatibility = JavaVersion.VERSION_11.toString()
            targetCompatibility = JavaVersion.VERSION_11.toString()
        }
        val classpath = androidTeaVMClasspath(project, compileTask)
        val reflectionClasses = reflectionClasses(project, extension, ANDROID_BACKEND, project.provider {
            classpath.files
        })
        val generateTask = project.tasks.register<GenerateCTask>("gdx_teavm_android_generate") {
            group = TASK_GROUP
            description = "Generate the gdx-teavm Android native C/CMake payload."
            dependsOn(compileTask)
            getMainClass().set(extension.android.mainClass)
            getClasspath().from(classpath)
            getDaemonClasspath().from(compilerConfiguration)
            getOutputDir().set(extension.android.generatedSourcesDir().map { it.asFile })
            getTargetFileName().set(extension.android.targetFileName)
            getDebugInformation().set(extension.android.debugInformation)
            getOptimization().set(extension.android.optimization)
            getFastGlobalAnalysis().set(extension.android.fastGlobalAnalysis)
            getOutOfProcess().set(extension.android.outOfProcess)
            getProcessMemory().set(extension.android.processMemory)
            getPreservedClasses().addAll(extension.android.preservedClasses)
            getPreservedClasses().addAll(reflectionClasses)
            getProperties().putAll(extension.toGlobalProperties(project))
            getProperties().putAll(extension.toNativeProperties(project, extension.android))
            getProperties().put(REFLECTION_CLASSES, reflectionClasses.map(::joinTokenList))
            getProperties().put(PLUGIN_CLASSPATH, project.provider {
                classpath.files.joinToString(File.pathSeparator) { file -> file.absolutePath }
            })
            getMinHeapSize().set(extension.android.minHeapSizeMb)
            getMaxHeapSize().set(extension.android.maxHeapSizeMb)
            getHeapDump().set(extension.android.heapDump)
            getShortFileNames().set(extension.android.shortFileNames)
            getObfuscated().set(extension.android.obfuscated)
        }
        project.tasks.configureEach {
            if(name == "preBuild"
                || name.startsWith("configureCMake")
                || name.startsWith("buildCMake")
                || (name.startsWith("externalNativeBuild") && !name.startsWith("externalNativeBuildClean"))) {
                dependsOn(generateTask)
            }
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
            path.contains("/backends/$BACKEND_ANDROID_MODULE/") || path.contains("/$BACKEND_ANDROID_MODULE/") || path.contains("$BACKEND_ANDROID_MODULE-") -> ANDROID_BACKEND
            else -> null
        }
    }

    private fun GdxTeaVMExtension.isNativeTargetDeclared(): Boolean {
        return isTargetDeclared(GdxTeaVMTarget.GLFW)
            || isTargetDeclared(GdxTeaVMTarget.PSP)
            || isTargetDeclared(GdxTeaVMTarget.ANDROID)
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
        const val ANDROID_APPLICATION_PLUGIN = "com.android.application"
        const val ANDROID_LIBRARY_PLUGIN = "com.android.library"
        const val ANDROID_IDE_CONFIGURATION_NAME = "gdxTeaVMAndroidIde"
        const val ANDROID_NATIVE_SOURCE_DIR = "src/native/java"
        const val ANDROID_RUNTIME_SOURCE_DIR = "src/android/java"
        const val ANDROID_RUNTIME_GENERATED_SOURCE_DIR = "generated/source/gdx-teavm/android/runtime/java"
        const val ANDROID_RUNTIME_RESOURCE_DIR = "gdx-teavm/android/runtime/java"
        val ANDROID_RUNTIME_RESOURCE_PREFIX_SEGMENTS = ANDROID_RUNTIME_RESOURCE_DIR.split('/')
        const val TEAVM_GROUP = "org.teavm"
        const val TEAVM_CLASSLIB_MODULE = "teavm-classlib"
        val TEAVM_SOURCE_SET_TASK_NAMES = setOf(
            "teavmClasses",
            "compileTeavmJava",
            "processTeavmResources"
        )
        const val BACKEND_WEB_MODULE = "backend-web"
        const val BACKEND_GLFW_MODULE = "backend-glfw"
        const val BACKEND_PSP_MODULE = "backend-psp"
        const val BACKEND_ANDROID_MODULE = "backend-android"
        const val WEB_BACKEND = "web"
        const val GLFW_BACKEND = "glfw"
        const val PSP_BACKEND = "psp"
        const val ANDROID_BACKEND = "android"
        const val PLUGIN_CLASSPATH = "gdx.teavm.classpath"
        const val REFLECTION_CLASSES = "gdx.teavm.reflection.classes"
        const val VALIDATION_ONLY_MAIN_CLASS = "com.github.xpenatan.gdx.teavm.gradle.ValidationOnlyMainClass"
    }
}
