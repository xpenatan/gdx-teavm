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

    nativeDefaults {
        mainClass = "TestCLauncher"
        minHeapSizeMb = 64
        maxHeapSizeMb = 512
    }

    glfw {
        optimization = OptimizationLevel.NONE
        obfuscated = false
        buildType = "Debug"
        consoleLog = true
    }

    glfw("release") {
        optimization = OptimizationLevel.NONE
        obfuscated = true
        buildType = "Release"
    }
}
