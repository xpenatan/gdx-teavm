
dependencies {
    implementation(project(":backends:backend-teavm-c"))
}

val mainClassName = "com.github.xpenatan.gdx.examples.teavm.BuildCTestDemo"

tasks.register<JavaExec>("core-build") {
    group = "example-teavm"
    description = "Build teavm test example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}