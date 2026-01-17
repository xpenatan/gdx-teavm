package com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.profiling.GLErrorListener;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.debug.GLDebugProfiler;

public class HelloTriangle extends ApplicationAdapter {
    ShaderProgram shader;
    Mesh mesh;
    GLDebugProfiler glProfiler;

    @Override
    public void create() {
        glProfiler = new GLDebugProfiler(Gdx.graphics);
        glProfiler.setListener(new GLErrorListener() {
            @Override
            public void onError (int error) {
                System.out.println("GLProfiler: error: " + error);
            }
        });
        glProfiler.enable();

        glProfiler.reset();

        String vertexShader = "" +
                "attribute vec4 vPosition;    \n" +
                "void main()                  \n" +
                "{                            \n" +
                "   gl_Position = vPosition;  \n" +
                "}                            \n";

        String fragmentShader = "#ifdef GL_ES\n" +
                "precision mediump float;\n" +
                "#endif\n" +
                "void main()                                  \n" +
                "{                                            \n" +
                "  gl_FragColor = vec4 ( 1.0, 1.0, 1.0, 1.0 );\n" +
                "}";

        shader = new ShaderProgram(vertexShader, fragmentShader);
        if (!shader.isCompiled()) {
            System.out.println("Shader compilation failed: " + shader.getLog());
            throw new RuntimeException("Shader compilation failed");
        }
        mesh = new Mesh(true, 3, 0, new VertexAttribute(Usage.Position, 3, "vPosition"));
        float[] vertices = {0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f};
        mesh.setVertices(vertices);
    }

    @Override
    public void render() {
        glProfiler.reset();
        Gdx.gl20.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
        Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shader.bind();
        mesh.render(shader, GL20.GL_TRIANGLES);
    }
}
