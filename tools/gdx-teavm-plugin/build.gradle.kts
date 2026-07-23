import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.gradle.api.tasks.bundling.Jar

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
    alias(libs.plugins.easyPublishing)
}

val gdxTeaVMGroup = "com.github.xpenatan.gdx-teavm"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation(libs.teavmGradlePlugin)
    testImplementation(libs.junit)
}

val generatedPluginInfoDir = layout.buildDirectory.dir("generated/sources/gdxTeaVMPluginInfo/kotlin")
val generatedPluginVersion = providers.provider { project.version.toString() }

val generateGdxTeaVMPluginInfo = tasks.register("generateGdxTeaVMPluginInfo") {
    inputs.property("groupId", gdxTeaVMGroup)
    inputs.property("version", generatedPluginVersion)
    outputs.dir(generatedPluginInfoDir)

    doLast {
        val version = generatedPluginVersion.get()
        val outputFile = generatedPluginInfoDir.get()
            .file("com/github/xpenatan/gdx/teavm/gradle/GdxTeaVMPluginInfo.kt")
            .asFile
        outputFile.parentFile.mkdirs()
        outputFile.writeText(
            """
            package com.github.xpenatan.gdx.teavm.gradle

            internal object GdxTeaVMPluginInfo {
                const val GROUP = "$gdxTeaVMGroup"
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
    compilerOptions.jvmTarget.set(JvmTarget.JVM_17)
}

tasks.withType<Jar>().matching { it.name == "sourcesJar" }.configureEach {
    dependsOn(generateGdxTeaVMPluginInfo)
}

gradlePlugin {
    plugins {
        create("gdxTeaVM") {
            id = gdxTeaVMGroup
            implementationClass = "com.github.xpenatan.gdx.teavm.gradle.GdxTeaVMGradlePlugin"
        }
    }
}

easyPublishing {
    groupId.set(gdxTeaVMGroup)
    releaseVersion.set(libs.versions.gdxTeavmRelease)
    snapshotVersion.set(libs.versions.gdxTeavmSnapshot)

    snapshotRepositoryUrl.set("https://central.sonatype.com/repository/maven-snapshots/")
    releaseRepositoryUrl.set("https://central.sonatype.com")
    username.set(providers.environmentVariable("CENTRAL_PORTAL_USERNAME"))
    password.set(providers.environmentVariable("CENTRAL_PORTAL_PASSWORD"))
    signingKey.set(providers.environmentVariable("SIGNING_KEY"))
    signingPassword.set(providers.environmentVariable("SIGNING_PASSWORD"))

    pomName.set("gdx-teavm Gradle plugin")
    pomDescription.set("Gradle plugin for building libGDX TeaVM web and native targets")
    projectUrl.set("https://github.com/xpenatan/gdx-teavm")

    developerId.set("Xpe")
    developerName.set("Natan")

    scmUrl.set("https://github.com/xpenatan/gdx-teavm")
    scmConnection.set("scm:git:https://github.com/xpenatan/gdx-teavm.git")
    scmDeveloperConnection.set("scm:git:ssh://git@github.com/xpenatan/gdx-teavm.git")
}
