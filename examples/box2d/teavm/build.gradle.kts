plugins {
    id("org.gretty") version("3.1.0")
}

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

val mainClassName = "com.github.xpenatan.gdx.examples.box2d.Build"

dependencies {
    implementation(project(":examples:box2d:core"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Use snapshots
//    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:1.0.0-SNAPSHOT")
//    implementation("com.github.xpenatan.gdx-teavm:gdx-box2d-teavm:1.0.0-SNAPSHOT")

    // Or source projects
    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-box2d:gdx-box2d-teavm"))
}

tasks.register<JavaExec>("buildExampleBox2D") {
    dependsOn("classes")
    group = "teavm"
    description = "Build Box2D example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("runBox2D") {
    group = "examples-teavm"
    description = "Run Box2D example"
    val list = arrayOf(
        "clean",
        "buildExampleBox2D",
        "jettyRun"
    )
    dependsOn(list)
    tasks.findByName("buildExampleBox2D")?.mustRunAfter("clean")
    tasks.findByName("jettyRun")?.mustRunAfter("buildExampleBox2D")
}
