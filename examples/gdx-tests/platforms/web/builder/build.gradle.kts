dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:gdx-tests:core"))

    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

val mainClassName = "BuildGdxTest"
val gdxTestsAssetsDir = file(providers.gradleProperty("gdxSourcePath").get())
    .resolve("tests/gdx-tests-android/assets")

tasks.register<JavaExec>("gdx_tests_build_web") {
    group = "examples-teavm"
    description = "Build gdx-tests example"
    mainClass.set(mainClassName)
    args = mutableListOf(gdxTestsAssetsDir.absolutePath)
    classpath = sourceSets["main"].runtimeClasspath
}
