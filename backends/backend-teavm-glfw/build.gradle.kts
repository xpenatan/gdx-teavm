plugins {
    id("java-library")
}

val moduleName = "backend-teavm-c"

sourceSets["main"].java.setSrcDirs(mutableSetOf("src/main/java/"))
sourceSets["main"].resources.srcDir("src/main/cpp/")

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api(project(":backends:backend-shared"))
    api("com.github.xpenatan.jParser:jParser-core:${LibExt.jParserVersion}")
    api("com.github.xpenatan.jParser:jParser-build:${LibExt.jParserVersion}")
}