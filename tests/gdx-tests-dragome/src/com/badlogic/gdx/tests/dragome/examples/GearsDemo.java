package com.badlogic.gdx.tests.dragome.examples;


import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
	public CameraInputController inputController;
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
	SpriteBatch batch;
	long time;
	int fps;

	CameraInputController cameraController;

	@Override
	public void create () {

		environment = new Environment();
//		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .1f, .1f, .1f, 1f));
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, .5f, .5f, .5f, 5f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, 0f, -0.5f, -0.5f));

		sl = new PointLight().setPosition(-5, 10, -6).setColor(1, 1,1, 1)
			.setIntensity(150);

//		sl2 = new PointLight().setPosition(0, 7, 5).setColor(0.3f, 0.8f, 0.3f, 1)
//			.setIntensity(20);
//
//		sl3 = new PointLight().setPosition(0, 9, 6).setColor(0.3f, 0.3f, 0.8f, 1)
//			.setIntensity(20);

//		environment.add(sl);
//		environment.add(sl2);
//		environment.add(sl3);


		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(-10, 3, 10f);
		cam.lookAt(-3, 0, 0);
		cam.near = 1f;
		cam.far = 30f;
		cam.update();

		cameraController = new CameraInputController(cam);
		cameraController.autoUpdate = false;
		cameraController.forwardTarget = false;
		cameraController.translateTarget = false;

		Gdx.input.setInputProcessor(new InputMultiplexer(cameraController));

		time = TimeUtils.millis();

		viewport = new ScreenViewport(cam);

		DefaultShaderProvider defaultShaderProvider = new DefaultShaderProvider();
		modelBatch = new ModelBatch(defaultShaderProvider);

		ModelBuilder modelBuilder = new ModelBuilder();
		model1 = gear(modelBuilder, 1.0f, 4.0f, 1.0f, 20, 0.7f, Color.RED);
		gear1 = new ModelInstance(model1);

		model2 = gear(modelBuilder, 0.5f, 2.0f, 2.0f, 10, 0.7f, Color.GREEN);
		gear2 = new ModelInstance(model2);

		model3 = gear(modelBuilder, 1.3f, 2.0f, 1.5f, 10, 0.7f, Color.BLUE);
		gear3 = new ModelInstance(model3);

		font = new BitmapFont();

		batch = new SpriteBatch();

		lightModel = modelBuilder.createSphere(1, 1, 1, 10, 10, new Material(ColorAttribute.createDiffuse(1, 1, 1, 1)), Usage.Position);
		lightModel.nodes.get(0).parts.get(0).setRenderable(pLight = new Renderable());
	}

	@Override
	public void render () {
//		inputController.update();

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
			batch.setProjectionMatrix(viewport.getCamera().combined);
			batch.begin();
			font.draw(batch, "FPS: " + fps, 100, 100);
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
		// draw front face
		// GL_TRIANGLE_STRIP
		MeshPartBuilder part = builder.part("gear", GL20.GL_TRIANGLE_STRIP, Usage.Position | Usage.Normal, new Material(ColorAttribute.createDiffuse(color)));
		da = 2.0f * (float)Math.PI / teeth / 4.0f;
		for (i = 0; i <= teeth; i++) {
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

			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[0]), r0 * (float)Math.sin(ar[0]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r0 * (float)Math.cos(ar[3]), r0 * (float)Math.sin(ar[3]), -dz)));
			part.index(part.vertex(vertTmp1.setPos(r1 * (float)Math.cos(ar[0]), r1 * (float)Math.sin(ar[0]), -dz)));
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
}
