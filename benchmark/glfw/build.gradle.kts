plugins {
    id("java-library")
}

val mainClassName = "com.github.xpenatan.gdx.teavm.benchmarks.glfw.BuildTeaVMBenchmark"

dependencies {
    implementation(project(":benchmark:core"))
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-glfw"))
}

fun benchmarkProperty(name: String, defaultValue: String): String {
    return (findProperty(name) as String?) ?: defaultValue
}

fun benchmarkArgs(): List<String> {
    return listOf(
        "--seconds=${benchmarkProperty("benchSeconds", "15")}",
        "--warmup=${benchmarkProperty("benchWarmup", "3")}",
        "--width=${benchmarkProperty("benchWidth", "640")}",
        "--height=${benchmarkProperty("benchHeight", "480")}"
    )
}

fun glfwRunArgs(action: String): List<String> {
    val args = mutableListOf("Release", action)
    if(benchmarkProperty("benchGlfwConsole", "true").toBoolean()) {
        args += "console"
    }
    args += "--continueOnTimeout=${benchmarkProperty("benchGlfwContinueOnTimeout", "true")}"
    args += benchmarkArgs()
    return args
}

tasks.register<JavaExec>("buildRelease") {
    group = "benchmark"
    description = "Generate and build the configured test object with TeaVM C/GLFW"
    dependsOn("classes")
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args(listOf("Release", "build") + benchmarkArgs())
}

tasks.register<JavaExec>("benchmarkRelease") {
    group = "benchmark"
    description = "Generate, build, and run the configured test object with TeaVM C/GLFW"
    dependsOn("classes")
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args(glfwRunArgs("run"))
}

tasks.register<JavaExec>("runRelease") {
    group = "benchmark"
    description = "Run the configured test object with the existing TeaVM C/GLFW executable"
    dependsOn("classes")
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args(glfwRunArgs("runExisting"))
}
