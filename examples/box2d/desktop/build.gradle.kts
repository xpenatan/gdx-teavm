import java.io.File

val mainClassName = "com.github.xpenatan.gdx.examples.box2d.Main"
val assetsDir = File("../desktop/assets");

dependencies {
    implementation(project(":examples:box2d:core"))
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
}

tasks.register<JavaExec>("runBox2DDesktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run Box2D example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath

    if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        // Required to run on macOS
        jvmArgs?.add("-XstartOnFirstThread")
    }
}

//tasks.register('dist', Jar) {
//    manifest {
//        attributes 'Main-Class': project.mainClassName
//    }
//    dependsOn configurations.runtimeClasspath
//    from {
//        configurations.runtimeClasspath.collect { it.isDirectory() ? it : zipTree(it) }
//    }
//    with jar
//}