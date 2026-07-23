dependencies {
    implementation(libs.gdx.core)
    implementation(project(":examples:freetype:core"))
    implementation(project(":backends:backend-glfw"))
    implementation(project(":extensions:c:gdx-freetype-c"))
}

val mainClassName = "BuildTeaVMFreetypeDemo"
val desktopCTaskGroup = "example-desktop-c"

tasks.register<JavaExec>("freetype_desktop_c_generate") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources for the FreeType GLFW example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug")
}

tasks.register<JavaExec>("freetype_desktop_c_debug_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Debug FreeType GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "build")
}

tasks.register<JavaExec>("freetype_desktop_c_release_build") {
    group = desktopCTaskGroup
    description = "Generate TeaVM C sources and build the Release FreeType GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "build")
}

tasks.register<JavaExec>("freetype_desktop_c_debug_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Debug FreeType GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Debug", "run", "console")
}

tasks.register<JavaExec>("freetype_desktop_c_release_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release FreeType GLFW executable"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run")
}

tasks.register<JavaExec>("freetype_desktop_c_release_console_run") {
    group = desktopCTaskGroup
    description = "Generate, build, and run the Release FreeType GLFW executable with native console log output"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("Release", "run", "console")
}
