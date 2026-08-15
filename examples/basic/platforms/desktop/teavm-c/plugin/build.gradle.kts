import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:basic:core"))
}

gdxTeaVM {
    assets.from(file("../../../../assets"))
    reflection.add("com.badlogic.gdx.math.Vector2")

    nativeDefaults {
        mainClass.set("TestCLauncher")
        minHeapSizeMb.set(64)
        maxHeapSizeMb.set(512)
    }

    glfw {
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(false)
        buildType.set("Debug")
        consoleLog.set(true)
    }

    glfw("release") {
        optimization.set(OptimizationLevel.BALANCED)
        obfuscated.set(true)
        buildType.set("Release")
    }
}
