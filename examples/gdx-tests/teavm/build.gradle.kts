dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:gdx-tests:core"))

    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "BuildGdxTest"

tasks.register<JavaExec>("gdx_tests_build_web") {
    group = "examples-teavm"
    description = "Build gdx-tests example"
    mainClass.set(mainClassName)
    args = mutableListOf(LibExt.gdxTestsAssetsPath)
    classpath = sourceSets["main"].runtimeClasspath
}