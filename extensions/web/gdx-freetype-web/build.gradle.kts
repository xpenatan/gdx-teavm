val moduleName = "gdx-freetype-web"

dependencies {
    implementation(project(":backends:backend-web"))
    implementation(libs.gdx.core)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
