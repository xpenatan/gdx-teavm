import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:basic:core"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

gdxTeaVM {
    assets.from(file("../../../assets"))
    reflection.add("com.badlogic.gdx.math.Vector2")

    webDefaults {
        mainClass = "TestWebLauncher"
        optimization = OptimizationLevel.NONE
        obfuscated = false
    }

    js {
        debugInformation = true
        sourceMap = true
        sourceFilePolicy = SourceFilePolicy.COPY
        devServer {
            enabled = true
            autoReload = true
        }
    }
    wasm {
        devServer {
            enabled = true
            autoReload = true
        }
    }

    js("release") {
        optimization = OptimizationLevel.BALANCED
        obfuscated = true
        serverPort = 8181
    }

    wasm("release") {
        optimization = OptimizationLevel.BALANCED
        obfuscated = true
        serverPort = 8282
    }
}
