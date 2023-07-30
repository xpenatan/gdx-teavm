val moduleName = "gdx-box2d-teavm"

dependencies {
    implementation(project(":backends:backend-teavm"))
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
}

tasks.named("clean") {
    doFirst {
        val srcPath = "$projectDir/src/main/java"
        project.delete(files(srcPath))
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = moduleName
            from(components["java"])
        }
    }
}