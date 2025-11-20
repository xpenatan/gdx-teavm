import java.nio.file.Files
import java.nio.file.Paths
import java.net.URLEncoder

var libProjects = mutableSetOf(
    project(":backends:backend-teavm"),
    project(":extensions:gdx-freetype-teavm"),
    project(":extensions:asset-loader")
)

LibExt.isRelease = gradle.startParameter.taskNames.any { it == "publishRelease" }

configure(libProjects) {
    apply(plugin = "signing")
    apply(plugin = "maven-publish")

    if(LibExt.libVersion.isEmpty()) {
        throw RuntimeException("Version cannot be empty")
    }

    extensions.configure<PublishingExtension> {
        repositories {
            maven {
                val isSnapshot = LibExt.libVersion.endsWith("-SNAPSHOT")
                url = if (isSnapshot) {
                    uri("https://central.sonatype.com/repository/maven-snapshots/")
                } else {
                    uri(rootProject.layout.buildDirectory.dir("staging-deploy"))
                }
                if(isSnapshot) {
                    val user = System.getenv("CENTRAL_PORTAL_USERNAME")
                    val pass = System.getenv("CENTRAL_PORTAL_PASSWORD")
                    credentials {
                        username = user
                        password = pass
                    }
                }
            }
        }
        publications.configureEach {
            if (this is MavenPublication) {
                pom {
                    name.set(LibExt.libName)
                    description.set("Tool to generate libgdx to javascript using teaVM")
                    url.set("https://github.com/xpenatan/gdx-teavm")
                    developers {
                        developer {
                            id.set("Xpe")
                            name.set("Natan")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://https://github.com/xpenatan/gdx-teavm.git")
                        developerConnection.set("scm:git:ssh://https://github.com/xpenatan/gdx-teavm.git")
                        url.set("http://https://github.com/xpenatan/gdx-teavm/tree/master")
                    }
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }

    tasks.withType<Javadoc> {
        options.encoding = "UTF-8"
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none", "-quiet")
    }

    extensions.configure<org.gradle.api.plugins.JavaPluginExtension> {
        withJavadocJar()
        withSourcesJar()
    }

    val signingKey = System.getenv("SIGNING_KEY").orEmpty()
    val signingPassword = System.getenv("SIGNING_PASSWORD").orEmpty()
    if (signingKey.isNotEmpty() && signingPassword.isNotEmpty()) {
        extensions.configure<SigningExtension> {
            useInMemoryPgpKeys(signingKey, signingPassword)
            sign(extensions.getByType<PublishingExtension>().publications)
        }
    }
}

if(!LibExt.libVersion.endsWith("-SNAPSHOT")) {
    tasks.register<Zip>("zipStagingDeploy") {
        dependsOn(libProjects.map { it.tasks.named("publish") })
        from(rootProject.layout.buildDirectory.dir("staging-deploy"))
        archiveFileName.set("staging-deploy.zip")
        destinationDirectory.set(rootProject.layout.buildDirectory)
        onlyIf { !project.version.toString().endsWith("-SNAPSHOT") }
    }

    tasks.register("uploadToMavenCentral") {
        dependsOn("zipStagingDeploy")
        doLast {
            if (!project.version.toString().endsWith("-SNAPSHOT")) {
                // Define paths
                val stagingDir = rootProject.layout.buildDirectory.dir("staging-deploy").get().asFile
                val zipFile = rootProject.layout.buildDirectory.file("staging-deploy.zip").get().asFile

                if (!stagingDir.exists()) {
                    throw GradleException("Staging directory $stagingDir does not exist. Ensure the publish task ran successfully.")
                }

                if (!zipFile.exists()) {
                    throw GradleException("Zip file ${zipFile.absolutePath} was not created. Check the zip command output.")
                }

                if (!Files.isReadable(Paths.get(zipFile.absolutePath))) {
                    throw GradleException("Zip file ${zipFile.absolutePath} is not readable. Check file permissions.")
                }

                val username = System.getenv("CENTRAL_PORTAL_USERNAME") ?: throw GradleException("CENTRAL_PORTAL_USERNAME environment variable not set")
                val password = System.getenv("CENTRAL_PORTAL_PASSWORD") ?: throw GradleException("CENTRAL_PORTAL_PASSWORD environment variable not set")

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
    }

    libProjects.forEach { project ->
        project.tasks.withType<PublishToMavenRepository>().configureEach {
            finalizedBy(rootProject.tasks.named("uploadToMavenCentral"))
        }
    }
}

tasks.register("publishRelease") {
    group = "publishing"
    dependsOn(libProjects.map { it.tasks.withType<PublishToMavenRepository>() })
}

tasks.register("publishSnapshot") {
    group = "publishing"
    dependsOn(libProjects.map { it.tasks.withType<PublishToMavenRepository>() })
}
