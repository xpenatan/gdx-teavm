package com.github.xpenatan.imgui.example.tests.imgui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.HdpiUtils;
import com.badlogic.gdx.tests.AbstractTestWrapper;
import com.badlogic.gdx.tests.InputTest;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.tests.TeaVMGdxTests;
import com.badlogic.gdx.utils.ScreenUtils;
import com.github.xpenatan.gdx.multiview.EmuFrameBuffer;
import com.github.xpenatan.imgui.example.tests.frame.GameFrame;
import imgui.ImDrawData;
import imgui.ImGui;
import imgui.ImGuiConfigFlags;
import imgui.ImGuiIO;
import imgui.ImVec2;
import imgui.gdx.ImGuiGdxImpl;
import imgui.gdx.ImGuiGdxInputMultiplexer;

/**
 * Requires Gdx-test
 */
public class ImGuiTestsApp implements Screen {

    private GdxTest test;

    private ImGuiGdxImpl impl;
    private ImGuiGdxInputMultiplexer input;

    private boolean gdxTestInit = false;

    private int selected = -1;

    private TeaVMGdxTests.TeaVMInstancer[] testList;

    private boolean dispose = false;

    @Override
    public void show() {
        testList = TeaVMGdxTests.getTestList();
        ImGui.CreateContext();

        ImGuiIO io = ImGui.GetIO();
        io.ConfigFlags(ImGuiConfigFlags.ImGuiConfigFlags_DockingEnable);

        input = new ImGuiGdxInputMultiplexer();
        impl = new ImGuiGdxImpl();

        Gdx.input = new TeaVMInputWrapper(Gdx.input) {
            @Override
            public boolean keyUp (int keycode) {
                if (keycode == Keys.ESCAPE) {
                    if (test != null) {
                        Gdx.app.log("GdxTest", "Exiting current test.");
                        dispose = true;
                    }
                }
                return false;
            }

            @Override
            public boolean touchDown (int screenX, int screenY, int pointer, int button) {
                if (screenX < Gdx.graphics.getWidth() / 10.0 && screenY < Gdx.graphics.getHeight() / 10.0) {
                    if (test != null) {
                        dispose = true;
                    }
                }
                return false;
            }
        };
        ((TeaVMInputWrapper)Gdx.input).multiplexer.addProcessor(input);
    }

    private void drawTestListWindow() {
        if(!gdxTestInit) {
            gdxTestInit = true;
            ImGui.SetNextWindowSize(ImVec2.TMP_1.set(250, 500));
            ImGui.SetNextWindowPos(ImVec2.TMP_1.set(20, 20));
        }
        ImGui.Begin("GdxTests");
        if(ImGui.Button("Start Test")) {
            if(selected >= 0 && selected < testList.length) {
                ((TeaVMInputWrapper)Gdx.input).multiplexer.removeProcessor(input);
                test = testList[selected].instance();
                Gdx.app.log("GdxTest", "Clicked on " + test.getClass().getName());
                test.create();
                test.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }
        ImGui.Separator();
        ImGui.BeginChild("List", ImVec2.TMP_1.set(0, 0));
        for(int i = 0; i < testList.length; i++) {
            String testName = testList[i].getSimpleName();
            boolean isSelected = selected == i;
            if(ImGui.Selectable(testName, isSelected)) {
                if(selected != i) {
                    selected = i;
                }
            }
        }
        ImGui.EndChild();
        ImGui.End();
    }

    @Override
    public void render(float delta) {
        if (test == null) {
            Gdx.gl.glClearColor(0.5f, 0.5f, 0.5f, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            impl.update();

            drawTestListWindow();

            ImGui.Render();
            ImDrawData drawData = ImGui.GetDrawData();
            impl.render(drawData);
        }
        else {
            if (dispose) {
                test.pause();
                test.dispose();
                test = null;
                Gdx.graphics.setVSync(true);
                TeaVMInputWrapper wrapper = ((TeaVMInputWrapper)Gdx.input);
                wrapper.multiplexer.addProcessor(input);
                wrapper.multiplexer.removeProcessor(wrapper.lastProcessor);
                wrapper.lastProcessor = null;
                dispose = false;
                HdpiUtils.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            } else {
                test.render();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        if (test != null) {
            test.resize(width, height);
        }
        else {
            HdpiUtils.glViewport(0, 0, width, height);
        }
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
        ImGui.DestroyContext();
    }
}