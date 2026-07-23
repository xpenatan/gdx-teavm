val moduleName = "asset-loader"

dependencies {
    implementation(libs.gdxCore)
    implementation(libs.teavmClasslib)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
