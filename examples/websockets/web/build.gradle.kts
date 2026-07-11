dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.github.deedywu.gdx-websockets:teavm-web:${LibExt.wsVersion}")
    implementation(project(":examples:websockets:core"))
    implementation(project(":backends:backend-web"))
}

val mainClassName = "BuildTeaVMWebSocketsWeb"
val webTaskGroup = "example-web"

tasks.register<JavaExec>("websockets_web_run") {
    group = webTaskGroup
    description = "Generate and serve the websocket JavaScript web example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("js")
}

tasks.register<JavaExec>("websockets_web_wasm_run") {
    group = webTaskGroup
    description = "Generate and serve the websocket Wasm web example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("wasm")
}
