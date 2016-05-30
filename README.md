# Libgdx Dragome Backend
This backend use [DragomeSDK](https://github.com/dragome/dragome-sdk) to generate your libgdx code to javascript. Its similar to GWT but better.
* Its faster to compile
* Easy to use
* Lightweight
* Use bytecodes code and not source code
* Full or partial assets downloading 

#Example
![alt tag](http://i.imgur.com/0LWcHQF.gif)


## Demos:
* [AnimationTest](http://xpenatan.github.io/gdx-dragome-backend/index.html?AnimationTest) 
* [Gears Demo](http://xpenatan.github.io/gdx-dragome-backend/index.html?Gears)
* [BulletTest](http://xpenatan.github.io/gdx-dragome-backend/index.html?BulletTest) (Experimental)

## Instructions for Eclipse (Import and launch)
* Must have Libgdx source project set in eclipse.
* Must have DragomeSDK source project set in eclipse.
  * DragomeSDK requires some dependecies. Use Ant with pom.xml to get them.
  * May need to have the forked [Dragome project] (https://github.com/xpenatan/dragome-sdk) (There is a chance it has modifications that is not yet in official branch).
* Recommended plugin: ( http://xzer.github.io/run-jetty-run-updatesite/nightly/ ). Also install Jetty 93 support. You have to change run/debug configuration to use jetty 9.3.6 (default is 6.1). 
  * At jetty configuration classpath you have to remove "javaee-web" or it wont compile.
  * Run/Debug project (Ex Gdx-tests-dragome) as "Run Jetty".
  * Look for context configuration to know the http link for testing. Ex: http://localhost:8080/[CONTEXT]/index.html

