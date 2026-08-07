plugins {
    id("java-library")
}

val moduleName = "backend-web"

sourceSets["main"].java.setSrcDirs(mutableSetOf("emu", "src/main/java/"))

tasks.named("compileJava") {
    mustRunAfter("clean")
}

dependencies {
    api(project(":backends:backend-shared"))

    implementation(libs.gdxCore)
    implementation(libs.jMultiplatform)
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
