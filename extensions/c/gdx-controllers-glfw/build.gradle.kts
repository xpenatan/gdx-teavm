val moduleName = "gdx-controllers-glfw"

dependencies {
    implementation(project(":backends:backend-glfw"))
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
