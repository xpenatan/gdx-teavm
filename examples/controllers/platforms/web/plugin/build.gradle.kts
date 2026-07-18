import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:controllers:core"))
    implementation(project(":extensions:web:gdx-controllers-web"))
}

gdxTeaVM {
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
