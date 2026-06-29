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
    implementation(project(":examples:basic:psp"))
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
    }
    wasm {
        mainClass.set("TestWebLauncher")
        relativePathInOutputDir.set("webapp")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
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
    psp {
        mainClass.set("PSPLauncherTest")
        optimization.set(OptimizationLevel.NONE)
        debugInformation.set(true)
        obfuscated.set(false)
        minHeapSizeMb.set(2)
        maxHeapSizeMb.set(8)
        debugMemory.set(true)
    }
}
