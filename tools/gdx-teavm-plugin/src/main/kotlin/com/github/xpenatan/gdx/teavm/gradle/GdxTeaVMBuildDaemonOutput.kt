package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.GradleException
import org.gradle.api.logging.LogLevel
import org.gradle.internal.logging.slf4j.ContextAwareTaskLogger
import org.teavm.gradle.tasks.TeaVMTask

internal const val TEAVM_BUILD_DAEMON_STDERR_PREFIX = "Build daemon [stderr]:"

/**
 * TeaVM labels every line forwarded from its out-of-process compiler's stderr. Remove that
 * transport detail while preserving ERROR-level logging so stderr remains red in Gradle consoles.
 */
internal fun restoreTeaVMBuildDaemonStderr(task: TeaVMTask) {
    val contextLogger = task.logger as? ContextAwareTaskLogger
        ?: throw GradleException("The Gradle task logger cannot restore TeaVM build-daemon stderr output")
    contextLogger.setMessageRewriter(::rewriteTeaVMBuildDaemonStderr)
}

internal fun rewriteTeaVMBuildDaemonStderr(
    level: LogLevel,
    message: String
): String {
    if(level == LogLevel.ERROR && message.startsWith(TEAVM_BUILD_DAEMON_STDERR_PREFIX)) {
        return message.removePrefix(TEAVM_BUILD_DAEMON_STDERR_PREFIX).trimStart()
    }
    return message
}
