package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class GdxTeaVMGlfwExtensionTest {
    @Test
    fun `cmake definitions use ordered indexed native properties`() {
        val project = ProjectBuilder.builder().build()
        val extension = GdxTeaVMExtension(project.objects, project)
        extension.glfw.cmakeDefinition("FIRST", "enabled")
        extension.glfw.cmakeDefinition("SECOND", "native resources")

        val properties = extension.toNativeProperties(project, extension.glfw).get()

        assertEquals("FIRST", properties["gdx.teavm.native.cmakeDefinitions.00000000.name"])
        assertEquals("enabled", properties["gdx.teavm.native.cmakeDefinitions.00000000.value"])
        assertEquals("SECOND", properties["gdx.teavm.native.cmakeDefinitions.00000001.name"])
        assertEquals("native resources", properties["gdx.teavm.native.cmakeDefinitions.00000001.value"])
    }

    @Test
    fun `cmake definition rejects blank names and null values`() {
        val project = ProjectBuilder.builder().build()
        val extension = GdxTeaVMExtension(project.objects, project)

        assertThrows(IllegalArgumentException::class.java) {
            extension.glfw.cmakeDefinition(" ", "value")
        }
        assertThrows(IllegalArgumentException::class.java) {
            extension.glfw.cmakeDefinition("NAME", null)
        }
    }
}
