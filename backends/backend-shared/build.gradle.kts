plugins {
    id("java-library")
}

val moduleName = "backend-shared"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api(project(":extensions:asset-loader"))
    api("org.reflections:reflections:${LibExt.reflectionVersion}")
    api("org.teavm:teavm-tooling:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-core:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-classlib:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-extension-spi:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-apis:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-impl:${LibExt.teaVMVersion}")

    testImplementation("junit:junit:4.13.2")
    testImplementation("com.google.truth:truth:${LibExt.truthVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
