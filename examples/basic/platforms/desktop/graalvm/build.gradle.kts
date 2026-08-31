import java.util.Locale

plugins {
    id("application")
}

val mainClassName = "Main"
val nativeImageName = "basic-desktop-graalvm"
val assetsDir = file("../../../assets")
val nativeOutputDir = layout.buildDirectory.dir("native/nativeCompile")
val isWindows = System.getProperty("os.name").startsWith("Windows", ignoreCase = true)
val nativeExecutableName = if(isWindows) "$nativeImageName.exe" else nativeImageName
val nativeExecutable = nativeOutputDir.map { it.file(nativeExecutableName) }
val nativeImageOutputBase = nativeOutputDir.map { it.file(nativeImageName) }
val nativeImageArgsFile = layout.buildDirectory.file("tmp/nativeCompile/native-image.args")
val exampleExitAfterSeconds = providers.gradleProperty("exampleExitAfterSeconds")
val nativeImageThreads = providers.gradleProperty("nativeImageThreads").orElse("2")
val nativeImageBuilderMaxHeap = providers.gradleProperty("nativeImageBuilderMaxHeap").orElse("4g")

fun lwjglNativesClassifier(): String {
    val osName = System.getProperty("os.name").lowercase(Locale.ROOT)
    val architecture = System.getProperty("os.arch").lowercase(Locale.ROOT)
    val isX64 = architecture == "amd64" || architecture == "x86_64"
    val isX86 = architecture == "x86" || architecture == "i386"
    val isArm64 = architecture == "aarch64" || architecture == "arm64"
    val isArm32 = architecture.startsWith("arm") && !isArm64

    return when {
        osName.startsWith("windows") && isX64 -> "natives-windows"
        osName.startsWith("windows") && isX86 -> "natives-windows-x86"
        osName.startsWith("mac") && isX64 -> "natives-macos"
        osName.startsWith("mac") && isArm64 -> "natives-macos-arm64"
        osName.startsWith("linux") && isX64 -> "natives-linux"
        osName.startsWith("linux") && isArm64 -> "natives-linux-arm64"
        osName.startsWith("linux") && isArm32 -> "natives-linux-arm32"
        else -> throw GradleException(
            "Unsupported LWJGL native platform: ${System.getProperty("os.name")} $architecture"
        )
    }
}

val lwjglNatives = lwjglNativesClassifier()

application {
    mainClass.set(mainClassName)
}

dependencies {
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
    implementation(libs.gdxBackendLwjgl3)
    implementation(variantOf(libs.gdxBox2dPlatform) { classifier("natives-desktop") })
    implementation(variantOf(libs.gdxFreetypePlatform) { classifier("natives-desktop") })
    implementation(project(":examples:basic:core"))

    runtimeOnly(variantOf(libs.lwjglCore) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjglGlfw) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjglJemalloc) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjglOpenal) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjglOpengl) { classifier(lwjglNatives) })
    runtimeOnly(variantOf(libs.lwjglStb) { classifier(lwjglNatives) })
}

tasks.register<JavaExec>("basic_desktop_graalvm_jvm_run") {
    dependsOn("classes")
    group = "example-desktop-graalvm"
    description = "Run the GraalVM module's SpriteBatchTest launcher on the current JVM"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
    jvmArgs("--enable-native-access=ALL-UNNAMED")

    if(System.getProperty("os.name").startsWith("Mac", ignoreCase = true)) {
        jvmArgs("-XstartOnFirstThread")
    }
    doFirst {
        if(exampleExitAfterSeconds.isPresent) {
            args("--exit-after-seconds=${exampleExitAfterSeconds.get()}")
        }
    }
}

tasks.named<JavaExec>("run") {
    workingDir = assetsDir
    jvmArgs("--enable-native-access=ALL-UNNAMED")
    if(System.getProperty("os.name").startsWith("Mac", ignoreCase = true)) {
        jvmArgs("-XstartOnFirstThread")
    }
}

val nativeCompile = tasks.register<Exec>("nativeCompile") {
    dependsOn("classes")
    group = "build"
    description = "Compile the GraalVM Native Image SpriteBatchTest executable"
    inputs.files(sourceSets["main"].runtimeClasspath)
    inputs.property("nativeImageThreads", nativeImageThreads)
    inputs.property("nativeImageBuilderMaxHeap", nativeImageBuilderMaxHeap)
    outputs.file(nativeExecutable)

    doFirst {
        val graalvmHome = providers.environmentVariable("GRAALVM_HOME")
            .orElse(providers.environmentVariable("JAVA_HOME"))
            .orElse(providers.systemProperty("java.home"))
            .get()
        val nativeImageCommand = file("$graalvmHome/bin/${if(isWindows) "native-image.cmd" else "native-image"}")
        if(!nativeImageCommand.isFile) {
            throw GradleException(
                "Native Image was not found at ${nativeImageCommand.absolutePath}. " +
                    "Set GRAALVM_HOME or JAVA_HOME to a GraalVM JDK that includes Native Image."
            )
        }

        nativeOutputDir.get().asFile.mkdirs()
        val nativeImageArgs = listOf(
            "--no-fallback",
            "--parallelism=${nativeImageThreads.get()}",
            "-J-Xmx${nativeImageBuilderMaxHeap.get()}",
            "-O3",
            "-march=native",
            "--enable-native-access=ALL-UNNAMED",
            "-cp",
            sourceSets["main"].runtimeClasspath.asPath,
            "-o",
            nativeImageOutputBase.get().asFile.absolutePath,
            mainClassName
        )
        val argsFile = nativeImageArgsFile.get().asFile
        argsFile.parentFile.mkdirs()
        argsFile.writeText(nativeImageArgs.joinToString(System.lineSeparator()) { argument ->
            "\"${argument.replace("\\", "\\\\").replace("\"", "\\\"")}\""
        })
        commandLine(nativeImageCommand.absolutePath, "@${argsFile.absolutePath}")
    }
}

val copyBasicAssetsToGraalVM = tasks.register<Copy>("copyBasicAssetsToGraalVM") {
    dependsOn(nativeCompile)
    from(assetsDir)
    into(nativeOutputDir)
}

tasks.register("basic_desktop_graalvm_build") {
    dependsOn(copyBasicAssetsToGraalVM)
    group = "example-desktop-graalvm"
    description = "Build the optimized GraalVM Native Image SpriteBatchTest executable and copy its assets"
}

tasks.register<Exec>("basic_desktop_graalvm_run") {
    dependsOn(copyBasicAssetsToGraalVM)
    group = "example-desktop-graalvm"
    description = "Build and run SpriteBatchTest as an optimized GraalVM native executable"
    executable = nativeExecutable.get().asFile.absolutePath
    workingDir = nativeOutputDir.get().asFile

    doFirst {
        if(exampleExitAfterSeconds.isPresent) {
            args("--exit-after-seconds=${exampleExitAfterSeconds.get()}")
        }
    }
}
