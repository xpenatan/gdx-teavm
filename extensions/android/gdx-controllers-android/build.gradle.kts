plugins {
    id("maven-publish")
}

val moduleName = "gdx-controllers-android"

dependencies {
    implementation(project(":backends:backend-android"))
    implementation(libs.gdx.core)
    implementation(libs.gdx.controllers.core)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
