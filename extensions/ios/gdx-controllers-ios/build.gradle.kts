val moduleName = "gdx-controllers-ios"

dependencies {
    implementation(project(":backends:backend-ios"))
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
