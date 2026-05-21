package com.github.xpenatan.gdx.teavm.backends.psp.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMPluginClasspath;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMPluginReflectionSupport;
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
            ArrayList<URL> classPathURLs = TeaVMPluginClasspath.getURLs(host.getClassLoader());
            TeaVMPluginReflectionSupport.install(host, config, classPathURLs);
            GdxPSPTargetWrapper.install(host, config, classPathURLs);
        }
    }
}
