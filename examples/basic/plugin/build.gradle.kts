import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:basic:core"))
    implementation(project(":examples:basic:web"))
    implementation(project(":examples:basic:desktop-c"))
}

gdxTeaVM {
    assets.from(file("../assets"))
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
    glfw {
        mainClass.set("TestCLauncher")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(false)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        buildType.set("Debug")
        consoleLog.set(true)
    }
}
