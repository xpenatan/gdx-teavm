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
    assets.from(file("../../assets"))

    ios {
        mainClass.set("FreetypeIOSLauncher")
        optimization.set(OptimizationLevel.NONE)
        debugInformation.set(false)
        obfuscated.set(false)
        minHeapSizeMb.set(16)
        maxHeapSizeMb.set(128)
        bundleIdentifier.set("com.github.xpenatan.gdxteavm.freetype.ios")
    }
}
