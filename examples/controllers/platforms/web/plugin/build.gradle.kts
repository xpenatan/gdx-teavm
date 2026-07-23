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
        mainClass.set("ControllerWebLauncher")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }
    wasm {
        mainClass.set("ControllerWebLauncher")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }
}
