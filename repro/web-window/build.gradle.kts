import org.teavm.gradle.api.OptimizationLevel

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(project(":backends:backend-web"))
}

gdxTeaVM {
    js {
        mainClass.set("repro.WebWindowFrameContract")
        optimization.set(OptimizationLevel.NONE)
        obfuscated.set(false)
        sourceMap.set(true)
    }
}
