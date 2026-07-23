import org.gradle.api.tasks.Copy

plugins {
    id("java-library")
}

val moduleName = "backend-android"

sourceSets["main"].java.setSrcDirs(mutableSetOf("../backend-glfw/emu", "emu", "src/main/java/"))

dependencies {
    implementation(libs.gdx.core)
    api(project(":backends:backend-shared"))
}

tasks.named<Copy>("processResources") {
    from("src/android/java") {
        into("gdx-teavm/android/runtime/java")
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
