val moduleName = "gdx-freetype-c"

dependencies {
    implementation(project(":backends:backend-shared"))
    implementation(libs.gdx.core)
    implementation(libs.gdx.freetype.core)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
