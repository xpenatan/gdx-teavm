val moduleName = "gdx-controllers-ios"

dependencies {
    implementation(project(":backends:backend-ios"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx-controllers:gdx-controllers-core:${LibExt.gdxControllerVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
