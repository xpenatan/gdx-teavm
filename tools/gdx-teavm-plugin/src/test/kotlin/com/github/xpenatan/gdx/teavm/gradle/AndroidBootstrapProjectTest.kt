package com.github.xpenatan.gdx.teavm.gradle

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import java.nio.file.Files

class AndroidBootstrapProjectTest {
    @Test
    fun `ensure writes bootstrap CMake project when generated sources are missing`() {
        val buildRoot = Files.createTempDirectory("gdx-teavm-android-bootstrap").toFile()
        try {
            val generatedSourcesDir = File(buildRoot, "c/src")

            AndroidBootstrapProject.ensure(buildRoot, generatedSourcesDir, "app")

            assertEquals(
                AndroidBootstrapProject.bootstrapCMake("app"),
                File(buildRoot, "CMakeLists.txt").readText()
            )
            assertEquals(
                AndroidBootstrapProject.bootstrapSource(),
                File(buildRoot, "bootstrap.c").readText()
            )
            assertTrue(generatedSourcesDir.isDirectory)
        }
        finally {
            buildRoot.deleteRecursively()
        }
    }

    @Test
    fun `ensure preserves generated TeaVM Android project`() {
        val buildRoot = Files.createTempDirectory("gdx-teavm-android-generated").toFile()
        try {
            val generatedSourcesDir = File(buildRoot, "c/src").apply { mkdirs() }
            File(generatedSourcesDir, "app_include.c").writeText("generated entrypoint\n")
            val cmakeFile = File(buildRoot, "CMakeLists.txt").apply {
                writeText("real generated cmake\n")
            }

            AndroidBootstrapProject.ensure(buildRoot, generatedSourcesDir, "app")

            assertEquals("real generated cmake\n", cmakeFile.readText())
            assertFalse(File(buildRoot, "bootstrap.c").exists())
        }
        finally {
            buildRoot.deleteRecursively()
        }
    }
}
