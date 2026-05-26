import java.util.*

pluginManagement {
    includeBuild("tools/gdx-teavm-plugin")

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }
}

include(":backends:backend-shared")
include(":backends:backend-web")
include(":backends:backend-glfw")
include(":backends:backend-psp")
include(":backends:backend-android")

include(":extensions:asset-loader")
include(":extensions:web:gdx-controllers-web")
include(":extensions:web:gdx-freetype-web")

//include(":tools:generator:core")
//include(":tools:generator:ui")
//include(":tools:generator:desktop")

include(":examples:basic:core")
include(":examples:basic:lwjgl3")
include(":examples:basic:graalvm")
include(":examples:basic:plugin")
include(":examples:basic:android")
include(":examples:basic:web")
include(":examples:basic:glfw")
include(":examples:basic:psp")

include(":benchmark")
include(":benchmark:core")
include(":benchmark:lwjgl3")
include(":benchmark:graalvm")
include(":benchmark:glfw")

include(":examples:freetype:core")
include(":examples:freetype:desktop")
include(":examples:freetype:web")

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
    include(":examples:gdx-tests:desktop")
    include(":examples:gdx-tests:teavm")
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
