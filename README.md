# Gdx Html5 tools

Gdx-Html5-tools¹ is a solution to run libgdx games in a browser using [TeaVM](https://github.com/konsoletyper/teavm) with some reflection support. It also contains some extensions like Bullet Physics(WIP) and ImGui(TODO).
<br><br>

¹: Gdx-Html5-tools is WIP. It's slowly being developed by a single dev. If you want to help, make a PR =D. I'm always at gdx discord.

## Setup:
- TODO

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

## TeaVM Examples:
* [gdx-tests](https://xpenatan.github.io/gdx-html5-tools/teavm/gdx-tests/)
* [demo-cubocy](https://xpenatan.github.io/gdx-html5-tools/teavm/demo-cubocy/)
* [demo-superjumper](https://xpenatan.github.io/gdx-html5-tools/teavm/demo-superjumper/)
* [test-freetype](https://xpenatan.github.io/gdx-html5-tools/teavm/test-freetype-packtest/)
* [test-bullet](https://xpenatan.github.io/gdx-html5-tools/teavm/test-bullet/)