package com.github.xpenatan.gdx.teavm.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.file.FileCollection
import org.teavm.gradle.config.ArtifactCoordinates

internal fun devServerRunnerClasspath(
    project: Project,
    runner: Configuration = project.configurations.detachedConfiguration(
        project.dependencies.create(ArtifactCoordinates.DEV_SERVER_RUNNER)
    )
): FileCollection {
    val classpath by lazy {
        val components = runner.incoming.resolutionResult.allComponents
        val runnerId = components.map { it.id }.filterIsInstance<ModuleComponentIdentifier>().singleOrNull {
            it.group == "org.teavm" && it.module == "teavm-devserver-runner"
        }
        // This literal identifies the broken publication, not the configured TeaVM version.
        // Healthy graphs, other releases and source-project substitutions need no repair.
        if(components.size == 2 && runnerId?.version == "0.16.0") {
            val teavmVersion = runnerId.version
            // Follow the globally selected runner version, including Gradle overrides.
            val runtime = project.configurations.detachedConfiguration(
                project.dependencies.create("org.teavm:teavm-tooling:$teavmVersion"),
                project.dependencies.create("org.teavm:teavm-devserver:$teavmVersion"),
                // Independent versions from the affected runner's published runtime metadata.
                project.dependencies.create("commons-cli:commons-cli:1.11.0"),
                project.dependencies.create("org.slf4j:slf4j-api:2.0.16")
            )
            project.files(runner, runtime)
        }
        else {
            runner
        }
    }
    return project.files(project.provider { classpath }).builtBy(runner)
}
