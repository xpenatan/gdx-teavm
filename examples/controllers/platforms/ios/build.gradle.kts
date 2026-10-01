import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:controllers:core"))
    implementation(project(":extensions:ios:gdx-controllers-ios"))
}

gdxTeaVM {
    assets.from(file("../../assets"))

    ios {
        preservedClasses.add("com.badlogic.gdx.controllers.IosControllerManager")
        mainClass = "ControllerIOSLauncher"
        optimization = OptimizationLevel.NONE
        debugInformation = false
        obfuscated = false
        minHeapSizeMb = 16
        maxHeapSizeMb = 128
        bundleIdentifier = "com.github.xpenatan.gdxteavm.controllers.ios"
    }
}
