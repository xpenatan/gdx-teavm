package com.github.xpenatan.teavm.generator.core.view;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.github.xpenatan.imgui.core.ImGui;
import com.github.xpenatan.imgui.gdx.ImGuiGdxImpl;
import com.github.xpenatan.imgui.gdx.ImGuiGdxInput;

public class MainApplication implements ApplicationListener {
    ImGuiGdxImpl impl;

    private GeneratorView window;

    @Override
    public void create() {
        ImGui.init();
        impl = new ImGuiGdxImpl();

        ImGuiGdxInput input = new ImGuiGdxInput();
        Gdx.input.setInputProcessor(input);

        window = new GeneratorView();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        impl.update();
        renderImGuiUI();
        ImGui.Render();
        impl.render(ImGui.GetDrawData());
    }

    private void renderImGuiUI() {
        window.render();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        impl.dispose();
        ImGui.dispose();
        window.dispose();
    }
}
