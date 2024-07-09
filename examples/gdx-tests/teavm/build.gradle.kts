plugins {
    id("org.gretty") version("3.1.0")
}

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:gdx-tests:core"))

    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-freetype-teavm"))

    implementation("com.github.xpenatan.gdx-imgui:imgui-teavm:${LibExt.gdxImGuiVersion}")
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildGdxTest"

tasks.register<JavaExec>("gdx-tests-build") {
    group = "examples-teavm"
    description = "Build gdx-tests example"
    mainClass.set(mainClassName)
    args = mutableListOf(LibExt.gdxTestsAssetsPath)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("gdx-tests-run-teavm") {
    group = "examples-teavm"
    description = "Run gdx-tests teavm app"
    val list = listOf("gdx-tests-build", "jettyRun")
    dependsOn(list)

    tasks.findByName("jettyRun")?.mustRunAfter("gdx-tests-build")
}