val moduleName = "generator-ui"

dependencies {
    implementation(project(":tools:generator:core"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":backends:backend-teavm"))

    implementation("com.github.xpenatan.gdx-imgui:imgui-core:${LibExt.gdxImGuiVersion}")
    implementation("com.github.xpenatan.gdx-imgui:gdx-impl:${LibExt.gdxImGuiVersion}")

    implementation(project(":extensions:gdx-freetype-teavm"))

    implementation("org.eclipse.jetty:jetty-server:${LibExt.jettyVersion}")
    implementation("org.eclipse.jetty:jetty-webapp:${LibExt.jettyVersion}")
}

//publishing {
//    publications {
//        create<MavenPublication>("maven") {
//            artifactId = moduleName
//            group = LibExt.groupId
//            version = LibExt.libVersion
//            from(components["java"])
//        }
//    }
//}