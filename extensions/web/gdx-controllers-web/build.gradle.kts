val moduleName = "gdx-controllers-web"

dependencies {
    implementation(project(":backends:backend-web"))
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
