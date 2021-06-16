package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

public class HelloTriangle implements ApplicationListener {
	ShaderProgram shader;
	Mesh mesh;

	@Override
	public void create () {
		String vertexShader = "attribute vec4 vPosition;    \n" + "void main()                  \n"
			+ "{                            \n" + "   gl_Position = vPosition;  \n" + "}                            \n";
		String fragmentShader = "#ifdef GL_ES\n" + "precision mediump float;\n" + "#endif\n"
			+ "void main()                                  \n" + "{                                            \n"
			+ "  gl_FragColor = vec4 ( 1.0, 1.0, 1.0, 1.0 );\n" + "}";

		shader = new ShaderProgram(vertexShader, fragmentShader);
		mesh = new Mesh(true, 3, 0, new VertexAttribute(Usage.Position, 3, "vPosition"));
		float[] vertices = {0.0f, 0.5f, 0.0f, -0.5f, -0.5f, 0.0f, 0.5f, -0.5f, 0.0f};
		mesh.setVertices(vertices);
	}

	@Override
	public void render () {
		Gdx.gl20.glViewport(0, 0, Gdx.graphics.getBackBufferWidth(), Gdx.graphics.getBackBufferHeight());
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl20.glClear(GL20.GL_COLOR_BUFFER_BIT);
		shader.begin();
		mesh.render(shader, GL20.GL_TRIANGLES);
		shader.end();
	}

	@Override
	public void resize (int width, int height) {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public void dispose () {
	}
}
