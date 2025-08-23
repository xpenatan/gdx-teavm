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

include(":backends:backend-teavm")

include(":extensions:asset-loader")
include(":extensions:gdx-freetype-teavm")

//include(":tools:generator:core")
//include(":tools:generator:ui")
//include(":tools:generator:desktop")

include(":examples:core:core")
include(":examples:core:desktop")
include(":examples:core:teavm")
include(":examples:core:web-audio")

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