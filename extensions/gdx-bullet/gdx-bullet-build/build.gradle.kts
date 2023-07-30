val mainClassName = "com.github.xpenatan.gdx.html5.bullet.Main"

dependencies {
    implementation(project(":extensions:gdx-bullet:gdx-bullet-base"))
    implementation("com.github.xpenatan.jParser:jParser-core:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:jParser-teavm:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:jParser-cpp:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:jParser-idl:${LibExt.jParserVersion}")
}

tasks.register<JavaExec>("generateNativeProject") {
    dependsOn("classes")
    group = "teavm"
    description = "Generate native project"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/jni/build/"
        project.delete(files(srcPath))
    }
}