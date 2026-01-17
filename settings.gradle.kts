import java.util.*

pluginManagement {
    repositories {
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

include(":extensions:asset-loader")
include(":extensions:gdx-controllers-teavm")
include(":extensions:gdx-freetype-teavm")

//include(":tools:generator:core")
//include(":tools:generator:ui")
//include(":tools:generator:desktop")

include(":examples:basic:core")
include(":examples:basic:desktop")
include(":examples:basic:teavm")
include(":examples:basic:teavm-glfw")

include(":examples:freetype:core")
include(":examples:freetype:desktop")
include(":examples:freetype:teavm")

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