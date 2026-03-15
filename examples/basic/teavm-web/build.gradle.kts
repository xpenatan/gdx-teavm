dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}:sources")
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "BuildTeaVMTestDemo"

tasks.register<JavaExec>("basic_run_web") {
    group = "examples-teavm"
    description = "Run basic example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}