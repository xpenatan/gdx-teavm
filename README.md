# Gdx-teaVM
![Build](https://github.com/xpenatan/gdx-html5-tools/workflows/Build/badge.svg)

Gdx-teaVM is a solution to run [libgdx](https://github.com/libgdx/libgdx) games in a web browser. [TeaVM](https://github.com/konsoletyper/teavm) is tool to convert java/kotlin bytecode to javascript. It's a WIP so not everything will work.

Note:
* Reflection support is very small so only reflection used in [ReflectionTest](https://github.com/xpenatan/gdx-teavm/blob/master/examples/core/core/src/main/java/com/github/xpenatan/gdx/examples/tests/ReflectionTest.java) and teaVM [tests](https://github.com/konsoletyper/teavm/tree/master/tests/src/test/java/org/teavm/classlib/java/lang/reflect) will work.
* teaVM does not support every class methods from java package. For example, if your code or a lib call a method from Class that it is not [here](https://github.com/konsoletyper/teavm/blob/master/classlib/src/main/java/org/teavm/classlib/java/lang/TClass.java), you will get errors. 
* Kotlin [discussions](https://github.com/libktx/ktx/discussions/443).

## TeaVM Examples:
* [gdx-tests](https://xpenatan.github.io/gdx-teavm/teavm/gdx-tests/)
* [demo-cubocy](https://xpenatan.github.io/gdx-teavm/teavm/demo-cubocy/)
* [demo-superjumper](https://xpenatan.github.io/gdx-teavm/teavm/demo-superjumper/)
* [test-freetype](https://xpenatan.github.io/gdx-teavm/teavm/test-freetype-packtest/)
* [test-bullet-wasm](https://xpenatan.github.io/gdx-teavm/teavm/test-bullet/)
* [test-box2d-wasm](https://xpenatan.github.io/gdx-teavm/teavm/test-box2d/)

## How it works:
The backend-web was created with the idea of code reuse in mind so multiple javascript backends can use it, it only contains java code. backend-teavm contains teaVM code to generate libgdx games to javascript.

Like GDX GWT backend, it uses the same solution to emulate classes. If the class contains JNI calls or a code that javascript can't handle, it needs to be emulated and have to be loaded first when compiling to javascript. Some backend code was ported from gdx-gwt-backend.

Box2d, Bullet and freetype extension use emscripten to convert C++ to Javascript/WebAssembly. Box2d and Bullet use a custom parser ([jParser](https://github.com/xpenatan/jParser)) to generate java class to bind javascript code.

## Setup:
```groovy
// Add sonatype repository to Root gradle
repositories {
    maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
    maven { url "https://oss.sonatype.org/content/repositories/releases/" }
}
```
    gdxTeaVMVersion = "1.0.0-SNAPSHOT"
```groovy
// In teaVM module
dependencies {
    implementation "com.github.xpenatan.gdx-teavm:backend-web:$project.gdxTeaVMVersion"
    implementation "com.github.xpenatan.gdx-teavm:backend-teavm-core:$project.gdxTeaVMVersion"
    implementation "com.github.xpenatan.gdx-teavm:backend-teavm-native:$project.gdxTeaVMVersion"

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
<br>

## jParser
[jParser](https://github.com/xpenatan/jParser) is a solution using [javaparser](https://github.com/javaparser/javaparser) to read a java source file and generate a new modified java source file.
The main goal is to make it easy to add, modify or remove part of the original source code and create a new java source from it. It's possible to have c++ and javascript code in the same java file.