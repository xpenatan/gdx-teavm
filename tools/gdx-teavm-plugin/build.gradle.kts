import com.github.xpenatan.easypublishing.EasyPublishingExtension
import com.github.xpenatan.easypublishing.EasyPublishingPlugin
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.bundling.Jar

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.easyPublishing)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.teavmGradlePlugin)
    testImplementation(libs.junit)
}

val generatedPluginInfoDir = layout.buildDirectory.dir("generated/sources/gdxTeaVMPluginInfo/kotlin")
val easyPublishingExtension = extensions.getByType<EasyPublishingExtension>()
val generatedPluginVersion = providers.provider {
    val releaseRequested = extensions.extraProperties
        .get(EasyPublishingPlugin.RELEASE_REQUESTED_EXTRA) as Boolean
    if(releaseRequested) {
        easyPublishingExtension.releaseVersion.get()
    }
    else {
        easyPublishingExtension.snapshotVersion.get()
    }
}

val generateGdxTeaVMPluginInfo = tasks.register("generateGdxTeaVMPluginInfo") {
    inputs.property("groupId", libs.versions.gdxTeavmGroup)
    inputs.property("version", generatedPluginVersion)
    outputs.dir(generatedPluginInfoDir)

    doLast {
        val groupId = libs.versions.gdxTeavmGroup.get()
        val version = generatedPluginVersion.get()
        val outputFile = generatedPluginInfoDir.get()
            .file("com/github/xpenatan/gdx/teavm/gradle/GdxTeaVMPluginInfo.kt")
            .asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package com.github.xpenatan.gdx.teavm.gradle

            internal object GdxTeaVMPluginInfo {
                const val GROUP = "$groupId"
                const val VERSION = "$version"
            }
            """.trimIndent() + "\n"
        )
    }
}

kotlin {
    sourceSets.named("main") {
        kotlin.srcDir(generatedPluginInfoDir)
    }
}

tasks.withType<KotlinCompile>().configureEach {
    dependsOn(generateGdxTeaVMPluginInfo)
    compilerOptions.jvmTarget = JvmTarget.JVM_17
}

tasks.withType<Jar>().matching { it.name == "sourcesJar" }.configureEach {
    dependsOn(generateGdxTeaVMPluginInfo)
}

gradlePlugin {
    plugins {
        create("gdxTeaVM") {
            id = libs.versions.gdxTeavmGroup.get()
            implementationClass = "com.github.xpenatan.gdx.teavm.gradle.GdxTeaVMGradlePlugin"
        }
    }
}

easyPublishing {
    groupId = libs.versions.gdxTeavmGroup
    releaseVersion = libs.versions.gdxTeavmRelease
    snapshotVersion = libs.versions.gdxTeavmSnapshot

    snapshotRepositoryUrl = "https://central.sonatype.com/repository/maven-snapshots/"
    releaseRepositoryUrl = "https://central.sonatype.com"
    username = providers.environmentVariable("CENTRAL_PORTAL_USERNAME")
    password = providers.environmentVariable("CENTRAL_PORTAL_PASSWORD")
    signingKey = providers.environmentVariable("SIGNING_KEY")
    signingPassword = providers.environmentVariable("SIGNING_PASSWORD")

    pomName = "gdx-teavm Gradle plugin"
    pomDescription = "Gradle plugin for building libGDX TeaVM web and native targets"
    projectUrl = "https://github.com/xpenatan/gdx-teavm"

    developerId = "Xpe"
    developerName = "Natan"

    scmUrl = "https://github.com/xpenatan/gdx-teavm"
    scmConnection = "scm:git:https://github.com/xpenatan/gdx-teavm.git"
    scmDeveloperConnection = "scm:git:ssh://git@github.com/xpenatan/gdx-teavm.git"
}
