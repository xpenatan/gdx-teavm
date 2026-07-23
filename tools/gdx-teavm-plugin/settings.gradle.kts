pluginManagement {
    repositories {
        maven {
            url = uri("https://central.sonatype.com/repository/maven-snapshots/")
        }
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://teavm.org/maven/repository/")
        }
    }
}

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../../gradle/libs.versions.toml"))
        }
    }

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://teavm.org/maven/repository/")
        }
    }
}
