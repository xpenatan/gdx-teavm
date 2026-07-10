plugins {
    id("java-library")
    id("maven-publish")
}

val moduleName = "gdx-websockets-glfw"

dependencies {
    implementation(project(":backends:backend-glfw"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.github.deedywu.gdx-websockets:core:${LibExt.wsVersion2}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            group = LibExt.groupId
            version = LibExt.libVersion
            from(components["java"])
        }
    }
}
