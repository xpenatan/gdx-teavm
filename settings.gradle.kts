import java.util.*

pluginManagement {
    includeBuild("tools/gdx-teavm-plugin")

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://teavm.org/maven/repository/")
        }
    }
}

val localPropertiesFile = File(settingsDir, "local.properties")
val localProperties = Properties()
if(localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

fun isAndroidSdkDir(path: String?): Boolean {
    if(path.isNullOrBlank()) {
        return false
    }
    val sdkDir = File(path)
    return sdkDir.isDirectory &&
        File(sdkDir, "platforms").isDirectory &&
        File(sdkDir, "platform-tools").isDirectory
}

val hasAndroidSdk = isAndroidSdkDir(System.getenv("ANDROID_HOME")) ||
    isAndroidSdkDir(System.getenv("ANDROID_SDK_ROOT")) ||
    isAndroidSdkDir(localProperties.getProperty("sdk.dir"))

include(":backends:backend-shared")
include(":backends:backend-web")
include(":backends:backend-glfw")
include(":backends:backend-ios")

if(hasAndroidSdk) {
    include(":backends:backend-android")
}
else {
    logger.lifecycle("Android SDK not found. Skipping Android modules. Set ANDROID_HOME, ANDROID_SDK_ROOT, or local.properties sdk.dir to enable them.")
}

include(":extensions:asset-loader")
include(":extensions:c:gdx-freetype-c")
include(":extensions:c:gdx-controllers-glfw")
include(":extensions:ios:gdx-controllers-ios")
if(hasAndroidSdk) {
    include(":extensions:android:gdx-controllers-android")
}
include(":extensions:web:gdx-controllers-web")
include(":extensions:web:gdx-freetype-web")

//include(":tools:generator:core")
//include(":tools:generator:ui")
//include(":tools:generator:desktop")

// Examples
include(":examples:shared")
include(":examples:basic:core")
include(":examples:basic:platforms:desktop:lwjgl3")
include(":examples:basic:platforms:desktop:graalvm")
include(":examples:basic:platforms:desktop:teavm-c:builder")
include(":examples:basic:platforms:desktop:teavm-c:plugin")
include(":examples:basic:platforms:web:builder")
include(":examples:basic:platforms:web:plugin")
include(":examples:basic:platforms:ios")

if(hasAndroidSdk) {
    include(":examples:basic:platforms:android")
}

include(":examples:freetype:core")
include(":examples:freetype:platforms:desktop:lwjgl3")
include(":examples:freetype:platforms:desktop:teavm-c:builder")
include(":examples:freetype:platforms:web:builder")
include(":examples:freetype:platforms:web:plugin")
include(":examples:freetype:platforms:ios")
if(hasAndroidSdk) {
    include(":examples:freetype:platforms:android")
}

include(":examples:controllers:core")
include(":examples:controllers:platforms:desktop:lwjgl3")
include(":examples:controllers:platforms:desktop:teavm-c:builder")
include(":examples:controllers:platforms:web:builder")
include(":examples:controllers:platforms:web:plugin")
include(":examples:controllers:platforms:ios")
if(hasAndroidSdk) {
    include(":examples:controllers:platforms:android")
}

// Benchmarks
include(":benchmark")
include(":benchmark:core")
include(":benchmark:lwjgl3")
include(":benchmark:graalvm")
include(":benchmark:glfw")

val file = File(settingsDir, "gradle.properties")

val properties = Properties()
if(file.exists()) {
    properties.load(file.inputStream())
}

val gdxSourcePath = properties.getOrDefault("gdxSourcePath", "") as String
val teavmPath = properties.getOrDefault("teavmPath", "") as String
val includeLibgdxSource = (properties.getOrDefault("includeLibgdxSource", "false") as String).toBoolean()
val includeTeaVMSource = (properties.getOrDefault("includeTeaVMSource", "false") as String).toBoolean()

if(includeLibgdxSource) {
    include(":examples:gdx-tests:core")
    include(":examples:gdx-tests:platforms:desktop:lwjgl2")
    include(":examples:gdx-tests:platforms:web:builder")
    includeBuild(gdxSourcePath)
}

if(includeTeaVMSource) {
    includeBuild(teavmPath) {
        dependencySubstitution {
            substitute(module("org.teavm:teavm-tooling")).using(project(":tools:core"))
            substitute(module("org.teavm:teavm-core")).using(project(":core"))
            substitute(module("org.teavm:teavm-classlib")).using(project(":classlib"))
            substitute(module("org.teavm:teavm-jso")).using(project(":jso:core"))
            substitute(module("org.teavm:teavm-jso-apis")).using(project(":jso:apis"))
            substitute(module("org.teavm:teavm-jso-impl")).using(project(":jso:impl"))
            substitute(module("org.teavm:teavm-interop")).using(project(":interop:core"))
            substitute(module("org.teavm:teavm-metaprogramming-api")).using(project(":metaprogramming:api"))
            substitute(module("org.teavm:teavm-metaprogramming-impl")).using(project(":metaprogramming:impl"))
        }
    }
}

//includeBuild("E:\\Dev\\Projects\\java\\jParser") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.jParser:jParser-base")).using(project(":jParser:jParser-base"))
//        substitute(module("com.github.xpenatan.jParser:jParser-build")).using(project(":jParser:jParser-build"))
//        substitute(module("com.github.xpenatan.jParser:jParser-build-tool")).using(project(":jParser:jParser-build-tool"))
//        substitute(module("com.github.xpenatan.jParser:jParser-core")).using(project(":jParser:jParser-core"))
//        substitute(module("com.github.xpenatan.jParser:jParser-cpp")).using(project(":jParser:jParser-cpp"))
//        substitute(module("com.github.xpenatan.jParser:jParser-idl")).using(project(":jParser:jParser-idl"))
//        substitute(module("com.github.xpenatan.jParser:jParser-teavm")).using(project(":jParser:jParser-teavm"))
//        substitute(module("com.github.xpenatan.jParser:idl-core")).using(project(":idl:idl-core"))
//        substitute(module("com.github.xpenatan.jParser:idl-teavm")).using(project(":idl:idl-teavm"))
//        substitute(module("com.github.xpenatan.jParser:loader-core")).using(project(":loader:loader-core"))
//        substitute(module("com.github.xpenatan.jParser:loader-teavm")).using(project(":loader:loader-teavm"))
//    }
//}
