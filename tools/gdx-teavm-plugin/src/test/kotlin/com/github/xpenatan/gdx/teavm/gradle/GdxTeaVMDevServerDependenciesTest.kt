package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.file.FileCollection
import org.gradle.api.attributes.Usage
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.jar.JarOutputStream

class GdxTeaVMDevServerDependenciesTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun `empty runner POM gains runtime dependencies lazily without a dependency list`() {
        val repository = temporaryFolder.newFolder("repository")
        val project = project(repository)
        val original = rawRunner(project)
        val classpath = devServerRunnerClasspath(project, original)
        // No files exist at configuration time, and no dependencies.txt is ever published.
        module(repository, "org.teavm", "teavm-devserver-runner", "0.16.0")
        module(repository, "org.teavm", "teavm-tooling", "0.16.0")
        module(repository, "org.teavm", "teavm-devserver", "0.16.0", dependency("transitive"))
        module(repository, "commons-cli", "commons-cli", "1.11.0")
        module(repository, "org.slf4j", "slf4j-api", "2.0.16")
        module(repository, "fixture", "transitive", "1.0")

        assertEquals(
            setOf("teavm-devserver-runner-0.16.0.jar", "teavm-tooling-0.16.0.jar",
                "teavm-devserver-0.16.0.jar", "commons-cli-1.11.0.jar", "slf4j-api-2.0.16.jar", "transitive-1.0.jar"),
            classpath.files.map { it.name }.toSet()
        )
        assertEquals(1, project.repositories.size)
        assertEquals(setOf("teavm-devserver-runner-0.16.0.jar"), original.files.map { it.name }.toSet())
    }

    @Test
    fun `complete runner POM keeps its dependency graph`() {
        val repository = temporaryFolder.newFolder("repository")
        module(repository, "org.teavm", "teavm-devserver-runner", "0.16.0", dependency("server-runtime"))
        module(repository, "fixture", "server-runtime", "1.0")
        assertEquals(
            setOf("teavm-devserver-runner-0.16.0.jar", "server-runtime-1.0.jar"),
            runner(project(repository)).files.map { it.name }.toSet()
        )
    }

    @Test
    fun `empty POMs for other versions and modules are untouched`() {
        val repository = temporaryFolder.newFolder("repository")
        module(repository, "org.teavm", "teavm-devserver-runner", "0.15.0")
        module(repository, "fixture", "unrelated", "0.16.0")
        val project = project(repository)
        assertEquals(setOf("teavm-devserver-runner-0.15.0.jar"),
            runner(project, "org.teavm:teavm-devserver-runner:0.15.0").files.map { it.name }.toSet())
        assertEquals(setOf("unrelated-0.16.0.jar"),
            runner(project, "fixture:unrelated:0.16.0").files.map { it.name }.toSet())
    }

    @Test
    fun `healthy Gradle runtime metadata is used unchanged`() {
        val repository = temporaryFolder.newFolder("repository")
        val directory = module(repository, "org.teavm", "teavm-devserver-runner", "0.16.0")
        module(repository, "fixture", "server-runtime", "1.0")
        directory.resolve("teavm-devserver-runner-0.16.0.module").writeText("""
            {"formatVersion":"1.1","component":{"group":"org.teavm","module":"teavm-devserver-runner","version":"0.16.0"},
             "variants":[
               {"name":"apiElements","attributes":{"org.gradle.usage":"java-api"},
                "files":[{"name":"teavm-devserver-runner-0.16.0.jar","url":"teavm-devserver-runner-0.16.0.jar"}]},
               {"name":"runtimeElements","attributes":{"org.gradle.usage":"java-runtime"},
                "dependencies":[{"group":"fixture","module":"server-runtime","version":{"requires":"1.0"}}],
                "files":[{"name":"teavm-devserver-runner-0.16.0.jar","url":"teavm-devserver-runner-0.16.0.jar"}]}]}
        """.trimIndent())
        val project = project(repository)
        project.repositories.withType(org.gradle.api.artifacts.repositories.MavenArtifactRepository::class.java).configureEach {
            metadataSources { gradleMetadata(); mavenPom() }
        }
        val runtime = rawRunner(project)
        runtime.attributes.attribute(Usage.USAGE_ATTRIBUTE, project.objects.named(Usage::class.java, Usage.JAVA_RUNTIME))
        assertEquals(setOf("teavm-devserver-runner-0.16.0.jar", "server-runtime-1.0.jar"),
            devServerRunnerClasspath(project, runtime).files.map { it.name }.toSet())
    }

    @Test
    fun `substituted source project is untouched`() {
        val project = project(temporaryFolder.newFolder("repository"))
        val source = ProjectBuilder.builder().withName("localRunner").withParent(project).build()
        val sourceJar = temporaryFolder.newFile("local-runner.jar")
        JarOutputStream(sourceJar.outputStream()).close()
        source.configurations.create("default").outgoing.artifact(sourceJar)
        val classpath = rawRunner(project)
        classpath.resolutionStrategy.dependencySubstitution {
            substitute(module("org.teavm:teavm-devserver-runner")).using(project(":localRunner"))
        }
        assertEquals(setOf(sourceJar), devServerRunnerClasspath(project, classpath).files)
    }

    @Test
    fun `default runner and runtime follow the shared TeaVM plugin version`() {
        val repository = temporaryFolder.newFolder("repository")
        val project = project(repository)
        val coordinates = org.teavm.gradle.config.ArtifactCoordinates.DEV_SERVER_RUNNER
        val version = project.dependencies.create(coordinates).version!!
        module(repository, "org.teavm", "teavm-devserver-runner", version,
            "<dependencies><dependency><groupId>org.teavm</groupId><artifactId>teavm-tooling</artifactId>" +
                "<version>$version</version><scope>runtime</scope></dependency>" +
                "<dependency><groupId>org.teavm</groupId><artifactId>teavm-devserver</artifactId>" +
                "<version>$version</version><scope>runtime</scope></dependency></dependencies>")
        module(repository, "org.teavm", "teavm-tooling", version)
        module(repository, "org.teavm", "teavm-devserver", version)
        assertEquals(setOf("teavm-devserver-runner-$version.jar", "teavm-tooling-$version.jar", "teavm-devserver-$version.jar"),
            devServerRunnerClasspath(project).files.map { it.name }.toSet())
        println("SHARED_TEAVM_VERSION=$version")
    }

    @Test
    fun `repair follows a Gradle runner version substitution`() {
        val repository = temporaryFolder.newFolder("repository")
        module(repository, "org.teavm", "teavm-devserver-runner", "0.16.0")
        module(repository, "org.teavm", "teavm-tooling", "0.16.0")
        module(repository, "org.teavm", "teavm-devserver", "0.16.0")
        module(repository, "commons-cli", "commons-cli", "1.11.0")
        module(repository, "org.slf4j", "slf4j-api", "2.0.16")
        val project = project(repository)
        val original = rawRunner(project, "org.teavm:teavm-devserver-runner:0.15.0")
        original.resolutionStrategy.dependencySubstitution {
            substitute(module("org.teavm:teavm-devserver-runner:0.15.0"))
                .using(module("org.teavm:teavm-devserver-runner:0.16.0"))
        }
        assertEquals(setOf("teavm-devserver-runner-0.16.0.jar", "teavm-tooling-0.16.0.jar",
            "teavm-devserver-0.16.0.jar", "commons-cli-1.11.0.jar", "slf4j-api-2.0.16.jar"),
            devServerRunnerClasspath(project, original).files.map { it.name }.toSet())
    }
    private fun project(repository: File): Project {
        val project = ProjectBuilder.builder().withProjectDir(temporaryFolder.newFolder("consumer")).build()
        project.repositories.maven { url = repository.toURI() }
        return project
    }

    private fun runner(project: Project, coordinates: String = "org.teavm:teavm-devserver-runner:0.16.0"): FileCollection =
        devServerRunnerClasspath(project, rawRunner(project, coordinates))

    private fun rawRunner(project: Project, coordinates: String = "org.teavm:teavm-devserver-runner:0.16.0"): Configuration =
        project.configurations.detachedConfiguration(project.dependencies.create(coordinates))

    private fun dependency(name: String): String =
        "<dependencies><dependency><groupId>fixture</groupId><artifactId>$name</artifactId>" +
            "<version>1.0</version><scope>runtime</scope></dependency></dependencies>"

    private fun module(repository: File, group: String, name: String, version: String, dependencies: String = ""): File {
        val directory = repository.resolve("${group.replace('.', '/')}/$name/$version")
        directory.mkdirs()
        directory.resolve("$name-$version.pom").writeText(
            "<project><modelVersion>4.0.0</modelVersion><groupId>$group</groupId>" +
                "<artifactId>$name</artifactId><version>$version</version>$dependencies</project>"
        )
        JarOutputStream(directory.resolve("$name-$version.jar").outputStream()).close()
        return directory
    }
}
