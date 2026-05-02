dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}:sources")
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

tasks.register<JavaExec>("basic_run_web") {
    group = "examples-teavm"
    description = "Run basic example"
    mainClass.set("BuildTeaVMTestDemo")
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register<JavaExec>("basic_visui_run_web") {
    group = "examples-teavm"
    description = "Run basic VisUI example"
    mainClass.set("BuildTeaVMVisUI")
    classpath = sourceSets["main"].runtimeClasspath
}