plugins {
    id("java-library")
}

val moduleName = "backend-glfw"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api(project(":backends:backend-shared"))
}