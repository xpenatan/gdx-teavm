package com.github.xpenatan.teavm.generator.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Color;
import com.github.xpenatan.teavm.generator.core.viewmodel.GeneratorViewModel;
import imgui.ImGui;
import imgui.ImGuiBoolean;
import imgui.ImGuiCol;
import imgui.ImGuiInternal;
import imgui.ImGuiItemFlags;
import imgui.ImGuiString;
import imgui.ImGuiStyleVar;
import imgui.ImGuiWindowFlags;
import imgui.ImVec2;

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
        loadingColor = Color.toIntBits(255, 255, 255, 255);
        errorColor = Color.toIntBits(255, 0, 0, 255);
        successColor = Color.toIntBits(0, 255, 0, 255);

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
        int flags = ImGuiWindowFlags.ImGuiWindowFlags_NoDecoration | ImGuiWindowFlags.ImGuiWindowFlags_NoMove | ImGuiWindowFlags.ImGuiWindowFlags_NoResize;
        ImGui.SetNextWindowPos(ImVec2.TMP_1.set(0, 0));
        ImGui.SetNextWindowSize(ImVec2.TMP_1.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
        ImGui.Begin(STR_WINDOW_TITLE, null, flags);
        renderContent();
        ImGui.End();
    }

    public void renderContent() {
        ImGui.Text(STR_TXT_GAME_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##Path", gameJarPath, gameJarPath.getSize());

        ImGui.Text(STR_TXT_APP_CLASS);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##ClassPath", appClassName, appClassName.getSize());

        ImGui.Text(STR_TXT_ASSET_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##AssetPath", assetsDirectory, assetsDirectory.getSize());

        ImGui.Text(STR_TXT_WEBAPP_PATH);
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        ImGui.InputText("##WebAppPath", webappDirectory, webappDirectory.getSize());

        ImGui.Text(STR_CKB_OBFUSCATE);
        ImGui.SameLine();
        ImGui.Checkbox("##obfuscate", obfuscateFlag);

        boolean compiling = viewModel.isCompiling();
        if(compiling) {
            ImGuiInternal.PushItemFlag(ImGuiItemFlags.ImGuiItemFlags_Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_Alpha, 0.5f);
        }

        ImVec2 cursorPos = ImGuiInternal.GetCurrentWindow().get_DC().get_CursorPos();
        float posX1 = cursorPos.get_x();
        float posY1 = cursorPos.get_y();

        if(compiling) {
            ImGui.Button("##COMPILE", ImVec2.TMP_1.set(BTN_BUILD_WIDTH, 0));
        }
        else {
            if(ImGui.Button(STR_BTN_COMPILE, ImVec2.TMP_1.set(BTN_BUILD_WIDTH, 0))) {
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
            ImGuiInternal.PopItemFlag();
            ImGui.PopStyleVar();
        }
        ImGui.SameLine();

        if(compiling) {
            float gTime = (float)ImGui.GetCurrentContext().get_Time();
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

        ImGui.PushStyleColor(ImGuiCol.ImGuiCol_PlotHistogram, progressColor);

        ImGui.ProgressBar(progress);

        ImGui.PopStyleColor();

        renderServerView();
    }

    private void renderServerView() {
        boolean compiling = viewModel.isCompiling();
        if(compiling) {
            ImGuiInternal.PushItemFlag(ImGuiItemFlags.ImGuiItemFlags_Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.ImGuiStyleVar_Alpha, 0.5f);
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
            ImGuiInternal.PopItemFlag();
            ImGui.PopStyleVar();
        }
    }

    public void dispose() {
        savePreference();
        viewModel.dispose();
    }
}
