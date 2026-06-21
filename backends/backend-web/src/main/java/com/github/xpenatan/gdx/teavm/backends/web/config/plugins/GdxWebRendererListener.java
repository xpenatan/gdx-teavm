package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetOutput;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.TeaLogHelper;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginAssetSupport;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import org.teavm.vm.BuildTarget;

public class GdxWebRendererListener {
    public enum TargetType {
        JAVASCRIPT,
        WASM_GC
    }

    private final GdxTeaVMPluginConfig config;
    private final ClassLoader classLoader;
    private final ArrayList<URL> classPathURLs;
    private final AssetsCopy.AssetPlan assetPlan;

    public GdxWebRendererListener(
            GdxTeaVMPluginConfig config,
            ClassLoader classLoader,
            ArrayList<URL> classPathURLs,
            AssetsCopy.AssetPlan assetPlan
    ) {
        this.config = config;
        this.classLoader = classLoader;
        this.classPathURLs = classPathURLs;
        this.assetPlan = assetPlan;
    }

    public void write(BuildTarget buildTarget, TargetType targetType, String outputName) throws IOException {
        writeWebApp(buildTarget, targetType, outputName);
        writeAssets(buildTarget);
    }

    private void writeWebApp(BuildTarget buildTarget, TargetType targetType, String outputName) throws IOException {
        String indexHtml = readString("webapp/index.html");
        String webXML = readString("webapp/WEB-INF/web.xml");
        String mode;
        String jsScript;

        if(targetType == TargetType.WASM_GC) {
            mode = "let teavm = await TeaVM.wasmGC.load(\"" + outputName + "\"); "
                    + "teavm.exports." + config.entryPointName + "([" + config.mainClassArgs + "]);";
            jsScript = scriptTag(outputName + "-runtime.js");
        }
        else {
            mode = config.entryPointName + "(" + config.mainClassArgs + ")";
            jsScript = scriptTag(outputName);
        }

        indexHtml = indexHtml.replace("%MODE%", mode);
        indexHtml = indexHtml.replace("%JS_SCRIPT%", jsScript);
        indexHtml = indexHtml.replace("%TITLE%", config.htmlTitle);
        indexHtml = indexHtml.replace("%WIDTH%", String.valueOf(config.htmlWidth));
        indexHtml = indexHtml.replace("%HEIGHT%", String.valueOf(config.htmlHeight));
        indexHtml = indexHtml.replace("%ARGS%", config.mainClassArgs);

        writeString(buildTarget, "index.html", indexHtml);
        writeString(buildTarget, "WEB-INF/web.xml", webXML);
    }

    private void writeAssets(BuildTarget buildTarget) throws IOException {
        AssetOutput output = AssetOutput.buildTarget(buildTarget);
        TeaLogHelper.logHeader("COPYING ASSETS");
        if(config.copyLoadingAsset) {
            AssetsCopy.copyClasspathResources(classLoader, Collections.singletonList(config.logoPath),
                    null, output, "assets");
        }

        AssetsCopy.AssetPlan plan = assetPlan != null
                ? assetPlan
                : GdxTeaVMPluginAssetSupport.createWebAssetPlan(config, classLoader, classPathURLs);
        AssetsCopy.copyPlanAssets(classLoader, plan, output, "assets");
        AssetsCopy.copyClasspathResources(classLoader, plan.scripts, null, output, "scripts");
    }

    private String scriptTag(String fileName) {
        return "<script type=\"text/javascript\" charset=\"utf-8\" src=\"" + fileName + "\"></script>";
    }

    private String readString(String resource) throws IOException {
        try(InputStream input = classLoader.getResourceAsStream(resource)) {
            if(input == null) {
                return "";
            }
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void writeString(BuildTarget buildTarget, String resource, String value) throws IOException {
        try(OutputStream output = buildTarget.createResource(resource)) {
            output.write(value.getBytes(StandardCharsets.UTF_8));
        }
    }
}