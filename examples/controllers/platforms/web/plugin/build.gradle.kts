import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:controllers:core"))
    implementation(project(":extensions:web:gdx-controllers-web"))
}

gdxTeaVM {
    assets.from(file("../../../assets"))

    js {
        mainClass = "ControllerWebLauncher"
        optimization = OptimizationLevel.BALANCED
        obfuscated = false
    }
    wasm {
        mainClass = "ControllerWebLauncher"
        optimization = OptimizationLevel.BALANCED
        obfuscated = false
    }
}
