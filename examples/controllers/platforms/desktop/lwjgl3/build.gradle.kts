val assetsDir = file("../../../assets")

dependencies {
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
    implementation(libs.gdx.backend.lwjgl3)
    implementation(libs.gdx.controllers.desktop)
    implementation(project(":examples:controllers:core"))
}

tasks.register<JavaExec>("controllers_desktop_run") {
    dependsOn("classes")
    group = "example-desktop"
    description = "Run gdx-controllers desktop example"
    mainClass.set("ControllerDesktopLauncher")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir

    if(org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        jvmArgs?.add("-XstartOnFirstThread")
    }
}
