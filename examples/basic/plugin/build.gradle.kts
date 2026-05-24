import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(project(":examples:basic:web"))
    implementation(project(":examples:basic:glfw"))
    implementation(project(":examples:basic:psp"))
}

gdxTeaVM {
    assets.from(file("../assets"))
    reflection.add("com.badlogic.gdx.math.Vector2")

    js {
        mainClass.set("TestWebLauncher")
        relativePathInOutputDir.set("webapp")
        targetFileName.set("app.js")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        debugInformation.set(true)
        sourceMap.set(true)
        sourceFilePolicy.set(SourceFilePolicy.COPY)
    }
    wasm {
        mainClass.set("TestWebLauncher")
        relativePathInOutputDir.set("webapp")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
    }
    glfw {
        mainClass.set("TestCLauncher")
        buildType.set("Debug")
        optimization.set(OptimizationLevel.AGGRESSIVE)
        obfuscated.set(false)
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
        consoleLog.set(true)
    }
    psp {
        mainClass.set("PSPLauncherTest")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        debugInformation.set(true)
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        debugMemory.set(true)
    }
}
