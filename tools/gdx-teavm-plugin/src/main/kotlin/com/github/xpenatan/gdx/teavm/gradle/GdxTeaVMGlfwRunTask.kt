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
import java.io.File
import java.lang.reflect.InvocationTargetException
import java.net.URLClassLoader

@DisableCachingByDefault(because = "Runs a generated native executable")
abstract class GdxTeaVMGlfwRunTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildRoot: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val generatedSourcesDir: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val releaseDir: DirectoryProperty

    @get:Input
    abstract val projectName: Property<String>

    @get:Input
    abstract val buildType: Property<String>

    @get:Input
    abstract val consoleLog: Property<Boolean>

    @get:Classpath
    abstract val backendClasspath: ConfigurableFileCollection

    @TaskAction
    fun run() {
        val classLoader = createBackendClassLoader()
        try {
            val nativeProject = createNativeProject(classLoader)
            val nativeBuildType = createNativeBuildType(classLoader)
            runExecutable(classLoader, nativeProject, nativeBuildType)
        }
        finally {
            classLoader.close()
        }
    }

    private fun createBackendClassLoader(): URLClassLoader {
        val urls = backendClasspath.files.map { it.toURI().toURL() }.toTypedArray()
        return URLClassLoader(urls, javaClass.classLoader)
    }

    private fun createNativeProject(classLoader: ClassLoader): Any {
        val nativeProjectClass = loadClass(classLoader, GLFW_NATIVE_PROJECT_CLASS_NAME)
        return try {
            nativeProjectClass
                .getDeclaredConstructor(
                    ClassLoader::class.java,
                    File::class.java,
                    File::class.java,
                    File::class.java
                )
                .newInstance(
                    classLoader,
                    buildRoot.get().asFile,
                    generatedSourcesDir.get().asFile,
                    releaseDir.get().asFile
                )
        }
        catch(e: InvocationTargetException) {
            throw e.targetException
        }
        catch(e: ReflectiveOperationException) {
            throw GradleException("Could not create $GLFW_NATIVE_PROJECT_CLASS_NAME", e)
        }
    }

    private fun createNativeBuildType(classLoader: ClassLoader): Any {
        val nativeBuildTypeClass = loadClass(classLoader, GLFW_NATIVE_BUILD_TYPE_CLASS_NAME)
        return try {
            nativeBuildTypeClass
                .getMethod("fromString", String::class.java)
                .invoke(null, buildType.get())
        }
        catch(e: InvocationTargetException) {
            throw e.targetException
        }
        catch(e: ReflectiveOperationException) {
            throw GradleException("Could not resolve GLFW native build type '${buildType.get()}'", e)
        }
    }

    private fun runExecutable(classLoader: ClassLoader, nativeProject: Any, nativeBuildType: Any) {
        val nativeProjectClass = loadClass(classLoader, GLFW_NATIVE_PROJECT_CLASS_NAME)
        val nativeBuildTypeClass = loadClass(classLoader, GLFW_NATIVE_BUILD_TYPE_CLASS_NAME)
        try {
            nativeProjectClass
                .getMethod("runExecutable", String::class.java, nativeBuildTypeClass, java.lang.Boolean.TYPE)
                .invoke(nativeProject, projectName.get(), nativeBuildType, consoleLog.get())
        }
        catch(e: InvocationTargetException) {
            throw e.targetException
        }
        catch(e: ReflectiveOperationException) {
            throw GradleException("Could not run GLFW executable with $GLFW_NATIVE_PROJECT_CLASS_NAME", e)
        }
    }

    private fun loadClass(classLoader: ClassLoader, className: String): Class<*> {
        try {
            return classLoader.loadClass(className)
        }
        catch(e: ClassNotFoundException) {
            throw GradleException("Could not find $className on the project runtime classpath", e)
        }
    }

    private companion object {
        const val GLFW_NATIVE_PROJECT_CLASS_NAME =
            "com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWNativeProject"
        const val GLFW_NATIVE_BUILD_TYPE_CLASS_NAME =
            "com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend\$NativeBuildType"
    }
}
