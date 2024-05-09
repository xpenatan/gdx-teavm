package com.github.xpenatan.imgui.example.tests.imgui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.tests.InputTest;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.tests.TeaVMGdxTests;
import com.github.xpenatan.gdx.multiview.EmuFrameBuffer;
import com.github.xpenatan.imgui.example.tests.frame.GameFrame;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.gdx.ImGuiGdxImpl;
import imgui.gdx.ImGuiGdxInputMultiplexer;

/**
 * Requires Gdx-test
 */
public class ImGuiTestsApp implements Screen {

    ImGuiGdxImpl impl;

    boolean gdxTestInit = false;

    int selected = -1;

    private GameFrame gameFrame;

    SpriteBatch batch;
    private OrthographicCamera camera;

    TeaVMGdxTests.TeaVMInstancer[] testList;

    @Override
    public void show() {
        ImGui.CreateContext(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(true);
        batch = new SpriteBatch();

        ImGuiIO io = ImGui.GetIO();

//        io.setIniFilename(null);
//        io.SetConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.SetDockingFlags(false, false, false, false);

        impl = new ImGuiGdxImpl();

        EmuFrameBuffer.setDefaultFramebufferHandleInitialized(false);

        gameFrame = new GameFrame(20, 20, 800, 400);

        testList = TeaVMGdxTests.getTestList();

        gameFrame.emuWindow.setApplicationListener(new InputTest());

        ImGuiGdxInputMultiplexer multiplexer = new ImGuiGdxInputMultiplexer();
        multiplexer.addProcessor(gameFrame.emuWindow.getEmuInput());
        Gdx.input.setInputProcessor(multiplexer);
    }

    private void drawTestListWindow() {
        if(!gdxTestInit) {
            gdxTestInit = true;
            ImGui.SetNextWindowSize(ImVec2.TMP_1.set(200, 500));
            ImGui.SetNextWindowPos(ImVec2.TMP_1.set(900, 20));
        }
        ImGui.Begin("GdxTests");
        ImGui.BeginChildFrame(313, ImVec2.TMP_1.set(0f, 0f));
        for(int i = 0; i < testList.length; i++) {
            String testName = testList[i].getSimpleName();
            boolean isSelected = selected == i;
            if(ImGui.Selectable(testName, isSelected)) {
                if(selected != i) {
                    selected = i;
                    GdxTest newTest = testList[i].instance();
                    gameFrame.emuWindow.setApplicationListener(newTest);
                }
            }
        }
        ImGui.EndChildFrame();
        ImGui.End();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        gameFrame.update();

        impl.update();
        drawTestListWindow();
        ImGui.Render();
        ImDrawData drawData = ImGui.GetDrawData();
        impl.render(drawData);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        gameFrame.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
//        gameFrame.windowX = 0;
//        gameFrame.windowY = 0;
//        gameFrame.windowWidth = width;
//        gameFrame.windowHeight = height;
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        impl.dispose();
        ImGui.disposeStatic();
        batch.dispose();
    }
}
