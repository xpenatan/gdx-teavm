package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.DelegatingTeaVMTarget;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetsCopy;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMTargetInstaller;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import org.teavm.model.ListableClassHolderSource;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.TeaVMTarget;
import org.teavm.vm.spi.TeaVMHost;

public class GdxWebTargetWrapper extends DelegatingTeaVMTarget {
    private final GdxWebRendererListener webOutput;
    private final GdxWebRendererListener.TargetType targetType;

    private GdxWebTargetWrapper(
            TeaVMTarget delegate,
            GdxWebRendererListener webOutput,
            GdxWebRendererListener.TargetType targetType
    ) {
        super(delegate);
        this.webOutput = webOutput;
        this.targetType = targetType;
    }

    public static void install(
            TeaVMHost host,
            GdxWebRendererListener.TargetType targetType,
            GdxTeaVMPluginConfig config,
            ArrayList<URL> classPathURLs,
            AssetsCopy.AssetPlan assetPlan
    ) {
        TeaVMTargetInstaller.install(host, GdxWebTargetWrapper.class, target -> {
            GdxWebRendererListener webOutput = new GdxWebRendererListener(config, host.getClassLoader(), classPathURLs,
                    assetPlan);
            return new GdxWebTargetWrapper(target, webOutput, targetType);
        });
    }

    @Override
    public void emit(ListableClassHolderSource classes, BuildTarget buildTarget, String outputName) throws IOException {
        webOutput.write(buildTarget, targetType, outputName);
        delegate.emit(classes, buildTarget, outputName);
    }
}
