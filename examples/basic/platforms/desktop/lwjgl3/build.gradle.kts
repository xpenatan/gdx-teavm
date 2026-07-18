val assetsDir = File("../../../assets")

dependencies {
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation(project(":examples:basic:core"))
}

val mainClassName = "Main"
tasks.register<JavaExec>("basic_desktop_run") {
    dependsOn("classes")
    group = "example-desktop"
    description = "Run basic example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        // Required to run on macOS
        jvmArgs?.add("-XstartOnFirstThread")
    }
}
