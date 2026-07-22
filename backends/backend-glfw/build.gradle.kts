plugins {
    id("java-library")
}

val moduleName = "backend-glfw"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api(project(":backends:backend-shared"))

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
