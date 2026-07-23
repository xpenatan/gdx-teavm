import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdx.core)
    implementation(project(":examples:basic:core"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

gdxTeaVM {
    assets.from(file("../../../assets"))
    reflection.add("com.badlogic.gdx.math.Vector2")

    js {
        mainClass.set("TestWebLauncher")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        debugInformation.set(true)
        sourceMap.set(true)
        sourceFilePolicy.set(SourceFilePolicy.COPY)
        devServer {
            enabled.set(true)
            autoReload.set(true)
        }
    }
    wasm {
        mainClass.set("TestWebLauncher")
        relativePathInOutputDir.set("webapp")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        devServer {
            enabled.set(true)
            autoReload.set(true)
        }
    }
}
