package com.github.xpenatan.gdx.html5.generator.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.math.MathUtils;
import com.github.xpenatan.gdx.html5.generator.core.viewmodel.GeneratorViewModel;
import com.github.xpenatan.imgui.ImDrawList;
import com.github.xpenatan.imgui.ImGui;
import com.github.xpenatan.imgui.ImGuiStyle;
import com.github.xpenatan.imgui.ImVec2;
import com.github.xpenatan.imgui.enums.ImGuiCol;
import com.github.xpenatan.imgui.enums.ImGuiItemFlags;
import com.github.xpenatan.imgui.enums.ImGuiStyleVar;

public class GeneratorView {
    private static final String PREF_JAR_PATH = "jarPath";
    private static final String PREF_APP_CLASS_NAME = "appClass";
    private static final String PREF_ASSET_PATH = "assetPath";
    private static final String PREF_WEBAPP_PATH = "webAppPath";
    private static final String PREF_OBFUSCATE = "obfuscate";

    private static final String STR_SERVER_START = "Start Server";
    private static final String STR_SERVER_STOP = "Stop Server";
    private static final String STR_BTN_COMPILE = "Build";
    private static final String STR_CKB_OBFUSCATE = "Obfuscate:";
    private static final String STR_TXT_APP_CLASS = "Application Class:";
    private static final String STR_TXT_ASSET_PATH = "Asset Path:";
    private static final String STR_TXT_WEBAPP_PATH = "WebApp Path:";
    private static final String STR_TXT_GAME_PATH = "Game Jar Path:";
    private static final String STR_WINDOW_TITLE = "Generator";

    private static final int BTN_BUILD_WIDTH = 50;

    private Preferences preferences;

    private final GeneratorViewModel viewModel;

    private float progress;
    private int loadingColor;

    public GeneratorView() {
        // TODO remove emulated class from Classloader
//        preferences = Gdx.app.getPreferences("gdx-html5-generator");

        viewModel = new GeneratorViewModel();

        viewModel.gameJarPath.setValue("E:\\Dev\\Projects\\Eclipse\\Libgdx\\tests\\gdx-tests-lwjgl\\build\\libs\\gdx-tests-lwjgl-1.10.0-SNAPSHOT.jar");
        viewModel.appClassName.setValue("com.badlogic.gdx.tests.lwjgl.GwtTestWrapper");
        viewModel.assetsDirectory.setValue("E:\\Dev\\Projects\\Eclipse\\Libgdx\\tests\\gdx-tests-android\\assets");
        viewModel.webappDirectory.setValue("C:\\Users\\Natan\\Desktop\\libgdx examples\\tests");

        loadPreference();

        loadingColor = ImGui.ColorToIntBits(255, 255, 255, 255);
    }

    private void loadPreference() {
        if (preferences == null)
            return;
    }

    private void savePreference() {
        if (preferences == null)
            return;
        preferences.putString(PREF_JAR_PATH, viewModel.gameJarPath.getValue());
        preferences.putString(PREF_APP_CLASS_NAME, viewModel.appClassName.getValue());
        preferences.putString(PREF_ASSET_PATH, viewModel.assetsDirectory.getValue());
        preferences.putString(PREF_WEBAPP_PATH, viewModel.webappDirectory.getValue());
        preferences.putBoolean(PREF_OBFUSCATE, viewModel.obfuscateFlag.getValue());
    }

    public void render() {
        ImGui.Begin(STR_WINDOW_TITLE);
        renderContent();
        ImGui.End();
    }

    public void renderContent() {
        ImGui.Text(STR_TXT_GAME_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##Path", viewModel.gameJarPath);

        ImGui.Text(STR_TXT_APP_CLASS);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##ClassPath", viewModel.appClassName);

        ImGui.Text(STR_TXT_ASSET_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##AssetPath", viewModel.assetsDirectory);

        ImGui.Text(STR_TXT_WEBAPP_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##WebAppPath", viewModel.webappDirectory);

        ImGui.Text(STR_CKB_OBFUSCATE);
        ImGui.SameLine();
        ImGui.Checkbox("##obfuscate", viewModel.obfuscateFlag);

        boolean compiling = viewModel.isCompiling();
        if (compiling) {
            ImGui.PushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
        }

        float posX1 = ImGui.GetWindowDCCursorPosX();
        float posY1 = ImGui.GetWindowDCCursorPosY();

        if(compiling) {
            ImGui.Button("##COMPILE", BTN_BUILD_WIDTH, 0);
        }
        else {
            if (ImGui.Button(STR_BTN_COMPILE, BTN_BUILD_WIDTH, 0)) {
                viewModel.compile();
            }
        }

        if (compiling) {
            ImGui.PopItemFlag();
            ImGui.PopStyleVar();
        }
        ImGui.SameLine();

        if(compiling) {
            float gTime = (float) ImGui.GetContextTime();
            float posX = posX1 + BTN_BUILD_WIDTH / 2.5f ;
            float posY = posY1 + 1;
            SpinnerView.drawSpinner("test", 6, 2, loadingColor, gTime, posX, posY, false);

            ImGui.SameLine();
        }

        ImGui.PushStyleColor(ImGuiCol.PlotHistogram, loadingColor);

        ImGui.ProgressBar(viewModel.getProgress());

        ImGui.PopStyleColor();

        renderServerView();
    }

    private void renderServerView() {
        boolean compiling = viewModel.isCompiling();
        if (compiling) {
            ImGui.PushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
        }

        boolean serverRunning = viewModel.isServerRunning();
        String buttonText = serverRunning ? STR_SERVER_STOP : STR_SERVER_START;
        if (ImGui.Button(buttonText)) {
            if (serverRunning)
                viewModel.stopLocalServer();
            else
                viewModel.startLocalServer();
        }
        if (compiling) {
            ImGui.PopItemFlag();
            ImGui.PopStyleVar();
        }
    }

    public void dispose() {
        savePreference();
        viewModel.dispose();
    }


}
