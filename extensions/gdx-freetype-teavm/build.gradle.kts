val moduleName = "gdx-freetype-teavm"

dependencies {
    implementation(project(":backends:backend-teavm"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}