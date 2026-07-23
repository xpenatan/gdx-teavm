plugins {
    id("java-library")
}

val moduleName = "backend-shared"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation(libs.gdx.core)
    api(project(":extensions:asset-loader"))
    api(libs.reflections)
    api(libs.bundles.teavm)

    testImplementation(libs.junit)
    testImplementation(libs.truth)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
