package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Project
import org.gradle.api.internal.project.ProjectInternal
import org.gradle.api.logging.LogLevel
import org.gradle.internal.logging.LoggingManagerFactory
import org.gradle.internal.logging.events.LogEvent
import org.gradle.internal.logging.events.OutputEvent
import org.gradle.internal.logging.events.OutputEventListener
import org.gradle.internal.logging.events.ProgressCompleteEvent
import org.gradle.internal.logging.events.ProgressEvent
import org.gradle.internal.logging.events.ProgressStartEvent
import org.gradle.internal.logging.events.StyledTextOutputEvent

internal const val GRADLE_DEPLOYMENT_TRANSITION_MESSAGE =
    "Reloadable deployment detected. Entering continuous build."
internal const val GRADLE_CONTINUOUS_WAITING_MESSAGE = "Waiting for changes to input files"

/**
 * A deployment started without `--continuous` makes Gradle run the task graph a second time so it
 * can discover the inputs to watch. Keep that automatic behavior, but hide this Gradle-owned
 * bootstrap pass because the initial TeaVM build and its useful output have already completed.
 * Errors are always forwarded, and normal output resumes before the first real source rebuild.
 */
internal class GdxTeaVMAutomaticContinuousOutput internal constructor(
    private val delegate: OutputEventListener,
    private val lock: Any,
    private val uninstall: (GdxTeaVMAutomaticContinuousOutput) -> Unit
) : OutputEventListener {
    private enum class State {
        IDLE,
        ARMED,
        FILTERING
    }

    private var state = State.IDLE
    private var owners = 1
    private var detachAfterEvent = false

    fun arm() {
        synchronized(lock) {
            if(state == State.IDLE) {
                state = State.ARMED
            }
        }
    }

    fun retain(): GdxTeaVMAutomaticContinuousOutput {
        synchronized(lock) {
            owners++
        }
        return this
    }

    fun close() {
        synchronized(lock) {
            if(owners == 0) {
                return
            }
            owners--
            if(owners == 0) {
                state = State.IDLE
                uninstall(this)
            }
        }
    }

    override fun onOutput(event: OutputEvent) {
        val forward = synchronized(lock) {
            val result = shouldForward(event)
            if(detachAfterEvent) {
                detachAfterEvent = false
                owners = 0
                uninstall(this)
            }
            result
        }
        if(forward) {
            delegate.onOutput(event)
        }
    }

    private fun shouldForward(event: OutputEvent): Boolean {
        val text = eventText(event)
        when(state) {
            State.IDLE -> return true
            State.ARMED -> {
                if(text?.contains(GRADLE_DEPLOYMENT_TRANSITION_MESSAGE) == true) {
                    state = State.FILTERING
                    return false
                }
                if(text?.contains(GRADLE_CONTINUOUS_WAITING_MESSAGE) == true) {
                    state = State.IDLE
                    detachAfterEvent = true
                }
                return true
            }
            State.FILTERING -> {
                if(text?.contains(GRADLE_CONTINUOUS_WAITING_MESSAGE) == true) {
                    state = State.IDLE
                    detachAfterEvent = true
                    return false
                }
                if(text?.contains("Change detected, executing build...") == true
                    || text?.contains("Build cancelled.") == true) {
                    state = State.IDLE
                    return true
                }
                if(event is ProgressStartEvent || event is ProgressEvent || event is ProgressCompleteEvent) {
                    return false
                }
                return event.logLevel == LogLevel.ERROR || event.logLevel == null
            }
        }
    }

    private fun eventText(event: OutputEvent): String? {
        return when(event) {
            is LogEvent -> event.message
            is StyledTextOutputEvent -> event.spans.joinToString(separator = "") { span -> span.text }
            else -> null
        }
    }

    companion object {
        fun install(project: Project): GdxTeaVMAutomaticContinuousOutput? {
            return try {
                val services = (project as ProjectInternal).services
                val rootLoggingManager = services.get(LoggingManagerFactory::class.java).root
                val startableRouter = readField(rootLoggingManager, "loggingRouter")
                val renderer = readField(startableRouter, "loggingRouter")
                val lock = readField(renderer, "lock")
                val transformer = readField(renderer, "transformer")
                val listenerField = accessibleField(transformer, "listener")

                synchronized(lock) {
                    val current = listenerField.get(transformer) as OutputEventListener
                    if(current is GdxTeaVMAutomaticContinuousOutput) {
                        return@synchronized current.retain()
                    }

                    val output = GdxTeaVMAutomaticContinuousOutput(current, lock) { installed ->
                        if(listenerField.get(transformer) === installed) {
                            listenerField.set(transformer, current)
                        }
                    }
                    listenerField.set(transformer, output)
                    output
                }
            }
            catch(t: Exception) {
                project.logger.info(
                    "Could not suppress Gradle's automatic deployment transition output.",
                    t
                )
                null
            }
        }

        private fun readField(instance: Any, name: String): Any {
            return accessibleField(instance, name).get(instance)
                ?: throw IllegalStateException("Gradle logging field '$name' is null")
        }

        private fun accessibleField(instance: Any, name: String): java.lang.reflect.Field {
            var type: Class<*>? = instance.javaClass
            while(type != null) {
                try {
                    return type.getDeclaredField(name).also { field ->
                        if(!field.trySetAccessible()) {
                            throw IllegalStateException("Gradle logging field '$name' is not accessible")
                        }
                    }
                }
                catch(_: NoSuchFieldException) {
                    type = type.superclass
                }
            }
            throw NoSuchFieldException("Gradle logging field '$name' does not exist")
        }
    }
}
