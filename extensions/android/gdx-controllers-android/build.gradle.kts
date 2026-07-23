plugins {
    id("maven-publish")
}

val moduleName = "gdx-controllers-android"

dependencies {
    implementation(project(":backends:backend-android"))
    implementation(libs.gdxCore)
    implementation(libs.gdxControllersCore)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
