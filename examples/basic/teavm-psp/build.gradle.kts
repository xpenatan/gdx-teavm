dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-psp"))
}

val mainClassName = "BuildTeaVMTestDemo"
tasks.register<JavaExec>("basic_build_teavm_psp") {
    description = "Build teavm test example"
    group = "example-teavm"
    description = "Build teavm psp example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}