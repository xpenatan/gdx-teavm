import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

val mainClassName = "com.github.xpenatan.gdx.teavm.benchmarks.web.WebBenchmarkLauncher"
val benchmarkTexture = file("../../examples/basic/assets/data/badlogicsmall.jpg")
val benchmarkAssetsDir = layout.buildDirectory.dir("benchmark-assets")

val prepareBenchmarkAssets = tasks.register<Sync>("prepareBenchmarkAssets") {
    from(benchmarkTexture) {
        into("data")
    }
    into(benchmarkAssetsDir)
}

dependencies {
    implementation(project(":benchmark:core"))
}

fun benchmarkProperty(name: String, defaultValue: String): String {
    return (findProperty(name) as String?) ?: defaultValue
}

fun javascriptString(value: String): String {
    return "\"" + value
        .replace("\\", "\\\\")
        .replace("\"", "\\\"") + "\""
}

fun benchmarkArgs(backend: String): String {
    return listOf(
        "--backend=$backend",
        "--test=${benchmarkProperty("benchTest", "spritebatch_default")}",
        "--sprites=${benchmarkProperty("benchSprites", "8191")}",
        "--seconds=${benchmarkProperty("benchSeconds", "15")}",
        "--warmup=${benchmarkProperty("benchWarmup", "3")}",
        "--width=${benchmarkProperty("benchWidth", "640")}",
        "--height=${benchmarkProperty("benchHeight", "480")}",
        "--rotate=${benchmarkProperty("benchRotate", "true")}",
        "--scale=${benchmarkProperty("benchScale", "true")}",
        "--clear=${benchmarkProperty("benchClear", "true")}"
    ).joinToString(", ", transform = ::javascriptString)
}

gdxTeaVM {
    assets.from(benchmarkAssetsDir)

    js {
        mainClass.set(mainClassName)
        mainClassArgs.set(benchmarkArgs("teavm-web-js"))
        htmlTitle.set("gdx-teavm JS benchmark")
        htmlWidth.set(benchmarkProperty("benchWidth", "640").toInt())
        htmlHeight.set(benchmarkProperty("benchHeight", "480").toInt())
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }
    wasm {
        mainClass.set(mainClassName)
        mainClassArgs.set(benchmarkArgs("teavm-web-wasm"))
        htmlTitle.set("gdx-teavm Wasm benchmark")
        htmlWidth.set(benchmarkProperty("benchWidth", "640").toInt())
        htmlHeight.set(benchmarkProperty("benchHeight", "480").toInt())
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }
}

tasks.matching { it.name == "generateJavaScript" || it.name == "generateWasmGC" }.configureEach {
    dependsOn(prepareBenchmarkAssets)
}
