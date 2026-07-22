val moduleName = "gdx-freetype-web"

dependencies {
    implementation(project(":backends:backend-web"))
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
