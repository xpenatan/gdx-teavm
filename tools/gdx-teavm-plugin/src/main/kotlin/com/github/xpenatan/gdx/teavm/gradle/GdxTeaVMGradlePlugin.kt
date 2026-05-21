package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPlugin
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
            configureCommonTarget(js, extension.js)
            js.properties.putAll(globalProperties)
            js.properties.putAll(extension.toWebProperties(project, extension.js))
            js.entryPointName.convention(extension.js.entryPointName)
            js.targetFileName.convention(extension.js.targetFileName)
            js.obfuscated.convention(extension.js.obfuscated)
            js.strict.convention(extension.js.strict)
        }

        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            val wasm = teavm.getWasmGC()
            configureCommonTarget(wasm, extension.wasm)
            wasm.properties.putAll(globalProperties)
            wasm.properties.putAll(extension.toWebProperties(project, extension.wasm))
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
        configureCommonTarget(c, nativeTarget)
        c.properties.putAll(extension.toGlobalProperties(project))
        c.properties.putAll(extension.toNativeProperties(project, nativeTarget))
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
        val sourceSets = project.extensions.findByType(SourceSetContainer::class.java) ?: return
        val teavmRuntimeClasspath = sourceSets.getByName(TeaVMPlugin.SOURCE_SET_NAME).runtimeClasspath
        getClasspath().setFrom(teavmRuntimeClasspath.filter { file ->
            isAllowedBackendClasspathEntry(file, targetBackend)
        })
    }

    private fun configureCommonTarget(
        teavmConfig: TeaVMCommonConfiguration,
        target: GdxTeaVMTargetExtension
    ) {
        teavmConfig.mainClass.convention(target.mainClass)
        teavmConfig.outputDir.convention(target.outputDir)
        teavmConfig.optimization.convention(target.optimization)
        teavmConfig.debugInformation.convention(target.debugInformation)
        teavmConfig.fastGlobalAnalysis.convention(target.fastGlobalAnalysis)
        teavmConfig.outOfProcess.convention(target.outOfProcess)
        teavmConfig.processMemory.convention(target.processMemory)
        teavmConfig.preservedClasses.addAll(target.preservedClasses)
        if(teavmConfig is TeaVMConfiguration) {
            teavmConfig.relativePathInOutputDir.convention(target.relativePathInOutputDir)
        }
    }

    private fun registerTasks(project: Project, extension: GdxTeaVMExtension) {
        val sourceSets = project.extensions.getByType<SourceSetContainer>()
        val teavmRuntimeClasspath = sourceSets.getByName(TeaVMPlugin.SOURCE_SET_NAME).runtimeClasspath

        if(extension.isTargetDeclared(GdxTeaVMTarget.JS)) {
            registerJsTasks(project, extension, teavmRuntimeClasspath)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.WASM)) {
            registerWasmTasks(project, extension, teavmRuntimeClasspath)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.GLFW)) {
            registerGlfwTasks(project, extension, teavmRuntimeClasspath)
        }
        if(extension.isTargetDeclared(GdxTeaVMTarget.PSP)) {
            registerPspTasks(project, extension)
        }
    }

    private fun registerJsTasks(project: Project, extension: GdxTeaVMExtension, teavmRuntimeClasspath: FileCollection) {
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
            serverClasspath.from(teavmRuntimeClasspath.filter { file ->
                isAllowedBackendClasspathEntry(file, WEB_BACKEND)
            })
        }
    }

    private fun registerWasmTasks(project: Project, extension: GdxTeaVMExtension, teavmRuntimeClasspath: FileCollection) {
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
            serverClasspath.from(teavmRuntimeClasspath.filter { file ->
                isAllowedBackendClasspathEntry(file, WEB_BACKEND)
            })
        }
    }

    private fun registerGlfwTasks(project: Project, extension: GdxTeaVMExtension, teavmRuntimeClasspath: FileCollection) {
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
            backendClasspath.from(teavmRuntimeClasspath.filter { file ->
                isAllowedBackendClasspathEntry(file, GLFW_BACKEND)
            })
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
    }
}
