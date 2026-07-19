import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:controllers:core"))
    implementation(project(":extensions:ios:gdx-controllers-ios"))
}

gdxTeaVM {
    assets.from(file("../../assets"))
    reflection("com.badlogic.gdx.controllers.IosControllerManager")

    ios {
        mainClass.set("ControllerIOSLauncher")
        optimization.set(OptimizationLevel.NONE)
        debugInformation.set(false)
        obfuscated.set(false)
        minHeapSizeMb.set(16)
        maxHeapSizeMb.set(128)
        bundleIdentifier.set("com.github.xpenatan.gdxteavm.controllers.ios")
    }
}
