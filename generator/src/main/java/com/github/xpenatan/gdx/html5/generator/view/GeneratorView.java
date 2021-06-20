package com.github.xpenatan.gdx.html5.generator.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.github.xpenatan.gdx.html5.generator.viewmodel.GeneratorViewModel;
import com.github.xpenatan.imgui.ImGui;
import com.github.xpenatan.imgui.ImGuiBoolean;
import com.github.xpenatan.imgui.ImGuiString;
import com.github.xpenatan.imgui.enums.ImGuiItemFlags;
import com.github.xpenatan.imgui.enums.ImGuiStyleVar;
import com.github.xpenatan.imgui.jnicode.ImGuiInternalNative;

public class GeneratorView {
    private static final String PREF_JAR_PATH = "jarPath";
    private static final String PREF_APP_CLASS_NAME = "appClass";
    private static final String PREF_ASSET_PATH = "assetPath";
    private static final String PREF_WEBAPP_PATH = "webAppPath";
    private static final String PREF_OBFUSCATE = "obfuscate";

    public static final String SERVER_START = "Start Server";
    public static final String SERVER_STOP = "Stop Server";

    private Preferences preferences;
    private final ImGuiString imGuiJarPathString;
    private final ImGuiString imGuiClassPathString;
    private final ImGuiString imGuiAssetPathString;
    private final ImGuiString imGuiWebappPathString;
    private final ImGuiBoolean obfuscateFlag;

    private final GeneratorViewModel viewModel;

    public GeneratorView() {
        //TODO remove emulated class from Classloader
//        preferences = Gdx.app.getPreferences("gdx-html5-generator");
        viewModel = new GeneratorViewModel();
        imGuiJarPathString = new ImGuiString("");
        imGuiClassPathString = new ImGuiString("");
        imGuiAssetPathString = new ImGuiString("");
        imGuiWebappPathString = new ImGuiString("");
        obfuscateFlag = new ImGuiBoolean();

        loadPreference();
    }

    private void loadPreference() {
        if(preferences == null)
            return;
        imGuiJarPathString.setValue(preferences.getString(PREF_JAR_PATH, ""));
        imGuiClassPathString.setValue(preferences.getString(PREF_APP_CLASS_NAME, ""));
        imGuiAssetPathString.setValue(preferences.getString(PREF_ASSET_PATH, ""));
        imGuiWebappPathString.setValue(preferences.getString(PREF_WEBAPP_PATH, ""));
        obfuscateFlag.setValue(preferences.getBoolean(PREF_OBFUSCATE, false));
    }

    private void savePreference() {
        if(preferences == null)
            return;

        preferences.putString(PREF_JAR_PATH, imGuiJarPathString.getValue());
        preferences.putString(PREF_APP_CLASS_NAME, imGuiClassPathString.getValue());
        preferences.putString(PREF_ASSET_PATH, imGuiAssetPathString.getValue());
        preferences.putString(PREF_WEBAPP_PATH, imGuiWebappPathString.getValue());
        preferences.putBoolean(PREF_OBFUSCATE, obfuscateFlag.getValue());
    }

    public void render() {
        ImGui.Begin("Generator");

        ImGui.Text("Game Jar Path:");
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        if(ImGui.InputText("##Path: ", imGuiJarPathString)) {
            String gameJarPath = imGuiJarPathString.getValue();
            viewModel.setJarPath(gameJarPath);
        }

        ImGui.Text("Application Class:");
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        if(ImGui.InputText("##ClassPath: ", imGuiClassPathString)) {
            String applicationClass = imGuiClassPathString.getValue();
            viewModel.setApplicationClass(applicationClass);
        }

        ImGui.Text("Asset Path:");
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        if(ImGui.InputText("##AssetPath: ", imGuiAssetPathString)) {
            String assetPath = imGuiAssetPathString.getValue();
            viewModel.setAssetPath(assetPath);
        }

        ImGui.Text("WebApp Path:");
        ImGui.SameLine();
        ImGui.SetNextItemWidth(-1);
        if(ImGui.InputText("##WebAppPath: ", imGuiWebappPathString)) {
            String webappPath = imGuiWebappPathString.getValue();
            viewModel.setWebAppDirectory(webappPath);
        }

        ImGui.Text("Obfuscate:");
        ImGui.SameLine();
        if(ImGui.Checkbox("##obfuscate", obfuscateFlag)) {
            viewModel.setObfuscate(obfuscateFlag.getValue());
        }

        boolean compiling = viewModel.isCompiling();
        if(compiling) {
            ImGui.PushItemFlag(ImGuiItemFlags.Disabled, true);
            ImGui.PushStyleVar(ImGuiStyleVar.Alpha,0.5f);
        }
        if(ImGui.Button("Compile")) {
            viewModel.compile();
        }

        boolean serverRunning = viewModel.isServerRunning();

        String buttonText = serverRunning ? SERVER_STOP : SERVER_START;

        if(ImGui.Button(buttonText)) {
            if(serverRunning)
                viewModel.stopLocalServer();
            else
                viewModel.startLocalServer();
        }
        if(compiling) {
            ImGui.PopItemFlag();
            ImGui.PopStyleVar();
        }

        ImGui.End();
    }

    public void dispose() {
        savePreference();
        viewModel.dispose();
    }
}
