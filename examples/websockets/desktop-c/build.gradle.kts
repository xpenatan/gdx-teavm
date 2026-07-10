dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:websockets:core"))
    implementation(project(":backends:backend-glfw"))
    implementation(project(":extensions:c:gdx-websockets-glfw"))
}

val mainClassName = "BuildTeaVMWebSocketsDemo"
val desktopCTaskGroup = "example-desktop-c"

tasks.register<JavaExec>("websockets_desktop_c_generate") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources for the Windows-only websocket GLFW example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug")
}

val buildDesktopCDebug = tasks.register<JavaExec>("websockets_desktop_c_debug_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Debug Windows-only websocket GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "build")
}

tasks.register<JavaExec>("websockets_desktop_c_release_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Release Windows-only websocket GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "build")
}

tasks.register("websockets_desktop_c_build") {
    group = desktopCTaskGroup
    description = "Compatibility alias for websockets_desktop_c_debug_build"
    dependsOn(buildDesktopCDebug)
}

tasks.register<JavaExec>("websockets_desktop_c_debug_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Debug Windows-only websocket GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "run", "console")
}

tasks.register<JavaExec>("websockets_desktop_c_release_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release Windows-only websocket GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run")
}

tasks.register<JavaExec>("websockets_desktop_c_release_console_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release Windows-only websocket GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run", "console")
}
