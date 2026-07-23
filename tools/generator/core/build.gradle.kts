val moduleName = "generator-core"

dependencies {
    implementation(libs.gdx.core)
    implementation(project(":backends:backend-teavm"))

    implementation(libs.gdx.imgui.core)
    implementation(libs.gdx.imgui.impl)

    implementation(project(":extensions:web:gdx-freetype-web"))

    implementation(libs.bundles.jetty)
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            artifactId = moduleName
//            group = "com.github.xpenatan.gdx-teavm"
//            version = project.version
//            from(components["java"])
//        }
//    }
//}
