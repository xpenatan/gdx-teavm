plugins {
    id("org.gretty") version("4.1.10")
}

project.extra["webAppDir"] = File(projectDir, "build/dist/webapp")
gretty {
    contextPath = "/"
}

//project.ext.assetsDir = new File("../desktop/assets")

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:freetype:core"))

    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "BuildFreetypeTest"

tasks.register<JavaExec>("freetype_build_web") {
    dependsOn("classes")
    group = "examples-teavm"
    description = "Build teavm FreeType example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("freetype_run_web") {
    group = "examples-teavm"
    description = "Run FreeType example"
    val list = arrayOf(
        "clean",
        "freetype_build_web",
        "jettyRun"
    )
    dependsOn(list)
    tasks.findByName("freetype_build_web")?.mustRunAfter("clean")
    tasks.findByName("jettyRun")?.mustRunAfter("freetype_build_web")
}