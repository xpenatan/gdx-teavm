plugins {
    id("java-library")
}

val moduleName = "backend-glfw"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api(project(":backends:backend-shared"))
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
