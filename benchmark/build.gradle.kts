import org.gradle.api.tasks.JavaExec
import java.time.Instant

data class BenchmarkReportRow(
    val backend: String,
    val test: String,
    val width: String,
    val height: String,
    val vsync: String,
    val avgFps: String,
    val minFps: String,
    val maxFps: String,
    val samples: String
)

fun parseBenchmarkReportRows(file: File): List<BenchmarkReportRow> {
    if(!file.isFile) {
        return emptyList()
    }
    return file.readLines()
        .drop(1)
        .filter { it.isNotBlank() }
        .mapNotNull { line ->
            val parts = line.split('\t')
            if(parts.size < 9) {
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
                    parts[8]
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
        appendLine("Each workload is an unchanged test object supplied by its launcher; workload behavior remains owned by its test class.")
        appendLine()
        appendLine("| Backend | Test | Size | VSync | Avg FPS | Min FPS | Max FPS | Samples |")
        appendLine("|---|---|---:|---:|---:|---:|---:|---:|")
        for(row in rows) {
            appendLine("| ${row.backend} | ${row.test} | ${row.width}x${row.height} | ${row.vsync} | ${row.avgFps} | ${row.minFps} | ${row.maxFps} | ${row.samples} |")
        }
        if(rows.isEmpty()) {
            appendLine()
            appendLine("No successful benchmark results were written.")
        }
    })
}

val benchmarkResultFile = layout.buildDirectory.file("benchmark-results/results.tsv")
val benchmarkReportFile = layout.buildDirectory.file("benchmark-results/results.md")

val prepareBenchmarkReport = tasks.register("prepareBenchmarkReport") {
    description = "Clear previous desktop benchmark report data before a comparison"

    doLast {
        benchmarkResultFile.get().asFile.delete()
        benchmarkReportFile.get().asFile.delete()
    }
}

evaluationDependsOn(":benchmark:glfw")
evaluationDependsOn(":benchmark:lwjgl3")

val resultArgument = "--resultFile=${benchmarkResultFile.get().asFile.absolutePath}"

val glfwBenchmark = project(":benchmark:glfw").tasks.named<JavaExec>("benchmarkRelease") {
    dependsOn(prepareBenchmarkReport)
    args(resultArgument)
}

val lwjgl3Benchmark = project(":benchmark:lwjgl3").tasks.named<JavaExec>("benchmark") {
    dependsOn(prepareBenchmarkReport)
    mustRunAfter(glfwBenchmark)
    args(resultArgument)
}

tasks.register("compare") {
    group = "benchmark"
    description = "Compare the configured test object on TeaVM C/GLFW and Java/LWJGL3"
    dependsOn(glfwBenchmark, lwjgl3Benchmark)

    doLast {
        val resultFile = benchmarkResultFile.get().asFile
        val reportFile = benchmarkReportFile.get().asFile
        writeBenchmarkMarkdownReport(resultFile, reportFile,
            "Desktop Benchmark (TeaVM C/GLFW vs Java/LWJGL3)")
        println("BENCH_REPORT $reportFile")
    }
}
