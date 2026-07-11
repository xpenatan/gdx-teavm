import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

val generatedDefaultFontAssetsDir = layout.buildDirectory.dir("generated/gdx-default-font-assets")
val gdxFontConfiguration = configurations.detachedConfiguration(
    dependencies.create("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
).apply {
    isTransitive = false
}

val extractGdxDefaultFontAssets = tasks.register<Copy>("extractGdxDefaultFontAssets") {
    from({ zipTree(gdxFontConfiguration.singleFile) }) {
        include("com/badlogic/gdx/utils/lsans-15.fnt")
        include("com/badlogic/gdx/utils/lsans-15.png")
    }
    into(generatedDefaultFontAssetsDir)
}

dependencies {
    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.github.deedywu.gdx-websockets:teavm-ios:${LibExt.wsVersion}")
    implementation(project(":examples:websockets:core"))
}

gdxTeaVM {
    assets.from(file("../../basic/assets"))
    assets.from(generatedDefaultFontAssetsDir)

    ios {
        mainClass.set("WebSocketsIOSLauncher")
        targetFileName.set("websockets")
        xcodeProjectName.set("WebSocketsIOS")
        xcodeScheme.set("WebSocketsIOS")
        bundleIdentifier.set("com.github.xpenatan.gdx.teavm.examples.websockets.ios")
        optimization.set(OptimizationLevel.NONE)
        debugInformation.set(false)
        obfuscated.set(false)
        minHeapSizeMb.set(16)
        maxHeapSizeMb.set(128)
    }
}

tasks.matching {
    it.name.startsWith("gdx_teavm_ios")
}.configureEach {
    dependsOn(extractGdxDefaultFontAssets)
}
