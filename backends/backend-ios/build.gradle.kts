plugins {
    id("java-library")
}

sourceSets["main"].java.setSrcDirs(mutableSetOf("../backend-glfw/emu", "emu", "src/main/java/"))

dependencies {
    implementation(libs.gdx.core)
    api(project(":backends:backend-shared"))
}
