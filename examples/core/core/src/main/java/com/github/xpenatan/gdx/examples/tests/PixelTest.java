package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.ScreenUtils;

public class PixelTest implements ApplicationListener {

    private static final String VERTEX = "" +
            "attribute vec4 a_position; \n" +
            "uniform mat4 u_projTrans; \n" +

            "void main() { \n" +
            "    gl_Position = u_projTrans * a_position; \n" +
            "} ";

    private static final String FRAGMENT = "" +
            "#ifdef GL_ES \n" +
            "precision mediump float; \n" +
            "#endif \n" +

            "uniform vec4 u_color;  \n" +

            "void main() { \n" +
            "    gl_FragColor = u_color; \n" +
            "}";

    ShaderProgram shader;
    Mesh pixelMesh;

    OrthographicCamera camera;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        this.shader = new ShaderProgram(VERTEX, FRAGMENT);
        if(!shader.isCompiled()) {
            System.out.println("ERRORR");
        }
        this.pixelMesh = new Mesh(true, 1, 0,
                new VertexAttribute(VertexAttributes.Usage.Position, 2, "a_position"));
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        int x = 100;
        int y = 200;

        ScreenUtils.clear(0, 0, 0, 1);
        shader.bind();
        shader.setUniformMatrix("u_projTrans", camera.combined);
        Gdx.gl.glEnable(GL30.GL_BLEND);
        shader.setUniformf("u_color", 0, 1, 0, 1);
        pixelMesh.setVertices(new float[]{x,y});
        pixelMesh.render(shader, GL30.GL_POINTS);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {

    }
}
