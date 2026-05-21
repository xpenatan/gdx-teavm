import java.net.URLEncoder
import java.nio.file.Files
import java.nio.file.Paths
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.PublishToMavenRepository
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.external.javadoc.StandardJavadocDocletOptions
import org.gradle.plugins.signing.SigningExtension

val taskNames = gradle.startParameter.taskNames
fun isTaskRequested(taskName: String): Boolean {
    return taskNames.any { it == taskName || it.endsWith(":$taskName") }
}

val isPrepareSnapshotDeploy = isTaskRequested("prepareSnapshotDeploy")
val isReleasePublish = isTaskRequested("publishRelease")
val isPrepareReleaseDeploy = isTaskRequested("prepareReleaseDeploy")
val isUploadToMavenCentral = isTaskRequested("uploadToMavenCentral")
val releaseProperty = providers.gradleProperty("gdxTeaVM.release")
    .map(String::toBoolean)
    .orElse(false)

LibExt.isRelease = isReleasePublish || isPrepareReleaseDeploy || isUploadToMavenCentral || releaseProperty.get()

fun MavenPom.configureGdxPom(nameValue: String, descriptionValue: String) {
    name.set(nameValue)
    description.set(descriptionValue)
    url.set("https://github.com/xpenatan/gdx-teavm")
    developers {
        developer {
            id.set("Xpe")
            name.set("Natan")
        }
    }
    scm {
        connection.set("scm:git@github.com:xpenatan/gdx-teavm.git")
        developerConnection.set("scm:git@github.com:xpenatan/gdx-teavm.git")
        url.set("https://github.com/xpenatan/gdx-teavm")
    }
    licenses {
        license {
            name.set("The Apache License, Version 2.0")
            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
        }
    }
}

fun Project.configureGdxMavenRepository(deployDir: String? = null) {
    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                val isSnapshot = LibExt.libVersion.endsWith("-SNAPSHOT")
                url = when {
                    deployDir != null -> uri(deployDir)
                    !isSnapshot -> uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
                    isPrepareSnapshotDeploy -> uri(rootProject.layout.buildDirectory.dir("snapshot-deploy"))
                    else -> uri("https://central.sonatype.com/repository/maven-snapshots/")
                }
                if(isSnapshot && !isPrepareSnapshotDeploy && deployDir == null) {
                    credentials {
                        username = System.getenv("CENTRAL_PORTAL_USERNAME")
                        password = System.getenv("CENTRAL_PORTAL_PASSWORD")
                    }
                }
            }
        }
    }
}

fun Project.configureGdxSigning() {
    val signingKey = System.getenv("SIGNING_KEY").orEmpty()
    val signingPassword = System.getenv("SIGNING_PASSWORD").orEmpty()
    if(signingKey.isNotEmpty() && signingPassword.isNotEmpty()) {
        extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(extensions.getByType<PublishingExtension>().publications)
        }
    }
}

fun Project.configureGdxJavaPublishArtifacts() {
    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }
    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }
}

fun Project.configureGdxPomMetadata(nameValue: String, descriptionValue: String) {
    extensions.configure<PublishingExtension> {
        publications.withType(MavenPublication::class.java).configureEach {
            pom.configureGdxPom(nameValue, descriptionValue)
        }
    }
}

fun Project.configureGradlePluginPublishing() {
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    group = LibExt.groupId
    version = LibExt.libVersion

    configureGdxJavaPublishArtifacts()
    configureGdxMavenRepository(providers.gradleProperty("gdxTeaVM.deployDir").orNull)
    configureGdxPomMetadata(
        "gdx-teavm Gradle plugin",
        "Gradle plugin for building libGDX TeaVM web and native targets"
    )
    configureGdxSigning()

    tasks.register("prepareSnapshotDeploy") {
        group = "publishing"
        description = "Publish the Gradle plugin snapshot marker and implementation artifacts to a local repository."
        dependsOn(tasks.withType(PublishToMavenRepository::class.java))
        onlyIf { LibExt.libVersion.endsWith("-SNAPSHOT") }
    }

    tasks.register("prepareReleaseDeploy") {
        group = "publishing"
        description = "Publish the Gradle plugin release marker and implementation artifacts to a local repository."
        dependsOn(tasks.withType(PublishToMavenRepository::class.java))
        onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }
    }
}

fun Project.configureLibraryPublishing() {
    val libProjects = mutableSetOf(
        project(":backends:backend-shared"),
        project(":backends:backend-web"),
        project(":backends:backend-glfw"),
        project(":backends:backend-psp"),
        project(":extensions:gdx-controllers-teavm"),
        project(":extensions:gdx-freetype-teavm"),
        project(":extensions:asset-loader")
    )

    configure(libProjects) {
        apply(plugin = "signing")
        apply(plugin = "maven-publish")

        if(LibExt.libVersion.isEmpty()) {
            throw RuntimeException("Version cannot be empty")
        }

        configureGdxMavenRepository()
        configureGdxPomMetadata(
            LibExt.libName,
            "Tool to generate libgdx to javascript using teaVM"
        )
        configureGdxJavaPublishArtifacts()
        configureGdxSigning()
    }

    val gradlePluginBuildDir = rootProject.layout.projectDirectory.dir("tools/gdx-teavm-plugin").asFile

    tasks.register<GradleBuild>("prepareGradlePluginSnapshotDeploy") {
        group = "publishing"
        description = "Prepare local snapshot deploy files for the gdx-teavm Gradle plugin."
        dir = gradlePluginBuildDir
        tasks = listOf("prepareSnapshotDeploy")
        startParameter.projectProperties["gdxTeaVM.deployDir"] =
            rootProject.layout.buildDirectory.dir("snapshot-deploy").get().asFile.absolutePath
    }

    tasks.register<GradleBuild>("prepareGradlePluginReleaseDeploy") {
        group = "publishing"
        description = "Prepare local release deploy files for the gdx-teavm Gradle plugin."
        dir = gradlePluginBuildDir
        tasks = listOf("prepareReleaseDeploy")
        startParameter.projectProperties["gdxTeaVM.release"] = "true"
        startParameter.projectProperties["gdxTeaVM.deployDir"] =
            rootProject.layout.buildDirectory.dir("staging-deploy").get().asFile.absolutePath
    }

    tasks.register<Zip>("zipStagingDeploy") {
        dependsOn(libProjects.map { it.tasks.named("publish") })
        dependsOn("prepareGradlePluginReleaseDeploy")
        from(rootProject.layout.buildDirectory.dir("staging-deploy"))
        archiveFileName.set("staging-deploy.zip")
        destinationDirectory.set(rootProject.layout.buildDirectory)
        onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }
    }

    tasks.register("uploadToMavenCentral") {
        dependsOn("zipStagingDeploy")
        onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }
        doLast {
            val stagingDir = rootProject.layout.buildDirectory.dir("staging-deploy").get().asFile
            val zipFile = rootProject.layout.buildDirectory.file("staging-deploy.zip").get().asFile

            if(!stagingDir.exists()) {
                throw GradleException("Staging directory $stagingDir does not exist. Ensure the publish task ran successfully.")
            }

            if(!zipFile.exists()) {
                throw GradleException("Zip file ${zipFile.absolutePath} was not created. Check the zip command output.")
            }

            if(!Files.isReadable(Paths.get(zipFile.absolutePath))) {
                throw GradleException("Zip file ${zipFile.absolutePath} is not readable. Check file permissions.")
            }

            val username = System.getenv("CENTRAL_PORTAL_USERNAME")
                ?: throw GradleException("CENTRAL_PORTAL_USERNAME environment variable not set")
            val password = System.getenv("CENTRAL_PORTAL_PASSWORD")
                ?: throw GradleException("CENTRAL_PORTAL_PASSWORD environment variable not set")

            val rawBundleName = "${LibExt.libName}-${LibExt.libVersion}"
            val encodedBundleName = URLEncoder.encode(rawBundleName, "UTF-8")

            providers.exec {
                commandLine = listOf(
                    "curl",
                    "-u",
                    "$username:$password",
                    "--request",
                    "POST",
                    "--form",
                    "bundle=@${zipFile.absolutePath}",
                    "https://central.sonatype.com/api/v1/publisher/upload?name=${encodedBundleName}"
                )
            }.result.get()
        }
    }

    tasks.register("publishRelease") {
        group = "publishing"
        dependsOn("prepareReleaseDeploy")
        finalizedBy("uploadToMavenCentral")
    }

    tasks.register("publishSnapshot") {
        group = "publishing"
        dependsOn(libProjects.map { it.tasks.withType(PublishToMavenRepository::class.java) })
        dependsOn("publishGradlePluginSnapshot")
    }

    tasks.register("prepareReleaseDeploy") {
        group = "publishing"
        dependsOn("zipStagingDeploy")
        onlyIf { !LibExt.libVersion.endsWith("-SNAPSHOT") }
    }

    tasks.register("prepareSnapshotDeploy") {
        group = "publishing"
        dependsOn(libProjects.map { it.tasks.withType(PublishToMavenRepository::class.java) })
        dependsOn("prepareGradlePluginSnapshotDeploy")
        onlyIf { LibExt.libVersion.endsWith("-SNAPSHOT") }
    }

    tasks.register<GradleBuild>("publishGradlePluginSnapshot") {
        group = "publishing"
        description = "Publish the gdx-teavm Gradle plugin snapshot marker and implementation artifacts."
        dir = gradlePluginBuildDir
        tasks = listOf("publish")
    }
}

val publishTargetProperty = "gdxTeaVMPublishTarget"
val publishTarget = if(extensions.extraProperties.has(publishTargetProperty)) {
    extensions.extraProperties.get(publishTargetProperty) as? GdxTeaVMPublishTarget
        ?: throw GradleException("$publishTargetProperty must be a GdxTeaVMPublishTarget")
}
else {
    throw GradleException("$publishTargetProperty must be configured before applying publish.gradle.kts")
}

when(publishTarget) {
    GdxTeaVMPublishTarget.LIBRARIES -> configureLibraryPublishing()
    GdxTeaVMPublishTarget.GRADLE_PLUGIN -> configureGradlePluginPublishing()
}
