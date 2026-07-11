dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:controllers:core"))
    implementation(project(":backends:backend-glfw"))
    implementation(project(":extensions:c:gdx-controllers-glfw"))
}

val mainClassName = "BuildTeaVMControllersDemo"
val desktopCTaskGroup = "example-desktop-c"

tasks.register<JavaExec>("controllers_desktop_c_generate") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources for the gdx-controllers GLFW example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug")
}

tasks.register<JavaExec>("controllers_desktop_c_debug_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Debug gdx-controllers GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "build")
}

tasks.register<JavaExec>("controllers_desktop_c_release_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Release gdx-controllers GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "build")
}

tasks.register<JavaExec>("controllers_desktop_c_debug_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Debug gdx-controllers GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "run", "console")
}

tasks.register<JavaExec>("controllers_desktop_c_release_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release gdx-controllers GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run")
}

tasks.register<JavaExec>("controllers_desktop_c_release_console_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release gdx-controllers GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run", "console")
}
