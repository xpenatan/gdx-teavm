plugins {
    id("org.gretty") version("3.1.0")
}

gretty {
    contextPath = "/"
    extraResourceBase("build/dist/webapp")
}

val mainClassName = "com.github.xpenatan.gdx.examples.bullet.Build"

dependencies {
    implementation(project(":examples:bullet:core"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    // Use snapshots
//    implementation("com.github.xpenatan.gdx-teavm:backend-teavm:1.0.0-SNAPSHOT")
//    implementation("com.github.xpenatan.gdx-teavm:gdx-bullet-teavm:1.0.0-SNAPSHOT")

    // Or source projects
    implementation(project(":backends:backend-teavm"))
    implementation(project(":extensions:gdx-bullet:gdx-bullet-teavm"))
}

tasks.register<JavaExec>("buildExampleBullet") {
    dependsOn("classes")
    group = "teavm"
    description = "Build Bullet example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.register("runBullet") {
    group = "examples-teavm"
    description = "Run Bullet example"
    val list = arrayOf(
        "clean",
        "buildExampleBullet",
        "jettyRun"
    )
    dependsOn(list)
    tasks.findByName("buildExampleBullet")?.mustRunAfter("clean")
    tasks.findByName("jettyRun")?.mustRunAfter("buildExampleBullet")
}
