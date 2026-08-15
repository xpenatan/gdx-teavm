package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.tasks.DevServerTask
import org.teavm.gradle.tasks.GenerateCTask
import org.teavm.gradle.tasks.GenerateJavaScriptTask
import org.teavm.gradle.tasks.GenerateWasmGCTask

class GdxTeaVMNamedTargetsTest {
    @Test
    fun `defaults are optional and built-in target conventions remain available`() {
        val project = configuredProject { extension ->
            extension.js(Action {
                mainClass.set("example.WebMain")
            })
            extension.glfw(Action {
                mainClass.set("example.GlfwMain")
            })
        }
        val extension = project.extensions.getByType(GdxTeaVMExtension::class.java)

        assertEquals(800, extension.js.htmlWidth.get())
        assertEquals(600, extension.js.htmlHeight.get())
        assertEquals(OptimizationLevel.BALANCED, extension.js.optimization.get())
        assertEquals(4, extension.glfw.minHeapSizeMb.get())
        assertEquals(128, extension.glfw.maxHeapSizeMb.get())
    }

    @Test
    fun `defaults apply regardless of declaration order and target values override them`() {
        val project = configuredProject { extension ->
            extension.js("dev", Action {
                htmlWidth.set(1920)
            })
            extension.glfw("debug", Action {
                maxHeapSizeMb.set(768)
            })
            extension.webDefaults(Action {
                mainClass.set("example.WebMain")
                htmlWidth.set(1280)
                htmlHeight.set(720)
                obfuscated.set(false)
            })
            extension.nativeDefaults(Action {
                minHeapSizeMb.set(64)
                maxHeapSizeMb.set(512)
                processMemory.set(1024)
            })
        }
        val extension = project.extensions.getByType(GdxTeaVMExtension::class.java)
        val js = extension.jsTargets().single().target
        val glfw = extension.glfwTargets().single().target

        assertEquals("example.WebMain", js.mainClass.get())
        assertEquals(1920, js.htmlWidth.get())
        assertEquals(720, js.htmlHeight.get())
        assertFalse(js.obfuscated.get())
        assertEquals(64, glfw.minHeapSizeMb.get())
        assertEquals(768, glfw.maxHeapSizeMb.get())
        assertEquals(1024, glfw.processMemory.get())
    }

    @Test
    fun `named JavaScript targets have isolated tasks configuration and output`() {
        val project = configuredProject { extension ->
            extension.webDefaults(Action {
                mainClass.set("example.WebMain")
                htmlWidth.set(1280)
            })
            extension.js("dev", Action {
                htmlTitle.set("Development")
                serverPort.set(8181)
                devServer(Action {
                    enabled.set(true)
                })
            })
            extension.js("release", Action {
                htmlTitle.set("Release")
                obfuscated.set(true)
            })
        }
        val extension = project.extensions.getByType(GdxTeaVMExtension::class.java)
        val targets = extension.jsTargets().associateBy { registration -> registration.name }
        val dev = targets.getValue("dev").target
        val release = targets.getValue("release").target

        assertNotSame(dev, release)
        assertEquals("example.WebMain", dev.mainClass.get())
        assertEquals("example.WebMain", release.mainClass.get())
        assertEquals("Development", dev.htmlTitle.get())
        assertEquals("Release", release.htmlTitle.get())
        assertTrue(dev.outputDir.get().asFile.invariantSeparatorsPath.endsWith("/build/dist/js/dev"))
        assertTrue(release.outputDir.get().asFile.invariantSeparatorsPath.endsWith("/build/dist/js/release"))

        val devCompile = project.tasks.getByName("gdx_teavm_web_js_dev_compile") as GenerateJavaScriptTask
        val releaseCompile = project.tasks.getByName("gdx_teavm_web_js_release_compile") as GenerateJavaScriptTask
        assertEquals("example.WebMain", devCompile.mainClass.get())
        assertEquals("example.WebMain", releaseCompile.mainClass.get())
        assertFalse(devCompile.outputDir.get() == releaseCompile.outputDir.get())
        assertTrue(project.tasks.getByName("gdx_teavm_web_js_dev_run") is GdxTeaVMRunDevServerTask)
        assertTrue(project.tasks.getByName("gdx_teavm_web_js_release_run") is GdxTeaVMRunWebTask)
        assertTrue(project.tasks.getByName("gdx_teavm_web_js_dev_dev_server") is DevServerTask)
        assertTrue(dependencyNames(project, "gdx_teavm_web_js_dev_build").contains(devCompile.name))
        assertTrue(dependencyNames(project, "gdx_teavm_web_js_release_build").contains(releaseCompile.name))
    }

    @Test
    fun `named Wasm targets create build run compile and runtime tasks`() {
        val project = configuredProject { extension ->
            extension.webDefaults(Action {
                mainClass.set("example.WebMain")
            })
            extension.wasm("dev", Action {
                devServer(Action {
                    enabled.set(true)
                })
            })
            extension.wasm("release", Action {
                processMemory.set(2048)
            })
        }

        assertTrue(project.tasks.getByName("gdx_teavm_web_wasm_dev_compile") is GenerateWasmGCTask)
        assertTrue(project.tasks.getByName("gdx_teavm_web_wasm_release_compile") is GenerateWasmGCTask)
        assertTrue(project.tasks.getByName("gdx_teavm_web_wasm_dev_run") is GdxTeaVMRunDevServerTask)
        assertTrue(project.tasks.getByName("gdx_teavm_web_wasm_release_run") is GdxTeaVMRunWebTask)
        assertTrue(project.tasks.findByName("gdx_teavm_web_wasm_dev_copy_runtime") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_web_wasm_release_copy_runtime") != null)
        assertTrue(
            dependencyNames(project, "gdx_teavm_web_wasm_release_build")
                .contains("gdx_teavm_web_wasm_release_copy_runtime")
        )
    }

    @Test
    fun `named GLFW targets create isolated TeaVM C generate build and run tasks`() {
        val project = configuredProject { extension ->
            extension.nativeDefaults(Action {
                mainClass.set("example.GlfwMain")
                minHeapSizeMb.set(64)
                maxHeapSizeMb.set(512)
            })
            extension.glfw("debug", Action {
                buildType.set("Debug")
                debugInformation.set(true)
            })
            extension.glfw("release", Action {
                buildType.set("Release")
                optimization.set(OptimizationLevel.BALANCED)
            })
        }

        val debugGenerate = project.tasks.getByName("gdx_teavm_glfw_debug_generate") as GenerateCTask
        val releaseGenerate = project.tasks.getByName("gdx_teavm_glfw_release_generate") as GenerateCTask
        assertEquals("example.GlfwMain", debugGenerate.mainClass.get())
        assertEquals(64, debugGenerate.minHeapSize.get())
        assertEquals(512, releaseGenerate.maxHeapSize.get())
        assertFalse(debugGenerate.outputDir.get() == releaseGenerate.outputDir.get())
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_debug_build") is GdxTeaVMNativeBuildTask)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_debug_run") is GdxTeaVMGlfwRunTask)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_release_build") is GdxTeaVMNativeBuildTask)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_release_run") is GdxTeaVMGlfwRunTask)
    }

    @Test
    fun `named iOS target creates the full lifecycle with isolated paths`() {
        val project = configuredProject { extension ->
            extension.nativeDefaults(Action {
                maxHeapSizeMb.set(512)
            })
            extension.ios("debug", Action {
                mainClass.set("example.IosMain")
            })
        }
        val target = project.extensions.getByType(GdxTeaVMExtension::class.java).iosTargets().single().target

        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_generate") is GenerateCTask)
        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_prepare_angle") is GdxTeaVMIosPrepareAngleTask)
        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_init_xcode") is GdxTeaVMIosInitXcodeTask)
        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_regenerate_xcode") is GdxTeaVMIosInitXcodeTask)
        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_open_xcode") is GdxTeaVMIosOpenXcodeTask)
        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_build_simulator") is GdxTeaVMIosBuildSimulatorTask)
        assertTrue(project.tasks.getByName("gdx_teavm_ios_debug_run_simulator") is GdxTeaVMIosRunSimulatorTask)
        assertTrue(target.outputDir.get().asFile.invariantSeparatorsPath.endsWith("/build/dist/ios/debug"))
        assertTrue(target.xcodeDerivedDataPath.get().asFile.invariantSeparatorsPath.endsWith("/build/xcode-derived/ios/debug"))
        val initTask = project.tasks.getByName("gdx_teavm_ios_debug_init_xcode") as GdxTeaVMIosInitXcodeTask
        assertEquals("gdx_teavm_ios_debug", initTask.gradleTaskPrefix.get())
    }

    @Test
    fun `legacy and named targets coexist without changing legacy task names`() {
        val project = configuredProject { extension ->
            extension.js(Action {
                mainClass.set("example.DefaultMain")
            })
            extension.js("preview", Action {
                mainClass.set("example.PreviewMain")
            })
            extension.glfw(Action {
                mainClass.set("example.DefaultGlfwMain")
            })
            extension.glfw("release", Action {
                mainClass.set("example.ReleaseGlfwMain")
            })
        }

        assertTrue(project.tasks.findByName("gdx_teavm_web_js_build") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_web_js_run") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_web_js_preview_build") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_web_js_preview_run") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_generate") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_build") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_run") != null)
        assertTrue(project.tasks.findByName("gdx_teavm_glfw_release_generate") != null)
    }

    @Test
    fun `repeated names configure one target and normalized name collisions are rejected`() {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(GdxTeaVMGradlePlugin::class.java)
        val extension = project.extensions.getByType(GdxTeaVMExtension::class.java)

        extension.js("previewBuild", Action {
            mainClass.set("example.WebMain")
        })
        extension.js("previewBuild", Action {
            htmlTitle.set("Preview")
        })

        assertEquals(1, extension.jsTargets().size)
        assertEquals("preview_build", extension.jsTargets().single().taskNameSegment)
        assertEquals("Preview", extension.jsTargets().single().target.htmlTitle.get())
        assertThrows(IllegalArgumentException::class.java) {
            extension.js("preview-build", Action {})
        }
        assertThrows(IllegalArgumentException::class.java) {
            extension.wasm("   ", Action {})
        }
    }

    @Test
    fun `Android target inherits optional native defaults`() {
        val project = ProjectBuilder.builder().build()
        val extension = GdxTeaVMExtension(project.objects, project)
        extension.nativeDefaults(Action {
            minHeapSizeMb.set(32)
            maxHeapSizeMb.set(256)
        })
        extension.android(Action {
            mainClass.set("example.AndroidMain")
            maxHeapSizeMb.set(384)
        })

        assertEquals(32, extension.android.minHeapSizeMb.get())
        assertEquals(384, extension.android.maxHeapSizeMb.get())
    }

    private fun configuredProject(configure: (GdxTeaVMExtension) -> Unit): Project {
        val project = ProjectBuilder.builder().build()
        project.pluginManager.apply(GdxTeaVMGradlePlugin::class.java)
        configure(project.extensions.getByType(GdxTeaVMExtension::class.java))
        (project as ProjectInternal).evaluate()
        return project
    }

    private fun dependencyNames(project: Project, taskName: String): Set<String> {
        val task = project.tasks.getByName(taskName)
        return task.taskDependencies.getDependencies(task).mapTo(linkedSetOf()) { dependency -> dependency.name }
    }
}
