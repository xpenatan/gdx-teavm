include(":backends:backend-teavm")

include(":extensions:gdx-freetype-teavm")

//include(":tools:generator:core")
//include(":tools:generator:ui")
//include(":tools:generator:desktop")

include(":examples:core:core")
include(":examples:core:desktop")
include(":examples:core:teavm")

include(":examples:freetype:core")
include(":examples:freetype:desktop")
include(":examples:freetype:teavm")

// ######### Add libgdx tests to project
// ######### Need to have libgdx project source code tag 1.12.1.
// ######### Need to disable in libgdx settings :tests:gdx-tests-gwt  because of some conflicts
// ######### Need to update assets path in desktop gradle and teavm build class.

//include(":examples:gdx-tests:core")
//include(":examples:gdx-tests:desktop")
//include(":examples:gdx-tests:teavm")
//includeBuild("E:\\Dev\\Projects\\java\\libgdx")

// #########

// ######### Replace teavm libs to use teavm sources
// ######### Only use it if you want to test teavm source code.
// ######### Change 'teavmPath' to your teavm source directory

//val teavmPath = "E:\\Dev\\Projects\\java\\teavm";

//includeBuild(teavmPath) {
//    dependencySubstitution {
//        substitute(module("org.teavm:teavm-tooling")).using(project(":tools:core"))
//        substitute(module("org.teavm:teavm-core")).using(project(":core"))
//        substitute(module("org.teavm:teavm-classlib")).using(project(":classlib"))
//        substitute(module("org.teavm:teavm-jso")).using(project(":jso:core"))
//        substitute(module("org.teavm:teavm-jso-apis")).using(project(":jso:apis"))
//        substitute(module("org.teavm:teavm-jso-impl")).using(project(":jso:impl"))
//    }
//}

// #########