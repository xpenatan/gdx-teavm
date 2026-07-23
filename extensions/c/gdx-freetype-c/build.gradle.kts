val moduleName = "gdx-freetype-c"

dependencies {
    implementation(project(":backends:backend-shared"))
    implementation(libs.gdxCore)
    implementation(libs.gdxFreetypeCore)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
