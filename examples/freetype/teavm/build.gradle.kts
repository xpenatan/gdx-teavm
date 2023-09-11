plugins {
    id("org.gretty") version("3.1.0")
}

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

//project.ext.assetsDir = new File("../desktop/assets")

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:freetype:core"))

    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildFreetypeTest"

tasks.register<JavaExec>("freetype-build") {
    dependsOn("classes")
    group = "examples-teavm"
    description = "Build teavm FreeType example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("freetype-run-teavm") {
    group = "examples-teavm"
    description = "Run FreeType Demo example"
    val list = arrayOf(
        "clean",
        "freetype-build",
        "jettyRun"
    )
    dependsOn(list)
    tasks.findByName("freetype-build")?.mustRunAfter("clean")
    tasks.findByName("jettyRun")?.mustRunAfter("freetype-build")
}