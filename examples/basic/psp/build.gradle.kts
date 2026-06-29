dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:basic:core"))
    implementation(project(":backends:backend-psp"))
}

val mainClassName = "BuildTeaVMTestDemo"
tasks.register<JavaExec>("basic_psp_build") {
    group = "example-psp"
    description = "Build PSP native example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}
