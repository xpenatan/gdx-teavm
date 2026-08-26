import org.gradle.internal.os.OperatingSystem

plugins {
    id("java-library")
}

val mainClassName = "com.github.xpenatan.gdx.teavm.benchmarks.lwjgl3.Lwjgl3BenchmarkLauncher"
val assetsDir = file("../../examples/basic/assets")

dependencies {
    implementation(project(":benchmark:core"))
    implementation(project(":examples:basic:core"))
    implementation(libs.gdxBackendLwjgl3)
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
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

tasks.register<JavaExec>("benchmark") {
    group = "benchmark"
    description = "Run the configured test object on Java/LWJGL3"
    dependsOn("classes")
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
    args(benchmarkArgs())
    standardInput = System.`in`

    if(OperatingSystem.current().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}
