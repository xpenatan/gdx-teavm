//project.ext.assetsDir = new File("../desktop/assets")

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:freetype:core"))

    implementation(project(":backends:backend-web"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

val mainClassName = "BuildFreetypeTest"

tasks.register<JavaExec>("freetype_build_web") {
    dependsOn("classes")
    group = "examples-teavm"
    description = "Build teavm FreeType example"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath
}