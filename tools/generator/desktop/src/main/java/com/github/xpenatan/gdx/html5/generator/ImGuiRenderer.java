package com.github.xpenatan.gdx.html5.generator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.gdx.ImGuiGdxImpl;
import imgui.gdx.ImGuiGdxInputMultiplexer;

public abstract class ImGuiRenderer extends ScreenAdapter {

    private ImGuiGdxImpl impl;

    protected ImGuiGdxInputMultiplexer input;

    @Override
    public void show() {
        super.show();
        ImGui.CreateContext(false);
//        ImGuiIO io = ImGui.GetIO();
//        if(Gdx.app.getType() == Application.ApplicationType.WebGL) {
//            // Not possible to have ini filename with webgl
//            ImGui.GetIO().setIniFilename(null);
//        }

//        io.SetConfigFlags(ImGuiConfigFlags.DockingEnable);

        input = new ImGuiGdxInputMultiplexer();
        impl = new ImGuiGdxImpl();
        Gdx.input.setInputProcessor(input);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        impl.update();

        renderImGui();

        ImGui.Render();
        ImDrawData drawData = ImGui.GetDrawData();
        impl.render(drawData);
    }

    public abstract void renderImGui();

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        ImGui.disposeStatic();
    }

}
