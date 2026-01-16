dependencies {
    implementation(project(":examples:gdx-tests:core"))
    // need to use lwjgl2 because lwjgl3 call makeCurrent and replace Gdx.input used inside gdx wrapper.
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-freetype-platform:${LibExt.gdxVersion}:natives-desktop")
}

val mainClassName = "Main"

sourceSets["main"].resources.srcDirs(File("../../core/assets"))

tasks.register<JavaExec>("gdx_tests_run_desktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run gdx tests example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = File(LibExt.gdxTestsAssetsPath)
}