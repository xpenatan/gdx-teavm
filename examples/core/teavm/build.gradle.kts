plugins {
    id("org.gretty") version("3.1.0")
    id("org.teavm") version("0.12.0-dev-6")
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

teavm {
    js {
        sourceMap = true
        debugInformation = true
        addedToWebApp = true
        mainClass = "com.github.xpenatan.gdx.examples.teavm.launcher.TeaVMTestLauncher"
    }
    wasmGC {
        sourceMap = true
        debugInformation = true
        addedToWebApp = false
        mainClass = "com.github.xpenatan.gdx.examples.teavm.launcher.TeaVMTestLauncher"
    }
}