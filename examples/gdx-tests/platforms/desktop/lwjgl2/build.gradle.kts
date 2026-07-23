dependencies {
    implementation(project(":examples:gdx-tests:core"))
    // need to use lwjgl2 because lwjgl3 call makeCurrent and replace Gdx.input used inside gdx wrapper.
    implementation(libs.gdx.backend.lwjgl)
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
    implementation(variantOf(libs.gdx.freetype.platform) { classifier("natives-desktop") })
}

val mainClassName = "Main"
val gdxTestsAssetsDir = file(providers.gradleProperty("gdxSourcePath").get())
    .resolve("tests/gdx-tests-android/assets")

sourceSets["main"].resources.srcDirs(File("../../../core/assets"))

tasks.register<JavaExec>("gdx_tests_run_desktop") {
    dependsOn("classes")
    group = "examples-desktop"
    description = "Run gdx tests example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
    workingDir = gdxTestsAssetsDir
}
