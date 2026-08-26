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
        mainClass = "FreetypeIOSLauncher"
        optimization = OptimizationLevel.NONE
        debugInformation = false
        obfuscated = false
        minHeapSizeMb = 16
        maxHeapSizeMb = 128
        bundleIdentifier = "com.github.xpenatan.gdxteavm.freetype.ios"
    }
}
