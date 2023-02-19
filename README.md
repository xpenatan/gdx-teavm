![Build](https://github.com/xpenatan/gdx-html5-tools/workflows/Build/badge.svg)

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/r/com.github.xpenatan.gdx-teavm/backend-teavm?nexusVersion=2&server=https%3A%2F%2Foss.sonatype.org&label=release)](https://repo.maven.apache.org/maven2/com/github/xpenatan/gdx-teavm/)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.xpenatan.gdx-teavm/backend-teavm?server=https%3A%2F%2Foss.sonatype.org&label=snapshot)](https://oss.sonatype.org/content/repositories/snapshots/com/github/xpenatan/gdx-teavm/)

gdx-teaVM is a backend solution to run [libgdx](https://github.com/libgdx/libgdx) games in a web browser. It uses [TeaVM](https://github.com/konsoletyper/teavm) behind the scene to convert Java/Kotlin bytecode to Javascript.

Note:
* Reflection support is very small so only reflection used in [tests](https://github.com/konsoletyper/teavm/tree/master/tests/src/test/java/org/teavm/classlib/java/lang/reflect) will work.
* teaVM does not support every class methods from java package or JNI native methods. Check teaVM java classes [here](https://github.com/konsoletyper/teavm/blob/master/classlib/src/main/java/org/teavm/classlib).
* Kotlin [discussions](https://github.com/libktx/ktx/discussions/443).
* Box2d, Bullet and freetype extension use [emscripten](https://emscripten.org/) to convert C++ to Javascript/WebAssembly. Box2d and Bullet use a custom parser ([jParser](https://github.com/xpenatan/jParser)) to help bind javascript code.

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
}
```
    gdxTeaVMVersion = "1.0.0-SNAPSHOT"
```groovy
// In teaVM module
dependencies {
    implementation "com.github.xpenatan.gdx-teavm:backend-teavm:$project.gdxTeaVMVersion"

    // Bullet extension
    implementation "com.github.xpenatan.gdx-teavm:gdx-bullet-teavm:$project.gdxTeaVMVersion"
    // Box2D extension
    implementation "com.github.xpenatan.gdx-teavm:gdx-box2d-teavm:$project.gdxTeaVMVersion"
    // FreeType extension
    implementation "com.github.xpenatan.gdx-teavm:gdx-freetype-teavm:$project.gdxTeaVMVersion"
}
```

## Supported Extensions:
- Box2D (WIP)¹
- Bullet Physics (WIP)²
- FreeType

¹: Box2D extension is WIP, please check if the class/method your game use is in [webidl](https://github.com/xpenatan/gdx-teavm/blob/master/extensions/gdx-box2d/gdx-box2d-build/jni/box2D.idl) file. If not, you can use gdx-box2d-gwt. <br>
²: Bullet extension is WIP, please check if the class/method your game use is in [webidl](https://github.com/xpenatan/gdx-teavm/blob/master/extensions/gdx-bullet/gdx-bullet-build/jni/bullet.idl) file. This extension does not support some custom [c++ code](https://github.com/libgdx/libgdx/tree/master/extensions/gdx-bullet/jni/src/custom/gdx) from libgdx bullet.

## Generator:
A WIP standalone tool to convert your libgdx game in .jar or .class format to javascript.  [Example](https://youtu.be/BIL_5eaxg9w)
<br>
<br>
Note: The compiled jar game should not be obfuscated.

Setup: TODO
