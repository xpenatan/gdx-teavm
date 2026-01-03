package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.jParser.builder.BuildConfig;
import com.github.xpenatan.jParser.builder.BuildMultiTarget;
import com.github.xpenatan.jParser.builder.JBuilder;
import com.github.xpenatan.jParser.builder.targets.WindowsMSVCTarget;
import com.github.xpenatan.jParser.core.util.CustomFileDescriptor;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTargetType;
import org.teavm.tooling.TeaVMTool;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildCTestDemo {

    public static void main(String[] args) throws IOException {
        String output = new File("build/dist").getCanonicalPath();
//
//        TeaBuilder.config(output);
//
//        TeaVMTool tool = new TeaVMTool();
//        tool.setTargetType(TeaVMTargetType.C);
//        tool.setObfuscated(false);
//        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
//        tool.setMainClass(TestCLauncher.class.getName());
//        tool.setShortFileNames(false);
//        TeaBuilder.build(tool, true);

        String libName = "test";
        String buildRootPath = new File("build/c++").getAbsolutePath().replace("\\", "/");
        String buildRootGenSourcePath = buildRootPath + "/src";
        String compiledLibsPath = buildRootPath + "/libs";

        BuildConfig config = new BuildConfig(
                libName,
                buildRootPath,
                buildRootGenSourcePath,
                compiledLibsPath
        );
        CustomFileDescriptor sourceCode = new CustomFileDescriptor("build/dist/c");
        CustomFileDescriptor customCode = new CustomFileDescriptor("src/cpp/custom");
        config.additionalSourceDirs.add(sourceCode);
        config.additionalSourceDirs.add(customCode);

        ArrayList<BuildMultiTarget> targets = new ArrayList<>();
        targets.add(setupWindowsTarget(sourceCode, customCode, compiledLibsPath));
        JBuilder.build(config, targets);
    }

    static private BuildMultiTarget setupWindowsTarget(CustomFileDescriptor sourceCode, CustomFileDescriptor customCode, String compiledLibsPath) {
        String sourceCodePath = sourceCode.file().getAbsolutePath();

        BuildMultiTarget multiTarget = new BuildMultiTarget();

        // Make a static library
        WindowsMSVCTarget compileStaticTarget = new WindowsMSVCTarget();
        compileStaticTarget.isStatic = true;
        compileStaticTarget.cppFlags.add("/std:c17");
        compileStaticTarget.cppFlags.add("/Zi");
        compileStaticTarget.cppFlags.add("/Od");
        compileStaticTarget.cppInclude.add("**/all.c");
        multiTarget.add(compileStaticTarget);

        WindowsMSVCTarget linkTarget = new WindowsMSVCTarget();
        linkTarget.shouldCompile = false;
        linkTarget.linkerFlags.add("/include:main");
        linkTarget.linkerFlags.add(compiledLibsPath + "/windows/vc/" + "test" + "64_.lib");
        linkTarget.linkerFlags.add("shell32.lib");
        linkTarget.libSuffix = ".exe";
        multiTarget.add(linkTarget);

        return multiTarget;
    }
}
