dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:controllers:core"))
    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:web:gdx-controllers-web"))
}

val mainClassName = "BuildTeaVMControllersWeb"
val webTaskGroup = "example-web"

tasks.register<JavaExec>("controllers_web_run") {
    group = webTaskGroup
    description = "Generate and serve the gdx-controllers JavaScript web example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("js")
}

tasks.register<JavaExec>("controllers_web_wasm_run") {
    group = webTaskGroup
    description = "Generate and serve the gdx-controllers Wasm web example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("wasm")
}
