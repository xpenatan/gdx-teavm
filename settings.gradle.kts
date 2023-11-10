include(":backends:backend-teavm")

include(":extensions:gdx-freetype-teavm")

include(":tools:generator:core")
include(":tools:generator:ui")
include(":tools:generator:desktop")

include(":examples:core:core")
include(":examples:core:desktop")
include(":examples:core:teavm")

include(":examples:freetype:core")
include(":examples:freetype:desktop")
include(":examples:freetype:teavm")

// ######### Add libgdx tests to project
// ######### Need to have libgdx project source code tag 1.12.0.
// ######### Need to disable in libgdx settings :tests:gdx-tests-android, :tests:gdx-tests-gwt and :backends:gdx-backend-android because of some conflicts
// ######### Need to update assets path in desktop gradle and teavm build class.

//include(":examples:gdx-tests:core")
//include(":examples:gdx-tests:desktop")
//include(":examples:gdx-tests:teavm")
//includeBuild("E:\\Dev\\Projects\\java\\libgdx")

// #########

// ######### Replace teavm libs to use teavm sources
// ######### Only use it if you want to test new teavm code.
// ######### Change to your teavm source directory

//includeBuild("D:\\Dev\\Projects\\java\\teavm") {
//    dependencySubstitution {
//        substitute module('org.teavm:teavm-tooling') using project(':tools:core')
//        substitute module('org.teavm:teavm-core') using project(':core')
//        substitute module('org.teavm:teavm-classlib') using project(':classlib')
//        substitute module('org.teavm:teavm-jso') using project(':jso:core')
//        substitute module('org.teavm:teavm-jso-apis') using project(':jso:apis')
//        substitute module('org.teavm:teavm-jso-impl') using project(':jso:impl')
//    }
//}

//includeBuild("E:\\Dev\\Projects\\java\\gdx-imgui") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui")).using(project(":imgui:core"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-desktop")).using(project(":imgui:desktop"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imgui-teavm")).using(project(":imgui:teavm"))
//        substitute(module("com.github.xpenatan.gdx-imgui:gdx")).using(project(":extensions:gdx"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imlayout-core")).using(project(":extensions:imlayout:imlayout-core"))
//        substitute(module("com.github.xpenatan.gdx-imgui:imlayout-desktop")).using(project(":extensions:imlayout:imlayout-desktop"))
//        substitute(module("com.github.xpenatan.gdx-imgui:gdx-frame-viewport")).using(project(":extensions:gdx-frame-viewport"))
//    }
//}

// #########