val mainClassName = "com.github.xpenatan.gdx.html5.generator.Main"

dependencies {
    implementation(project(":tools:generator:core"))
    implementation("com.badlogicgames.gdx:gdx-platform:${LibExt.gdxVersion}:natives-desktop")
    implementation("com.badlogicgames.gdx:gdx-backend-lwjgl:${LibExt.gdxVersion}")
}

tasks.register<JavaExec>("runGenerator") {
    group = "teavm"
    description = "Run Generator"
    mainClass.set(mainClassName)
    classpath = sourceSets["main"].runtimeClasspath

    if (org.gradle.internal.os.OperatingSystem.current() == org.gradle.internal.os.OperatingSystem.MAC_OS) {
        // Required to run on macOS
        jvmArgs?.add("-XstartOnFirstThread")
    }
}

//tasks.register<Copy>("copyDependencies") {
//    from { configurations.default }
//            {
//                exclude "core*.jar"
//                exclude "jorbis-*.jar"
//                exclude "sac-*.jar"
//                exclude "validation-api-*.jar"
//                exclude "jutils-*.jar"
//                exclude "jsinterop-*.jar"
//                exclude "jlayer-*.jar"
//                exclude "jinput-*.jar"
//                exclude "javax.*.jar"
//                exclude "gwt-user*.jar"
//            }
//    into 'build/libs/dependencies'
//}

//def projectsToCollect = [':tools:generator:desktop', ':tools:generator:core']


//TODO fix dist

//tasks.register<Jar>("dist") {
//    dependsOn([copyDependencies, projectsToCollect.collect { it + ":compileJava" }])
//    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
//    manifest {
//        attributes(
//                'Main-Class': project.mainClassName
//        )
//    }
//    from files(projectsToCollect.collect { project(it).sourceSets.main.output })
//    from {
//        (configurations.provided).collect
//                {
//                    it.isDirectory() ? it : zipTree(it)
//                }
//    }
//    with jar
//}