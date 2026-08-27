package com.github.xpenatan.gdx.teavm.gradle

/**
 * Native build configuration used by generated GLFW build scripts.
 */
enum class GlfwBuildType(
    internal val backendValue: String,
    internal val scriptBaseName: String
) {
    /** Uses the generated debug CMake configuration and build script. */
    DEBUG("Debug", "app_debug"),

    /** Uses the generated release CMake configuration and build script. */
    RELEASE("Release", "app_release")
}
