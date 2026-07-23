val assetsDir = File("../../../assets")

dependencies {
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
    implementation(libs.gdx.backend.lwjgl3)
    implementation(variantOf(libs.gdx.box2d.platform) { classifier("natives-desktop") })
    implementation(variantOf(libs.gdx.freetype.platform) { classifier("natives-desktop") })
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
