val moduleName = "gdx-freetype-web"

dependencies {
    implementation(project(":backends:backend-web"))
    implementation(libs.gdxCore)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
