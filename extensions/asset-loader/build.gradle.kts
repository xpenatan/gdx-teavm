val moduleName = "asset-loader"

dependencies {
    implementation(libs.gdx.core)
    implementation(libs.teavm.classlib)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
