val moduleName = "gdx-controllers-web"

dependencies {
    implementation(project(":backends:backend-web"))
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
