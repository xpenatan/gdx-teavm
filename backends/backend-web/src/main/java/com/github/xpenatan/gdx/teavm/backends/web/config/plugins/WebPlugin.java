package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

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
        host.add(new WebClassTransformer());
        host.add(new JavaObjectExporterDependency());
    }
}
