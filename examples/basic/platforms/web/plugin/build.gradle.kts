import org.teavm.gradle.api.OptimizationLevel
import org.teavm.gradle.api.SourceFilePolicy

plugins {
    id("com.github.xpenatan.gdx-teavm")
}

dependencies {
    implementation(libs.gdxCore)
    implementation(project(":examples:basic:core"))
    implementation(project(":extensions:web:gdx-freetype-web"))
}

gdxTeaVM {
    assets.from(file("../../../assets"))

    webDefaults {
        preservedClasses.add("com.badlogic.gdx.math.Vector2")
        // Keep glTF model types used by JSON deserialization; BasicReflectionPolicy grants member access.
        preservedClasses.addAll(
            "net.mgsx.gltf.data.animation.GLTFAnimation",
            "net.mgsx.gltf.data.animation.GLTFAnimationChannel",
            "net.mgsx.gltf.data.animation.GLTFAnimationSampler",
            "net.mgsx.gltf.data.animation.GLTFAnimationTarget",
            "net.mgsx.gltf.data.camera.GLTFCamera",
            "net.mgsx.gltf.data.camera.GLTFOrthographic",
            "net.mgsx.gltf.data.camera.GLTFPerspective",
            "net.mgsx.gltf.data.data.GLTFAccessor",
            "net.mgsx.gltf.data.data.GLTFAccessorSparse",
            "net.mgsx.gltf.data.data.GLTFAccessorSparseIndices",
            "net.mgsx.gltf.data.data.GLTFAccessorSparseValues",
            "net.mgsx.gltf.data.data.GLTFBuffer",
            "net.mgsx.gltf.data.data.GLTFBufferView",
            "net.mgsx.gltf.data.extensions.KHRLightsPunctual",
            "net.mgsx.gltf.data.extensions.KHRLightsPunctual\$GLTFLight",
            "net.mgsx.gltf.data.extensions.KHRLightsPunctual\$GLTFLightNode",
            "net.mgsx.gltf.data.extensions.KHRLightsPunctual\$GLTFLights",
            "net.mgsx.gltf.data.extensions.KHRLightsPunctual\$GLTFSpotLight",
            "net.mgsx.gltf.data.extensions.KHRMaterialsEmissiveStrength",
            "net.mgsx.gltf.data.extensions.KHRMaterialsIOR",
            "net.mgsx.gltf.data.extensions.KHRMaterialsIridescence",
            "net.mgsx.gltf.data.extensions.KHRMaterialsPBRSpecularGlossiness",
            "net.mgsx.gltf.data.extensions.KHRMaterialsSpecular",
            "net.mgsx.gltf.data.extensions.KHRMaterialsTransmission",
            "net.mgsx.gltf.data.extensions.KHRMaterialsUnlit",
            "net.mgsx.gltf.data.extensions.KHRMaterialsVolume",
            "net.mgsx.gltf.data.extensions.KHRTextureTransform",
            "net.mgsx.gltf.data.geometry.GLTFMesh",
            "net.mgsx.gltf.data.geometry.GLTFMorphTarget",
            "net.mgsx.gltf.data.geometry.GLTFPrimitive",
            "net.mgsx.gltf.data.GLTF",
            "net.mgsx.gltf.data.GLTFAsset",
            "net.mgsx.gltf.data.GLTFEntity",
            "net.mgsx.gltf.data.GLTFExtensions",
            "net.mgsx.gltf.data.GLTFExtras",
            "net.mgsx.gltf.data.GLTFObject",
            "net.mgsx.gltf.data.material.GLTFMaterial",
            "net.mgsx.gltf.data.material.GLTFpbrMetallicRoughness",
            "net.mgsx.gltf.data.scene.GLTFNode",
            "net.mgsx.gltf.data.scene.GLTFScene",
            "net.mgsx.gltf.data.scene.GLTFSkin",
            "net.mgsx.gltf.data.texture.GLTFImage",
            "net.mgsx.gltf.data.texture.GLTFNormalTextureInfo",
            "net.mgsx.gltf.data.texture.GLTFOcclusionTextureInfo",
            "net.mgsx.gltf.data.texture.GLTFSampler",
            "net.mgsx.gltf.data.texture.GLTFTexture",
            "net.mgsx.gltf.data.texture.GLTFTextureInfo"
        )
        mainClass = "TestWebLauncher"
        optimization = OptimizationLevel.NONE
        obfuscated = false
    }

    js("devServer") {
        debugInformation = true
        sourceMap = true
        sourceFilePolicy = SourceFilePolicy.COPY
        devServer {
            enabled = true
            autoReload = true
        }
    }
    wasm("devServer") {
        devServer {
            enabled = true
            autoReload = true
        }
    }

    js("release") {
        optimization = OptimizationLevel.BALANCED
        obfuscated = true
        serverPort = 8181
    }

    wasm("release") {
        optimization = OptimizationLevel.BALANCED
        obfuscated = true
        serverPort = 8282
    }
}
