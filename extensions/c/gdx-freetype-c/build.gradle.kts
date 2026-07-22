val moduleName = "gdx-freetype-c"

dependencies {
    implementation(project(":backends:backend-shared"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-freetype:${LibExt.gdxVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
