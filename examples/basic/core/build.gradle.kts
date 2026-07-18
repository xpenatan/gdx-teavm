dependencies {
    implementation(project(":examples:shared"))

    implementation("io.github.quillraven.fleks:Fleks:2.12")
    implementation("com.kotcrab.vis:vis-ui:1.5.8")

    implementation("com.badlogicgames.gdx:gdx:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-box2d:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-freetype:${LibExt.gdxVersion}")
    implementation("com.badlogicgames.gdx:gdx-ai:${LibExt.aiVersion}")

    implementation("com.github.mgsx-dev.gdx-gltf:gltf:${LibExt.gdxGltfVersion}")

    implementation("net.onedaybeard.artemis:artemis-odb:2.3.0")
}
