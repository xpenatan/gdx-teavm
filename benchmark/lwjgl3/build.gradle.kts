import org.gradle.internal.os.OperatingSystem

plugins {
    id("java-library")
}

val mainClassName = "com.github.xpenatan.gdx.teavm.benchmarks.lwjgl3.Lwjgl3BenchmarkLauncher"
val assetsDir = file("../../examples/basic/assets")

dependencies {
    implementation(project(":benchmark:core"))
    implementation(libs.gdx.backend.lwjgl3)
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
}

fun benchmarkProperty(name: String, defaultValue: String): String {
    return (findProperty(name) as String?) ?: defaultValue
}

fun benchmarkArgs(): List<String> {
    return listOf(
        "--test=${benchmarkProperty("benchTest", "spritebatch_default")}",
        "--seconds=${benchmarkProperty("benchSeconds", "15")}",
        "--warmup=${benchmarkProperty("benchWarmup", "3")}",
        "--width=${benchmarkProperty("benchWidth", "640")}",
        "--height=${benchmarkProperty("benchHeight", "480")}",
        "--rotate=${benchmarkProperty("benchRotate", "true")}",
        "--scale=${benchmarkProperty("benchScale", "true")}",
        "--clear=${benchmarkProperty("benchClear", "true")}"
    )
}

tasks.register<JavaExec>("benchmark") {
    group = "benchmark"
    description = "Run benchmark on stock libGDX LWJGL3"
    dependsOn("classes")
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
    args(benchmarkArgs())
    standardInput = System.`in`

    if(OperatingSystem.current().isMacOsX) {
        jvmArgs("-XstartOnFirstThread")
    }
}
