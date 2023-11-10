dependencies {
    implementation(project(":examples:gdx-tests:core"))
    // need to use lwjgl2 because lwjgl3 call makeCurrent and replace Gdx.input used inside gdx wrapper.
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")

    implementation("com.github.xpenatan.gdx-imgui:desktop:${LibExt.gdxImGuiVersion}")

    implementation("com.badlogicgames.gdx:gdx-bullet-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-box2d-platform:${LibExt.gdxVersion}:natives-desktop")
}

val mainClassName = "com.github.xpenatan.imgui.example.tests.Main"

// Change to your source asset directory
val assetsDir = File("E:\\Dev\\Projects\\java\\libgdx\\tests\\gdx-tests-android\\assets\\");

tasks.register<JavaExec>("gdx-tests-run-desktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run gdx tests example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = assetsDir
}