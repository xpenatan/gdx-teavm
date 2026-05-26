plugins {
    id("java-library")
}

val moduleName = "backend-android"

sourceSets["main"].java.setSrcDirs(mutableSetOf("../backend-glfw/emu", "emu", "src/main/java/"))

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
