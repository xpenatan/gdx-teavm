dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:websockets:core"))
    implementation(project(":backends:backend-glfw"))
    implementation("com.github.deedywu.gdx-websockets:teavm-desktop-c:${LibExt.wsVersion}")
}

val mainClassName = "BuildTeaVMWebSocketsDemo"
val desktopCTaskGroup = "example-desktop-c"
val linuxCurlPath = providers.gradleProperty("gdxTeaVMLinuxCurlPath")
val macCurlPath = providers.gradleProperty("gdxTeaVMMacCurlPath")

fun JavaExec.configureNativeCurlRuntime() {
    val configuredLinuxPath = linuxCurlPath.orNull
    if(!configuredLinuxPath.isNullOrBlank()) {
        systemProperty("gdxTeaVMLinuxCurlPath", configuredLinuxPath)
    }

    val configuredMacPath = macCurlPath.orNull
    if(!configuredMacPath.isNullOrBlank()) {
        systemProperty("gdxTeaVMMacCurlPath", configuredMacPath)
    }
}

tasks.register<JavaExec>("websockets_desktop_c_generate") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources for the websocket GLFW example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    configureNativeCurlRuntime()
    args("Debug")
}

val buildDesktopCDebug = tasks.register<JavaExec>("websockets_desktop_c_debug_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Debug websocket GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    configureNativeCurlRuntime()
    args("Debug", "build")
}

tasks.register<JavaExec>("websockets_desktop_c_release_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Release websocket GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    configureNativeCurlRuntime()
    args("Release", "build")
}

tasks.register("websockets_desktop_c_build") {
    group = desktopCTaskGroup
    description = "Compatibility alias for websockets_desktop_c_debug_build"
    dependsOn(buildDesktopCDebug)
}

tasks.register<JavaExec>("websockets_desktop_c_debug_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Debug websocket GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    configureNativeCurlRuntime()
    args("Debug", "run", "console")
}

tasks.register<JavaExec>("websockets_desktop_c_release_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release websocket GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    configureNativeCurlRuntime()
    args("Release", "run")
}

tasks.register<JavaExec>("websockets_desktop_c_release_console_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release websocket GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    configureNativeCurlRuntime()
    args("Release", "run", "console")
}
