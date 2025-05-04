# gdx-teavm

![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/release.yml/badge.svg)
![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/snapshot.yml/badge.svg)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/releases/com.github.xpenatan.gdx-teavm/backend-teavm?nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org&label=release)](https://repo.maven.apache.org/maven2/com/github/xpenatan/gdx-teavm/)
[![Static Badge](https://img.shields.io/badge/snapshot---SNAPSHOT-red)](https://oss.sonatype.org/content/repositories/snapshots/com/github/xpenatan/gdx-teavm/)

**gdx-teavm** is a backend solution for running [libGDX](https://github.com/libgdx/libgdx) games directly in web browsers. It leverages [TeaVM](https://github.com/konsoletyper/teavm), a tool that compiles Java or Kotlin bytecode into JavaScript, enabling seamless execution of game logic within the browser environment without needing additional plugins or complex setup.
Additionally, gdx-teavm incorporates [Emscripten](https://emscripten.org/) to handle some of the Java Native Interface (JNI) code, allowing for the execution of specific internal functions that require native performance.

Note:
* Reflection support is very small so only reflection used in [tests](https://github.com/konsoletyper/teavm/tree/master/tests/src/test/java/org/teavm/classlib/java/lang/reflect) will work.
* TeaVM does not support every class methods from java package or JNI native methods. Check teaVM java classes [here](https://github.com/konsoletyper/teavm/blob/master/classlib/src/main/java/org/teavm/classlib).

## TeaVM Examples
[libGDX-TeaVM](https://github.com/xpenatan/libGDX-TeaVM)


## Old TeaVM Examples:
* [gdx-tests](https://xpenatan.github.io/gdx-teavm/teavm/gdx-tests/) (Updated 07/09/2024)
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


### Versions:
| gdx-teavm |  LibGDX  | TeaVM  |
|:---------:|:--------:|:------:|
|   1.0.5   |  1.12.1  | 0.11.0 |
|   1.1.0   |  1.13.1  | 0.11.0 |
|   1.2.0   |  1.13.1  | 0.12.0 |

## Setup:
```groovy
// Add sonatype repository to Root gradle
repositories {
    
    mavenCentral()
    maven { url = uri("https://oss.sonatype.org/content/repositories/snapshots/") }
    maven { url = uri("https://oss.sonatype.org/content/repositories/releases/") }
    maven { url = uri("https://jitpack.io") }
    
    // If there is a problem with the teavm repository, you can try using http
    maven {
        url = uri("http://teavm.org/maven/repository/")
        isAllowInsecureProtocol = true
    }
}

// Version
gdxTeaVMVersion = "-SNAPSHOT"
gdxTeaVMVersion = "[LAST_TAG_VERSION]"

// In teaVM module
dependencies {
    implementation "com.github.xpenatan.gdx-teavm:backend-teavm:$project.gdxTeaVMVersion"

    // FreeType extension
    implementation "com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:$project.gdxTeaVMVersion"
}
```