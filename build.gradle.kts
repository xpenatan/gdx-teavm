plugins {
    id("java")
    alias(libs.plugins.easyPublishing)
}

subprojects {
    val isAndroidApplicationExample = path.startsWith(":examples:") && path.endsWith(":android")
    if(!isAndroidApplicationExample) {
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

val publishingModules = mutableListOf(
    ":backends:backend-shared",
    ":backends:backend-web",
    ":backends:backend-glfw",
    ":backends:backend-ios",
    ":extensions:c:gdx-freetype-c",
    ":extensions:c:gdx-controllers-glfw",
    ":extensions:ios:gdx-controllers-ios",
    ":extensions:web:gdx-controllers-web",
    ":extensions:web:gdx-freetype-web",
    ":extensions:asset-loader"
)
if(findProject(":backends:backend-android") != null) {
    publishingModules.add(":backends:backend-android")
}
if(findProject(":extensions:android:gdx-controllers-android") != null) {
    publishingModules.add(":extensions:android:gdx-controllers-android")
}

easyPublishing {
    modules(publishingModules)

    groupId = libs.versions.gdxTeavmGroup
    releaseVersion = libs.versions.gdxTeavmRelease
    snapshotVersion = libs.versions.gdxTeavmSnapshot

    snapshotRepositoryUrl = "https://central.sonatype.com/repository/maven-snapshots/"
    releaseRepositoryUrl = "https://central.sonatype.com"
    username = providers.environmentVariable("CENTRAL_PORTAL_USERNAME")
    password = providers.environmentVariable("CENTRAL_PORTAL_PASSWORD")
    signingKey = providers.environmentVariable("SIGNING_KEY")
    signingPassword = providers.environmentVariable("SIGNING_PASSWORD")

    pomName = libs.versions.gdxTeavmName
    pomDescription = "Tool to generate libgdx to javascript using teaVM"
    projectUrl = "https://github.com/xpenatan/gdx-teavm"

    developerId = "Xpe"
    developerName = "Natan"

    scmUrl = "https://github.com/xpenatan/gdx-teavm"
    scmConnection = "scm:git:https://github.com/xpenatan/gdx-teavm.git"
    scmDeveloperConnection = "scm:git:ssh://git@github.com/xpenatan/gdx-teavm.git"

    nestedBuild("gradle-plugin") {
        directory = layout.projectDirectory.dir("tools/gdx-teavm-plugin")
    }
}
