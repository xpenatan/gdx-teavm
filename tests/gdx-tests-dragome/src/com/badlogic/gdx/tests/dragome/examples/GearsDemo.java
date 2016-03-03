package com.badlogic.gdx.tests.dragome.examples;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.backends.dragome.DragomeApplication;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader;
import com.badlogic.gdx.backends.dragome.preloader.AssetDownloader.AssetLoaderListener;
import com.badlogic.gdx.backends.dragome.preloader.AssetFilter.AssetType;
import com.badlogic.gdx.backends.dragome.preloader.Preloader;
import com.badlogic.gdx.backends.dragome.typedarrays.shared.Float32Array;
import com.badlogic.gdx.backends.dragome.typedarrays.shared.Int32Array;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.dragome.commons.compiler.annotations.MethodAlias;
import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsDelegateGenerator;
import com.dragome.web.html.dom.html5canvas.interfaces.ImageElement;

//TODO In progress
public class GearsDemo implements ApplicationListener {

	public PerspectiveCamera cam;
	public CameraInputController inputController;
	public ModelBatch modelBatch;
	public Model model;
	public ModelInstance instance;
	public Environment environment;
	
	int loaded = 0;
	boolean start = false;
	SpriteBatch batch;
	
	Texture texture;
	@Override
	public void create () { 
		
		String path = null;
		DragomeApplication app = (DragomeApplication)Gdx.app;
		Preloader preloader = app.getPreloader();
		
		path = "com/badlogic/gdx/graphics/g3d/shaders/default.fragment.glsl";
		System.out.println("path: " + path);
		preloader.loadAsset(path, AssetType.Text, null, new AssetLoaderListener<Object>() {
			
			@Override
			public void onSuccess(Object result) {
				loaded++;
			}
			
			@Override
			public void onProgress(double amount) {
			}
			
			@Override
			public void onFailure() {
			}
		});
		
		path = "com/badlogic/gdx/graphics/g3d/shaders/default.vertex.glsl";
		System.out.println("path: " + path);
		preloader.loadAsset(path, AssetType.Text, null, new AssetLoaderListener<Object>() {
			
			@Override
			public void onSuccess(Object result) {
				loaded++;
			}
			
			@Override
			public void onProgress(double amount) {
			}
			
			@Override
			public void onFailure() {
			}
		});
	
		batch = new SpriteBatch();
		
		
		
//		modelBatch = new ModelBatch(defaultShaderProvider);

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .4f, .4f, .4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(10f, 10f, 10f);
		cam.lookAt(0, 0, 0);
		cam.near = 1f;
		cam.far = 30f;
		cam.update();
		
		
		
		path = "awesome.png";
		System.out.println("path: " + path);
		preloader.loadAsset(path, AssetType.Image, null, new AssetLoaderListener<Object>() {
			
			@Override
			public void onSuccess(Object result) {
				FileHandle internal = Gdx.files.internal("awesome.png");
				texture = new Texture(internal);
			}
			
			
			@Override
			public void onProgress(double amount) {
			}
			
			@Override
			public void onFailure() {
			}
		});
		
		

//		Gdx.input.setInputProcessor(new InputMultiplexer(this, inputController = new CameraInputController(cam)));
	}

	@Override
	public void render () {
//		inputController.update();

		if(loaded == 2 && start == false)
		{
			start = true;
			DefaultShaderProvider defaultShaderProvider = new DefaultShaderProvider();
			modelBatch = new ModelBatch(defaultShaderProvider);
			
			ModelBuilder modelBuilder = new ModelBuilder();
			model = modelBuilder.createBox(5f, 5f, 5f, new Material(ColorAttribute.createDiffuse(Color.GREEN)), Usage.Position
				| Usage.Normal);
			instance = new ModelInstance(model);
		}
		
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

//		instance.transform.rotate(Vector3.X, 1);
//		
//		
		if(modelBatch != null)
		{
			System.out.println("aaaa");
			modelBatch.begin(cam);
			modelBatch.render(instance, environment);
			modelBatch.end();
		}
		
		if(texture != null)
		{
//			batch.setProjectionMatrix(cam.combined);
			batch.begin();
			batch.draw(texture, 0, 0);
			batch.end();
		}
	}

	@Override
	public void dispose () {
		if(modelBatch != null)
			modelBatch.dispose();
		if(model != null)
			model.dispose();
	}

	public boolean needsGL20 () {
		return true;
	}

	public void resume () {
	}

	public void resize (int width, int height) {
	}

	public void pause () {
	}
}
