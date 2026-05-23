dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}:sources")
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-glfw"))
}

val mainClassName = "BuildTeaVMTestDemo"

tasks.register<JavaExec>("basic_generate_teavm_glfw") {
    group = "example-teavm"
    description = "Generate TeaVM C sources for the basic GLFW example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug")
}

val buildTeaVMGlfwDebug = tasks.register<JavaExec>("basic_build_teavm_glfw_debug") {
    group = "example-teavm"
    description = "Generate TeaVM C sources and build the Debug GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "build")
}

tasks.register<JavaExec>("basic_build_teavm_glfw_release") {
    group = "example-teavm"
    description = "Generate TeaVM C sources and build the Release GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "build")
}

tasks.register("basic_build_teavm_glfw") {
    group = "example-teavm"
    description = "Compatibility alias for basic_build_teavm_glfw_debug"
    dependsOn(buildTeaVMGlfwDebug)
}

tasks.register<JavaExec>("basic_run_teavm_glfw_debug") {
    group = "example-teavm"
    description = "Generate, build, and run the Debug GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "run")
}

tasks.register<JavaExec>("basic_run_teavm_glfw_release") {
    group = "example-teavm"
    description = "Generate, build, and run the Release GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run")
}

tasks.register<JavaExec>("basic_run_teavm_glfw_release_console") {
    group = "example-teavm"
    description = "Generate, build, and run the Release GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run", "console")
}
