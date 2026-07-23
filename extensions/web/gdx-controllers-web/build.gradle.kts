val moduleName = "gdx-controllers-web"

dependencies {
    implementation(project(":backends:backend-web"))
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
