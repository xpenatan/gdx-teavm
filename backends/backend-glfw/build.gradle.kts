plugins {
    id("java-library")
}

val moduleName = "backend-glfw"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation(libs.gdx.core)
    api(project(":backends:backend-shared"))

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
