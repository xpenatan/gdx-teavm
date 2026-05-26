plugins {
    id("java")
}

LibExt.initProperties(rootDir)

val androidApplicationProjects = setOf(":examples:basic:android")

subprojects {
    if(path !in androidApplicationProjects) {
        apply {
            plugin("java")
        }

        java.sourceCompatibility = JavaVersion.VERSION_11
        java.targetCompatibility = JavaVersion.VERSION_11
    }

    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven { url = uri("https://central.sonatype.com/repository/maven-snapshots/") }
        maven { url = uri("https://jitpack.io") }

        maven {
            url = uri("http://teavm.org/maven/repository/")
            isAllowInsecureProtocol = true
        }
    }

    configurations.configureEach {
        // Check for updates every sync
        resolutionStrategy.cacheChangingModulesFor(0, "seconds")
    }
}

extra["gdxTeaVMPublishTarget"] = GdxTeaVMPublishTarget.LIBRARIES
apply(plugin = "publish")
