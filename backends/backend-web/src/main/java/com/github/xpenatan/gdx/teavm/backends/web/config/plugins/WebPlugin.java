package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.GdxTeaVMPluginConfig;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMPluginClasspath;
import com.github.xpenatan.gdx.teavm.backends.shared.config.plugin.TeaVMPluginReflectionSupport;
import java.net.URL;
import java.util.ArrayList;
import org.teavm.backend.wasm.TeaVMWasmGCHost;
import org.teavm.backend.javascript.TeaVMJavaScriptHost;
import org.teavm.jso.impl.JSOPlugin;
import org.teavm.vm.spi.Before;
import org.teavm.vm.spi.TeaVMHost;
import org.teavm.vm.spi.TeaVMPlugin;

/**
 * @author xpenatan
 */
@Before(JSOPlugin.class)
public class WebPlugin implements TeaVMPlugin {

    @Override
    public void install(TeaVMHost host) {
        TeaVMJavaScriptHost javaScriptHost = host.getExtension(TeaVMJavaScriptHost.class);
        TeaVMWasmGCHost wasmGCHost = host.getExtension(TeaVMWasmGCHost.class);
        if(javaScriptHost == null && wasmGCHost == null) {
            return;
        }

        host.add(new WebClassTransformer());
        host.add(new JavaObjectExporterDependency());

        GdxTeaVMPluginConfig config = GdxTeaVMPluginConfig.from(host.getProperties());
        ArrayList<URL> classPathURLs = TeaVMPluginClasspath.getURLs(host.getClassLoader(), config.classpath);
        TeaVMPluginReflectionSupport.install(host, config, classPathURLs);

        if(config.webappEnabled) {
            if(javaScriptHost != null) {
                GdxWebTargetWrapper.install(host, GdxWebRendererListener.TargetType.JAVASCRIPT, config, classPathURLs);
            }
            else if(wasmGCHost != null) {
                GdxWebTargetWrapper.install(host, GdxWebRendererListener.TargetType.WASM_GC, config, classPathURLs);
            }
        }
    }
}
