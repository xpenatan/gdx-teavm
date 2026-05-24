import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.SourceSetContainer
import org.gradle.internal.os.OperatingSystem
import java.time.Instant
import java.util.concurrent.TimeUnit

data class BenchmarkReportRow(
    val backend: String,
    val test: String,
    val sprites: String,
    val width: String,
    val height: String,
    val rotate: String,
    val scale: String,
    val clear: String,
    val vsync: String,
    val avgFps: String,
    val minFps: String,
    val maxFps: String,
    val samples: String
)

data class BenchmarkScenario(
    val name: String,
    val clear: String
)

fun benchmarkProperty(name: String, defaultValue: String): String {
    return (findProperty(name) as String?) ?: defaultValue
}

fun benchmarkArgs(
    testName: String = benchmarkProperty("benchTest", "spritebatch_default"),
    resultFile: File? = null,
    clearValue: String = benchmarkProperty("benchClear", "true")
): List<String> {
    val args = mutableListOf(
        "--test=$testName",
        "--sprites=${benchmarkProperty("benchSprites", "8191")}",
        "--seconds=${benchmarkProperty("benchSeconds", "15")}",
        "--warmup=${benchmarkProperty("benchWarmup", "3")}",
        "--width=${benchmarkProperty("benchWidth", "640")}",
        "--height=${benchmarkProperty("benchHeight", "480")}",
        "--rotate=${benchmarkProperty("benchRotate", "true")}",
        "--scale=${benchmarkProperty("benchScale", "true")}",
        "--clear=$clearValue"
    )
    if(resultFile != null) {
        args += "--resultFile=${resultFile.absolutePath}"
    }
    return args
}

fun parseBenchmarkReportRows(file: File): List<BenchmarkReportRow> {
    if(!file.isFile) {
        return emptyList()
    }
    return file.readLines()
        .drop(1)
        .filter { it.isNotBlank() }
        .mapNotNull { line ->
            val parts = line.split('\t')
            if(parts.size < 13) {
                null
            }
            else {
                BenchmarkReportRow(
                    parts[0],
                    parts[1],
                    parts[2],
                    parts[3],
                    parts[4],
                    parts[5],
                    parts[6],
                    parts[7],
                    parts[8],
                    parts[9],
                    parts[10],
                    parts[11],
                    parts[12]
                )
            }
        }
}

fun writeBenchmarkMarkdownReport(resultFile: File, reportFile: File, title: String) {
    val rows = parseBenchmarkReportRows(resultFile)
    reportFile.parentFile.mkdirs()
    reportFile.writeText(buildString {
        appendLine("# $title")
        appendLine()
        appendLine("Generated: ${Instant.now()}")
        appendLine()
        appendLine("Missing backend rows mean that run did not reach `BENCH_RESULT`; check the console output for the failure.")
        appendLine()
        appendLine("| Backend | Test | Sprites | Size | Rotate | Scale | Clear | VSync | Avg FPS | Min FPS | Max FPS | Samples |")
        appendLine("|---|---|---:|---:|---:|---:|---:|---:|---:|---:|---:|---:|")
        for(row in rows) {
            appendLine("| ${row.backend} | ${row.test} | ${row.sprites} | ${row.width}x${row.height} | ${row.rotate} | ${row.scale} | ${row.clear} | ${row.vsync} | ${row.avgFps} | ${row.minFps} | ${row.maxFps} | ${row.samples} |")
        }
        if(rows.isEmpty()) {
            appendLine()
            appendLine("No successful benchmark results were written.")
        }
    })
}

fun runtimeClasspath(projectPath: String) =
    project(projectPath).extensions.getByType(SourceSetContainer::class.java)
        .named("main").get().runtimeClasspath

fun JavaExec.configureLwjgl3BenchmarkProcess(benchmarkArgs: List<String>) {
    mainClass.set("com.github.xpenatan.gdx.teavm.benchmarks.lwjgl3.Lwjgl3BenchmarkLauncher")
    classpath = runtimeClasspath(":benchmark:lwjgl3")
    workingDir = file("../examples/basic/assets")
    args(benchmarkArgs)
    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err

    if(OperatingSystem.current().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}

fun nativeExecutableName(imageName: String): String {
    return if(OperatingSystem.current().isWindows) "$imageName.exe" else imageName
}

fun benchmarkTimeoutSeconds(): Long {
    val warmup = benchmarkProperty("benchWarmup", "3").toLong()
    val seconds = benchmarkProperty("benchSeconds", "15").toLong()
    return warmup + seconds + 30
}

fun runNativeBenchmark(executable: File, workingDir: File, benchmarkArgs: List<String>, backendName: String) {
    if(!executable.isFile) {
        throw GradleException("Expected $backendName benchmark executable was not built: ${executable.absolutePath}")
    }

    val processBuilder = ProcessBuilder(listOf(executable.absolutePath) + benchmarkArgs)
        .directory(workingDir)
        .redirectErrorStream(true)
    val process = processBuilder.start()
    val outputThread = Thread {
        process.inputStream.bufferedReader().useLines { lines ->
            lines.forEach { println(it) }
        }
    }
    outputThread.isDaemon = true
    outputThread.start()

    val timeoutSeconds = benchmarkTimeoutSeconds()
    val completed = process.waitFor(timeoutSeconds, TimeUnit.SECONDS)
    if(!completed) {
        process.destroyForcibly()
        process.waitFor(5, TimeUnit.SECONDS)
        outputThread.join(1000)
        throw GradleException("$backendName benchmark timed out after ${timeoutSeconds}s")
    }
    outputThread.join(1000)
    if(process.exitValue() != 0) {
        throw GradleException("$backendName benchmark failed with exit code ${process.exitValue()}")
    }
}

fun runGraalvmReleaseBenchmark(benchmarkArgs: List<String>) {
    val outputDir = project(":benchmark:graalvm").layout.buildDirectory.dir("native/nativeReleaseCompile").get().asFile
    val executable = outputDir.resolve(nativeExecutableName("benchmark-graalvm-release"))
    runNativeBenchmark(executable, outputDir, benchmarkArgs, "GraalVM")
}

fun JavaExec.configureGlfwBenchmarkProcess(benchmarkArgs: List<String>, buildAction: String = "run") {
    mainClass.set("com.github.xpenatan.gdx.teavm.benchmarks.glfw.BuildTeaVMBenchmark")
    classpath = runtimeClasspath(":benchmark:glfw")
    workingDir = project(":benchmark:glfw").projectDir
    val processArgs = mutableListOf("Release", buildAction)
    if(buildAction != "build" && benchmarkProperty("benchGlfwConsole", "true").toBoolean()) {
        processArgs += "console"
    }
    if(buildAction != "build") {
        processArgs += "--continueOnTimeout=${benchmarkProperty("benchGlfwContinueOnTimeout", "true")}"
    }
    processArgs += benchmarkArgs
    args(processArgs)
    standardInput = System.`in`
    standardOutput = System.out
    errorOutput = System.err
}

val spriteBatchResultFile = layout.buildDirectory.file("benchmark-results/spritebatch/results.tsv")
val spriteBatchReportFile = layout.buildDirectory.file("benchmark-results/spritebatch/results.md")
val matrixResultFile = layout.buildDirectory.file("benchmark-results/matrix/results.tsv")
val matrixReportFile = layout.buildDirectory.file("benchmark-results/matrix/results.md")

val prepareSpriteBatchReport = tasks.register("prepareSpriteBatchReport") {
    group = "benchmark"
    description = "Clear previous SpriteBatch benchmark report data"

    doLast {
        spriteBatchResultFile.get().asFile.delete()
        spriteBatchReportFile.get().asFile.delete()
    }
}

val compareSpriteBatchGlfw = tasks.register<JavaExec>("compareSpriteBatchGlfw") {
    group = "benchmark"
    description = "Run SpriteBatch default benchmark on TeaVM GLFW Release"
    dependsOn(":benchmark:glfw:classes", prepareSpriteBatchReport)
    configureGlfwBenchmarkProcess(benchmarkArgs("spritebatch_default", spriteBatchResultFile.get().asFile))
}

val compareSpriteBatchLwjgl3 = tasks.register<JavaExec>("compareSpriteBatchLwjgl3") {
    group = "benchmark"
    description = "Run SpriteBatch default benchmark on stock libGDX LWJGL3"
    dependsOn(":benchmark:lwjgl3:classes", prepareSpriteBatchReport)
    mustRunAfter(compareSpriteBatchGlfw)
    configureLwjgl3BenchmarkProcess(benchmarkArgs("spritebatch_default", spriteBatchResultFile.get().asFile))
}

val compareSpriteBatchGraalvm = tasks.register("compareSpriteBatchGraalvm") {
    group = "benchmark"
    description = "Run SpriteBatch default benchmark on GraalVM native image Release"
    dependsOn(":benchmark:graalvm:copyBenchmarkAssetsToReleaseNativeCompile", prepareSpriteBatchReport)
    mustRunAfter(compareSpriteBatchLwjgl3)

    doLast {
        runGraalvmReleaseBenchmark(benchmarkArgs("spritebatch_default", spriteBatchResultFile.get().asFile))
    }
}

tasks.register("compareSpriteBatch") {
    group = "benchmark"
    description = "Compare SpriteBatch default mode on LWJGL3, GraalVM native image Release, and TeaVM GLFW Release"
    dependsOn(compareSpriteBatchGlfw, compareSpriteBatchLwjgl3, compareSpriteBatchGraalvm)

    doLast {
        val resultFile = spriteBatchResultFile.get().asFile
        val reportFile = spriteBatchReportFile.get().asFile
        writeBenchmarkMarkdownReport(resultFile, reportFile, "SpriteBatch Benchmark")
        println("BENCH_REPORT $reportFile")
    }
}

val compareGlfw = tasks.register<JavaExec>("compareGlfw") {
    group = "benchmark"
    description = "Run selected benchmark on TeaVM GLFW Release"
    dependsOn(":benchmark:glfw:classes")
    configureGlfwBenchmarkProcess(benchmarkArgs())
}

val compareLwjgl3 = tasks.register<JavaExec>("compareLwjgl3") {
    group = "benchmark"
    description = "Run selected benchmark on stock libGDX LWJGL3"
    dependsOn(":benchmark:lwjgl3:classes")
    mustRunAfter(compareGlfw)
    configureLwjgl3BenchmarkProcess(benchmarkArgs())
}

val compareGraalvm = tasks.register("compareGraalvm") {
    group = "benchmark"
    description = "Run selected benchmark on GraalVM native image Release"
    dependsOn(":benchmark:graalvm:copyBenchmarkAssetsToReleaseNativeCompile")
    mustRunAfter(compareLwjgl3)

    doLast {
        runGraalvmReleaseBenchmark(benchmarkArgs())
    }
}

tasks.register("compare") {
    group = "benchmark"
    description = "Run selected benchmark on LWJGL3, GraalVM native image Release, and TeaVM GLFW Release"
    dependsOn(compareGlfw, compareLwjgl3, compareGraalvm)
}

val prepareMatrixReport = tasks.register("prepareBenchmarkMatrixReport") {
    group = "benchmark"
    description = "Clear previous benchmark matrix report data"

    doLast {
        matrixResultFile.get().asFile.delete()
        matrixReportFile.get().asFile.delete()
    }
}

val matrixScenarios = listOf(
    BenchmarkScenario("spritebatch_default", benchmarkProperty("benchClear", "true")),
    BenchmarkScenario("spritebatch_fast", benchmarkProperty("benchClear", "true")),
    BenchmarkScenario("spritebatch_direct_getters", benchmarkProperty("benchClear", "true")),
    BenchmarkScenario("spritebatch_direct_array_state", benchmarkProperty("benchClear", "true")),
    BenchmarkScenario("spritebatch_simple_direct", benchmarkProperty("benchClear", "true")),
    BenchmarkScenario("spritebatch_precomputed_arraycopy", benchmarkProperty("benchClear", "true")),
    BenchmarkScenario("spritebatch_begin_end", benchmarkProperty("benchClear", "false"))
)

val glfwMatrixBuild = tasks.register<JavaExec>("benchmarkMatrixGlfwBuild") {
    group = "benchmark"
    description = "Build the TeaVM GLFW benchmark executable once for matrix runs"
    dependsOn(":benchmark:glfw:classes", prepareMatrixReport)
    configureGlfwBenchmarkProcess(emptyList(), "build")
}

val graalvmMatrixBuild = tasks.register("benchmarkMatrixGraalvmBuild") {
    group = "benchmark"
    description = "Build the GraalVM native image Release benchmark executable once for matrix runs"
    dependsOn(":benchmark:graalvm:copyBenchmarkAssetsToReleaseNativeCompile", prepareMatrixReport)
}

var previousMatrixTask: TaskProvider<out Task>? = null
val matrixTasks = mutableListOf<TaskProvider<out Task>>()

for(scenario in matrixScenarios) {
    val testName = scenario.name
    val taskSuffix = testName.split('_').joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }
    val previousBeforeGlfw = previousMatrixTask
    val glfwTask = tasks.register<JavaExec>("benchmarkMatrix${taskSuffix}Glfw") {
        group = "benchmark"
        description = "Run $testName benchmark on TeaVM GLFW Release"
        dependsOn(glfwMatrixBuild)
        previousBeforeGlfw?.let { mustRunAfter(it) }
        configureGlfwBenchmarkProcess(benchmarkArgs(testName, matrixResultFile.get().asFile, scenario.clear), "runExisting")
    }
    previousMatrixTask = glfwTask
    matrixTasks += glfwTask

    val lwjgl3Task = tasks.register<JavaExec>("benchmarkMatrix${taskSuffix}Lwjgl3") {
        group = "benchmark"
        description = "Run $testName benchmark on stock libGDX LWJGL3"
        dependsOn(":benchmark:lwjgl3:classes", prepareMatrixReport)
        mustRunAfter(glfwTask)
        configureLwjgl3BenchmarkProcess(benchmarkArgs(testName, matrixResultFile.get().asFile, scenario.clear))
    }
    previousMatrixTask = lwjgl3Task
    matrixTasks += lwjgl3Task

    val graalvmTask = tasks.register("benchmarkMatrix${taskSuffix}Graalvm") {
        group = "benchmark"
        description = "Run $testName benchmark on GraalVM native image Release"
        dependsOn(graalvmMatrixBuild)
        mustRunAfter(lwjgl3Task)

        doLast {
            runGraalvmReleaseBenchmark(benchmarkArgs(testName, matrixResultFile.get().asFile, scenario.clear))
        }
    }
    previousMatrixTask = graalvmTask
    matrixTasks += graalvmTask
}

tasks.register("benchmarkMatrix") {
    group = "benchmark"
    description = "Run benchmark matrix and write a Markdown report"
    dependsOn(matrixTasks)

    doLast {
        val resultFile = matrixResultFile.get().asFile
        val reportFile = matrixReportFile.get().asFile
        writeBenchmarkMarkdownReport(resultFile, reportFile, "gdx-teavm Benchmark Matrix")
        println("BENCH_MATRIX_DONE Report written to ${reportFile.absolutePath}")
    }
}
