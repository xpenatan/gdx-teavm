plugins {
    id("java-library")
}

val moduleName = "backend-teavm"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

val compileJavaTask = tasks.getByPath("compileJava")!!
compileJavaTask.doFirst {
    sourceSets["main"].runtimeClasspath.forEach {
        println(it)
    }
}
compileJavaTask.dependsOn("clean")
compileJavaTask.mustRunAfter("clean")

dependencies {
    implementation("org.reflections:reflections:${LibExt.reflectionVersion}")
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    api(project(":extensions:asset-loader"))

    api("org.teavm:teavm-tooling:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-core:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-classlib:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-apis:${LibExt.teaVMVersion}")
    api("org.teavm:teavm-jso-impl:${LibExt.teaVMVersion}")

    implementation("com.github.xpenatan:jMultiplatform:0.1.2")

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