plugins {
    id("maven-publish")
}

val moduleName = "gdx-controllers-android"

dependencies {
    implementation(project(":backends:backend-android"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-core:${LibExt.gdxControllerVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
