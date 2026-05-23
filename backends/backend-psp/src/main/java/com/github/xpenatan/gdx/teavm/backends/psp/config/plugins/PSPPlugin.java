package com.github.xpenatan.gdx.teavm.backends.psp.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaReflectionSupplier;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMPluginClasspath;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.SmartArrayFastPathTransformer;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.SpriteBatchDrawTransformer;
import java.net.URL;
import java.util.ArrayList;
import org.teavm.backend.c.TeaVMCHost;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

public class PSPPlugin implements TeaVMPlugin {
    @Override
    public void install(TeaVMHost host) {
        GdxTeaVMPluginConfig config = GdxTeaVMPluginConfig.from(host.getProperties());
        if(!"psp".equals(config.nativeBackend)) {
            return;
        }
        TeaVMCHost cHost = host.getExtension(TeaVMCHost.class);
        if(cHost != null) {
            host.add(new SpriteBatchDrawTransformer());
            host.add(new SmartArrayFastPathTransformer());
            ArrayList<URL> classPathURLs = TeaVMPluginClasspath.getURLs(host.getClassLoader(), config.classpath);
            TeaReflectionSupplier.printDebugLogs = config.reflectionDebug;
            if(config.reflectionEnabled) {
                TeaReflectionSupplier.addReflectionClass(config.reflectionClasses);
                if(config.reflectionDebug) {
                    TeaReflectionSupplier.printReflectionClasses();
                }
            }
            GdxPSPTargetWrapper.install(host, config, classPathURLs);
        }
    }
}
