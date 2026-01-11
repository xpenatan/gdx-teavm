val mainClassName = "com.github.xpenatan.gdx.examples.desktop.Main"
val assetsDir = File("/assets")

dependencies {
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation(project(":examples:freetype:core"))
}

tasks.register<JavaExec>("freetype_run_desktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run Freetype example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        // Required to run on macOS
        jvmArgs?.add("-XstartOnFirstThread")
    }
}

//tasks.register("dist", Jar) {
//    manifest {
//        attributes "Main-Class": project.mainClassName
//    }
//    dependsOn configurations.runtimeClasspath
//    from {
//        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
//    }
//    with jar
//}