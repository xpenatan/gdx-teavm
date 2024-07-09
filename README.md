## gdx-teavm

![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/release.yml/badge.svg)
![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/snapshot.yml/badge.svg)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/releases/com.github.xpenatan.gdx-teavm/backend-teavm?nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org&label=release)](https://repo.maven.apache.org/maven2/com/github/xpenatan/gdx-teavm/)
[![Static Badge](https://img.shields.io/badge/snapshot---SNAPSHOT-red)](https://oss.sonatype.org/content/repositories/snapshots/com/github/xpenatan/gdx-teavm/)

gdx-teavm is a backend solution for running [libgdx](https://github.com/libgdx/libgdx) games in a web browser. It uses [TeaVM](https://github.com/konsoletyper/teavm) behind the scene to convert Java/Kotlin bytecode to Javascript.

Note:
* Reflection support is very small so only reflection used in [tests](https://github.com/konsoletyper/teavm/tree/master/tests/src/test/java/org/teavm/classlib/java/lang/reflect) will work.
* TeaVM does not support every class methods from java package or JNI native methods. Check teaVM java classes [here](https://github.com/konsoletyper/teavm/blob/master/classlib/src/main/java/org/teavm/classlib).
* Kotlin [discussions](https://github.com/libktx/ktx/discussions/443).
* Box2d, Bullet, ImGui, PhysX and Freetype libraries use [emscripten](https://emscripten.org/) and [jParser](https://github.com/xpenatan/jParser) to convert C++ to Javascript/WebAssembly.

## TeaVM Examples:
* [gdx-tests](https://xpenatan.github.io/gdx-teavm/teavm/gdx-tests/)
* [demo-cubocy](https://xpenatan.github.io/gdx-teavm/teavm/demo-cubocy/)
* [demo-superjumper](https://xpenatan.github.io/gdx-teavm/teavm/demo-superjumper/)
* [test-freetype](https://xpenatan.github.io/gdx-teavm/teavm/test-freetype-packtest/)
* [test-bullet-wasm](https://xpenatan.github.io/gdx-teavm/teavm/test-bullet/)
* [test-box2d-wasm](https://xpenatan.github.io/gdx-teavm/teavm/test-box2d/)
* [micronaut-libgdx-teavm](https://github.com/hollingsworthd/micronaut-libgdx-teavm)
* [6dof-vehicle](https://xpenatan.github.io/gdx-6dof-vehicle-demo/teavm/vehicle/) ([source](https://github.com/xpenatan/gdx-6dof-vehicle-demo))

## TeaVM Games:
* [Age of Conquest](https://www.ageofconquest.com/webapp/) ([website](https://www.ageofconquest.com/))
* [Retro Commander](https://www.retrocommander.com/webapp/) ([website](https://www.retrocommander.com/))


## Setup:
```groovy
// Add sonatype repository to Root gradle
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    maven { url "https://oss.sonatype.org/content/repositories/releases/" }
    maven { url "https://teavm.org/maven/repository/" }
    
    // If there is a problem with the teavm repository, you can try using http
    maven {
        url = uri("http://teavm.org/maven/repository/")
        isAllowInsecureProtocol = true
    }
}
```
    // SNAPSHOT:
    gdxTeaVMVersion = "-SNAPSHOT"
    // RELEASE:
    gdxTeaVMVersion = "[LAST_TAG_VERSION]"
```groovy
// In teaVM module
dependencies {
    implementation "com.github.xpenatan.gdx-teavm:backend-teavm:$project.gdxTeaVMVersion"

    // FreeType extension
    implementation "com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:$project.gdxTeaVMVersion"
}
```

## Supported libraries:
- [Box2d](https://github.com/xpenatan/gdx-box2d) (WIP). Use GWTBox2d for now.
- [Bullet](https://github.com/xpenatan/gdx-bullet) (WIP)
- [PhysX](https://github.com/xpenatan/gdx-physx) (WIP)
- [ImGui](https://github.com/xpenatan/gdx-imgui) (WIP)
- [Lua](https://github.com/xpenatan/gdx-lua) (WIP)
- FreeType
