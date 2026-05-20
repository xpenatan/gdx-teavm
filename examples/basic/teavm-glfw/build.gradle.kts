dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}:sources")
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-glfw"))
}

val mainClassName = "BuildTeaVMTestDemo"

fun registerTeaVMGlfwTask(
    taskName: String,
    descriptionText: String,
    buildType: String = "Debug",
    build: Boolean = false,
    run: Boolean = false,
    console: Boolean = false
) =
    tasks.register<JavaExec>(taskName) {
        group = "example-teavm"
        description = descriptionText
        mainClass.set(mainClassName)
        classpath = sourceSets["main"].runtimeClasspath

        args(buildType)
        if (run) {
            args("run")
        } else if (build) {
            args("build")
        }
        if (console) {
            args("console")
        }
    }

registerTeaVMGlfwTask(
    "basic_generate_teavm_glfw",
    "Generate TeaVM C sources for the basic GLFW example"
)

val buildTeaVMGlfwDebug = registerTeaVMGlfwTask(
    "basic_build_teavm_glfw_debug",
    "Generate TeaVM C sources and build the Debug GLFW executable",
    buildType = "Debug",
    build = true
)

registerTeaVMGlfwTask(
    "basic_build_teavm_glfw_release",
    "Generate TeaVM C sources and build the Release GLFW executable",
    buildType = "Release",
    build = true
)

tasks.register("basic_build_teavm_glfw") {
    group = "example-teavm"
    description = "Compatibility alias for basic_build_teavm_glfw_debug"
    dependsOn(buildTeaVMGlfwDebug)
}

registerTeaVMGlfwTask(
    "basic_run_teavm_glfw_debug",
    "Generate, build, and run the Debug GLFW executable",
    buildType = "Debug",
    build = true,
    run = true
)

registerTeaVMGlfwTask(
    "basic_run_teavm_glfw_release",
    "Generate, build, and run the Release GLFW executable",
    buildType = "Release",
    build = true,
    run = true
)

registerTeaVMGlfwTask(
    "basic_run_teavm_glfw_release_console",
    "Generate, build, and run the Release GLFW executable with native console log output",
    buildType = "Release",
    build = true,
    run = true,
    console = true
)
