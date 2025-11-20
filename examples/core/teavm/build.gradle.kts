import org.teavm.gradle.api.SourceFilePolicy

plugins {
    id("org.gretty") version("4.1.10")
    id("org.teavm") version(LibExt.teaVMVersion)
}

project.extra["webAppDir"] = File(projectDir, "build/dist/webapp")
gretty {
    contextPath = "/"
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}:sources")

    implementation(project(":examples:core:core"))

    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildTeaVMTestDemo"
val mainConfigClassName = "com.github.xpenatan.gdx.examples.teavm.ConfigTeaVMTestDemo"

tasks.register<JavaExec>("core-config") {
    group = "example-teavm"
    description = "Config webapp test example"
    mainClass.set(mainConfigClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

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
        sourceFilePolicy = SourceFilePolicy.COPY

        devServer {
            port = 8080
        }
    }
    wasmGC {
        obfuscated = false
        mainClass = main
        targetFileName = "app.wasm"
        relativePathInOutputDir = "webapp"
        outputDir = file("${layout.buildDirectory.get()}/dist")
        sourceMap = true
        debugInformation = true
        sourceFilePolicy = SourceFilePolicy.COPY
    }
}