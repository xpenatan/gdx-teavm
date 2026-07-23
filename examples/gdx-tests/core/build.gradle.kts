plugins {
    id("java-library")
}

dependencies {
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:freetype:core"))

    // Required
    implementation(variantOf(libs.gdxPlatform) { classifier("natives-desktop") })
    implementation(libs.gdxBackendLwjgl3)
    api(libs.gdxTests)
}
