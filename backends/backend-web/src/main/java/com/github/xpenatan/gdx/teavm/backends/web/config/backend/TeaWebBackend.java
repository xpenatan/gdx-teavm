package com.github.xpenatan.gdx.teavm.backends.web.config.backend;

import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFilter;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.web.config.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompilerData;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import org.teavm.tooling.TeaVMTargetType;

public class TeaWebBackend extends TeaBackend {
    public String logoPath = "startup-logo.png";
    public String htmlTitle = "gdx-teavm";
    public String mainClassArgs = "";
    public int htmlWidth = 800;
    public int htmlHeight = 600;
    public boolean copyLoadingAsset = true;
    public String webappFolderName = "webapp";
    public boolean isWebAssembly;
    public AssetFilter scriptFilter;
    public boolean startJettyAfterBuild = false;

    private JettyServer server;

    public TeaWebBackend setWebAssembly(boolean isWebAssembly) {
        this.isWebAssembly = isWebAssembly;
        return this;
    }

    public TeaWebBackend setLogoPath(String logoPath) {
        this.logoPath = logoPath;
        return this;
    }

    public TeaWebBackend setHtmlTitle(String htmlTitle) {
        this.htmlTitle = htmlTitle;
        return this;
    }

    public TeaWebBackend setMainClassArgs(String mainClassArgs) {
        this.mainClassArgs = mainClassArgs;
        return this;
    }

    public TeaWebBackend setHtmlWidth(int htmlWidth) {
        this.htmlWidth = htmlWidth;
        return this;
    }

    public TeaWebBackend setHtmlHeight(int htmlHeight) {
        this.htmlHeight = htmlHeight;
        return this;
    }

    public TeaWebBackend setCopyLoadingAsset(boolean copyLoadingAsset) {
        this.copyLoadingAsset = copyLoadingAsset;
        return this;
    }

    public TeaWebBackend setWebappFolderName(String webappFolderName) {
        this.webappFolderName = webappFolderName;
        return this;
    }

    public TeaWebBackend setScriptFilter(AssetFilter scriptFilter) {
        this.scriptFilter = scriptFilter;
        return this;
    }

    public TeaWebBackend setStartJettyAfterBuild(boolean startJettyAfterBuild) {
        this.startJettyAfterBuild = startJettyAfterBuild;
        return this;
    }

    public void startJetty(String path) {
        server.stopServer();
        server.startServer(path);
    }

    @Override
    protected void setup(TeaCompilerData data) {
        server = new JettyServer();

        if(isWebAssembly) {
            targetType = TeaVMTargetType.WEBASSEMBLY_GC;
            tool.setTargetFileName(data.outputName + ".wasm");
        } else {
            targetType = TeaVMTargetType.JAVASCRIPT;
            tool.setTargetFileName(data.outputName + ".js");
        }

        if(data.releasePath != null) {
            releasePath = new FileHandle(data.releasePath.getAbsolutePath().replace("\\", "/"));
        }
        else {
            releasePath = new FileHandle(new File(data.output, webappFolderName).getAbsolutePath().replace("\\", "/"));
        }
        tool.setTargetDirectory(releasePath.file());
        setupWebapp(data);
    }

    @Override
    protected void build(TeaCompilerData data) {
        super.build(data);
        if(startJettyAfterBuild) {
            startJetty(data.output.getPath());
        }
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

            copyRuntime(releasePath.file());
        }

        indexHtml = indexHtml.replace("%MODE%", mode);
        indexHtml = indexHtml.replace("%JS_SCRIPT%", jsScript);
        indexHtml = indexHtml.replace("%TITLE%", htmlTitle);
        indexHtml = indexHtml.replace("%WIDTH%", String.valueOf(htmlWidth));
        indexHtml = indexHtml.replace("%HEIGHT%", String.valueOf(htmlHeight));
        indexHtml = indexHtml.replace("%ARGS%", mainClassArgs);

        ArrayList<String> rootAssets = new ArrayList<>();

        if(copyLoadingAsset) {
            rootAssets.add(logo);
        }

        FileHandle assetsFolder = releasePath.child(ASSETS_FOLDER_NAME);
        FileHandle indexHandler = releasePath.child("index.html");
        FileHandle webXMLFile = releasePath.child("WEB-INF").child("web.xml");
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

    @Override
    protected void copyAssets(TeaCompilerData data) {
        super.copyAssets(data);
        FileHandle scriptsFolder = releasePath.child("scripts");
        AssetsCopy.copyResources(classLoader, scripts, scriptFilter, scriptsFolder);
    }
}
