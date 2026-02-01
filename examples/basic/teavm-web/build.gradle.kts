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
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "BuildTeaVMTestDemo"

tasks.register<JavaExec>("basic_build_web") {
    group = "examples-teavm"
    description = "Build basic example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("basic_run_web") {
    group = "examples-teavm"
    description = "Run basic example"
    val list = listOf("basic_build_web", "jettyRun")
    dependsOn(list)

    tasks.findByName("jettyRun")?.mustRunAfter("basic_build_web")
}