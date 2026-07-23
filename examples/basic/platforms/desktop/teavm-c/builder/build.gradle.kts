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
    description = "Generate TeaVM C sources for the basic GLFW example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug")
}

tasks.register<JavaExec>("basic_desktop_c_debug_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Debug GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "build")
}

tasks.register<JavaExec>("basic_desktop_c_release_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Release GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "build")
}

tasks.register<JavaExec>("basic_desktop_c_debug_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Debug GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "run", "console")
}

tasks.register<JavaExec>("basic_desktop_c_release_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run")
}

tasks.register<JavaExec>("basic_desktop_c_release_console_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run", "console")
}
