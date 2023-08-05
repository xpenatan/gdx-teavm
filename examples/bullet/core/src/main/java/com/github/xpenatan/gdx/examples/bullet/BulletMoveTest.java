package com.github.xpenatan.gdx.examples.bullet;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.InputProcessor;

public class BulletMoveTest extends ApplicationAdapter implements InputProcessor {
//    int btVersion;
//    PerspectiveCamera camera;
//
//    ScreenViewport viewport;
//    ScreenViewport guiViewport;
//
//    Array<ModelInstance> boxes = new Array<>();
//    Array<btRigidBody> colObjs = new Array<>();
//
//    ModelInstance ground;
//
//    ModelBatch modelBatch;
//
//    SpriteBatch batch;
//
//    Environment environment;
//
//    btDiscreteDynamicsWorld world;
//    //    DebugDrawer debugDrawer;
//    btDefaultCollisionConfiguration collisionConfiguration;
//    btCollisionDispatcher dispatcher;
//    btDbvtBroadphase broadphase;
//    btSequentialImpulseConstraintSolver solver;
////    ClosestRayResultCallback raycast;
//
//    BitmapFont font;
//
//    Model boxModel;
//
//    boolean debug = true;
//
//    boolean freeze = false;
//
//    Matrix4 mat = new Matrix4();
//
//    static Vector3 rayFrom = new Vector3();
//    static Vector3 rayTo = new Vector3();
//
//    CameraInputController cameraController;
//
//    int totalBoxes = 500;
//
//    @Override
//    public void create() {
//        Bullet.init();
//
//        btVersion = LinearMath.btGetVersion();
//
//        collisionConfiguration = new btDefaultCollisionConfiguration();
//        dispatcher = new btCollisionDispatcher(collisionConfiguration);
//        broadphase = new btDbvtBroadphase();
//        solver = new btSequentialImpulseConstraintSolver();
//        world = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
//        Vector3 gravity = new Vector3(0, -10, 0);
//        world.setGravity(gravity);
//
////        debugDrawer = new DebugDrawer();
////        world.setDebugDrawer(debugDrawer);
////        debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE | btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints);
////        raycast = new ClosestRayResultCallback(Vector3.Zero, Vector3.Z);
//
//        camera = new PerspectiveCamera();
//        viewport = new ScreenViewport(camera);
//        guiViewport = new ScreenViewport();
//
//        modelBatch = new ModelBatch();
//
//        environment = new Environment();
//        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1.f));
//        DirectionalLight set = new DirectionalLight().set(1.0f, 1.0f, 1.0f, -1f, -1f, -0.4f);
//        environment.add(set);
//
//        batch = new SpriteBatch();
//
//        camera.position.z = 5;
//        camera.position.y = 2;
//
//        camera.lookAt(0, 0, 0);
//
//        ModelBuilder builder = new ModelBuilder();
//
//        float r = 1;
//        float g = 1;
//        float b = 1;
//        float a = 1;
//
//        final Texture texture = new Texture(Gdx.files.internal("data/badlogic.jpg"));
//        texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
//        final Material material = new Material(TextureAttribute.createDiffuse(texture),
//                FloatAttribute.createShininess(4f));
//
//        boxModel = builder.createBox(1, 1, 1, material, Usage.Position | Usage.Normal | Usage.TextureCoordinates);
//
//        float groundWidth = 60f;
//        float groundHeight = 0.3f;
//        float groundDepth = 60f;
//        Model groundBox = builder.createBox(groundWidth, groundHeight, groundDepth, new Material(ColorAttribute.createDiffuse(r, g, b, a), ColorAttribute.createSpecular(r, g, b, a), FloatAttribute.createShininess(16f)), Usage.Position | Usage.Normal);
//        ground = createBox("ground", false, 0, 0, -2, 0, 0, 0, 0, groundBox, groundWidth, groundHeight, groundDepth, 0, 0, 1);
//
//        font = new BitmapFont();
//        font.setColor(1, 0, 0, 1);
//
//        cameraController = new CameraInputController(camera);
//        cameraController.autoUpdate = false;
//        cameraController.forwardTarget = false;
//        cameraController.translateTarget = false;
//
//        Gdx.input.setInputProcessor(new InputMultiplexer(this, cameraController));
//
//        ModelInstance createBox = null;
//        float x = 0;
//        float y = 1;
//        float z = 0;
//        float axisX = 0;
//        float axisY = 0;
//        float axisZ = 0;
//        createBox = createBox("ID: " + 2, true, 0.4f, x, y, z, axisX, axisY, axisZ, boxModel, 1, 1, 1, r, g, b);
//        boxes.add(createBox);
//    }
//
//    Vector3 tmp = new Vector3();
//
//    public ModelInstance createBox(String userData, boolean add, float mass, float x, float y, float z, float axiX, float axiY, float axiZ, Model model, float x1, float y1, float z1, float colorR, float colorG, float colorB) {
//        ModelInstance modelInstance = new ModelInstance(model);
//
//        ColorAttribute attr = ColorAttribute.createDiffuse(colorR, colorG, colorB, 1);
//        modelInstance.materials.get(0).set(attr);
//
//        modelInstance.transform.translate(x, y, z);
//
//        modelInstance.transform.rotate(Vector3.X, axiX);
//        modelInstance.transform.rotate(Vector3.Y, axiY);
//        modelInstance.transform.rotate(Vector3.Z, axiZ);
//
//        TestMotionState motionState = new TestMotionState(modelInstance.transform);
//        btBoxShape shape = new btBoxShape(tmp.set(x1 / 2f, y1 / 2f, z1 / 2f));
//
//        btRigidBody body = new btRigidBody(0, null, null, new Vector3(0, 0, 0));
//        if(add)
//            colObjs.add(body);
//
//        btCompoundShape compoundShape = new btCompoundShape();
//        compoundShape.addChildShape(new Matrix4(), shape);
//        tmp.setZero();
//        compoundShape.calculateLocalInertia(mass, tmp);
//        body.setCollisionShape(compoundShape);
//        body.setRestitution(0.7f);
//        body.setFriction(0);
//        body.setMassProps(mass, tmp);
//        body.setMotionState(motionState);
//
//        world.addRigidBody(body);
//        return modelInstance;
//    }
//
//    @Override
//    public void render() {
//
//        camera.update();
//        Gdx.gl.glClearColor(0.9f, 0.9f, 0.9f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
//
//        if(freeze == false) {
//            world.stepSimulation(Gdx.graphics.getDeltaTime());
//        }
//
//        btRigidBody body = colObjs.get(0);
//        float deltaTime = Gdx.graphics.getDeltaTime();
//        boolean up = Gdx.input.isKeyPressed(Keys.UP);
//        boolean down = Gdx.input.isKeyPressed(Keys.DOWN);
//        boolean left = Gdx.input.isKeyPressed(Keys.LEFT);
//        boolean right = Gdx.input.isKeyPressed(Keys.RIGHT);
//        float speed = 150;
//        float turnSpeed = 2.3f;
//        if(up) {
//            Vector3 vec = new Vector3(speed * deltaTime, 0, 0);
//            body.applyCentralForce(vec);
//            body.activate();
//        }
//
//        if(down) {
//            Vector3 vec = new Vector3(-speed * deltaTime, 0, 0);
//            body.applyCentralForce(vec);
//            body.activate();
//        }
//
//        if(left) {
//            Vector3 vec = new Vector3(turnSpeed * deltaTime, 0, 0);
//            body.applyTorqueImpulse(vec);
//            body.activate();
//        }
//
//        if(right) {
//            Vector3 vec = new Vector3(-turnSpeed * deltaTime, 0, 0);
//            body.applyTorqueImpulse(vec);
//            body.activate();
//        }
//
//        modelBatch.begin(camera);
//        for(int i = 0; i < boxes.size; i++) {
//            ModelInstance modelInstance = boxes.get(i);
//
//            modelBatch.render(modelInstance, environment);
//        }
//        modelBatch.render(ground, environment);
//        modelBatch.end();
//
//        if(debug) {
////            debugDrawer.begin(camera);
////            world.debugDrawWorld();
////            debugDrawer.end();
//        }
//
//        batch.begin();
//        font.draw(batch, "\nFPS: " + Gdx.graphics.getFramesPerSecond() +
//                "\nTotal Boxes: " + totalBoxes +
//                "\nBullet Version: " + btVersion +
//                "\nInputs: Enter for fullscreen, Space to un/freeze simulation\nHold Left/Right mouse to manipulate camera", 30, Gdx.graphics.getHeight() - 14);
//        font.draw(batch, "Libgdx teaVM Backend + teaVM Bullet Extension by xpenatan", 20, 30);
//        batch.end();
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        viewport.update(width, height, false);
//        guiViewport.update(width, height, true);
//        Camera guiCam = guiViewport.getCamera();
//        guiCam.update();
//        batch.setProjectionMatrix(guiCam.combined);
//    }
//
//    @Override
//    public void pause() {
//    }
//
//    @Override
//    public void resume() {
//    }
//
//    @Override
//    public void dispose() {
////        world.dispose();
////        solver.dispose();
////        broadphase.dispose();
////        dispatcher.dispose();
////        collisionConfiguration.dispose();
//    }

    @Override
    public boolean keyDown(int keycode) {
//        if(keycode == Keys.SPACE) {
//            freeze = !freeze;
//        }
//        else if(keycode == Keys.ENTER) {
//            if(Gdx.graphics.isFullscreen()) {
//                Gdx.graphics.setWindowedMode(0, 0);
//            }
//            else {
//                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
//            }
//        }
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
////        if(button == Buttons.LEFT) {
////            Ray ray = camera.getPickRay(screenX, screenY);
////            float ScaleToMeter = 1;
////            rayFrom.set(ray.origin.scl(ScaleToMeter));
////            rayTo.set(ray.direction).scl(200).add(rayFrom);
////            raycast.setCollisionObject(null);
////            raycast.setClosestHitFraction(1f);
////            world.rayTest(rayFrom, rayTo, raycast);
////
////            if(raycast.hasHit()) {
////                btCollisionObject collisionObject = raycast.getCollisionObject();
////                System.out.println("HIT Body:" + collisionObject.userData);
////            }
////        }
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