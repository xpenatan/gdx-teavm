plugins {
    id("java-library")
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:freetype:core"))

    // Required
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl3:${LibExt.gdxVersion}")
    api("com.badlogicgames.gdx:gdx-tests")
}