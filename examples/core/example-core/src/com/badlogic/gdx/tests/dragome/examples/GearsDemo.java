package com.badlogic.gdx.tests.dragome.examples;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder.VertexInfo;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/** @author xpenatan */
public class GearsDemo implements ApplicationListener {

	public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model1;
	public Model model2;
	public Model model3;
	public ModelInstance gear1;
	public ModelInstance gear2;
	public ModelInstance gear3;
	public Environment environment;
	Renderable pLight;
	Model lightModel;
	PointLight sl;
//	PointLight sl2;
//	PointLight sl3;
	protected final Matrix4 transform = new Matrix4();
	Vector3 center = new Vector3(), transformedCenter = new Vector3();
	private float angle;

	BitmapFont font;
	Viewport viewport;
	ScreenViewport guiViewport;
	SpriteBatch batch;
	long time;
	int fps;

	CameraInputController cameraController;

	@Override
	public void create () {

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .2f, .2f, .2f, 2f));
//		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -0.5f, -0.5f));

		sl = new PointLight().setPosition(-5, 10, -6).setColor(1, 1,1, 1)
			.setIntensity(150);

//		sl2 = new PointLight().setPosition(0, 7, 5).setColor(0.3f, 0.8f, 0.3f, 1)
//			.setIntensity(20);
//
//		sl3 = new PointLight().setPosition(0, 9, 6).setColor(0.3f, 0.3f, 0.8f, 1)
//			.setIntensity(20);

		environment.add(sl);
//		environment.add(sl2);
//		environment.add(sl3);


		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(-10, 3, 10f);
		cam.lookAt(-3, 0, 0);
		cam.near = 1f;
		cam.far = 100f;
		cam.update();

		cameraController = new CameraInputController(cam);
		cameraController.autoUpdate = false;
		cameraController.forwardTarget = false;
		cameraController.translateTarget = false;

		Gdx.input.setInputProcessor(new InputMultiplexer(cameraController));

		time = TimeUtils.millis();

		viewport = new ScreenViewport(cam);
		guiViewport = new ScreenViewport();

		DefaultShaderProvider defaultShaderProvider = new DefaultShaderProvider(defaultVertex, defaultFragment);
		modelBatch = new ModelBatch(defaultShaderProvider);

		ModelBuilder modelBuilder = new ModelBuilder();
		model1 = gear(modelBuilder, 1.0f, 4.0f, 1.0f, 20, 0.7f, Color.RED);
		gear1 = new ModelInstance(model1);

		model2 = gear(modelBuilder, 0.5f, 2.0f, 2.0f, 10, 0.7f, Color.GREEN);
		gear2 = new ModelInstance(model2);

		model3 = gear(modelBuilder, 1.3f, 2.0f, 1.5f, 10, 0.7f, Color.BLUE);
		gear3 = new ModelInstance(model3);

//		font = new BitmapFont();

		batch = new SpriteBatch();

		lightModel = modelBuilder.createSphere(1, 1, 1, 10, 10, new Material(ColorAttribute.createDiffuse(1, 1, 1, 1)), Usage.Position);
		lightModel.nodes.get(0).parts.get(0).setRenderable(pLight = new Renderable());
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		cam.update();

		if(modelBatch != null)
		{
			angle += 1.0f;

			gear1.transform.setToTranslation(-3.0f, -2.0f, 0.0f);
			gear2.transform.setToTranslation(3.1f, -2.0f, 0.0f);
			gear3.transform.setToTranslation(-3.1f, 4.2f, 0.0f);
			gear1.transform.rotate(Vector3.Z, angle);
			gear2.transform.rotate(Vector3.Z, -2.0f * angle - 9.0f);
			gear3.transform.rotate(Vector3.Z, -2.0f * angle - 25.0f);

			final float delta = Gdx.graphics.getDeltaTime();

			sl.position.sub(transformedCenter);
			sl.position.rotate(Vector3.X, delta * 50f);
			sl.position.rotate(Vector3.Y, delta * 13f);
			sl.position.rotate(Vector3.Z, delta * 3f);
			sl.position.add(transformedCenter.set(center).mul(transform));



			modelBatch.begin(cam);
			modelBatch.render(gear1, environment);
			modelBatch.render(gear2, environment);
			modelBatch.render(gear3, environment);
			pLight.worldTransform.setTranslation(sl.position);
			modelBatch.render(pLight);
			modelBatch.end();

			float timeSec = TimeUtils.millis() - time;

			if(timeSec > 1000)
			{
				time  = TimeUtils.millis();
				fps = Gdx.graphics.getFramesPerSecond();
			}
			batch.begin();
			if(font != null)
				font.draw(batch, "FPS: " + fps, 15, Gdx.graphics.getHeight() - 15);
			batch.end();
		}
	}

	@Override
	public void dispose () {
		if(modelBatch != null)
			modelBatch.dispose();
		if(model1 != null)
			model1.dispose();
		if(model2 != null)
			model2.dispose();
		if(model3 != null)
			model3.dispose();
	}

	public boolean needsGL20 () {
		return true;
	}

	public void resume () {
	}

	public void resize (int width, int height) {
		viewport.update(width, height, false);
		guiViewport.update(width, height, true);
		Camera guiCam = guiViewport.getCamera();
		guiCam.update();
		batch.setProjectionMatrix(guiCam.combined);
	}

	public void pause () {
	}

	static void gear_angle(int i, int teeth, float [] ar) {
		float angle = i * 2.0f * MathUtils.PI / teeth;
		float da = 2.0f * MathUtils.PI / teeth / 4.0f;
		ar[0] = angle;
		ar[1] = angle + 1.0f * da;
		ar[2] = angle + 2.0f * da;
		ar[3] = angle + 3.0f * da;
	}

	private static Model gear(ModelBuilder builder, float inner_radius, float outer_radius, float width, int teeth, float tooth_depth, Color color) {
		// Ported from https://github.com/jeffboody/gears2/blob/master/project/jni/gear.c by xpenatan
		int i;
		float r0, r1, r2, dz;
		float angle, da;
		float u, v, len;

		float [] ar = new float[4];

		VertexInfo vertTmp1 = new VertexInfo();

		r0 = inner_radius;
		r1 = outer_radius - tooth_depth / 2.0f;
		r2 = outer_radius + tooth_depth / 2.0f;

		dz = 0.5f * width;

		builder.begin();

		MeshPartBuilder part = null;

		// draw front face
		// GL_TRIANGLE_STRIP
		part = builder.part("gear", GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		da = 2.0f * (float)Math.PI / teeth / 4.0f;
		for (i = 0; i < teeth; i++) {
			angle = i * 2.0f * (float)Math.PI / teeth;
			gear_angle(i, teeth, ar);
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), dz)));
		}
		part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(0.0f), r0 * (float)Math.sin(0.0f), dz)));
		part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(0.0f), r1 * (float)Math.sin(0.0f), dz)));

		// draw front sides of teeth
		// GL_TRIANGLES
		part = builder.part("gear", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		for(i = 0; i < teeth; i++)
		{
			gear_angle(i, teeth, ar);
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), dz)));

			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), dz)));
		}

		// draw back face
		// GL_TRIANGLE_STRIP
		part = builder.part("gear", GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		for(i = 0; i < teeth; i++) {
			gear_angle(i, teeth, ar);

			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), -dz)));
		}
		part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(0.0f), r1 * (float)Math.sin(0.0f), -dz)));
		part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(0.0f), r0 * (float)Math.sin(0.0f), -dz)));

		// draw back sides of teeth
		// GL_TRIANGLES
		part = builder.part("gear", GL20.GL_TRIANGLES, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		for(i = 0; i < teeth; i++)
		{
			gear_angle(i, teeth, ar);
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), -dz)));

			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), -dz)));

		}

		// draw outward faces of teeth
		// GL_TRIANGLE_STRIP
		// repeated vertices are necessary to achieve flat shading in ES2
		part = builder.part("gear", GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		for(i = 0; i < teeth; i++)
		{
			gear_angle(i, teeth, ar);
			vertTmp1.hasNormal = false;
			if(i > 0)
			{
				part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), dz)));
				part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), -dz)));
			}

			u = r2 * (float)Math.cos(ar[1]) - r1 * (float)Math.cos(ar[0]);
			v = r2 * (float)Math.sin(ar[1]) - r1 * (float)Math.sin(ar[0]);
			len = (float)Math.sqrt(u * u + v * v);
			u /= len;
			v /= len;

			vertTmp1.setNor(v, -u, 0.0f);
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), -dz)));

			vertTmp1.setNor((float)Math.cos(ar[0]), (float)Math.sin(ar[0]), 0.0f);
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[1]), r2 * (float)Math.sin(ar[1]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), -dz)));

			u = r1 * (float)Math.cos(ar[3]) - r2 * (float)Math.cos(ar[2]);
			v = r1 * (float)Math.sin(ar[3]) - r2 * (float)Math.sin(ar[2]);

			vertTmp1.setNor(v, -u, 0.0f);
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r2 * (float)Math.cos(ar[2]), r2 * (float)Math.sin(ar[2]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), -dz)));

			vertTmp1.setNor((float)Math.cos(ar[0]), (float)Math.sin(ar[0]), 0.0f);
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[3]), r1 * (float)Math.sin(ar[3]), -dz)));
		}
		vertTmp1.hasNormal = false;
		part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(0.0f), r1 * (float)Math.sin(0.0f), dz)));
		part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(0.0f), r1 * (float)Math.sin(0.0f), -dz)));



		// draw inside radius cylinder
		// GL_TRIANGLE_STRIP
		part = builder.part("gear", GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		for(i = 0; i < teeth; i++)
		{
			gear_angle(i, teeth, ar);
			vertTmp1.setNor(-(float)Math.cos(ar[0]), -(float)Math.sin(ar[0]), 0.0f);
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), dz)));
		}
		vertTmp1.setNor(-(float)Math.cos(0.0f), -(float)Math.sin(0.0f), 0.0f);
		part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(0.0f), r0 * (float)Math.sin(0.0f), -dz)));
		part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(0.0f), r0 * (float)Math.sin(0.0f), dz)));

		return builder.end();
	}

	public static String defaultFragment = "#ifdef GL_ES\r\n" +
		"#define LOWP lowp\r\n" +
		"#define MED mediump\r\n" +
		"#define HIGH highp\r\n" +
		"precision mediump float;\r\n" +
		"#else\r\n" +
		"#define MED\r\n" +
		"#define LOWP\r\n" +
		"#define HIGH\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#if defined(specularTextureFlag) || defined(specularColorFlag)\r\n" +
		"#define specularFlag\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef normalFlag\r\n" +
		"varying vec3 v_normal;\r\n" +
		"#endif //normalFlag\r\n" +
		"\r\n" +
		"#if defined(colorFlag)\r\n" +
		"varying vec4 v_color;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef blendedFlag\r\n" +
		"varying float v_opacity;\r\n" +
		"#ifdef alphaTestFlag\r\n" +
		"varying float v_alphaTest;\r\n" +
		"#endif //alphaTestFlag\r\n" +
		"#endif //blendedFlag\r\n" +
		"\r\n" +
		"#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)\r\n" +
		"#define textureFlag\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef diffuseTextureFlag\r\n" +
		"varying MED vec2 v_diffuseUV;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef specularTextureFlag\r\n" +
		"varying MED vec2 v_specularUV;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef emissiveTextureFlag\r\n" +
		"varying MED vec2 v_emissiveUV;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef diffuseColorFlag\r\n" +
		"uniform vec4 u_diffuseColor;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef diffuseTextureFlag\r\n" +
		"uniform sampler2D u_diffuseTexture;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef specularColorFlag\r\n" +
		"uniform vec4 u_specularColor;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef specularTextureFlag\r\n" +
		"uniform sampler2D u_specularTexture;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef normalTextureFlag\r\n" +
		"uniform sampler2D u_normalTexture;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef emissiveColorFlag\r\n" +
		"uniform vec4 u_emissiveColor;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef emissiveTextureFlag\r\n" +
		"uniform sampler2D u_emissiveTexture;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef lightingFlag\r\n" +
		"varying vec3 v_lightDiffuse;\r\n" +
		"\r\n" +
		"#if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)\r\n" +
		"#define ambientFlag\r\n" +
		"#endif //ambientFlag\r\n" +
		"\r\n" +
		"#ifdef specularFlag\r\n" +
		"varying vec3 v_lightSpecular;\r\n" +
		"#endif //specularFlag\r\n" +
		"\r\n" +
		"#ifdef shadowMapFlag\r\n" +
		"uniform sampler2D u_shadowTexture;\r\n" +
		"uniform float u_shadowPCFOffset;\r\n" +
		"varying vec3 v_shadowMapUv;\r\n" +
		"#define separateAmbientFlag\r\n" +
		"\r\n" +
		"float getShadowness(vec2 offset)\r\n" +
		"{\r\n" +
		"    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 16581375.0);\r\n" +
		"    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));\r\n" +
		"}\r\n" +
		"\r\n" +
		"float getShadow()\r\n" +
		"{\r\n" +
		"	return (//getShadowness(vec2(0,0)) +\r\n" +
		"			getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +\r\n" +
		"			getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +\r\n" +
		"			getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +\r\n" +
		"			getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;\r\n" +
		"}\r\n" +
		"#endif //shadowMapFlag\r\n" +
		"\r\n" +
		"#if defined(ambientFlag) && defined(separateAmbientFlag)\r\n" +
		"varying vec3 v_ambientLight;\r\n" +
		"#endif //separateAmbientFlag\r\n" +
		"\r\n" +
		"#endif //lightingFlag\r\n" +
		"\r\n" +
		"#ifdef fogFlag\r\n" +
		"uniform vec4 u_fogColor;\r\n" +
		"varying float v_fog;\r\n" +
		"#endif // fogFlag\r\n" +
		"\r\n" +
		"void main() {\r\n" +
		"	#if defined(normalFlag)\r\n" +
		"		vec3 normal = v_normal;\r\n" +
		"	#endif // normalFlag\r\n" +
		"\r\n" +
		"	#if defined(diffuseTextureFlag) && defined(diffuseColorFlag) && defined(colorFlag)\r\n" +
		"		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor * v_color;\r\n" +
		"	#elif defined(diffuseTextureFlag) && defined(diffuseColorFlag)\r\n" +
		"		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * u_diffuseColor;\r\n" +
		"	#elif defined(diffuseTextureFlag) && defined(colorFlag)\r\n" +
		"		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV) * v_color;\r\n" +
		"	#elif defined(diffuseTextureFlag)\r\n" +
		"		vec4 diffuse = texture2D(u_diffuseTexture, v_diffuseUV);\r\n" +
		"	#elif defined(diffuseColorFlag) && defined(colorFlag)\r\n" +
		"		vec4 diffuse = u_diffuseColor * v_color;\r\n" +
		"	#elif defined(diffuseColorFlag)\r\n" +
		"		vec4 diffuse = u_diffuseColor;\r\n" +
		"	#elif defined(colorFlag)\r\n" +
		"		vec4 diffuse = v_color;\r\n" +
		"	#else\r\n" +
		"		vec4 diffuse = vec4(1.0);\r\n" +
		"	#endif\r\n" +
		"\r\n" +
		"	#if defined(emissiveTextureFlag) && defined(emissiveColorFlag)\r\n" +
		"		vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV) * u_emissiveColor;\r\n" +
		"	#elif defined(emissiveTextureFlag)\r\n" +
		"		vec4 emissive = texture2D(u_emissiveTexture, v_emissiveUV);\r\n" +
		"	#elif defined(emissiveColorFlag)\r\n" +
		"		vec4 emissive = u_emissiveColor;\r\n" +
		"	#else\r\n" +
		"		vec4 emissive = vec4(0.0);\r\n" +
		"	#endif\r\n" +
		"\r\n" +
		"	#if (!defined(lightingFlag))\r\n" +
		"		gl_FragColor.rgb = diffuse.rgb + emissive.rgb;\r\n" +
		"	#elif (!defined(specularFlag))\r\n" +
		"		#if defined(ambientFlag) && defined(separateAmbientFlag)\r\n" +
		"			#ifdef shadowMapFlag\r\n" +
		"				gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + getShadow() * v_lightDiffuse)) + emissive.rgb;\r\n" +
		"				//gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);\r\n" +
		"			#else\r\n" +
		"				gl_FragColor.rgb = (diffuse.rgb * (v_ambientLight + v_lightDiffuse)) + emissive.rgb;\r\n" +
		"			#endif //shadowMapFlag\r\n" +
		"		#else\r\n" +
		"			#ifdef shadowMapFlag\r\n" +
		"				gl_FragColor.rgb = getShadow() * (diffuse.rgb * v_lightDiffuse) + emissive.rgb;\r\n" +
		"			#else\r\n" +
		"				gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + emissive.rgb;\r\n" +
		"			#endif //shadowMapFlag\r\n" +
		"		#endif\r\n" +
		"	#else\r\n" +
		"		#if defined(specularTextureFlag) && defined(specularColorFlag)\r\n" +
		"			vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * u_specularColor.rgb * v_lightSpecular;\r\n" +
		"		#elif defined(specularTextureFlag)\r\n" +
		"			vec3 specular = texture2D(u_specularTexture, v_specularUV).rgb * v_lightSpecular;\r\n" +
		"		#elif defined(specularColorFlag)\r\n" +
		"			vec3 specular = u_specularColor.rgb * v_lightSpecular;\r\n" +
		"		#else\r\n" +
		"			vec3 specular = v_lightSpecular;\r\n" +
		"		#endif\r\n" +
		"\r\n" +
		"		#if defined(ambientFlag) && defined(separateAmbientFlag)\r\n" +
		"			#ifdef shadowMapFlag\r\n" +
		"			gl_FragColor.rgb = (diffuse.rgb * (getShadow() * v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;\r\n" +
		"				//gl_FragColor.rgb = texture2D(u_shadowTexture, v_shadowMapUv.xy);\r\n" +
		"			#else\r\n" +
		"				gl_FragColor.rgb = (diffuse.rgb * (v_lightDiffuse + v_ambientLight)) + specular + emissive.rgb;\r\n" +
		"			#endif //shadowMapFlag\r\n" +
		"		#else\r\n" +
		"			#ifdef shadowMapFlag\r\n" +
		"				gl_FragColor.rgb = getShadow() * ((diffuse.rgb * v_lightDiffuse) + specular) + emissive.rgb;\r\n" +
		"			#else\r\n" +
		"				gl_FragColor.rgb = (diffuse.rgb * v_lightDiffuse) + specular + emissive.rgb;\r\n" +
		"			#endif //shadowMapFlag\r\n" +
		"		#endif\r\n" +
		"	#endif //lightingFlag\r\n" +
		"\r\n" +
		"	#ifdef fogFlag\r\n" +
		"		gl_FragColor.rgb = mix(gl_FragColor.rgb, u_fogColor.rgb, v_fog);\r\n" +
		"	#endif // end fogFlag\r\n" +
		"\r\n" +
		"	#ifdef blendedFlag\r\n" +
		"		gl_FragColor.a = diffuse.a * v_opacity;\r\n" +
		"		#ifdef alphaTestFlag\r\n" +
		"			if (gl_FragColor.a <= v_alphaTest)\r\n" +
		"				discard;\r\n" +
		"		#endif\r\n" +
		"	#else\r\n" +
		"		gl_FragColor.a = 1.0;\r\n" +
		"	#endif\r\n" +
		"\r\n" +
		"}\r\n" +
		"";

	public static String defaultVertex = "#if defined(diffuseTextureFlag) || defined(specularTextureFlag) || defined(emissiveTextureFlag)\r\n" +
		"#define textureFlag\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#if defined(specularTextureFlag) || defined(specularColorFlag)\r\n" +
		"#define specularFlag\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#if defined(specularFlag) || defined(fogFlag)\r\n" +
		"#define cameraPositionFlag\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"attribute vec3 a_position;\r\n" +
		"uniform mat4 u_projViewTrans;\r\n" +
		"\r\n" +
		"#if defined(colorFlag)\r\n" +
		"varying vec4 v_color;\r\n" +
		"attribute vec4 a_color;\r\n" +
		"#endif // colorFlag\r\n" +
		"\r\n" +
		"#ifdef normalFlag\r\n" +
		"attribute vec3 a_normal;\r\n" +
		"uniform mat3 u_normalMatrix;\r\n" +
		"varying vec3 v_normal;\r\n" +
		"#endif // normalFlag\r\n" +
		"\r\n" +
		"#ifdef textureFlag\r\n" +
		"attribute vec2 a_texCoord0;\r\n" +
		"#endif // textureFlag\r\n" +
		"\r\n" +
		"#ifdef diffuseTextureFlag\r\n" +
		"uniform vec4 u_diffuseUVTransform;\r\n" +
		"varying vec2 v_diffuseUV;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef emissiveTextureFlag\r\n" +
		"uniform vec4 u_emissiveUVTransform;\r\n" +
		"varying vec2 v_emissiveUV;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef specularTextureFlag\r\n" +
		"uniform vec4 u_specularUVTransform;\r\n" +
		"varying vec2 v_specularUV;\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef boneWeight0Flag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"attribute vec2 a_boneWeight0;\r\n" +
		"#endif //boneWeight0Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight1Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight1;\r\n" +
		"#endif //boneWeight1Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight2Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight2;\r\n" +
		"#endif //boneWeight2Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight3Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight3;\r\n" +
		"#endif //boneWeight3Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight4Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight4;\r\n" +
		"#endif //boneWeight4Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight5Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight5;\r\n" +
		"#endif //boneWeight5Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight6Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight6;\r\n" +
		"#endif //boneWeight6Flag\r\n" +
		"\r\n" +
		"#ifdef boneWeight7Flag\r\n" +
		"#ifndef boneWeightsFlag\r\n" +
		"#define boneWeightsFlag\r\n" +
		"#endif\r\n" +
		"attribute vec2 a_boneWeight7;\r\n" +
		"#endif //boneWeight7Flag\r\n" +
		"\r\n" +
		"#if defined(numBones) && defined(boneWeightsFlag)\r\n" +
		"#if (numBones > 0) \r\n" +
		"#define skinningFlag\r\n" +
		"#endif\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"uniform mat4 u_worldTrans;\r\n" +
		"\r\n" +
		"#if defined(numBones)\r\n" +
		"#if numBones > 0\r\n" +
		"uniform mat4 u_bones[numBones];\r\n" +
		"#endif //numBones\r\n" +
		"#endif\r\n" +
		"\r\n" +
		"#ifdef shininessFlag\r\n" +
		"uniform float u_shininess;\r\n" +
		"#else\r\n" +
		"const float u_shininess = 20.0;\r\n" +
		"#endif // shininessFlag\r\n" +
		"\r\n" +
		"#ifdef blendedFlag\r\n" +
		"uniform float u_opacity;\r\n" +
		"varying float v_opacity;\r\n" +
		"\r\n" +
		"#ifdef alphaTestFlag\r\n" +
		"uniform float u_alphaTest;\r\n" +
		"varying float v_alphaTest;\r\n" +
		"#endif //alphaTestFlag\r\n" +
		"#endif // blendedFlag\r\n" +
		"\r\n" +
		"#ifdef lightingFlag\r\n" +
		"varying vec3 v_lightDiffuse;\r\n" +
		"\r\n" +
		"#ifdef ambientLightFlag\r\n" +
		"uniform vec3 u_ambientLight;\r\n" +
		"#endif // ambientLightFlag\r\n" +
		"\r\n" +
		"#ifdef ambientCubemapFlag\r\n" +
		"uniform vec3 u_ambientCubemap[6];\r\n" +
		"#endif // ambientCubemapFlag \r\n" +
		"\r\n" +
		"#ifdef sphericalHarmonicsFlag\r\n" +
		"uniform vec3 u_sphericalHarmonics[9];\r\n" +
		"#endif //sphericalHarmonicsFlag\r\n" +
		"\r\n" +
		"#ifdef specularFlag\r\n" +
		"varying vec3 v_lightSpecular;\r\n" +
		"#endif // specularFlag\r\n" +
		"\r\n" +
		"#ifdef cameraPositionFlag\r\n" +
		"uniform vec4 u_cameraPosition;\r\n" +
		"#endif // cameraPositionFlag\r\n" +
		"\r\n" +
		"#ifdef fogFlag\r\n" +
		"varying float v_fog;\r\n" +
		"#endif // fogFlag\r\n" +
		"\r\n" +
		"\r\n" +
		"#if numDirectionalLights > 0\r\n" +
		"struct DirectionalLight\r\n" +
		"{\r\n" +
		"	vec3 color;\r\n" +
		"	vec3 direction;\r\n" +
		"};\r\n" +
		"uniform DirectionalLight u_dirLights[numDirectionalLights];\r\n" +
		"#endif // numDirectionalLights\r\n" +
		"\r\n" +
		"#if numPointLights > 0\r\n" +
		"struct PointLight\r\n" +
		"{\r\n" +
		"	vec3 color;\r\n" +
		"	vec3 position;\r\n" +
		"};\r\n" +
		"uniform PointLight u_pointLights[numPointLights];\r\n" +
		"#endif // numPointLights\r\n" +
		"\r\n" +
		"#if	defined(ambientLightFlag) || defined(ambientCubemapFlag) || defined(sphericalHarmonicsFlag)\r\n" +
		"#define ambientFlag\r\n" +
		"#endif //ambientFlag\r\n" +
		"\r\n" +
		"#ifdef shadowMapFlag\r\n" +
		"uniform mat4 u_shadowMapProjViewTrans;\r\n" +
		"varying vec3 v_shadowMapUv;\r\n" +
		"#define separateAmbientFlag\r\n" +
		"#endif //shadowMapFlag\r\n" +
		"\r\n" +
		"#if defined(ambientFlag) && defined(separateAmbientFlag)\r\n" +
		"varying vec3 v_ambientLight;\r\n" +
		"#endif //separateAmbientFlag\r\n" +
		"\r\n" +
		"#endif // lightingFlag\r\n" +
		"\r\n" +
		"void main() {\r\n" +
		"	#ifdef diffuseTextureFlag\r\n" +
		"		v_diffuseUV = u_diffuseUVTransform.xy + a_texCoord0 * u_diffuseUVTransform.zw;\r\n" +
		"	#endif //diffuseTextureFlag\r\n" +
		"	\r\n" +
		"	#ifdef emissiveTextureFlag\r\n" +
		"		v_emissiveUV = u_emissiveUVTransform.xy + a_texCoord0 * u_emissiveUVTransform.zw;\r\n" +
		"	#endif //emissiveTextureFlag\r\n" +
		"\r\n" +
		"	#ifdef specularTextureFlag\r\n" +
		"		v_specularUV = u_specularUVTransform.xy + a_texCoord0 * u_specularUVTransform.zw;\r\n" +
		"	#endif //specularTextureFlag\r\n" +
		"	\r\n" +
		"	#if defined(colorFlag)\r\n" +
		"		v_color = a_color;\r\n" +
		"	#endif // colorFlag\r\n" +
		"		\r\n" +
		"	#ifdef blendedFlag\r\n" +
		"		v_opacity = u_opacity;\r\n" +
		"		#ifdef alphaTestFlag\r\n" +
		"			v_alphaTest = u_alphaTest;\r\n" +
		"		#endif //alphaTestFlag\r\n" +
		"	#endif // blendedFlag\r\n" +
		"	\r\n" +
		"	#ifdef skinningFlag\r\n" +
		"		mat4 skinning = mat4(0.0);\r\n" +
		"		#ifdef boneWeight0Flag\r\n" +
		"			skinning += (a_boneWeight0.y) * u_bones[int(a_boneWeight0.x)];\r\n" +
		"		#endif //boneWeight0Flag\r\n" +
		"		#ifdef boneWeight1Flag				\r\n" +
		"			skinning += (a_boneWeight1.y) * u_bones[int(a_boneWeight1.x)];\r\n" +
		"		#endif //boneWeight1Flag\r\n" +
		"		#ifdef boneWeight2Flag		\r\n" +
		"			skinning += (a_boneWeight2.y) * u_bones[int(a_boneWeight2.x)];\r\n" +
		"		#endif //boneWeight2Flag\r\n" +
		"		#ifdef boneWeight3Flag\r\n" +
		"			skinning += (a_boneWeight3.y) * u_bones[int(a_boneWeight3.x)];\r\n" +
		"		#endif //boneWeight3Flag\r\n" +
		"		#ifdef boneWeight4Flag\r\n" +
		"			skinning += (a_boneWeight4.y) * u_bones[int(a_boneWeight4.x)];\r\n" +
		"		#endif //boneWeight4Flag\r\n" +
		"		#ifdef boneWeight5Flag\r\n" +
		"			skinning += (a_boneWeight5.y) * u_bones[int(a_boneWeight5.x)];\r\n" +
		"		#endif //boneWeight5Flag\r\n" +
		"		#ifdef boneWeight6Flag\r\n" +
		"			skinning += (a_boneWeight6.y) * u_bones[int(a_boneWeight6.x)];\r\n" +
		"		#endif //boneWeight6Flag\r\n" +
		"		#ifdef boneWeight7Flag\r\n" +
		"			skinning += (a_boneWeight7.y) * u_bones[int(a_boneWeight7.x)];\r\n" +
		"		#endif //boneWeight7Flag\r\n" +
		"	#endif //skinningFlag\r\n" +
		"\r\n" +
		"	#ifdef skinningFlag\r\n" +
		"		vec4 pos = u_worldTrans * skinning * vec4(a_position, 1.0);\r\n" +
		"	#else\r\n" +
		"		vec4 pos = u_worldTrans * vec4(a_position, 1.0);\r\n" +
		"	#endif\r\n" +
		"		\r\n" +
		"	gl_Position = u_projViewTrans * pos;\r\n" +
		"		\r\n" +
		"	#ifdef shadowMapFlag\r\n" +
		"		vec4 spos = u_shadowMapProjViewTrans * pos;\r\n" +
		"		v_shadowMapUv.xyz = (spos.xyz / spos.w) * 0.5 + 0.5;\r\n" +
		"		v_shadowMapUv.z = min(v_shadowMapUv.z, 0.998);\r\n" +
		"	#endif //shadowMapFlag\r\n" +
		"	\r\n" +
		"	#if defined(normalFlag)\r\n" +
		"		#if defined(skinningFlag)\r\n" +
		"			vec3 normal = normalize((u_worldTrans * skinning * vec4(a_normal, 0.0)).xyz);\r\n" +
		"		#else\r\n" +
		"			vec3 normal = normalize(u_normalMatrix * a_normal);\r\n" +
		"		#endif\r\n" +
		"		v_normal = normal;\r\n" +
		"	#endif // normalFlag\r\n" +
		"\r\n" +
		"    #ifdef fogFlag\r\n" +
		"        vec3 flen = u_cameraPosition.xyz - pos.xyz;\r\n" +
		"        float fog = dot(flen, flen) * u_cameraPosition.w;\r\n" +
		"        v_fog = min(fog, 1.0);\r\n" +
		"    #endif\r\n" +
		"\r\n" +
		"	#ifdef lightingFlag\r\n" +
		"		#if	defined(ambientLightFlag)\r\n" +
		"        	vec3 ambientLight = u_ambientLight;\r\n" +
		"		#elif defined(ambientFlag)\r\n" +
		"        	vec3 ambientLight = vec3(0.0);\r\n" +
		"		#endif\r\n" +
		"			\r\n" +
		"		#ifdef ambientCubemapFlag 		\r\n" +
		"			vec3 squaredNormal = normal * normal;\r\n" +
		"			vec3 isPositive  = step(0.0, normal);\r\n" +
		"			ambientLight += squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +\r\n" +
		"					squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +\r\n" +
		"					squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);\r\n" +
		"		#endif // ambientCubemapFlag\r\n" +
		"\r\n" +
		"		#ifdef sphericalHarmonicsFlag\r\n" +
		"			ambientLight += u_sphericalHarmonics[0];\r\n" +
		"			ambientLight += u_sphericalHarmonics[1] * normal.x;\r\n" +
		"			ambientLight += u_sphericalHarmonics[2] * normal.y;\r\n" +
		"			ambientLight += u_sphericalHarmonics[3] * normal.z;\r\n" +
		"			ambientLight += u_sphericalHarmonics[4] * (normal.x * normal.z);\r\n" +
		"			ambientLight += u_sphericalHarmonics[5] * (normal.z * normal.y);\r\n" +
		"			ambientLight += u_sphericalHarmonics[6] * (normal.y * normal.x);\r\n" +
		"			ambientLight += u_sphericalHarmonics[7] * (3.0 * normal.z * normal.z - 1.0);\r\n" +
		"			ambientLight += u_sphericalHarmonics[8] * (normal.x * normal.x - normal.y * normal.y);			\r\n" +
		"		#endif // sphericalHarmonicsFlag\r\n" +
		"\r\n" +
		"		#ifdef ambientFlag\r\n" +
		"			#ifdef separateAmbientFlag\r\n" +
		"				v_ambientLight = ambientLight;\r\n" +
		"				v_lightDiffuse = vec3(0.0);\r\n" +
		"			#else\r\n" +
		"				v_lightDiffuse = ambientLight;\r\n" +
		"			#endif //separateAmbientFlag\r\n" +
		"		#else\r\n" +
		"	        v_lightDiffuse = vec3(0.0);\r\n" +
		"		#endif //ambientFlag\r\n" +
		"\r\n" +
		"			\r\n" +
		"		#ifdef specularFlag\r\n" +
		"			v_lightSpecular = vec3(0.0);\r\n" +
		"			vec3 viewVec = normalize(u_cameraPosition.xyz - pos.xyz);\r\n" +
		"		#endif // specularFlag\r\n" +
		"			\r\n" +
		"		#if (numDirectionalLights > 0) && defined(normalFlag)\r\n" +
		"			for (int i = 0; i < numDirectionalLights; i++) {\r\n" +
		"				vec3 lightDir = -u_dirLights[i].direction;\r\n" +
		"				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);\r\n" +
		"				vec3 value = u_dirLights[i].color * NdotL;\r\n" +
		"				v_lightDiffuse += value;\r\n" +
		"				#ifdef specularFlag\r\n" +
		"					float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));\r\n" +
		"					v_lightSpecular += value * pow(halfDotView, u_shininess);\r\n" +
		"				#endif // specularFlag\r\n" +
		"			}\r\n" +
		"		#endif // numDirectionalLights\r\n" +
		"\r\n" +
		"		#if (numPointLights > 0) && defined(normalFlag)\r\n" +
		"			for (int i = 0; i < numPointLights; i++) {\r\n" +
		"				vec3 lightDir = u_pointLights[i].position - pos.xyz;\r\n" +
		"				float dist2 = dot(lightDir, lightDir);\r\n" +
		"				lightDir *= inversesqrt(dist2);\r\n" +
		"				float NdotL = clamp(dot(normal, lightDir), 0.0, 1.0);\r\n" +
		"				vec3 value = u_pointLights[i].color * (NdotL / (1.0 + dist2));\r\n" +
		"				v_lightDiffuse += value;\r\n" +
		"				#ifdef specularFlag\r\n" +
		"					float halfDotView = max(0.0, dot(normal, normalize(lightDir + viewVec)));\r\n" +
		"					v_lightSpecular += value * pow(halfDotView, u_shininess);\r\n" +
		"				#endif // specularFlag\r\n" +
		"			}\r\n" +
		"		#endif // numPointLights\r\n" +
		"	#endif // lightingFlag\r\n" +
		"}\r\n" +
		"";
}
