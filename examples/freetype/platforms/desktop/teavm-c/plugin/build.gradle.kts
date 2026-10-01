import com.github.xpenatan.gdx.teavm.gradle.GlfwBuildType
import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:freetype:core"))
    implementation(project(":extensions:c:gdx-freetype-c"))
}

gdxTeaVM {
    assets.from(file("../../../../assets"))

    nativeDefaults {
        mainClass = "FreetypeCLauncher"
        minHeapSizeMb = 64
        maxHeapSizeMb = 512
    }

    glfw("debug") {
        optimization = OptimizationLevel.NONE
        obfuscated = false
        buildType = GlfwBuildType.DEBUG
        consoleLog = true
    }

    glfw("release") {
        optimization = OptimizationLevel.BALANCED
        obfuscated = false
        buildType = GlfwBuildType.RELEASE
        consoleLog = true
    }
}
