plugins {
    id("org.gretty") version("3.1.0")
    id("org.teavm") version(LibExt.teaVMVersion)
}

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:core:core"))

    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildTeaVMTestDemo"

tasks.register<JavaExec>("core-build") {
    group = "example-teavm"
    description = "Build teavm test example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("core-run-teavm") {
    group = "example-teavm"
    description = "Run Test Demo example"
    val list = listOf("core-build", "jettyRun")
    dependsOn(list)

    tasks.findByName("jettyRun")?.mustRunAfter("core-build")
}

val main = "com.github.xpenatan.gdx.examples.teavm.launcher.TeaVMTestLauncher"

teavm {
    js {
        obfuscated = false
        mainClass = main
        targetFileName = "app.js"
        relativePathInOutputDir = "webapp"
        outputDir = file("${layout.buildDirectory.get()}/dist")
        sourceMap = true
        debugInformation = true
    }
    wasmGC {
        obfuscated = false
        mainClass = main
        targetFileName = "app.wasm"
        relativePathInOutputDir = "webapp"
        outputDir = file("${layout.buildDirectory.get()}/dist")
        sourceMap = true
        debugInformation = true
    }
}