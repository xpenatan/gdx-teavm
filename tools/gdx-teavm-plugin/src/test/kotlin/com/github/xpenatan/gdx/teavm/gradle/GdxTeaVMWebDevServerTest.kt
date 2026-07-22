package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.logging.LogLevel
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.internal.logging.events.LogEvent
import org.gradle.internal.logging.events.OutputEvent
import org.gradle.internal.logging.events.OutputEventListener
import org.gradle.internal.logging.events.StyledTextOutputEvent
import org.gradle.internal.operations.OperationIdentifier
import org.gradle.api.tasks.bundling.Jar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.teavm.gradle.TeaVMPlugin
import org.teavm.gradle.tasks.DevServerTask

class GdxTeaVMWebDevServerTest {
    @Test
    fun `automatic continuous bootstrap output is hidden without hiding errors or real rebuilds`() {
        val forwarded = arrayListOf<OutputEvent>()
        var uninstalled = false
        val output = GdxTeaVMAutomaticContinuousOutput(
            OutputEventListener { event -> forwarded.add(event) },
            Any()
        ) {
            uninstalled = true
        }
        val before = LogEvent(1L, "test", LogLevel.LIFECYCLE, "initial build", null)
        val transition = StyledTextOutputEvent(
            2L,
            "test",
            LogLevel.QUIET,
            OperationIdentifier(1L),
            GRADLE_DEPLOYMENT_TRANSITION_MESSAGE
        )
        val duplicateTaskOutput = LogEvent(3L, "test", LogLevel.LIFECYCLE, "duplicate task", null)
        val serverError = LogEvent(4L, "test", LogLevel.ERROR, "server stderr: failure", null)
        val waiting = StyledTextOutputEvent(
            5L,
            "test",
            LogLevel.QUIET,
            OperationIdentifier(2L),
            "$GRADLE_CONTINUOUS_WAITING_MESSAGE..."
        )
        val realRebuild = LogEvent(6L, "test", LogLevel.LIFECYCLE, "real rebuild", null)

        output.arm()
        listOf(before, transition, duplicateTaskOutput, serverError, waiting).forEach(output::onOutput)

        assertEquals(listOf(before, serverError), forwarded)
        assertTrue(uninstalled)
        output.onOutput(realRebuild)
        assertEquals(listOf(before, serverError, realRebuild), forwarded)
        output.close()
        assertTrue(uninstalled)
    }

    @Test
    fun `TeaVM child stderr is restored to error output`() {
        val message = "server stderr: | Copied [Classpath] example.txt"
        var errorMessage: String? = null

        val rewritten = rewriteTeaVMDevServerStderr(LogLevel.WARN, message) { errorMessage = it }

        assertNull(rewritten)
        assertEquals("| Copied [Classpath] example.txt", errorMessage)
        assertEquals(
            "ordinary warning",
            rewriteTeaVMDevServerStderr(LogLevel.WARN, "ordinary warning") {
                throw AssertionError("Ordinary warnings must not be rerouted")
            }
        )
        assertEquals(
            message,
            rewriteTeaVMDevServerStderr(LogLevel.ERROR, message) {
                throw AssertionError("Already-red error output must not be rerouted")
            }
        )
    }

    @Test
    fun `web run tasks preserve build and serve behavior by default`() {
        val project = configuredProject { extension ->
            extension.js(Action {
                mainClass.set("example.JsMain")
            })
            extension.wasm(Action {
                mainClass.set("example.WasmMain")
            })
        }

        val jsRun = project.tasks.getByName("gdx_teavm_web_js_run")
        val wasmRun = project.tasks.getByName("gdx_teavm_web_wasm_run")
        val extension = project.extensions.getByType(GdxTeaVMExtension::class.java)

        assertTrue(jsRun is GdxTeaVMRunWebTask)
        assertTrue(wasmRun is GdxTeaVMRunWebTask)
        assertTrue(dependencyNames(jsRun).contains("gdx_teavm_web_js_build"))
        assertTrue(dependencyNames(wasmRun).contains("gdx_teavm_web_wasm_build"))
        assertTrue(extension.js.outOfProcess.get())
        assertTrue(extension.wasm.outOfProcess.get())
        assertEquals(1024, extension.js.processMemory.get())
        assertEquals(1024, extension.wasm.processMemory.get())
    }

    @Test
    fun `web run tasks delegate to TeaVM dev servers when enabled`() {
        val project = configuredProject { extension ->
            extension.js(Action {
                mainClass.set("example.JsMain")
                serverPort.set(8181)
                devServer(Action {
                    enabled.set(true)
                    autoReload.set(true)
                })
            })
            extension.wasm(Action {
                mainClass.set("example.WasmMain")
                serverPort.set(8282)
                devServer(Action {
                    enabled.set(true)
                    autoReload.set(true)
                })
            })
        }

        val jsRun = project.tasks.getByName("gdx_teavm_web_js_run")
        val wasmRun = project.tasks.getByName("gdx_teavm_web_wasm_run")
        val jsDevServer = project.tasks.getByName(TeaVMPlugin.JS_DEV_SERVER_TASK_NAME) as DevServerTask
        val wasmDevServer = project.tasks.getByName(TeaVMPlugin.WASM_GC_DEV_SERVER_TASK_NAME) as DevServerTask

        assertTrue(jsRun is GdxTeaVMRunDevServerTask)
        assertTrue(wasmRun is GdxTeaVMRunDevServerTask)
        assertTrue(dependencyNames(jsRun).contains(TeaVMPlugin.JS_DEV_SERVER_TASK_NAME))
        assertTrue(dependencyNames(wasmRun).contains(TeaVMPlugin.WASM_GC_DEV_SERVER_TASK_NAME))
        assertFalse(dependencyNames(jsRun).contains("gdx_teavm_web_js_build"))
        assertFalse(dependencyNames(wasmRun).contains("gdx_teavm_web_wasm_build"))
        assertEquals(8181, jsDevServer.port.get())
        assertEquals(8282, wasmDevServer.port.get())
        assertFalse(jsDevServer.autoReload.get())
        assertFalse(wasmDevServer.autoReload.get())
        assertEquals(1024, jsDevServer.processMemory.get())
        assertEquals(1024, wasmDevServer.processMemory.get())
        assertEquals("/", jsDevServer.targetFilePath.get())
        assertEquals("/", wasmDevServer.targetFilePath.get())
        assertFalse(jsDevServer.projectPath.get() == wasmDevServer.projectPath.get())
        assertTrue(jsDevServer.projectPath.get().contains("#gdx-teavm-js-"))
        assertTrue(wasmDevServer.projectPath.get().contains("#gdx-teavm-wasm-"))
        assertTrue(jsDevServer.allProjectPaths.get().contains(jsDevServer.projectPath.get()))
        assertTrue(wasmDevServer.allProjectPaths.get().contains(wasmDevServer.projectPath.get()))
        assertFalse(jsDevServer.allProjectPaths.get().contains(wasmDevServer.projectPath.get()))
        assertFalse(wasmDevServer.allProjectPaths.get().contains(jsDevServer.projectPath.get()))
        jsRun as GdxTeaVMRunDevServerTask
        wasmRun as GdxTeaVMRunDevServerTask
        assertEquals(jsDevServer.projectPath.get(), jsRun.devServerProjectPath.get())
        assertEquals(wasmDevServer.projectPath.get(), wasmRun.devServerProjectPath.get())
        assertTrue(jsRun.deploymentId.get().endsWith(":js"))
        assertTrue(wasmRun.deploymentId.get().endsWith(":wasm"))
        assertFalse(jsRun.deploymentId.get() == wasmRun.deploymentId.get())
        assertEquals(8181, jsRun.port.get())
        assertEquals(8282, wasmRun.port.get())
        assertTrue(jsRun.autoBuild.get())
        assertTrue(wasmRun.autoBuild.get())
        assertTrue(jsRun.autoReload.get())
        assertTrue(wasmRun.autoReload.get())
        assertEquals("/app.js.ws", jsRun.reloadEndpoint.get())
        assertEquals("/app.wasm.ws", wasmRun.reloadEndpoint.get())
        assertFalse(jsRun.entryServerPort.get() == jsRun.port.get())
        assertFalse(wasmRun.entryServerPort.get() == wasmRun.port.get())
        assertEquals("http://127.0.0.1:${jsRun.entryServerPort.get()}", jsDevServer.proxyUrl.get())
        assertEquals("http://127.0.0.1:${wasmRun.entryServerPort.get()}", wasmDevServer.proxyUrl.get())
        assertEquals("/", jsDevServer.proxyPath.get())
        assertEquals("/", wasmDevServer.proxyPath.get())
        assertFalse(jsDevServer.properties.get().containsKey("gdx.teavm.classpath"))
        assertFalse(wasmDevServer.properties.get().containsKey("gdx.teavm.classpath"))
        if(System.getProperty("os.name", "").startsWith("Windows", ignoreCase = true)) {
            val pathingJar = project.tasks.getByName("gdxTeaVMDevServerClasspathJar")
            assertTrue(pathingJar is Jar)
            assertEquals(
                setOf("gdx-teavm-dev-server-classpath.jar"),
                jsDevServer.serverClasspath.files.mapTo(linkedSetOf()) { file -> file.name }
            )
            assertEquals(
                setOf("gdx-teavm-dev-server-classpath.jar"),
                wasmDevServer.serverClasspath.files.mapTo(linkedSetOf()) { file -> file.name }
            )
        }
    }

    @Test
    fun `development server automatic build can be disabled`() {
        val project = configuredProject { extension ->
            extension.js(Action {
                mainClass.set("example.JsMain")
                devServer(Action {
                    enabled.set(true)
                    autoBuild.set(false)
                })
            })
        }

        val jsRun = project.tasks.getByName("gdx_teavm_web_js_run") as GdxTeaVMRunDevServerTask

        assertFalse(jsRun.autoBuild.get())
    }

    private fun configuredProject(configure: (GdxTeaVMExtension) -> Unit): Project {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(GdxTeaVMGradlePlugin::class.java)
        configure(project.extensions.getByType(GdxTeaVMExtension::class.java))
        (project as ProjectInternal).evaluate()
        return project
    }

    private fun dependencyNames(task: org.gradle.api.Task): Set<String> {
        return task.taskDependencies.getDependencies(task).mapTo(linkedSetOf()) { dependency -> dependency.name }
    }
}
