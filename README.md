# Gdx Web tools
![Build](https://github.com/xpenatan/gdx-web-tools/workflows/Build/badge.svg)

[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.github.xpenatan.gdx-web-tools/dragome-backend?label=dragome-backend&server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/#nexus-search;gav~com.github.xpenatan.dragome-sdk~dragome-js-commons~~~~kw,versionexpand)


#TODO

This backend use [DragomeSDK](https://github.com/xpenatan/dragome-sdk) fork to generate libgdx games to javascript. A alternative to gwt backend.

* Its fast to compile ¹
* Easy to use
* Lightweight ²
* Use bytecodes and not source code
* ProGuard ready Shrink/Obfuscate ³
* Reflection ready

<p align="left"><img src="http://i.imgur.com/Gz5CgvK.png"/></p>

¹: It can take seconds to compile when using proguard shrink + obfuscation.

²: All in one jar is about 16mb and includes Dragome Backend, Proguard and DragomeSDK.

³: Shrink remove unused classes and Obfuscate makes your class/methods smaller and harder to mess with it.


**Quick Setup:**

```Gradle
//######## Root build.gradle

repositories {
  mavenLocal()
  mavenCentral()
  maven { url "https://oss.sonatype.org/content/repositories/snapshots/" }
  maven { url "https://oss.sonatype.org/content/repositories/releases/" }
}

project(":dragome") {
    apply plugin: "java"

    dependencies {
        compile project(":core")
        compile "com.github.xpenatan:gdx-backend-dragome:1.0.0-ALL-SNAPSHOT"
    }
}

//######## Dragome Project build.gradle

apply plugin: 'org.akhikhl.gretty'

sourceCompatibility = 1.8

gretty {
  extraResourceBase 'webapp'
}

sourceSets.main.java.srcDirs = ["src"]

buildscript {
  repositories {
    jcenter()
  }

  dependencies {
    classpath 'org.akhikhl.gretty:gretty:+'
  }
}
```
**Running:**
```
gradlew :dragome:jettyRun
```

Current demos:
* [XpeEditor](https://xpenatan.github.io/XpeEngine/XpeEditor/index.html?XpeEditore) - A bit heavy for low end systems
* [AnimationTest](http://xpenatan.github.io/gdx-dragome-backend/index.html?AnimationTest) 
* [Gears Demo](http://xpenatan.github.io/gdx-dragome-backend/index.html?Gears)
* [BulletTest](http://xpenatan.github.io/gdx-dragome-backend/index.html?BulletTest)
* [BulletTestCollection](http://xpenatan.github.io/gdx-dragome-backend/index.html?BulletCollection)
* [Box2DTestCollection](http://xpenatan.github.io/gdx-dragome-backend/index.html?Box2DCollection)
* [UITest](http://xpenatan.github.io/gdx-dragome-backend/index.html?UITest)
* [Quantum](https://xpenatan.github.io/gdx-quantum/)
* [Shader-toy](https://xpenatan.github.io/gdx-shadertoy/) - [Github](https://github.com/xpenatan/gdx-shadertoy)
* [Pax-Britannica](https://xpenatan.github.io/libgdx-demo-pax-britannica/) - [Github](https://github.com/xpenatan/libgdx-demo-pax-britannica)
* [FreeType](http://xpenatan.github.io/gdx-dragome-backend/index.html?FreeType)


# Go to [Wiki](https://github.com/xpenatan/gdx-dragome-backend/wiki) for more information.

#Example
<p align="center"><img src="http://i.imgur.com/v8I8lQy.gif"/></p>

<p align="center"><img src="http://i.imgur.com/fznvMb7.gif"/></p>

<p align="center"><img src="http://i.imgur.com/r3c3lhX.gif"/></p>

<p align="center"><img src="http://i.imgur.com/rDMH2Fw.gif"/></p>

