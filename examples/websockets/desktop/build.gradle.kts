dependencies {
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.github.deedywu.gdx-websockets:common:${LibExt.wsVersion2}")
    implementation(project(":examples:websockets:core"))
}

tasks.register<JavaExec>("websockets_desktop_run") {
    dependsOn("classes")
    group = "example-desktop"
    description = "Run websocket desktop example"
    mainClass.set("WebSocketsDesktopLauncher")
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = file(".")

    if(org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        jvmArgs?.add("-XstartOnFirstThread")
    }
}
