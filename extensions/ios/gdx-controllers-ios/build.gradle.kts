val moduleName = "gdx-controllers-ios"

dependencies {
    implementation(project(":backends:backend-ios"))
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
