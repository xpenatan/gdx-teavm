plugins {
    id("java-library")
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:freetype:core"))

    // Required
    implementation(variantOf(libs.gdx.platform) { classifier("natives-desktop") })
    implementation(libs.gdx.backend.lwjgl3)
    api(libs.gdx.tests)
}
