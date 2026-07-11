plugins {
    id("java")
}

LibExt.initProperties(rootDir)

val androidApplicationProjects = setOf(
    ":examples:basic:android",
    ":examples:websockets:android",
)

subprojects {
    if(path !in androidApplicationProjects) {
        apply {
            plugin("java")
        }

        java.sourceCompatibility = JavaVersion.VERSION_17
        java.targetCompatibility = JavaVersion.VERSION_17
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }

        maven {
            url = uri("https://teavm.org/maven/repository/")
        }
    }

    configurations.configureEach {
        // Check for updates every sync
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}

extra["gdxTeaVMPublishTarget"] = GdxTeaVMPublishTarget.LIBRARIES
apply(plugin = "publish")
