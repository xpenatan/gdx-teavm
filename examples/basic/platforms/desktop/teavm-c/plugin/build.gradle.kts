import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:basic:core"))
}

gdxTeaVM {
    assets.from(file("../../../../assets"))
    reflection.add("com.badlogic.gdx.math.Vector2")

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
