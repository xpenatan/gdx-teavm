pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven {
            url = uri("https://teavm.org/maven/repository/")
        }
    }
}

dependencyResolutionManagement {
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
