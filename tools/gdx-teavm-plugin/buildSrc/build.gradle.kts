plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

kotlin {
    sourceSets.named("main") {
        kotlin.srcDir("../../../buildSrc/src/main/kotlin")
        kotlin.include("LibExt.kt")
    }
}
