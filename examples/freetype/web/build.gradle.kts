import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:freetype:core"))
    implementation(project(":extensions:gdx-freetype-teavm"))
}

gdxTeaVM {
    assets.from(file("../desktop/assets"))

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
