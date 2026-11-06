import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":backends:backend-web"))
}

gdxTeaVM {
    js {
        mainClass.set(providers.gradleProperty("webWindowEntry").getOrElse("repro.WebWindowFrameContract"))
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        sourceMap.set(true)
    }
}
