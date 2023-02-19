package com.github.xpenatan.teavm.generator.core.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.github.xpenatan.imgui.core.ImGui;
import com.github.xpenatan.imgui.core.ImGuiBoolean;
import com.github.xpenatan.imgui.core.ImGuiString;
import com.github.xpenatan.imgui.core.enums.ImGuiCol;
import com.github.xpenatan.imgui.core.enums.ImGuiItemFlags;
import com.github.xpenatan.imgui.core.enums.ImGuiStyleVar;
import com.github.xpenatan.imgui.core.enums.ImGuiWindowFlags;
import com.github.xpenatan.teavm.generator.core.viewmodel.GeneratorViewModel;

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
    private int errorColor;
    private int successColor;

    private final ImGuiString gameJarPath;
    private final ImGuiString appClassName;
    private final ImGuiString assetsDirectory;
    private final ImGuiString webappDirectory;
    private final ImGuiBoolean obfuscateFlag;

    public GeneratorView() {
        preferences = Gdx.app.getPreferences("gdx-html5-generator");

        viewModel = new GeneratorViewModel();

        loadingColor = ImGui.ColorToIntBits(255, 255, 255, 255);
        errorColor = ImGui.ColorToIntBits(255, 0, 0, 255);
        successColor = ImGui.ColorToIntBits(0, 255, 0, 255);

        gameJarPath = new ImGuiString();
        appClassName = new ImGuiString();
        assetsDirectory = new ImGuiString();
        webappDirectory = new ImGuiString();
        obfuscateFlag = new ImGuiBoolean();

        loadPreference();
    }

    private void loadPreference() {
        if(preferences == null)
            return;
        gameJarPath.setValue(preferences.getString(PREF_JAR_PATH, ""));
        appClassName.setValue(preferences.getString(PREF_APP_CLASS_NAME, ""));
        assetsDirectory.setValue(preferences.getString(PREF_ASSET_PATH, ""));
        webappDirectory.setValue(preferences.getString(PREF_WEBAPP_PATH, ""));
        obfuscateFlag.setValue(preferences.getBoolean(PREF_JAR_PATH, false));
    }

    private void savePreference() {
        if(preferences == null)
            return;
        preferences.putString(PREF_JAR_PATH, gameJarPath.getValue());
        preferences.putString(PREF_APP_CLASS_NAME, appClassName.getValue());
        preferences.putString(PREF_ASSET_PATH, assetsDirectory.getValue());
        preferences.putString(PREF_WEBAPP_PATH, webappDirectory.getValue());
        preferences.putBoolean(PREF_OBFUSCATE, obfuscateFlag.getValue());
        preferences.flush();
    }

    public void render() {
        ImGuiWindowFlags flags = ImGuiWindowFlags.NoDecoration.or(ImGuiWindowFlags.NoMove).or(ImGuiWindowFlags.NoResize);
        ImGui.SetNextWindowPos(0, 0);
        ImGui.SetNextWindowSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        ImGui.Begin(STR_WINDOW_TITLE, flags);
        renderContent();
        ImGui.End();
    }

    public void renderContent() {
        ImGui.Text(STR_TXT_GAME_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##Path", gameJarPath);

        ImGui.Text(STR_TXT_APP_CLASS);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##ClassPath", appClassName);

        ImGui.Text(STR_TXT_ASSET_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##AssetPath", assetsDirectory);

        ImGui.Text(STR_TXT_WEBAPP_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##WebAppPath", webappDirectory);

        ImGui.Text(STR_CKB_OBFUSCATE);
        ImGui.SameLine();
        ImGui.Checkbox("##obfuscate", obfuscateFlag);

        boolean compiling = viewModel.isCompiling();
        if(compiling) {
            ImGui.PushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
        }

        float posX1 = ImGui.GetWindowDCCursorPosX();
        float posY1 = ImGui.GetWindowDCCursorPosY();

        if(compiling) {
            ImGui.Button("##COMPILE", BTN_BUILD_WIDTH, 0);
        }
        else {
            if(ImGui.Button(STR_BTN_COMPILE, BTN_BUILD_WIDTH, 0)) {
                viewModel.compile(
                        gameJarPath.getValue(),
                        appClassName.getValue(),
                        assetsDirectory.getValue(),
                        webappDirectory.getValue(),
                        obfuscateFlag.getValue()
                );
            }
        }

        if(compiling) {
            ImGui.PopItemFlag();
            ImGui.PopStyleVar();
        }
        ImGui.SameLine();

        if(compiling) {
            float gTime = (float)ImGui.GetContextTime();
            float posX = posX1 + BTN_BUILD_WIDTH / 2.5f;
            float posY = posY1 + 1;
            SpinnerView.drawSpinner("test", 6, 2, loadingColor, gTime, posX, posY, false);

            ImGui.SameLine();
        }
        float progress = viewModel.getProgress();
        int progressColor = loadingColor;
        if(viewModel.getError()) {
            progressColor = errorColor;
        }
        else if(progress == 1.0f) {
            progressColor = successColor;
        }

        ImGui.PushStyleColor(ImGuiCol.PlotHistogram, progressColor);

        ImGui.ProgressBar(progress);

        ImGui.PopStyleColor();

        renderServerView();
    }

    private void renderServerView() {
        boolean compiling = viewModel.isCompiling();
        if(compiling) {
            ImGui.PushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.Alpha, 0.5f);
        }

        boolean serverRunning = viewModel.isServerRunning();
        String buttonText = serverRunning ? STR_SERVER_STOP : STR_SERVER_START;
        if(ImGui.Button(buttonText)) {
            if(serverRunning)
                viewModel.stopLocalServer();
            else
                viewModel.startLocalServer(webappDirectory.getValue());
        }
        if(compiling) {
            ImGui.PopItemFlag();
            ImGui.PopStyleVar();
        }
    }

    public void dispose() {
        savePreference();
        viewModel.dispose();
    }
}
