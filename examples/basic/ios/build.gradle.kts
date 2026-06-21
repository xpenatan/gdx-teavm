import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation(project(":examples:basic:core"))
}

gdxTeaVM {
    assets.from(file("../assets"))

    ios {
        mainClass.set("TestIOSLauncher")
        optimization.set(OptimizationLevel.NONE)
        debugInformation.set(false)
        obfuscated.set(false)
        minHeapSizeMb.set(16)
        maxHeapSizeMb.set(128)
    }
}
