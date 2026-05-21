package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader
import java.util.concurrent.CountDownLatch

@DisableCachingByDefault(because = "Starts a blocking local HTTP server")
abstract class GdxTeaVMRunWebTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val webappDir: DirectoryProperty

    @get:Input
    abstract val port: Property<Int>

    @get:Classpath
    abstract val serverClasspath: ConfigurableFileCollection

    @TaskAction
    fun run() {
        val root = webappDir.get().asFile.canonicalFile
        val classLoader = createServerClassLoader()
        val server = createJettyServer(classLoader)
        val shutdownHook = Thread {
            stopServer(server)
        }

        Runtime.getRuntime().addShutdownHook(shutdownHook)
        try {
            startServer(server, port.get(), root.absolutePath)
            logger.lifecycle("Serving ${root.absolutePath}")
            CountDownLatch(1).await()
        }
        finally {
            stopServer(server)
            removeShutdownHook(shutdownHook)
            classLoader.close()
        }
    }

    private fun createServerClassLoader(): URLClassLoader {
        val urls = serverClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        return URLClassLoader(urls, javaClass.classLoader)
    }

    private fun createJettyServer(classLoader: ClassLoader): Any {
        try {
            val serverClass = classLoader.loadClass(JETTY_SERVER_CLASS_NAME)
            return serverClass.getDeclaredConstructor().newInstance()
        }
        catch(e: ClassNotFoundException) {
            throw GradleException(
                "Could not find $JETTY_SERVER_CLASS_NAME on the project runtime classpath. " +
                        "Check that the gdx-teavm plugin added the backend-web dependency to the TeaVM classpath.",
                e
            )
        }
        catch(e: InvocationTargetException) {
            throw e.targetException
        }
    }

    private fun startServer(server: Any, port: Int, webappPath: String) {
        invokeServerMethod(server, "startServer", Integer.TYPE, String::class.java, java.lang.Boolean.TYPE) {
            it.invoke(server, port, webappPath, false)
        }
        if(!isServerRunning(server)) {
            throw GradleException("Jetty server did not start on port $port")
        }
    }

    private fun stopServer(server: Any) {
        invokeServerMethod(server, "stopServer") {
            it.invoke(server)
        }
    }

    private fun isServerRunning(server: Any): Boolean {
        return invokeServerMethod(server, "isServerRunning") {
            it.invoke(server)
        } as Boolean
    }

    private fun removeShutdownHook(shutdownHook: Thread) {
        try {
            Runtime.getRuntime().removeShutdownHook(shutdownHook)
        }
        catch(_: IllegalStateException) {
            // JVM shutdown is already in progress.
        }
    }

    private fun invokeServerMethod(server: Any, name: String, vararg parameterTypes: Class<*>, invoke: (java.lang.reflect.Method) -> Any?): Any? {
        try {
            return invoke(server.javaClass.getMethod(name, *parameterTypes))
        }
        catch(e: InvocationTargetException) {
            throw e.targetException
        }
        catch(e: ReflectiveOperationException) {
            throw GradleException("Could not call $JETTY_SERVER_CLASS_NAME.$name", e)
        }
    }

    private companion object {
        const val JETTY_SERVER_CLASS_NAME = "com.github.xpenatan.gdx.teavm.backends.web.config.backend.JettyServer"
    }
}
