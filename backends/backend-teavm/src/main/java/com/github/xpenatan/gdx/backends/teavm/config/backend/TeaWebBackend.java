package com.github.xpenatan.gdx.backends.teavm.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.config.AssetsCopy;
import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaCompilerData;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import org.teavm.tooling.TeaVMTargetType;

abstract class TeaWebBackend extends TeaBackend {
    public String logoPath = "startup-logo.png";
    public String htmlTitle = "app";
    public String mainClassArgs = "";
    public int htmlWidth = 800;
    public int htmlHeight = 600;
    public boolean showLoadingLogo = true;
    public String webappFolderName = "webapp";
    public boolean useDefaultHtmlIndex;

    @Override
    protected void setup(TeaCompilerData data) {
        releasePath = new File(data.output, "webapp");
        tool.setTargetDirectory(releasePath);
        setupWebapp(data);
    }

    protected void setupWebapp(TeaCompilerData data) {
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
        String logo = logoPath;

        String mode = "main(%ARGS%)";
        String jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\""  + data.outputName + ".js\"></script>";
        if(targetType == TeaVMTargetType.WEBASSEMBLY_GC) {
            mode = "let teavm = await TeaVM.wasmGC.load(\"" + data.outputName + ".wasm\"); teavm.exports.main([%ARGS%]);";
            String jsName = "wasm-gc-runtime.min.js";
            jsScript = "<script type=\"text/javascript\" charset=\"utf-8\" src=\"" + jsName + "\"></script>";

            copyRuntime(releasePath);
        }

        indexHtml = indexHtml.replace("%MODE%", mode);
        indexHtml = indexHtml.replace("%JS_SCRIPT%", jsScript);
        indexHtml = indexHtml.replace("%TITLE%", htmlTitle);
        indexHtml = indexHtml.replace("%WIDTH%", String.valueOf(htmlWidth));
        indexHtml = indexHtml.replace("%HEIGHT%", String.valueOf(htmlHeight));
        indexHtml = indexHtml.replace("%ARGS%", mainClassArgs);

        ArrayList<String> rootAssets = new ArrayList<>();

        if(showLoadingLogo) {
            rootAssets.add(logo);
        }

        FileHandle webappFolder = new FileHandle(releasePath);
        FileHandle assetsFolder = webappFolder.child(ASSETS_FOLDER_NAME);
        FileHandle indexHandler = webappFolder.child("index.html");
        FileHandle webXMLFile = webappFolder.child("WEB-INF").child("web.xml");
        indexHandler.writeString(indexHtml, false);
        webXMLFile.writeString(webXML, false);
        AssetsCopy.copyResources(classLoader, rootAssets, null, assetsFolder);
    }

    public static void copyRuntime(File setTargetDirectory) {
        try {
            var name = new StringBuilder("wasm-gc-runtime.min");
            setTargetDirectory.mkdirs();
            var resourceName = "org/teavm/backend/wasm/" + name + ".js";
            var classLoader = TeaBuilder.class.getClassLoader();
            try (var input = classLoader.getResourceAsStream(resourceName)) {
                Files.copy(input, setTargetDirectory.toPath().resolve(name + ".js"), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch(Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
