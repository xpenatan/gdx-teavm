# gdx-teavm

![Build](https://github.com/xpenatan/gdx-teavm/actions/workflows/snapshot.yml/badge.svg)

[![Maven Central Version](https://img.shields.io/maven-central/v/com.github.xpenatan.gdx-teavm/backend-teavm)](https://central.sonatype.com/artifact/com.github.xpenatan.gdx-teavm/backend-teavm)
[![Static Badge](https://img.shields.io/badge/snapshot---SNAPSHOT-red)](https://central.sonatype.com/service/rest/repository/browse/maven-snapshots/com/github/xpenatan/gdx-teavm/)

**gdx-teavm** is a backend solution for running [libGDX](https://github.com/libgdx/libgdx) games directly in web browsers. It leverages [TeaVM](https://github.com/konsoletyper/teavm), a tool that compiles Java or Kotlin bytecode into JavaScript or WebAssembly, enabling seamless execution of game logic within the browser environment without needing additional plugins or complex setup.
Additionally, gdx-teavm incorporates [Emscripten](https://emscripten.org/) to handle some of the Java Native Interface (JNI) code, allowing for the execution of specific internal functions that require native performance.

## TeaVM Examples
[libGDX-TeaVM](https://github.com/xpenatan/libGDX-TeaVM)

### Versions:
| gdx-teavm | LibGDX | TeaVM  |
|:---------:|:------:|:------:|
|   1.2.1   | 1.13.5 | 0.12.0 |
|   1.2.0   | 1.13.1 | 0.12.0 |
|   1.1.0   | 1.13.1 | 0.11.0 |
|   1.0.5   | 1.12.1 | 0.11.0 |

## Setup:
```groovy
// Add sonatype repository to Root gradle
repositories {
    
    mavenCentral()
    maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
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