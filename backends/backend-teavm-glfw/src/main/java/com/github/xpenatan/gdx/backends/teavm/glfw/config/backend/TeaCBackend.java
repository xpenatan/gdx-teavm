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
import org.teavm.tooling.TeaVMTargetType;

public class TeaCBackend extends TeaBackend {

    public boolean shouldGenerateSource = true;

    @Override
    protected void setup(TeaCompilerData data) {
        targetType = TeaVMTargetType.C;
        releasePath = new File(data.output, "c/release");
    }

    @Override
    protected void build(TeaCompilerData data) {
        File generatedSources = new File(data.output, "c/src");
        tool.setTargetDirectory(generatedSources);

        if(shouldGenerateSource) {
            super.build(data);
        }

        String libName = data.outputName;
        String buildRootPath = data.output.getAbsolutePath().replace("\\", "/");
        String buildCPath = buildRootPath + "/c/build";
        String buildRootGenSourcePath = generatedSources.getAbsolutePath().replace("\\", "/");
        String outputPath = releasePath.getAbsolutePath().replace("\\", "/");

        BuildConfig config = new BuildConfig(
                libName,
                buildCPath,
                buildRootGenSourcePath,
                outputPath
        );
        CustomFileDescriptor sourceCode = new CustomFileDescriptor(generatedSources);
        config.additionalSourceDirs.add(sourceCode);

        ArrayList<BuildMultiTarget> targets = new ArrayList<>();
        targets.add(setupWindowsTarget(data, outputPath));
        JBuilder.build(config, targets);
    }

    static private BuildMultiTarget setupWindowsTarget(TeaCompilerData data, String outputPath) {

        BuildMultiTarget multiTarget = new BuildMultiTarget();

        // Make a static library
        WindowsMSVCTarget compileStaticTarget = new WindowsMSVCTarget();
        compileStaticTarget.isStatic = true;
        compileStaticTarget.shouldUseHelper = false;
        compileStaticTarget.cppFlags.add("/std:c17");
        compileStaticTarget.cppFlags.add("/Zi");
        compileStaticTarget.cppFlags.add("/Od");
        compileStaticTarget.cppInclude.add("**/all.c");
        compileStaticTarget.libDirSuffix = "";
        multiTarget.add(compileStaticTarget);

        WindowsMSVCTarget linkTarget = new WindowsMSVCTarget();
        linkTarget.libDirSuffix = "";
        linkTarget.shouldCompile = false;
        linkTarget.shouldUseHelper = false;
        linkTarget.linkerFlags.add("/include:main");
        linkTarget.linkerFlags.add(outputPath + "/" + data.outputName + "64_.lib");
        linkTarget.linkerFlags.add("shell32.lib");
        linkTarget.libSuffix = ".exe";
        multiTarget.add(linkTarget);

        return multiTarget;
    }
}