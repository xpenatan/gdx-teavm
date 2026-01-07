package com.github.xpenatan.gdx.backends.teavm.config;

import com.badlogic.gdx.files.FileHandle;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import org.teavm.tooling.TeaVMTargetType;

@Deprecated
public class DefaultWebApp extends BaseWebApp {

    @Override
    public void setup(TeaClassLoader classLoader, TeaBuildConfiguration config, FileHandle webappFolder) {
        InputStream indexSteam = classLoader.getResourceAsStream("webapp/index.html");
        InputStream webXMLStream = classLoader.getResourceAsStream("webapp/WEB-INF/web.xml");

        String webXML = "";
        String indexHtml = "";

        if(indexSteam != null) {
            try {
                byte[] bytes = indexSteam.readAllBytes();
                indexHtml = new String(bytes);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        if(webXMLStream != null) {
            try {
                byte[] bytes = webXMLStream.readAllBytes();
                webXML = new String(bytes);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        String logo = config.logoPath;

        String mode = "main(%ARGS%)";
        String jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\""  + config.targetFileName + ".js\"></script>";
        if(config.targetType == TeaVMTargetType.WEBASSEMBLY_GC) {
            mode = "let teavm = await TeaVM.wasmGC.load(\"" + config.targetFileName + ".wasm\"); teavm.exports.main([%ARGS%]);";
            String jsName = "wasm-gc-runtime.min.js";
            jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"" + jsName + "\"></script>";
        }

        indexHtml = indexHtml.replace("%MODE%", mode);
        indexHtml = indexHtml.replace("%JS_SCRIPT%", jsScript);
        indexHtml = indexHtml.replace("%TITLE%", config.htmlTitle);
        indexHtml = indexHtml.replace("%WIDTH%", String.valueOf(config.htmlWidth));
        indexHtml = indexHtml.replace("%HEIGHT%", String.valueOf(config.htmlHeight));
        indexHtml = indexHtml.replace("%ARGS%", config.mainClassArgs);

        ArrayList<String> rootAssets = new ArrayList<>();

        if(config.showLoadingLogo) {
            rootAssets.add(logo);
        }

        FileHandle assetsFolder = webappFolder.child("assets");
        FileHandle indexHandler = webappFolder.child("index.html");
        FileHandle webXMLFile = webappFolder.child("WEB-INF").child("web.xml");
        indexHandler.writeString(indexHtml, false);
        webXMLFile.writeString(webXML, false);
        AssetsCopy.copyResources(classLoader, rootAssets, null, assetsFolder);
    }
}