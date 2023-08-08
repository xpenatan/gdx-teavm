include(":backends:backend-teavm")

include(":extensions:gdx-freetype-teavm")

//include(":extensions:gdx-box2d:gdx-box2d-base")
//include(":extensions:gdx-box2d:gdx-box2d-build")
//include(":extensions:gdx-box2d:gdx-box2d-teavm")

include(":tools:generator:core")
include(":tools:generator:desktop")

include(":examples:core:core")
include(":examples:core:desktop")
include(":examples:core:teavm")

//include(":examples:box2d:core")
//include(":examples:box2d:desktop")
//include(":examples:box2d:teavm")

include(":examples:freetype:core")
include(":examples:freetype:desktop")
include(":examples:freetype:teavm")

// ######### Add libgdx tests to project
// ######### Need to have libgdx source code tag 1.11.0. Change to your libgdx source directory.
// ######### You also need to update assets path in desktop gradle and teavm build class.

//include ":examples:gdx-tests:core"
//include ":examples:gdx-tests:desktop"
//include ":examples:gdx-tests:teavm"
//includeBuild('D:\\Dev\\Projects\\java\\libgdx')

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

//includeBuild("E:\\Dev\\Projects\\java\\jParser") {
//    dependencySubstitution {
//        substitute(module("com.github.xpenatan.jParser:jParser-core")).using(project(":jParser:core"))
//        substitute(module("com.github.xpenatan.jParser:jParser-teavm")).using(project(":jParser:teavm"))
//        substitute(module("com.github.xpenatan.jParser:jParser-cpp")).using(project(":jParser:cpp"))
//        substitute(module("com.github.xpenatan.jParser:loader-core")).using(project(":jParser:loader:loader-core"))
//    }
//}

// #########