val mainClassName = "com.github.xpenatan.gdx.html5.box2d.Main"

dependencies {
    implementation("com.github.xpenatan.jParser:jParser-core:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:jParser-teavm:${LibExt.jParserVersion}")
    implementation("com.github.xpenatan.jParser:jParser-idl:${LibExt.jParserVersion}")
}

tasks.register<JavaExec>("generateNativeProject") {
    dependsOn("classes")
    group = "teavm"
    description = "Generate native project"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}