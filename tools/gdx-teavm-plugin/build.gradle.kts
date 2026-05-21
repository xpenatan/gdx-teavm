import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

extra["gdxTeaVMPublishTarget"] = GdxTeaVMPublishTarget.GRADLE_PLUGIN
apply(from = "../../buildSrc/src/main/kotlin/publish.gradle.kts")

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

dependencies {
    implementation("org.teavm:teavm-gradle-plugin:${LibExt.teaVMVersion}")
}

val generatedPluginInfoDir = layout.buildDirectory.dir("generated/sources/gdxTeaVMPluginInfo/kotlin")

val generateGdxTeaVMPluginInfo = tasks.register("generateGdxTeaVMPluginInfo") {
    val groupId = LibExt.groupId
    val version = LibExt.libVersion
    inputs.property("groupId", groupId)
    inputs.property("version", version)
    outputs.dir(generatedPluginInfoDir)

    doLast {
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
    compilerOptions.jvmTarget.set(JvmTarget.JVM_11)
}

tasks.named("sourcesJar").configure {
    dependsOn(generateGdxTeaVMPluginInfo)
}

gradlePlugin {
    plugins {
        create("gdxTeaVM") {
            id = LibExt.groupId
            implementationClass = "com.github.xpenatan.gdx.teavm.gradle.GdxTeaVMGradlePlugin"
        }
    }
}
