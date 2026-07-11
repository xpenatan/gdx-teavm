import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.android.application") version "8.12.0"
    id("com.github.xpenatan.gdx-teavm")
}

val generatedAndroidDir = layout.buildDirectory.dir("generated/gdx-teavm/android")
val generatedAndroidCMakeFile = generatedAndroidDir.map { it.file("CMakeLists.txt") }
val generatedDefaultFontAssetsDir = layout.buildDirectory.dir("generated/gdx-default-font-assets")
val androidCxxDir = rootProject.layout.buildDirectory.dir("android-cxx/examples-websockets-android")
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

android {
    namespace = "com.github.xpenatan.gdx.teavm.examples.websockets.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.github.xpenatan.gdx.teavm.examples.websockets.android"
        minSdk = 21
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        externalNativeBuild {
            cmake {
                arguments += "-DANDROID_STL=c++_static"
            }
        }
    }

    splits {
        abi {
            isEnable = true
            reset()
            include("arm64-v8a", "armeabi-v7a", "x86", "x86_64")
            isUniversalApk = false
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isJniDebuggable = true
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=Debug"
                }
            }
            ndk {
                debugSymbolLevel = "FULL"
            }
        }
        release {
            signingConfig = signingConfigs.getByName("debug")
            isDebuggable = false
            isJniDebuggable = false
            isMinifyEnabled = false
            isShrinkResources = false
            externalNativeBuild {
                cmake {
                    arguments += "-DCMAKE_BUILD_TYPE=Release"
                }
            }
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = generatedAndroidCMakeFile.get().asFile
            buildStagingDirectory = androidCxxDir.get().asFile
        }
    }

    sourceSets {
        getByName("main") {
            assets.srcDir(file("../../basic/assets"))
            assets.srcDir(generatedDefaultFontAssetsDir)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.github.deedywu.gdx-websockets:teavm-android:${LibExt.wsVersion}")
    add("teavm", project(":examples:websockets:core"))
    add("teavm", "com.github.deedywu.gdx-websockets:teavm-android:${LibExt.wsVersion}")
}

gdxTeaVM {
    android {
        mainClass.set("WebSocketsAndroidLauncher")
        optimization.set(OptimizationLevel.NONE)
        debugInformation.set(false)
        obfuscated.set(false)
        minHeapSizeMb.set(16)
        maxHeapSizeMb.set(128)
    }
}

tasks.named("preBuild").configure {
    dependsOn(extractGdxDefaultFontAssets)
}

tasks.matching {
    it.name.startsWith("externalNativeBuildClean")
}.configureEach {
    onlyIf("generated gdx-teavm Android CMake source exists") {
        generatedAndroidCMakeFile.get().asFile.isFile && androidCxxDir.get().asFile.isDirectory
    }
}

tasks.named("clean").configure {
    doFirst {
        project.delete(androidCxxDir)
        project.delete(layout.projectDirectory.dir(".cxx"))
    }
}
