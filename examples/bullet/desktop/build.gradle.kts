import java.io.File

val mainClassName = "com.github.xpenatan.gdx.examples.bullet.Main"
val assetsDir = File("$projectDir/assets");

dependencies {
    implementation(project(":examples:bullet:core"))
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")

    // gdx-teavm Version
    implementation(project(":extensions:gdx-bullet:gdx-bullet-desktop"))
}

tasks.register<JavaExec>("runBulletDesktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run bullet example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        // Required to run on macOS
        jvmArgs?.add("-XstartOnFirstThread")
    }
}
//
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