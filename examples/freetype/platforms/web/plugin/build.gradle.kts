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
        mainClass = "FreetypeTestLauncher"
        optimization = OptimizationLevel.BALANCED
        obfuscated = false
    }
    wasm {
        mainClass = "FreetypeTestLauncher"
        optimization = OptimizationLevel.BALANCED
        obfuscated = false
    }
}
