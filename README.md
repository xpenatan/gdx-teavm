# Libgdx Dragome Backend
This backend use [DragomeSDK](https://github.com/dragome/dragome-sdk) to generate your libgdx code to javascript. Its similar to GWT but better.
* Its faster to compile
* Easy to use
* Lightweight
* Use bytecodes code and not source code
* Full or partial assets downloading 

#Example
![alt tag](http://i.imgur.com/Tf8vUA1.png)


## Demos
* [AnimationTest](http://xpenatan.github.io/dragome-backend/index.html?AnimationTest) 
* [Gears Demo](http://xpenatan.github.io/dragome-backend/index.html?Gears)

## Instructions for Eclipse (Import and play)
* Need to have **Libgdx GDX source project and my forked Dragome project** (has modifications that is not yet in the main)
* Requires this plugin (http://xzer.github.io/run-jetty-run-updatesite/nightly/) and install Jetty 93 support. You have to change run/debug configuration to use jetty 9.3.6 (default is 6.1). There could be more easy plugins.
* Run/Debug project (Ex Gdx-tests-dragome) as "Run Jetty".
* At jetty configuration classpath you have to remove "javaee-web" or it wont compile.
* Look for the context configuration to know what path you need to use in run/debug configuration. Ex: http://localhost:8080/[CONTEXT]/index.html
* Dont need to worry about dependecies. Already in forked Dragome project.

## Instructions for Maven/Gradle
* TODO
