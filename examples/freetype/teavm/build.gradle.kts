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

    // Use snapshots
//  implementation("com.github.xpenatan.gdx-teavm:backend-teavm:1.0.0-SNAPSHOT")
//  implementation("com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:1.0.0-SNAPSHOT")

    // Or source projects
    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildFreetypeTest"

tasks.register<JavaExec>("buildExampleCoreFreeType") {
    dependsOn("classes")
    group = "teavm"
    description = "Build teavm FreeType example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("runCoreFreeTypeTest") {
    group = "examples-teavm"
    description = "Run FreeType Demo example"
    val list = arrayOf(
        "clean",
        "buildExampleCoreFreeType",
        "jettyRun"
    )
    dependsOn(list)
    tasks.findByName("buildExampleCoreFreeType")?.mustRunAfter("clean")
    tasks.findByName("jettyRun")?.mustRunAfter("buildExampleCoreFreeType")
}