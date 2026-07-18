import java.util.concurrent.TimeUnit

plugins {
    id("application")
    id("org.graalvm.buildtools.native") version "1.1.0"
}

val mainClassName = "Main"
val nativeImageName = "basic-desktop-graalvm"
val assetsDir = File("../../../assets")
val lwjglVersion = "3.3.3"
val graalvmJavaVersion = JavaVersion.current().majorVersion.toInt()
val pgoProfileDir = layout.buildDirectory.dir("native/pgo-profiles/$nativeImageName")
val pgoProfileFile = pgoProfileDir.map { it.file("default.iprof") }
val pgoTrainSeconds = providers.gradleProperty("pgoTrainSeconds").orElse("60")
val nativeImageThreads = providers.gradleProperty("nativeImageThreads").orElse("2")
val nativeImageBuilderMaxHeap = providers.gradleProperty("nativeImageBuilderMaxHeap").orElse("4g")

fun lwjglNativesClassifier(): String {
    val os = org.gradle.internal.os.OperatingSystem.current()
    val arch = System.getProperty("os.arch").lowercase()
    val isArm64 = arch == "aarch64" || arch == "arm64"
    val isX86 = arch == "x86" || arch == "i386"

    return when {
        os.isWindows -> if (isX86) "natives-windows-x86" else "natives-windows"
        os.isMacOsX -> if (isArm64) "natives-macos-arm64" else "natives-macos"
        os.isLinux -> if (isArm64) "natives-linux-arm64" else "natives-linux"
        else -> throw GradleException("Unsupported LWJGL native platform: ${System.getProperty("os.name")} $arch")
    }
}

val lwjglNatives = lwjglNativesClassifier()

application {
    mainClass.set(mainClassName)
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation(project(":examples:basic:core"))

    runtimeOnly("org.lwjgl:lwjgl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-glfw:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-jemalloc:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-openal:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-opengl:$lwjglVersion:$lwjglNatives")
    runtimeOnly("org.lwjgl:lwjgl-stb:$lwjglVersion:$lwjglNatives")
}

tasks.register<JavaExec>("basic_run_desktop_graalvm_jvm") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run the basic GraalVM desktop launcher on the JVM"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if (org.gradle.internal.os.OperatingSystem.current().isMacOsX) {
        jvmArgs?.add("-XstartOnFirstThread")
    }
}

graalvmNative {
    toolchainDetection.set(false)
    metadataRepository {
        enabled.set(false)
    }
    binaries {
        named("main") {
            imageName.set(nativeImageName)
            mainClass.set(mainClassName)
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(graalvmJavaVersion))
            })
            fallback.set(false)
            resources.autodetect()
            jvmArgs("-Xmx${nativeImageBuilderMaxHeap.get()}")
            buildArgs.addAll(
                "-H:+ReportExceptionStackTraces",
                "--parallelism=${nativeImageThreads.get()}"
            )
        }
        create("release") {
            imageName.set("$nativeImageName-release")
            mainClass.set(mainClassName)
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(graalvmJavaVersion))
            })
            fallback.set(false)
            resources.autodetect()
            classpath(sourceSets["main"].runtimeClasspath)
            jvmArgs("-Xmx${nativeImageBuilderMaxHeap.get()}")
            buildArgs.addAll(
                "-H:+ReportExceptionStackTraces",
                "--parallelism=${nativeImageThreads.get()}",
                "-O3",
                "-march=native"
            )
        }
        create("pgoInstrument") {
            imageName.set("$nativeImageName-pgo-instrument")
            mainClass.set(mainClassName)
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(graalvmJavaVersion))
            })
            fallback.set(false)
            resources.autodetect()
            classpath(sourceSets["main"].runtimeClasspath)
            jvmArgs("-Xmx${nativeImageBuilderMaxHeap.get()}")
            buildArgs.addAll(
                "-H:+ReportExceptionStackTraces",
                "--parallelism=${nativeImageThreads.get()}",
                "--pgo-instrument",
                "-H:-SamplingCollect",
                "-march=native",
                "-R:ProfilesDumpFile=${pgoProfileFile.get().asFile.absolutePath}",
                "-R:ProfilingDumpPeriod=1",
                "-R:+ProfilingDumpVerbose"
            )
        }
        create("pgoRelease") {
            imageName.set("$nativeImageName-pgo")
            mainClass.set(mainClassName)
            javaLauncher.set(javaToolchains.launcherFor {
                languageVersion.set(JavaLanguageVersion.of(graalvmJavaVersion))
            })
            fallback.set(false)
            resources.autodetect()
            classpath(sourceSets["main"].runtimeClasspath)
            pgoProfilesDirectory.set(pgoProfileDir)
            jvmArgs("-Xmx${nativeImageBuilderMaxHeap.get()}")
            buildArgs.addAll(
                "-H:+ReportExceptionStackTraces",
                "--parallelism=${nativeImageThreads.get()}",
                "-O3",
                "-march=native"
            )
        }
    }
    agent {
        enabled.set(true)
        metadataCopy {
            inputTaskNames.add("basic_run_desktop_graalvm_jvm")
            outputDirectories.add("src/main/resources/META-INF/native-image/gdx-teavm/basic-desktop-graalvm")
            mergeWithExisting.set(true)
        }
    }
}

val nativeExecutableName = if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
    "$nativeImageName.exe"
} else {
    nativeImageName
}
val nativeOutputDir = layout.buildDirectory.dir("native/nativeCompile")
val nativeExecutable = nativeOutputDir.map { it.file(nativeExecutableName) }
val releaseNativeImageName = "$nativeImageName-release"
val releaseNativeExecutableName = if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
    "$releaseNativeImageName.exe"
} else {
    releaseNativeImageName
}
val releaseNativeOutputDir = layout.buildDirectory.dir("native/nativeReleaseCompile")
val releaseNativeExecutable = releaseNativeOutputDir.map { it.file(releaseNativeExecutableName) }
val pgoInstrumentNativeImageName = "$nativeImageName-pgo-instrument"
val pgoInstrumentNativeExecutableName = if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
    "$pgoInstrumentNativeImageName.exe"
} else {
    pgoInstrumentNativeImageName
}
val pgoInstrumentNativeOutputDir = layout.buildDirectory.dir("native/nativePgoInstrumentCompile")
val pgoInstrumentNativeExecutable = pgoInstrumentNativeOutputDir.map { it.file(pgoInstrumentNativeExecutableName) }
val pgoNativeImageName = "$nativeImageName-pgo"
val pgoNativeExecutableName = if (org.gradle.internal.os.OperatingSystem.current().isWindows) {
    "$pgoNativeImageName.exe"
} else {
    pgoNativeImageName
}
val pgoNativeOutputDir = layout.buildDirectory.dir("native/nativePgoReleaseCompile")
val pgoNativeExecutable = pgoNativeOutputDir.map { it.file(pgoNativeExecutableName) }

val copyBasicAssetsToNativeCompile = tasks.register<Copy>("copyBasicAssetsToNativeCompile") {
    dependsOn("nativeCompile")
    group = "examples-desktop"
    description = "Copy basic example assets next to the GraalVM native executable"
    from(assetsDir)
    into(nativeOutputDir)
}

tasks.register<Exec>("basic_run_desktop_graalvm") {
    dependsOn(copyBasicAssetsToNativeCompile)
    group = "examples-desktop"
    description = "Build and run the basic desktop GraalVM native executable"
    executable = nativeExecutable.get().asFile.absolutePath
    workingDir = nativeOutputDir.get().asFile
}

val copyBasicAssetsToReleaseNativeCompile = tasks.register<Copy>("copyBasicAssetsToReleaseNativeCompile") {
    dependsOn("nativeReleaseCompile")
    group = "examples-desktop"
    description = "Copy basic example assets next to the optimized GraalVM native executable"
    from(assetsDir)
    into(releaseNativeOutputDir)
}

tasks.register<Exec>("basic_run_desktop_graalvm_release") {
    dependsOn(copyBasicAssetsToReleaseNativeCompile)
    group = "examples-desktop"
    description = "Build and run the optimized basic desktop GraalVM native executable"
    executable = releaseNativeExecutable.get().asFile.absolutePath
    workingDir = releaseNativeOutputDir.get().asFile
}

val copyBasicAssetsToPgoInstrumentNativeCompile = tasks.register<Copy>("copyBasicAssetsToPgoInstrumentNativeCompile") {
    dependsOn("nativePgoInstrumentCompile")
    group = "examples-desktop"
    description = "Copy basic example assets next to the instrumented GraalVM native executable"
    from(assetsDir)
    into(pgoInstrumentNativeOutputDir)
}

val trainBasicPgo = tasks.register("basic_train_desktop_graalvm_pgo") {
    dependsOn(copyBasicAssetsToPgoInstrumentNativeCompile)
    group = "examples-desktop"
    description = "Run the instrumented GraalVM native executable and write a PGO profile"
    doLast {
        val trainSeconds = pgoTrainSeconds.get().toLong()
        if (trainSeconds < 2) {
            throw GradleException("pgoTrainSeconds must be at least 2 so GraalVM can dump a profile.")
        }

        val profileDir = pgoProfileDir.get().asFile
        val profileFile = pgoProfileFile.get().asFile
        profileDir.mkdirs()
        profileDir.listFiles { file -> file.isFile && file.extension == "iprof" }?.forEach { file ->
            if (!file.delete()) {
                throw GradleException("Could not delete stale PGO profile: ${file.absolutePath}")
            }
        }

        val process = ProcessBuilder(
            listOf(
                pgoInstrumentNativeExecutable.get().asFile.absolutePath,
                "-XX:ProfilesDumpFile=${profileFile.absolutePath}",
                "-XX:ProfilingDumpPeriod=1"
            )
        )
            .directory(pgoInstrumentNativeOutputDir.get().asFile)
            .inheritIO()
            .start()

        val exited = process.waitFor(trainSeconds, TimeUnit.SECONDS)
        if (!exited) {
            process.destroyForcibly()
            process.waitFor()
            logger.lifecycle("PGO training stopped after ${trainSeconds}s.")
        }

        if (!profileFile.isFile) {
            throw GradleException("PGO training did not produce ${profileFile.absolutePath}")
        }
        if (exited && process.exitValue() != 0) {
            logger.warn(
                "PGO training exited with ${process.exitValue()}, but ${profileFile.name} was written. " +
                    "Continuing because LWJGL/GraalVM may crash during instrumented shutdown on Windows."
            )
        }
    }
}

val validatePgoProfile = tasks.register("validatePgoProfile") {
    group = "examples-desktop"
    description = "Validate that the GraalVM PGO profile exists"
    mustRunAfter(trainBasicPgo)
    doLast {
        val profileFile = pgoProfileFile.get().asFile
        if (!profileFile.isFile) {
            throw GradleException(
                "Missing PGO profile: ${profileFile.absolutePath}. " +
                    "Run :examples:basic:platforms:desktop:graalvm:basic_train_desktop_graalvm_pgo first."
            )
        }
        val staleProfiles = profileFile.parentFile.listFiles { file ->
            file.isFile && file.extension == "iprof" && file.name != profileFile.name
        }?.toList().orEmpty()
        if (staleProfiles.isNotEmpty()) {
            throw GradleException(
                "Found stale PGO profile(s): ${staleProfiles.joinToString { it.name }}. " +
                    "Run :examples:basic:platforms:desktop:graalvm:basic_train_desktop_graalvm_pgo again to clean and regenerate the profile."
            )
        }
    }
}

tasks.named("nativePgoReleaseCompile") {
    dependsOn(validatePgoProfile)
    mustRunAfter(trainBasicPgo)
}

val copyBasicAssetsToPgoNativeCompile = tasks.register<Copy>("copyBasicAssetsToPgoNativeCompile") {
    dependsOn("nativePgoReleaseCompile")
    group = "examples-desktop"
    description = "Copy basic example assets next to the PGO-optimized GraalVM native executable"
    from(assetsDir)
    into(pgoNativeOutputDir)
}

tasks.register<Exec>("basic_run_desktop_graalvm_pgo") {
    dependsOn(buildBasicPgo)
    group = "examples-desktop"
    description = "Build and run the PGO-optimized basic desktop GraalVM native executable"
    executable = pgoNativeExecutable.get().asFile.absolutePath
    workingDir = pgoNativeOutputDir.get().asFile
}

tasks.register("basic_build_desktop_graalvm") {
    dependsOn(copyBasicAssetsToNativeCompile)
    group = "examples-desktop"
    description = "Build the basic desktop GraalVM native executable and copy its assets"
}


tasks.register("basic_build_desktop_graalvm_release") {
    dependsOn(copyBasicAssetsToReleaseNativeCompile)
    group = "examples-desktop"
    description = "Build the optimized basic desktop GraalVM native executable and copy its assets"
}

val buildBasicPgo = tasks.register("basic_build_desktop_graalvm_pgo") {
    dependsOn(trainBasicPgo, copyBasicAssetsToPgoNativeCompile)
    group = "examples-desktop"
    description = "Train, build the PGO-optimized basic desktop GraalVM native executable, and copy its assets"
}
