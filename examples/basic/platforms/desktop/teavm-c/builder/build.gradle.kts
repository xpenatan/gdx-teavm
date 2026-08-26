dependencies {
    implementation(libs.gdxCore)
//    implementation(variantOf(libs.gdxCore) { classifier("sources") })
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-glfw"))
}

val mainClassName = "BuildTeaVMTestDemo"
val desktopCTaskGroup = "example-desktop-c"

tasks.register<JavaExec>("basic_desktop_c_generate") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources for SpriteBatchTest on GLFW"
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug")
}

tasks.register<JavaExec>("basic_desktop_c_debug_build") {
    group = desktopCTaskGroup
    description = "Generate and build the Debug TeaVM C/GLFW SpriteBatchTest executable"
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "build")
}

tasks.register<JavaExec>("basic_desktop_c_release_build") {
    group = desktopCTaskGroup
    description = "Generate and build the Release TeaVM C/GLFW SpriteBatchTest executable"
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "build")
}

tasks.register<JavaExec>("basic_desktop_c_debug_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run SpriteBatchTest on Debug TeaVM C/GLFW with console output"
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "run", "console")
}

tasks.register<JavaExec>("basic_desktop_c_release_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run SpriteBatchTest on Release TeaVM C/GLFW"
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run")
}

tasks.register<JavaExec>("basic_desktop_c_release_console_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run SpriteBatchTest on Release TeaVM C/GLFW with console output"
    mainClass = mainClassName
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run", "console")
}
