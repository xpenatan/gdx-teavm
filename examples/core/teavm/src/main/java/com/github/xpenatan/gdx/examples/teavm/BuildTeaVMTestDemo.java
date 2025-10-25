package com.github.xpenatan.gdx.examples.teavm;

import com.github.xpenatan.gdx.backends.teavm.config.TeaBuilder;
import com.github.xpenatan.gdx.examples.teavm.launcher.TeaVMTestLauncher;
import java.io.File;
import java.io.IOException;
import org.teavm.tooling.TeaVMSourceFilePolicy;
import org.teavm.tooling.TeaVMTool;
import org.teavm.tooling.sources.DirectorySourceFileProvider;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) throws IOException {
        ConfigTeaVMTestDemo.configureWebapp();

        TeaVMTool tool = new TeaVMTool();
        tool.setObfuscated(false);
        tool.setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE);
        tool.setMainClass(TeaVMTestLauncher.class.getName());

        tool.setDebugInformationGenerated(true);
        tool.setSourceMapsFileGenerated(true);
        tool.setSourceFilePolicy(TeaVMSourceFilePolicy.COPY);

//        File coreSourcePath = new File("../core/src/main/java");
//        tool.addSourceFileProvider(new DirectorySourceFileProvider(coreSourcePath));
//        File teavmSourcePath = new File("../../../backends/backend-teavm/src/main/java");
//        tool.addSourceFileProvider(new DirectorySourceFileProvider(teavmSourcePath));
//        File teavmEmuSourcePath = new File("../../../backends/backend-teavm/emu/");
//        tool.addSourceFileProvider(new DirectorySourceFileProvider(teavmEmuSourcePath));

        int size = 64 * (1 << 20);
        tool.setMaxDirectBuffersSize(size);
        TeaBuilder.build(tool);
    }
}
