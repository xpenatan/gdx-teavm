plugins {
    id("java-library")
}

val moduleName = "backend-web"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

val compileJavaTask = tasks.getByPath("compileJava")!!
compileJavaTask.doFirst {
    sourceSets["main"].runtimeClasspath.forEach {
        println(it)
    }
}
compileJavaTask.mustRunAfter("clean")

dependencies {
    api(project(":backends:backend-shared"))

    implementation(libs.gdx.core)
    implementation(libs.j.multiplatform)
    implementation(libs.bundles.jetty)

    testImplementation(libs.truth)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}
