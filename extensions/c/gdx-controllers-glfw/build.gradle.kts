val moduleName = "gdx-controllers-glfw"

dependencies {
    implementation(project(":backends:backend-glfw"))
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
