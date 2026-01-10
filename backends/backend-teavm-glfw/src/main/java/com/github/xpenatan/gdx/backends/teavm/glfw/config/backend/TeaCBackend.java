package com.github.xpenatan.gdx.backends.teavm.glfw.config.backend;

import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaBackend;
import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaCompilerData;
import com.github.xpenatan.jParser.builder.BuildConfig;
import com.github.xpenatan.jParser.builder.BuildMultiTarget;
import com.github.xpenatan.jParser.builder.JBuilder;
import com.github.xpenatan.jParser.builder.targets.WindowsMSVCTarget;
import com.github.xpenatan.jParser.core.util.CustomFileDescriptor;
import java.io.File;
import java.util.ArrayList;
import org.teavm.backend.c.CTarget;
import org.teavm.backend.c.generate.CNameProvider;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.vm.TeaVMBuilder;

public class TeaCBackend extends TeaBackend {

    public boolean shouldGenerateSource = true;

    protected String buildRootPath;
    protected String generatedSources;
    protected String externalSources;

    @Override
    protected void setup(TeaCompilerData data) {
        targetType = TeaVMTargetType.C;
        if(data.releasePath != null) {
            releasePath = data.releasePath.getAbsolutePath().replace("\\", "/");
        }
        else {
            releasePath = new File(data.output, "c/release").getAbsolutePath().replace("\\", "/");
        }
        buildRootPath = data.output.getAbsolutePath().replace("\\", "/");
        generatedSources = buildRootPath +  "/c/src";
        externalSources = buildRootPath +  "/external_cpp";
        tool.setTargetDirectory(new File(generatedSources));
    }

    @Override
    protected void build(TeaCompilerData data) {
        if(shouldGenerateSource) {
            super.build(data);
        }

//        CTarget cTarget = new CTarget(new CNameProvider());

//        TeaVMBuilder vmBuilder = new TeaVMBuilder(cTarget);



        String libName = data.outputName;
        String buildCPath = buildRootPath + "/c/build";

        BuildConfig config = new BuildConfig(
                libName,
                buildCPath,
                generatedSources,
                releasePath
        );
        config.additionalSourceDirs.add(new CustomFileDescriptor(generatedSources));
        config.additionalSourceDirs.add(new CustomFileDescriptor(externalSources));

        ArrayList<BuildMultiTarget> targets = new ArrayList<>();
        targets.add(setupWindowsTarget(data));
        JBuilder.build(config, targets);
    }

    private BuildMultiTarget setupWindowsTarget(TeaCompilerData data) {

        String glfwPath = externalSources + "/glfw";
        String stbPath = externalSources + "/stb";
        String glewPath = externalSources + "/glew-2.3.0";

        BuildMultiTarget multiTarget = new BuildMultiTarget();

        // Make a static library
        WindowsMSVCTarget compileStaticTarget = new WindowsMSVCTarget();
        compileStaticTarget.isStatic = true;
        compileStaticTarget.shouldUseHelper = false;
        compileStaticTarget.headerDirs.add("-I" + generatedSources);
        compileStaticTarget.headerDirs.add("-I" + glfwPath + "/include");
        compileStaticTarget.headerDirs.add("-I" + stbPath + "/include");
        compileStaticTarget.headerDirs.add("-I" + glewPath + "/include");
        compileStaticTarget.cppFlags.add("/std:c17");
        compileStaticTarget.cppFlags.add("/Zi");
        compileStaticTarget.cppFlags.add("/DGLEW_STATIC");
        compileStaticTarget.cppFlags.add("/DWIN32");
        compileStaticTarget.cppFlags.add("/D_MBCS");
        compileStaticTarget.cppFlags.add("/D_WINDOWS");
        compileStaticTarget.cppFlags.add("/nologo");
        compileStaticTarget.cppFlags.add("/MD");
        compileStaticTarget.cppInclude.add("**/app_include.c");
        compileStaticTarget.libDirSuffix = "";
        multiTarget.add(compileStaticTarget);

        WindowsMSVCTarget linkTarget = new WindowsMSVCTarget();
        linkTarget.libDirSuffix = "";
        linkTarget.shouldCompile = false;
        linkTarget.shouldUseHelper = false;
        linkTarget.linkerFlags.add("/IMPLIB:" + releasePath + "/" + data.outputName + "64_.lib");
        linkTarget.linkerFlags.add(glfwPath + "/lib-vc2022/glfw3.lib");
        linkTarget.linkerFlags.add(glewPath + "/lib/Release/x64/glew32s.lib");
        linkTarget.linkerFlags.add("opengl32.lib");
        linkTarget.linkerFlags.add("kernel32.lib");
        linkTarget.linkerFlags.add("user32.lib");
        linkTarget.linkerFlags.add("gdi32.lib");
        linkTarget.linkerFlags.add("winspool.lib");
        linkTarget.linkerFlags.add("shell32.lib");
        linkTarget.linkerFlags.add("ole32.lib");
        linkTarget.linkerFlags.add("oleaut32.lib");
        linkTarget.linkerFlags.add("uuid.lib");
        linkTarget.linkerFlags.add("comdlg32.lib");
        linkTarget.linkerFlags.add("advapi32.lib");
        linkTarget.linkerFlags.add(buildRootPath + "/c/build/target/windows/vc/static/app_include.obj");
        linkTarget.libSuffix = ".exe";
        multiTarget.add(linkTarget);

        return multiTarget;
    }
}