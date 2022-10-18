# Gdx Html5 tools

Gdx-Html5-tools is a solution to run libgdx games in a browser using [TeaVM](https://github.com/konsoletyper/teavm) with some reflection support.

## TeaVM Examples:
* [gdx-tests](https://xpenatan.github.io/gdx-html5-tools/teavm/gdx-tests/)
* [demo-cubocy](https://xpenatan.github.io/gdx-html5-tools/teavm/demo-cubocy/)
* [demo-superjumper](https://xpenatan.github.io/gdx-html5-tools/teavm/demo-superjumper/)
* [test-freetype](https://xpenatan.github.io/gdx-html5-tools/teavm/test-freetype-packtest/)
* [test-bullet-wasm](https://xpenatan.github.io/gdx-html5-tools/teavm/test-bullet/)

## How it works:
The backend-web was created with the idea of code reuse in mind so that each compiler-backend doesn't have to start from scratch.
The backend-web contains some interface required for specific javascript communication and that's where the compiler backend comes in.
It started with a very simple test case using Dragome and teaVM, but when some breaking bugs in the Dragome compiler began to appear I decided to stop.
Some codes are ported from GWT backend.

Like GWT backend, it uses the same solution to emulate classes. If the class contains JNI calls or a code that javascript can't handle, it needs to be emulated and have to be loaded first when compiling to javascript.
backend-web contains an emu folder that emulate some classes that work for any compiler solution while teaVM contains specific emulation code that it's only for teaVM compiler. 

## Setup:
- TODO

## Supported Extensions:
- Box2D
- Bullet Physics (WIP)¹
- FreeType

¹: Don't support custom libgdx bullet physics code. The goal is to be 1-1 to C++ classes and only support Vector3 and Matrix4.

## Gdx-Html5-Generator:
A standalone tool to convert your libgdx game in .jar format to javascript. There is no need to create a html gradle module.
<br>
<br>
Note: The compiled jar game should not be obfuscated.
<br>
<br>
Required inputs:
* Compiled Jar file. Ex: C:\MyGame\MyGame.jar
* Application class name. Ex: com.mygame.game.MyGameApplication
* Assets path¹. Ex: C:\MyGame\Android\assets 
* Webapp path².

The generator also contains a local server button to test your html5 game.
<br>
Open http://localhost:8080/ in your browser and your game will start if the generation process was a success.


¹: For now its required to know where are the assets. In later versions the generator will search inside the jar file.
<br>
²: The destination of the generated files. This is the files where you add to your website.

Generator example: https://youtu.be/BIL_5eaxg9w

## JParser
Is a solution using [javaparser](https://github.com/javaparser/javaparser) to read a java source file and generate a new modified java source file.
The main goal is to make it easy to add, modify or remove part of the original source code and generate it with something else.
It's being used in Bullet and Box2D extension to read WebIDL file and generate methods.
The TeaVMCodeParser is an additional parser for teaVM to generate methods that binds java code to javascript code. 