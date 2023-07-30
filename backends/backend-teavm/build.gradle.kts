plugins {
    id("java-library")
}

val moduleName = "backend-teavm"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

dependencies {
    implementation("org.reflections:reflections:${LibExt.reflectionVersion}")
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")

    api("org.teavm:teavm-tooling:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-core:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-classlib:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-apis:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-impl:${LibExt.teaVMVersion}")
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}