package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.logging.LogLevel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class GdxTeaVMBuildDaemonOutputTest {
    @Test
    fun `TeaVM build daemon stderr label is removed without demoting the error`() {
        val message = "Build daemon [stderr]: | Copied [Internal] assets/ui/ui.png (11293 bytes)"
        var errorMessage: String? = null

        val rewritten = rewriteTeaVMBuildDaemonStderr(LogLevel.ERROR, message) { errorMessage = it }

        assertNull(rewritten)
        assertEquals("| Copied [Internal] assets/ui/ui.png (11293 bytes)", errorMessage)
    }

    @Test
    fun `ordinary errors and non-error daemon messages are unchanged`() {
        assertEquals(
            "ordinary error",
            rewriteTeaVMBuildDaemonStderr(LogLevel.ERROR, "ordinary error") {
                throw AssertionError("Ordinary errors must not be rewritten")
            }
        )
        assertEquals(
            "Build daemon [stderr]: informational message",
            rewriteTeaVMBuildDaemonStderr(LogLevel.INFO, "Build daemon [stderr]: informational message") {
                throw AssertionError("Non-error messages must not be rewritten")
            }
        )
    }
}
