plugins {
    id("org.gretty") version("4.1.10")
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

tasks.register<JavaExec>("core_build_teavm") {
    group = "example-teavm"
    description = "Build teavm test example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("core_run_web") {
    group = "example-teavm"
    description = "Run Test Demo example"
    val list = listOf("core_build_teavm", "jettyRun")
    dependsOn(list)

    tasks.findByName("jettyRun")?.mustRunAfter("core_build_teavm")
}