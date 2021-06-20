package com.github.xpenatan.gdx.html5.generator.view;

import com.badlogic.gdx.Preferences;
import com.github.xpenatan.gdx.html5.generator.viewmodel.GeneratorViewModel;
import com.github.xpenatan.imgui.ImGui;
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
    private static final String STR_BTN_COMPILE = "Compile";
    private static final String STR_CKB_OBFUSCATE = "Obfuscate:";
    private static final String STR_TXT_APP_CLASS = "Application Class:";
    private static final String STR_TXT_ASSET_PATH = "Asset Path:";
    private static final String STR_TXT_WEBAPP_PATH = "WebApp Path:";
    private static final String STR_TXT_GAME_PATH = "Game Jar Path:";
    private static final String STR_WINDOW_TITLE = "Generator";

    private Preferences preferences;

    private final GeneratorViewModel viewModel;

    public GeneratorView() {
        // TODO remove emulated class from Classloader
//        preferences = Gdx.app.getPreferences("gdx-html5-generator");

        viewModel = new GeneratorViewModel();


        loadPreference();
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

        if (ImGui.Button(STR_BTN_COMPILE)) {
            viewModel.compile();
        }

        renderServerView();

        if (compiling) {
            ImGui.PopItemFlag();
            ImGui.PopStyleVar();
        }

        ImGui.End();
    }

    private void renderServerView() {
        boolean serverRunning = viewModel.isServerRunning();
        String buttonText = serverRunning ? STR_SERVER_STOP : STR_SERVER_START;
        if (ImGui.Button(buttonText)) {
            if (serverRunning)
                viewModel.stopLocalServer();
            else
                viewModel.startLocalServer();
        }
    }

    public void dispose() {
        savePreference();
        viewModel.dispose();
    }
}
