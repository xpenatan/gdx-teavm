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
    implementation("com.github.xpenatan.jParser:loader-teavm:${LibExt.jParserVersion}")

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

tasks.register("runBulletTeavm") {
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
