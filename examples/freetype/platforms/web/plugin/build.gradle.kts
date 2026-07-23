import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:freetype:core"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

gdxTeaVM {
    assets.from(file("../../../assets"))

    js {
        mainClass.set("FreetypeTestLauncher")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }
    wasm {
        mainClass.set("FreetypeTestLauncher")
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
    }
}
