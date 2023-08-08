package com.github.xpenatan.gdx.examples.bullet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import bullet.Bullet;
import bullet.btDefaultCollisionConstructionInfo;
import bullet.bulletcollision.broadphasecollision.btDbvtBroadphase;
import bullet.bulletcollision.collisiondispatch.btCollisionDispatcher;
import bullet.bulletcollision.collisiondispatch.btDefaultCollisionConfiguration;
import bullet.bulletcollision.collisionshapes.btBoxShape;
import bullet.bulletdynamics.constraintsolver.btSequentialImpulseConstraintSolver;
import bullet.bulletdynamics.dynamics.btDiscreteDynamicsWorld;
import bullet.bulletdynamics.dynamics.btRigidBody;
import bullet.linearmath.btVector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class BulletTestScreen extends ScreenAdapter implements InputProcessor {
    private int btVersion;
    private PerspectiveCamera camera;
    private ScreenViewport viewport;
    private ScreenViewport guiViewport;
    private Array<ModelInstance> boxes = new Array<>();
    private ModelInstance ground;
    private ModelBatch modelBatch;
    private SpriteBatch batch;
    private Environment environment;

    Array<btRigidBody> colObjs = new Array<>();
    btDiscreteDynamicsWorld world;
//    DebugDrawer debugDrawer;
    btDefaultCollisionConfiguration collisionConfiguration;
    btCollisionDispatcher dispatcher;
    btDbvtBroadphase broadphase;
    btSequentialImpulseConstraintSolver solver;
//    ClosestRayResultCallback raycast;

    private BitmapFont font;
    private Model boxModel;
    private long timeNow;
    private long time;
    private boolean debug = false;
    private boolean freeze = false;
    private Matrix4 mat = new Matrix4();
    private static Vector3 rayFrom = new Vector3();
    private static Vector3 rayTo = new Vector3();
    private CameraInputController cameraController;
    private int totalBoxes = 500;

    @Override
    public void show() {
        btVersion = Bullet.btGetVersion();

        btDefaultCollisionConstructionInfo info = new btDefaultCollisionConstructionInfo();
        collisionConfiguration = new btDefaultCollisionConfiguration(info);
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        btVector3 gravity = new btVector3(0, -10, 0);
        world.setGravity(gravity);
//
//        debugDrawer = new DebugDrawer();
//        world.setDebugDrawer(debugDrawer);
//        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE | btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints);
//        raycast = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);

        camera = new PerspectiveCamera();
        viewport = new ScreenViewport(camera);
        guiViewport = new ScreenViewport();

        modelBatch = new ModelBatch();

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1.f));
        DirectionalLight set = new DirectionalLight().set(1.0f, 1.0f, 1.0f, -1f, -1f, -0.4f);
        environment.add(set);

        batch = new SpriteBatch();

        camera.position.z = 43;
        camera.position.y = 2;

        camera.lookAt(0, 0, 0);

        ModelBuilder builder = new ModelBuilder();

        float r = 1;
        float g = 1;
        float b = 1;
        float a = 1;

        final Texture texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        final Material material = new Material(TextureAttribute.createDiffuse(texture),
                FloatAttribute.createShininess(4f));

        boxModel = builder.createBox(1, 1, 1, material, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        float groundWidth = 60f;
        float groundHeight = 0.3f;
        float groundDepth = 60f;
        Model groundBox = builder.createBox(groundWidth, groundHeight, groundDepth, new Material(ColorAttribute.createDiffuse(r, g, b, a), ColorAttribute.createSpecular(r, g, b, a), FloatAttribute.createShininess(16f)), VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        ground = createBox("ground", false, 0, 0, -2, 0, 0, 0, 0, groundBox, groundWidth, groundHeight, groundDepth, 0, 0, 1);

        resetBoxes();

        font = new BitmapFont();
        font.setColor(1, 0, 0, 1);
        time = System.currentTimeMillis();

        cameraController = new CameraInputController(camera);
        cameraController.autoUpdate = false;
        cameraController.forwardTarget = false;
        cameraController.translateTarget = false;

        Gdx.input.setInputProcessor(new InputMultiplexer(this, cameraController));
    }

    public ModelInstance createBox(String userData, boolean add, float mass, float x, float y, float z, float axiX, float axiY, float axiZ, Model model, float x1, float y1, float z1, float colorR, float colorG, float colorB) {
        ModelInstance modelInstance = new ModelInstance(model);

        ColorAttribute attr = ColorAttribute.createDiffuse(colorR, colorG, colorB, 1);
        modelInstance.materials.get(0).set(attr);

        modelInstance.transform.translate(x, y, z);

        modelInstance.transform.rotate(Vector3.X, axiX);
        modelInstance.transform.rotate(Vector3.Y, axiY);
        modelInstance.transform.rotate(Vector3.Z, axiZ);

        TestMotionState motionState = new TestMotionState(modelInstance.transform);
        btBoxShape shape = new btBoxShape(new btVector3(x1 / 2f, y1 / 2f, z1 / 2f));
        btVector3 tmp = new btVector3();
        shape.calculateLocalInertia(mass, tmp);
        btRigidBody body = new btRigidBody(mass, motionState, shape, tmp);
//        body.setUserPointer(colObjs.size);
        if(add)
            colObjs.add(body);
        body.setRestitution(0.7f);

        world.addRigidBody(body);
        return modelInstance;
    }

    public void resetBoxes() {
        for(int i = 0; i < colObjs.size; i++) {
            btRigidBody btCollisionObject = colObjs.get(i);
            world.removeRigidBody(btCollisionObject);
            btCollisionObject.dispose();
        }
        colObjs.clear();
        boxes.clear();

        int count = 0;

        int offsetY = 15;

        for(int i = 0; i < totalBoxes; i++) {
            ModelInstance createBox = null;
            float x = MathUtils.random(-5.0f, 5.0f);
            float y = MathUtils.random(offsetY + 4f, offsetY + 9f);
            float z = MathUtils.random(-5.0f, 5.0f);
            float axisX = MathUtils.random(0, 360);
            float axisY = MathUtils.random(0, 360);
            float axisZ = MathUtils.random(0, 360);
            float r = 1f;
            float g = 1f;
            float b = 1f;

            createBox = createBox("ID: " + count, true, 0.4f, x, y, z, axisX, axisY, axisZ, boxModel, 1, 1, 1, r, g, b);
            count++;
            boxes.add(createBox);
        }
    }

    @Override
    public void render(float delta) {

        camera.update();
        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        if(freeze == false) {
            timeNow = System.currentTimeMillis();

            if(timeNow - time > 8000) {
                resetBoxes();
                time = System.currentTimeMillis();
            }

            world.stepSimulation(Gdx.graphics.getDeltaTime());
        }

        modelBatch.begin(camera);
        for(int i = 0; i < boxes.size; i++) {
            ModelInstance modelInstance = boxes.get(i);

            modelBatch.render(modelInstance, environment);
        }
        modelBatch.render(ground, environment);
        modelBatch.end();

        if(debug) {
//            debugDrawer.begin(camera);
//            world.debugDrawWorld();
//            debugDrawer.end();
        }

        batch.begin();
        font.draw(batch, "\nFPS: " + Gdx.graphics.getFramesPerSecond() +
                "\nTotal Boxes: " + totalBoxes +
                "\nBullet Version: " + btVersion +
                "\nInputs: Enter for fullscreen, Space to un/freeze simulation\nHold Left/Right mouse to manipulate camera", 30, Gdx.graphics.getHeight() - 14);
        font.draw(batch, "Libgdx teaVM Backend + teaVM Bullet Extension by xpenatan", 20, 30);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
        guiViewport.update(width, height, true);
        Camera guiCam = guiViewport.getCamera();
        guiCam.update();
        batch.setProjectionMatrix(guiCam.combined);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
//        world.dispose();
//        solver.dispose();
//        broadphase.dispose();
//        dispatcher.dispose();
//        collisionConfiguration.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.SPACE) {
            freeze = !freeze;

            if(!freeze) {
                time = System.currentTimeMillis() + (time - timeNow);
            }
        }
        else if(keycode == Input.Keys.ENTER) {
            if(Gdx.graphics.isFullscreen()) {
                Gdx.graphics.setWindowedMode(0, 0);
            }
            else {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }
        else if(keycode == Input.Keys.D) {
            debug = !debug;
        }
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
//        if(button == Buttons.LEFT) {
//            Ray ray = camera.getPickRay(screenX, screenY);
//            float ScaleToMeter = 1;
//            rayFrom.set(ray.origin.scl(ScaleToMeter));
//            rayTo.set(ray.direction).scl(200).add(rayFrom);
//            raycast.setCollisionObject(null);
//            raycast.setClosestHitFraction(1f);
//            world.rayTest(rayFrom, rayTo, raycast);
//
//            if(raycast.hasHit()) {
//                btCollisionObject collisionObject = raycast.getCollisionObject();
//                System.out.println("HIT Body:" + collisionObject.getUserPointer());
//            }
//        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}