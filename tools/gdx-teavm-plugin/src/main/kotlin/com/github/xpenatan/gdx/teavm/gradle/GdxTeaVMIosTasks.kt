package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Classpath
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.work.DisableCachingByDefault
import java.io.File
import java.net.URI
import java.security.MessageDigest
import java.util.zip.ZipFile

@DisableCachingByDefault(because = "Downloads and extracts the selected ANGLE framework payload")
abstract class GdxTeaVMIosPrepareAngleTask : DefaultTask() {
    @get:OutputDirectory
    abstract val frameworksDir: DirectoryProperty

    @TaskAction
    fun prepareAngle() {
        val outputDir = frameworksDir.get().asFile
        val marker = File(outputDir, ".metalanglekit-$METALANGLEKIT_VERSION")
        if(marker.isFile) {
            return
        }

        val cacheDir = File(project.gradle.gradleUserHomeDir, "caches/gdx-teavm/ios")
        cacheDir.mkdirs()
        val zipFile = File(cacheDir, "metalanglekit-$METALANGLEKIT_VERSION.zip")
        if(!zipFile.isFile || sha256(zipFile) != METALANGLEKIT_SHA256) {
            logger.lifecycle("Downloading MetalANGLEKit $METALANGLEKIT_VERSION")
            URI(METALANGLEKIT_URL).toURL().openStream().use { input ->
                zipFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        val digest = sha256(zipFile)
        if(digest != METALANGLEKIT_SHA256) {
            throw GradleException("MetalANGLEKit checksum mismatch. Expected $METALANGLEKIT_SHA256 but got $digest")
        }

        project.delete(outputDir)
        outputDir.mkdirs()
        ZipFile(zipFile).use { zip ->
            val entries = zip.entries()
            while(entries.hasMoreElements()) {
                val entry = entries.nextElement()
                val output = File(outputDir, entry.name)
                if(entry.isDirectory) {
                    output.mkdirs()
                }
                else {
                    output.parentFile?.mkdirs()
                    zip.getInputStream(entry).use { input ->
                        output.outputStream().use { fileOutput ->
                            input.copyTo(fileOutput)
                        }
                    }
                }
            }
        }
        marker.writeText(METALANGLEKIT_SHA256)
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while(true) {
                val read = input.read(buffer)
                if(read < 0) {
                    break
                }
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
    }

    private companion object {
        const val METALANGLEKIT_VERSION = "v1.2.1"
        const val METALANGLEKIT_URL =
            "https://github.com/libgdx/MetalANGLEKit/releases/download/v1.2.1/metalanglekit.zip"
        const val METALANGLEKIT_SHA256 =
            "c7785cbe15eb9e5962677513725c8f0e33039f344235cc7691ee5ac35ff5ea91"
    }
}

@DisableCachingByDefault(because = "Creates an editable Xcode project from backend templates")
abstract class GdxTeaVMIosInitXcodeTask : DefaultTask() {
    @get:Classpath
    abstract val backendClasspath: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val xcodeProjectDir: DirectoryProperty

    @get:Internal
    abstract val generatedSourcesDir: DirectoryProperty

    @get:Internal
    abstract val releasePath: DirectoryProperty

    @get:Input
    abstract val xcodeProjectName: Property<String>

    @get:Input
    abstract val gradleProjectPath: Property<String>

    @get:Input
    abstract val nativeLibraryName: Property<String>

    @get:Input
    abstract val bundleIdentifier: Property<String>

    @get:Input
    abstract val overwrite: Property<Boolean>

    @get:Input
    abstract val graphicsApi: Property<String>

    @TaskAction
    fun initXcode() {
        val xcodeRoot = xcodeProjectDir.get().asFile
        val projectName = xcodeProjectName.get()
        val api = normalizeGraphicsApi(graphicsApi.get())
        val projectFile = File(xcodeRoot, "$projectName.xcodeproj/project.pbxproj")
        if(projectFile.isFile && !overwrite.get()) {
            logger.lifecycle("Xcode project already exists: ${projectFile.parentFile.absolutePath}")
            return
        }

        val generatedSources = generatedSourcesDir.get().asFile
        val release = releasePath.get().asFile
        val sourcesDir = File(xcodeRoot, "Sources")
        val replacements = linkedMapOf(
            "GdxTeaVMIOSSpike" to projectName,
            "../c/src/app_include.c" to relativePath(xcodeRoot, File(generatedSources, "app_include.c")),
            "../c/release/assets" to relativePath(xcodeRoot, File(release, "assets")),
            "../../c/src/ios_bridge.h" to relativePath(sourcesDir, File(generatedSources, "ios_bridge.h")),
            "com.github.xpenatan.gdxteavm.ios.spike" to bundleIdentifier.get(),
            "\${IOS_GRADLE_PROJECT_PATH}" to gradleProjectPath.get(),
            "\${IOS_NATIVE_LIBRARY_NAME}" to nativeLibraryName.get(),
            "\${IOS_GRAPHICS_API}" to api,
            "\${IOS_VIEW_CONTROLLER_TEMPLATE}" to viewControllerTemplate(api),
            "\${IOS_FRAMEWORK_SEARCH_PATHS}" to frameworkSearchPaths(api),
            "\${IOS_GLES_BUILD_FILES}" to glesBuildFiles(api),
            "\${IOS_GLES_FILE_REFERENCES}" to glesFileReferences(api),
            "\${IOS_GLES_FRAMEWORK_FILES}" to glesFrameworkFiles(api),
            "\${IOS_GLES_FRAMEWORK_GROUP_FILES}" to glesFrameworkGroupFiles(api),
            "\${IOS_ANGLE_BUILD_FILES}" to angleBuildFiles(api),
            "\${IOS_ANGLE_FILE_REFERENCES}" to angleFileReferences(api),
            "\${IOS_ANGLE_FRAMEWORK_FILES}" to angleFrameworkFiles(api),
            "\${IOS_ANGLE_FRAMEWORK_GROUP_FILES}" to angleFrameworkGroupFiles(api),
            "\${IOS_ANGLE_COPY_BUILD_PHASE}" to angleCopyBuildPhase(api),
            "\${IOS_ANGLE_COPY_BUILD_PHASE_REF}" to angleCopyBuildPhaseRef(api),
            "\${IOS_ANGLE_EMBED_FILES}" to angleEmbedFiles(api)
        )

        writeTemplate(
            "templates/ios/xcode/GdxTeaVMIOSSpike.xcodeproj/project.pbxproj",
            projectFile,
            replacements
        )
        writeTemplate(
            "templates/ios/xcode/Sources/GdxTeaVMIOSSpikeApp.swift",
            File(sourcesDir, "${projectName}App.swift"),
            replacements
        )
        writeTemplate(
            "templates/ios/xcode/Sources/${viewControllerTemplate(api)}",
            File(sourcesDir, "TeaVMViewController.swift"),
            replacements
        )
        writeTemplate(
            "templates/ios/xcode/Sources/IOSControllerBridge.swift",
            File(sourcesDir, "IOSControllerBridge.swift"),
            replacements
        )
        writeTemplate(
            "templates/ios/xcode/Sources/GdxTeaVMIOSSpike-Bridging-Header.h",
            File(sourcesDir, "$projectName-Bridging-Header.h"),
            replacements
        )
        writeTemplate("templates/ios/xcode/Sources/Info.plist", File(sourcesDir, "Info.plist"), replacements)
        writeTemplate(
            "templates/ios/xcode/Sources/LaunchScreen.storyboard",
            File(sourcesDir, "LaunchScreen.storyboard"),
            replacements
        )
        writeTemplate("templates/ios/xcode/README.md", File(xcodeRoot, "README.md"), replacements)
    }

    private fun writeTemplate(resourceName: String, outputFile: File, replacements: Map<String, String>) {
        var content = readBackendResource(resourceName)
        replacements.forEach { (from, to) ->
            content = content.replace(from, to)
        }
        outputFile.parentFile?.mkdirs()
        outputFile.writeText(content)
    }

    private fun readBackendResource(resourceName: String): String {
        backendClasspath.files.forEach { file ->
            if(file.isDirectory) {
                val resourceFile = File(file, resourceName)
                if(resourceFile.isFile) {
                    return resourceFile.readText()
                }
            }
            else if(file.isFile) {
                ZipFile(file).use { zip ->
                    val entry = zip.getEntry(resourceName)
                    if(entry != null) {
                        return zip.getInputStream(entry).bufferedReader().readText()
                    }
                }
            }
        }
        throw GradleException("iOS Xcode template not found in backend classpath: $resourceName")
    }

    private fun relativePath(fromDirectory: File, toFile: File): String {
        val from = fromDirectory.toPath().toAbsolutePath().normalize()
        val to = toFile.toPath().toAbsolutePath().normalize()
        return from.relativize(to).toString().replace(File.separatorChar, '/')
    }

    private fun normalizeGraphicsApi(value: String): String {
        return when(value.trim().lowercase()) {
            "angle", "metalangle", "metal-angle" -> "angle"
            "gles", "opengles", "open-gles", "opengl-es" -> "gles"
            else -> throw GradleException("Unsupported iOS graphics API '$value'. Supported values are 'angle' and 'gles'.")
        }
    }

    private fun viewControllerTemplate(api: String): String {
        return if(api == "angle") "TeaVMViewControllerANGLE.swift" else "TeaVMViewController.swift"
    }

    private fun frameworkSearchPaths(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\t\t\t\t\tFRAMEWORK_SEARCH_PATHS = (\n" +
            "\t\t\t\t\t\t\"$(inherited)\",\n" +
            "\t\t\t\t\t\t\"$(PROJECT_DIR)/Frameworks/ANGLE\",\n" +
            "\t\t\t\t\t);\n"
    }

    private fun glesBuildFiles(api: String): String {
        if(api != "gles") {
            return ""
        }
        return "\t\t\t1E2A00000000000000000004 /* GLKit.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A00000000000000000015 /* GLKit.framework */; };\n" +
            "\t\t\t1E2A00000000000000000005 /* OpenGLES.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A00000000000000000016 /* OpenGLES.framework */; };\n"
    }

    private fun glesFileReferences(api: String): String {
        if(api != "gles") {
            return ""
        }
        return "\t\t\t1E2A00000000000000000015 /* GLKit.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = GLKit.framework; path = System/Library/Frameworks/GLKit.framework; sourceTree = SDKROOT; };\n" +
            "\t\t\t1E2A00000000000000000016 /* OpenGLES.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = OpenGLES.framework; path = System/Library/Frameworks/OpenGLES.framework; sourceTree = SDKROOT; };\n"
    }

    private fun glesFrameworkFiles(api: String): String {
        if(api != "gles") {
            return ""
        }
        return "\t\t\t\t\t1E2A00000000000000000004 /* GLKit.framework in Frameworks */,\n" +
            "\t\t\t\t\t1E2A00000000000000000005 /* OpenGLES.framework in Frameworks */,\n"
    }

    private fun glesFrameworkGroupFiles(api: String): String {
        if(api != "gles") {
            return ""
        }
        return "\t\t\t\t\t1E2A00000000000000000015 /* GLKit.framework */,\n" +
            "\t\t\t\t\t1E2A00000000000000000016 /* OpenGLES.framework */,\n"
    }

    private fun angleBuildFiles(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\t\t\t1E2A00000000000000000007 /* MetalANGLEKit.xcframework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A00000000000000000018 /* MetalANGLEKit.xcframework */; };\n" +
            "\t\t\t1E2A00000000000000000008 /* libGLESv2.xcframework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A00000000000000000019 /* libGLESv2.xcframework */; };\n" +
            "\t\t\t1E2A00000000000000000009 /* libEGL.xcframework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A0000000000000000001A /* libEGL.xcframework */; };\n" +
            "\t\t\t1E2A0000000000000000000A /* libfeature_support.xcframework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A0000000000000000001B /* libfeature_support.xcframework */; };\n" +
            "\t\t\t1E2A0000000000000000000B /* Metal.framework in Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A0000000000000000001C /* Metal.framework */; };\n" +
            "\t\t\t1E2A0000000000000000000C /* MetalANGLEKit.xcframework in Embed Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A00000000000000000018 /* MetalANGLEKit.xcframework */; settings = {ATTRIBUTES = (CodeSignOnCopy, RemoveHeadersOnCopy, ); }; };\n" +
            "\t\t\t1E2A0000000000000000000D /* libGLESv2.xcframework in Embed Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A00000000000000000019 /* libGLESv2.xcframework */; settings = {ATTRIBUTES = (CodeSignOnCopy, RemoveHeadersOnCopy, ); }; };\n" +
            "\t\t\t1E2A0000000000000000000E /* libEGL.xcframework in Embed Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A0000000000000000001A /* libEGL.xcframework */; settings = {ATTRIBUTES = (CodeSignOnCopy, RemoveHeadersOnCopy, ); }; };\n" +
            "\t\t\t1E2A0000000000000000000F /* libfeature_support.xcframework in Embed Frameworks */ = {isa = PBXBuildFile; fileRef = 1E2A0000000000000000001B /* libfeature_support.xcframework */; settings = {ATTRIBUTES = (CodeSignOnCopy, RemoveHeadersOnCopy, ); }; };\n"
    }

    private fun angleFileReferences(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\t\t\t1E2A00000000000000000018 /* MetalANGLEKit.xcframework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.xcframework; name = MetalANGLEKit.xcframework; path = Frameworks/ANGLE/MetalANGLEKit.xcframework; sourceTree = SOURCE_ROOT; };\n" +
            "\t\t\t1E2A00000000000000000019 /* libGLESv2.xcframework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.xcframework; name = libGLESv2.xcframework; path = Frameworks/ANGLE/libGLESv2.xcframework; sourceTree = SOURCE_ROOT; };\n" +
            "\t\t\t1E2A0000000000000000001A /* libEGL.xcframework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.xcframework; name = libEGL.xcframework; path = Frameworks/ANGLE/libEGL.xcframework; sourceTree = SOURCE_ROOT; };\n" +
            "\t\t\t1E2A0000000000000000001B /* libfeature_support.xcframework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.xcframework; name = libfeature_support.xcframework; path = Frameworks/ANGLE/libfeature_support.xcframework; sourceTree = SOURCE_ROOT; };\n" +
            "\t\t\t1E2A0000000000000000001C /* Metal.framework */ = {isa = PBXFileReference; lastKnownFileType = wrapper.framework; name = Metal.framework; path = System/Library/Frameworks/Metal.framework; sourceTree = SDKROOT; };\n"
    }

    private fun angleFrameworkFiles(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\t\t\t\t\t1E2A00000000000000000007 /* MetalANGLEKit.xcframework in Frameworks */,\n" +
            "\t\t\t\t\t1E2A00000000000000000008 /* libGLESv2.xcframework in Frameworks */,\n" +
            "\t\t\t\t\t1E2A00000000000000000009 /* libEGL.xcframework in Frameworks */,\n" +
            "\t\t\t\t\t1E2A0000000000000000000A /* libfeature_support.xcframework in Frameworks */,\n" +
            "\t\t\t\t\t1E2A0000000000000000000B /* Metal.framework in Frameworks */,\n"
    }

    private fun angleFrameworkGroupFiles(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\t\t\t\t\t1E2A00000000000000000018 /* MetalANGLEKit.xcframework */,\n" +
            "\t\t\t\t\t1E2A00000000000000000019 /* libGLESv2.xcframework */,\n" +
            "\t\t\t\t\t1E2A0000000000000000001A /* libEGL.xcframework */,\n" +
            "\t\t\t\t\t1E2A0000000000000000001B /* libfeature_support.xcframework */,\n" +
            "\t\t\t\t\t1E2A0000000000000000001C /* Metal.framework */,\n"
    }

    private fun angleCopyBuildPhase(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\n/* Begin PBXCopyFilesBuildPhase section */\n" +
            "\t\t\t1E2A00000000000000000052 /* Embed Frameworks */ = {\n" +
            "\t\t\t\tisa = PBXCopyFilesBuildPhase;\n" +
            "\t\t\t\tbuildActionMask = 2147483647;\n" +
            "\t\t\t\tdstPath = \"\";\n" +
            "\t\t\t\tdstSubfolderSpec = 10;\n" +
            "\t\t\t\tfiles = (\n" +
            "\${IOS_ANGLE_EMBED_FILES}" +
            "\t\t\t\t);\n" +
            "\t\t\t\tname = \"Embed Frameworks\";\n" +
            "\t\t\t\trunOnlyForDeploymentPostprocessing = 0;\n" +
            "\t\t\t};\n" +
            "\t/* End PBXCopyFilesBuildPhase section */\n"
    }

    private fun angleCopyBuildPhaseRef(api: String): String {
        return if(api == "angle") "\t\t\t\t\t1E2A00000000000000000052 /* Embed Frameworks */,\n" else ""
    }

    private fun angleEmbedFiles(api: String): String {
        if(api != "angle") {
            return ""
        }
        return "\t\t\t\t\t1E2A0000000000000000000C /* MetalANGLEKit.xcframework in Embed Frameworks */,\n" +
            "\t\t\t\t\t1E2A0000000000000000000D /* libGLESv2.xcframework in Embed Frameworks */,\n" +
            "\t\t\t\t\t1E2A0000000000000000000E /* libEGL.xcframework in Embed Frameworks */,\n" +
            "\t\t\t\t\t1E2A0000000000000000000F /* libfeature_support.xcframework in Embed Frameworks */,\n"
    }
}

@DisableCachingByDefault(because = "Opens the generated Xcode project")
abstract class GdxTeaVMIosOpenXcodeTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val xcodeProjectDir: DirectoryProperty

    @get:Input
    abstract val xcodeProjectName: Property<String>

    @TaskAction
    fun openXcode() {
        requireMacOS()
        val xcodeProject = xcodeProjectFile()
        runCommand(listOf("open", xcodeProject.absolutePath), xcodeProjectDir.get().asFile)
    }

    private fun xcodeProjectFile(): File {
        val xcodeProject = File(xcodeProjectDir.get().asFile, "${xcodeProjectName.get()}.xcodeproj")
        if(!xcodeProject.isDirectory) {
            throw GradleException("Xcode project was not generated: ${xcodeProject.absolutePath}")
        }
        return xcodeProject
    }
}

@DisableCachingByDefault(because = "Runs xcodebuild")
abstract class GdxTeaVMIosBuildSimulatorTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildRoot: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val xcodeProjectDir: DirectoryProperty

    @get:OutputDirectory
    abstract val derivedDataPath: DirectoryProperty

    @get:Input
    abstract val xcodeProjectName: Property<String>

    @get:Input
    abstract val scheme: Property<String>

    @get:Input
    abstract val configuration: Property<String>

    @TaskAction
    fun buildSimulator() {
        requireMacOS()
        val xcodeProject = xcodeProjectFile()
        val command = listOf(
            "xcodebuild",
            "-quiet",
            "-project", xcodeProject.absolutePath,
            "-scheme", scheme.get(),
            "-sdk", "iphonesimulator",
            "-configuration", configuration.get(),
            "-derivedDataPath", derivedDataPath.get().asFile.absolutePath,
            "CODE_SIGNING_ALLOWED=NO",
            "build"
        )
        runCommand(command, buildRoot.get().asFile)
    }

    private fun xcodeProjectFile(): File {
        val xcodeProject = File(xcodeProjectDir.get().asFile, "${xcodeProjectName.get()}.xcodeproj")
        if(!xcodeProject.isDirectory) {
            throw GradleException("Xcode project was not generated: ${xcodeProject.absolutePath}")
        }
        return xcodeProject
    }
}

@DisableCachingByDefault(because = "Installs and launches an iOS simulator app")
abstract class GdxTeaVMIosRunSimulatorTask : DefaultTask() {
    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val buildRoot: DirectoryProperty

    @get:InputDirectory
    @get:PathSensitive(PathSensitivity.RELATIVE)
    abstract val derivedDataPath: DirectoryProperty

    @get:Input
    abstract val xcodeProjectName: Property<String>

    @get:Input
    abstract val configuration: Property<String>

    @get:Input
    abstract val simulatorDevice: Property<String>

    @get:Input
    abstract val bundleIdentifier: Property<String>

    @get:Input
    abstract val openSimulator: Property<Boolean>

    @TaskAction
    fun runSimulator() {
        requireMacOS()
        val device = simulatorDevice.get()
        if(openSimulator.get()) {
            runCommand(listOf("open", "-a", "Simulator"), buildRoot.get().asFile)
        }
        runCommand(listOf("xcrun", "simctl", "boot", device), buildRoot.get().asFile, allowFailure = true)

        val app = appBundle()
        runCommand(listOf("xcrun", "simctl", "terminate", device, bundleIdentifier.get()), buildRoot.get().asFile, allowFailure = true)
        runCommand(listOf("xcrun", "simctl", "uninstall", device, bundleIdentifier.get()), buildRoot.get().asFile, allowFailure = true)
        runCommand(listOf("xcrun", "simctl", "install", device, app.absolutePath), buildRoot.get().asFile)
        runCommand(listOf("xcrun", "simctl", "launch", device, bundleIdentifier.get()), buildRoot.get().asFile)
    }

    private fun appBundle(): File {
        val app = File(
            derivedDataPath.get().asFile,
            "Build/Products/${configuration.get()}-iphonesimulator/${xcodeProjectName.get()}.app"
        )
        if(!app.isDirectory) {
            throw GradleException("iOS simulator app was not built: ${app.absolutePath}")
        }
        return app
    }
}

private fun requireMacOS() {
    if(!System.getProperty("os.name").lowercase().contains("mac")) {
        throw GradleException("iOS Xcode tasks require macOS.")
    }
}

private fun DefaultTask.runCommand(command: List<String>, workingDir: File, allowFailure: Boolean = false) {
    logger.lifecycle(command.joinToString(" "))
    val process = ProcessBuilder(command)
        .directory(workingDir)
        .redirectErrorStream(true)
        .start()
    val output = process.inputStream.bufferedReader().readLines()
    val exitCode = process.waitFor()
    if(exitCode == 0 || !allowFailure) {
        output.forEach { logger.lifecycle(it) }
    }
    if(exitCode != 0 && !allowFailure) {
        throw GradleException("Command failed with exit code $exitCode: ${command.joinToString(" ")}")
    }
}
