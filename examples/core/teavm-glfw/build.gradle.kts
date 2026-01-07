dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
//    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}:sources")
    implementation(project(":examples:core:core"))
    implementation(project(":backends:backend-teavm-glfw"))
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildTeaVMTestDemo"

tasks.register<JavaExec>("core_build_teavm_glfw") {
    group = "example-teavm"
    description = "Build teavm test example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}