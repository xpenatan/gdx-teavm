dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:freetype:core"))
    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

val mainClassName = "BuildTeaVMFreetypeWeb"
val webTaskGroup = "example-web"

tasks.register<JavaExec>("freetype_web_run") {
    group = webTaskGroup
    description = "Generate and serve the FreeType JavaScript web example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("js")
}

tasks.register<JavaExec>("freetype_web_wasm_run") {
    group = webTaskGroup
    description = "Generate and serve the FreeType Wasm web example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    args("wasm")
}
