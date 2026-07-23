package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.shaders.BaseShader;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.profiling.GLProfiler;
import com.badlogic.gdx.math.Frustum;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.nio.Buffer;
import java.nio.FloatBuffer;

/**
 * Renders 24,389 textured cubes in one instanced draw call on web targets. Updating the visible
 * instance transforms stresses the NIO-buffer-to-WebGL upload path used by {@code glBufferData}.
 */
public class ModelInstancedRenderingTest extends ApplicationAdapter {

    private static final int WEB_INSTANCE_COUNT_SIDE = 54;
    private static final int DESKTOP_INSTANCE_COUNT_SIDE = 101;
    private static final int FLOATS_PER_MATRIX = 16;
    private static final int CUBE_INDEX_COUNT = 36;

    private Environment environment;
    private Mesh mesh;
    private Texture texture;
    private ModelBatch modelBatch;
    private GLProfiler profiler;
    private SpriteBatch spriteBatch;
    private BitmapFont font;
    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;
    private Frustum cameraFrustum;
    private Renderable renderable;
    private FloatBuffer offsets;

    private final Quaternion rotation = new Quaternion();
    private final Matrix4 transform = new Matrix4();
    private final Vector3 position = new Vector3();
    private final float[] matrixValues = new float[FLOATS_PER_MATRIX];

    private int instanceCount;
    private int instancesUpdated;
    private long updateTime;
    private long renderTime;
    private float cubeSize;
    private float cullingDistance;
    private boolean rotate = true;
    private boolean showStats = true;

    @Override
    public void create() {
        if(Gdx.gl30 == null) {
            throw new GdxRuntimeException("GLES 3.0 profile required for the model instancing demo");
        }

        initialize();
        setupInstancedMesh();
    }

    @Override
    public void render() {
        float delta = Gdx.graphics.getDeltaTime();
        profiler.reset();
        controller.update();
        ScreenUtils.clear(Color.BLACK, true);
        checkUserInput();

        long startTime = TimeUtils.nanoTime();
        updateInstances(delta);
        updateTime = TimeUtils.timeSinceNanos(startTime);

        startTime = TimeUtils.nanoTime();
        texture.bind(0);
        modelBatch.begin(camera);
        modelBatch.render(renderable);
        modelBatch.end();
        renderTime = TimeUtils.timeSinceNanos(startTime);

        if(showStats) {
            spriteBatch.begin();
            drawStats();
            spriteBatch.end();
        }
    }

    private void drawStats() {
        font.draw(spriteBatch,
                "WASD + mouse drag: camera, F1: Toggle stats, SPACE: Toggle rotation. rotation=" + rotate,
                10, 40);
        font.draw(spriteBatch,
                "3D Cubes: " + instanceCount + "  Matrix4 Updated: " + instancesUpdated
                        + "  Matrix4 Skipped: " + (instanceCount - instancesUpdated),
                10, 80);
        font.draw(spriteBatch,
                "Update Time: " + TimeUtils.nanosToMillis(updateTime) + "ms  Render Time: "
                        + TimeUtils.nanosToMillis(renderTime) + "ms",
                10, 120);
        font.draw(spriteBatch,
                "FPS: " + Gdx.graphics.getFramesPerSecond() + "  Draw Calls: " + profiler.getDrawCalls()
                        + "  Vert Count: " + (long)profiler.getVertexCount().latest
                        + "  Shader Switches: " + profiler.getShaderSwitches()
                        + "  Texture Bindings: " + profiler.getTextureBindings(),
                10, 160);
    }

    private void updateInstances(float delta) {
        instancesUpdated = 0;
        if(!rotate) {
            return;
        }

        // This is intentionally the hot loop from the original demo. It makes buffer upload
        // regressions visible when many instance matrices are changed every frame.
        for(int instance = 0; instance < instanceCount; instance++) {
            int targetIndex = instance * FLOATS_PER_MATRIX;

            position.set(offsets.get(targetIndex + Matrix4.M03),
                    offsets.get(targetIndex + Matrix4.M13),
                    offsets.get(targetIndex + Matrix4.M23));

            if(!cameraFrustum.sphereInFrustum(position, cubeSize * 2f)
                    || position.dst(camera.position) > cullingDistance) {
                continue;
            }

            instancesUpdated++;
            for(int value = 0; value < FLOATS_PER_MATRIX; value++) {
                matrixValues[value] = offsets.get(targetIndex + value);
            }

            transform.set(matrixValues);
            if((instance & 1) == 0) {
                transform.rotate(Vector3.X, 45f * delta);
            }
            else {
                transform.rotate(Vector3.Y, 45f * delta);
            }

            float[] updatedValues = transform.getValues();
            ((Buffer)offsets).position(targetIndex);
            offsets.put(updatedValues);
            mesh.updateInstanceData(targetIndex, updatedValues);
        }
    }

    private void checkUserInput() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)
                || (Gdx.app.getType() == Application.ApplicationType.Android && Gdx.input.isTouched())) {
            rotate = !rotate;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            showStats = !showStats;
        }
    }

    private void setupInstancedMesh() {
        mesh = new Mesh(true, 24, CUBE_INDEX_COUNT,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"),
                new VertexAttribute(VertexAttributes.Usage.TextureCoordinates, 2, "a_texCoords0"));

        float size = cubeSize;
        mesh.setVertices(new float[] {
                -size, size, -size, 0f, 1f,
                size, size, -size, 1f, 1f,
                size, -size, -size, 1f, 0f,
                -size, -size, -size, 0f, 0f,
                size, size, size, 1f, 1f,
                -size, size, size, 0f, 1f,
                -size, -size, size, 0f, 0f,
                size, -size, size, 1f, 0f,
                -size, size, size, 1f, 1f,
                -size, size, -size, 0f, 1f,
                -size, -size, -size, 0f, 0f,
                -size, -size, size, 1f, 0f,
                size, size, -size, 1f, 1f,
                size, size, size, 0f, 1f,
                size, -size, size, 0f, 0f,
                size, -size, -size, 1f, 0f,
                -size, size, size, 1f, 1f,
                size, size, size, 0f, 1f,
                size, size, -size, 0f, 0f,
                -size, size, -size, 1f, 0f,
                -size, -size, -size, 1f, 1f,
                size, -size, -size, 0f, 1f,
                size, -size, size, 0f, 0f,
                -size, -size, size, 1f, 0f
        });
        mesh.setIndices(new short[] {
                0, 1, 2, 2, 3, 0,
                4, 5, 6, 6, 7, 4,
                8, 9, 10, 10, 11, 8,
                12, 13, 14, 14, 15, 12,
                16, 17, 18, 18, 19, 16,
                20, 21, 22, 22, 23, 20
        });

        mesh.enableInstancedRendering(true, instanceCount,
                new VertexAttribute(VertexAttributes.Usage.Generic, 4, "i_worldTrans", 0),
                new VertexAttribute(VertexAttributes.Usage.Generic, 4, "i_worldTrans", 1),
                new VertexAttribute(VertexAttributes.Usage.Generic, 4, "i_worldTrans", 2),
                new VertexAttribute(VertexAttributes.Usage.Generic, 4, "i_worldTrans", 3));

        offsets = BufferUtils.newFloatBuffer(instanceCount * FLOATS_PER_MATRIX);
        createBoxField();
        ((Buffer)offsets).position(0);
        mesh.setInstanceData(offsets);

        renderable = new Renderable();
        renderable.meshPart.set("Cube", mesh, 0, CUBE_INDEX_COUNT, GL20.GL_TRIANGLES);
        renderable.environment = environment;
        renderable.worldTransform.idt();
        renderable.shader = createShader();
        renderable.shader.init();
    }

    private BaseShader createShader() {
        return new BaseShader() {
            @Override
            public void begin(Camera camera, RenderContext context) {
                program.bind();
                program.setUniformMatrix("u_projViewTrans", camera.combined);
                program.setUniformi("u_texture", 0);
                context.setDepthTest(GL20.GL_LEQUAL);
            }

            @Override
            public void init() {
                String previousVertexPrefix = ShaderProgram.prependVertexCode;
                String previousFragmentPrefix = ShaderProgram.prependFragmentCode;
                try {
                    ShaderProgram.prependVertexCode = "#version 300 es\n";
                    ShaderProgram.prependFragmentCode = "#version 300 es\n";
                    program = new ShaderProgram(
                            Gdx.files.internal("custom/modelinstance/shaders/instanced.vert"),
                            Gdx.files.internal("custom/modelinstance/shaders/instanced.frag"));
                }
                finally {
                    ShaderProgram.prependVertexCode = previousVertexPrefix;
                    ShaderProgram.prependFragmentCode = previousFragmentPrefix;
                }

                if(!program.isCompiled()) {
                    throw new GdxRuntimeException("Shader compile error: " + program.getLog());
                }
                init(program, renderable);
            }

            @Override
            public int compareTo(Shader other) {
                return 0;
            }

            @Override
            public boolean canRender(Renderable instance) {
                return true;
            }
        };
    }

    private void createBoxField() {
        texture = new Texture(Gdx.files.internal("custom/modelinstance/zebra.png"));
        int instanceCountSide = Math.round((float)Math.cbrt(instanceCount));

        for(int x = 1; x <= instanceCountSide; x++) {
            for(int y = 1; y <= instanceCountSide; y++) {
                for(int z = 1; z <= instanceCountSide; z++) {
                    position.set(
                            x / (instanceCountSide * 0.5f) - 1f,
                            y / (instanceCountSide * 0.5f) - 1f,
                            z / (instanceCountSide * 0.5f) - 1f);
                    rotation.setEulerAngles(MathUtils.random(-90, 90), MathUtils.random(-90, 90),
                            MathUtils.random(-90, 90));
                    transform.set(position, rotation);
                    offsets.put(transform.getValues());
                }
            }
        }
    }

    private void initialize() {
        Gdx.input.setCatchKey(Input.Keys.SPACE, true);
        Gdx.input.setCatchKey(Input.Keys.F1, true);

        environment = new Environment();
        environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.9f, 0.9f, 0.9f, 1f));

        camera = new PerspectiveCamera(45f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.near = 0.001f;
        camera.far = 2f;
        camera.position.set(0.1f, 0f, 0f);
        camera.direction.set(Vector3.Z);
        camera.up.set(Vector3.Y);
        camera.update();
        cameraFrustum = camera.frustum;

        modelBatch = new ModelBatch();
        spriteBatch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("custom/lsans-15.fnt"), false);
        font.setColor(Color.WHITE);
        font.getData().setScale(2f);

        int instanceCountSide;
        if(Gdx.app.getType() == Application.ApplicationType.Desktop) {
            instanceCountSide = DESKTOP_INSTANCE_COUNT_SIDE;
            cullingDistance = camera.far * 0.25f;
        }
        else {
            instanceCountSide = WEB_INSTANCE_COUNT_SIDE;
            cullingDistance = camera.far;
        }
        instanceCount = instanceCountSide * instanceCountSide * instanceCountSide;
        cubeSize = 1f / (float)Math.sqrt(instanceCount) * 0.95f;

        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(cubeSize * 16f);
        controller.setDegreesPerPixel(0.2f);
        Gdx.input.setInputProcessor(controller);

        profiler = new GLProfiler(Gdx.graphics);
        profiler.enable();
    }

    @Override
    public void resize(int width, int height) {
        if(width <= 0 || height <= 0 || camera == null) {
            return;
        }
        Gdx.gl.glViewport(0, 0, width, height);
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        spriteBatch.getProjectionMatrix().setToOrtho2D(0f, 0f, width, height);
    }

    @Override
    public void dispose() {
        Gdx.input.setInputProcessor(null);
        Gdx.input.setCatchKey(Input.Keys.SPACE, false);
        Gdx.input.setCatchKey(Input.Keys.F1, false);

        if(profiler != null) {
            profiler.disable();
        }
        if(renderable != null && renderable.shader != null) {
            renderable.shader.dispose();
        }
        if(texture != null) {
            texture.dispose();
        }
        if(mesh != null) {
            mesh.dispose();
        }
        if(modelBatch != null) {
            modelBatch.dispose();
        }
        if(spriteBatch != null) {
            spriteBatch.dispose();
        }
        if(font != null) {
            font.dispose();
        }
    }
}
