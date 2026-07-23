val assetsDir = File("../../../assets")

dependencies {
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
    implementation(libs.gdxBackendLwjgl3)
    implementation(variantOf(libs.gdxBox2dPlatform) { classifier("natives-desktop") })
    implementation(variantOf(libs.gdxFreetypePlatform) { classifier("natives-desktop") })
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
