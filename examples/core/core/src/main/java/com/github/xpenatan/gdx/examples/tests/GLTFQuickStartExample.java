package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.github.xpenatan.gdx.examples.profile.XGLProfiler;
import net.mgsx.gltf.loaders.glb.GLBAssetLoader;
import net.mgsx.gltf.loaders.glb.GLBLoader;
import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.loaders.shared.SceneAssetLoaderParameters;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.scene.SceneSkybox;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

public class GLTFQuickStartExample extends ApplicationAdapter {
    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private float time;
    private SceneSkybox skybox;
    private DirectionalLightEx light;

    private String MODEL_ASSET = "custom/models/BoomBox/glTF/BoomBox.gltf";

//    AssetManager assetManager;

//    XGLProfiler profiler;

    @Override
    public void create() {
        long millis = TimeUtils.millis();

//        profiler = new XGLProfiler(true);
//        profiler.enable();
//        assetManager = new AssetManager();
//        Texture.setAssetManager(assetManager);
//        assetManager.setLoader(SceneAsset.class, ".gltf", new GLTFAssetLoader());
//        assetManager.setLoader(SceneAsset.class, ".glb", new GLBAssetLoader());

        SceneAssetLoaderParameters params = new SceneAssetLoaderParameters();
        params.withData = true;
//        assetManager.load(MODEL_ASSET, SceneAsset.class, params);

        // create scene
        sceneManager = new SceneManager();

        // setup camera (The BoomBox model is very small so you may need to adapt camera settings for your scene)
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float d = .02f;
        camera.near = d / 1000f;
        camera.far = d * 4;
        sceneManager.setCamera(camera);

        // setup light
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        // setup quick IBL (image based lighting)
        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();

        // This texture is provided by the library, no need to have it in your assets.
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));

        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));

        // setup skybox
        skybox = new SceneSkybox(environmentCubemap);
        sceneManager.setSkyBox(skybox);


        sceneAsset = new GLTFLoader().load(Gdx.files.internal(MODEL_ASSET));
        scene = new Scene(sceneAsset.scene);
        sceneManager.addScene(scene);

        long newMillis = TimeUtils.timeSinceMillis(millis);

        System.out.println("Time to create Scene: " + newMillis);

//        String status = profiler.getStatus();
//        System.out.println("Profile Create Status: \n" + status);
//        profiler.reset();

    }

    @Override
    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
    }

    @Override
    public void render() {

        float deltaTime = Gdx.graphics.getDeltaTime();
        time += deltaTime;

//        assetManager.update();


        // animate camera
        camera.position.setFromSpherical(MathUtils.PI / 4, time * .3f).scl(.03f);
        camera.up.set(Vector3.Y);
        camera.lookAt(Vector3.Zero);
        camera.update();

        // render
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        sceneManager.update(deltaTime);
        sceneManager.render();

//        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            String status = profiler.getStatus();
//            System.out.println("Profile Render Status: \n" + status);
//        }
//        profiler.reset();
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        sceneAsset.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        skybox.dispose();
    }
}