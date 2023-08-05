plugins {
    id("net.freudasoft.gradle-cmake-plugin") version("0.0.2")
}

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

cmake {
    generator.set("MinGW Makefiles")

    sourceFolder.set(file("$projectDir/src/main/cpp"))

    buildConfig.set("Release")
    buildTarget.set("install")
    buildClean.set(true)
}

tasks.register("build_Bullet_Emscripten") {
    dependsOn("cmakeBuild")
    mustRunAfter("cmakeBuild")
    group = "gen"
    description = "Generate javascript"

    doLast {
        copy{
            from(
                "$buildDir/cmake/bullet.js",
                "$buildDir/cmake/bullet.wasm.js"
            )
            into("$projectDir/../gdx-bullet-teavm/src/main/resources")
        }
    }
}