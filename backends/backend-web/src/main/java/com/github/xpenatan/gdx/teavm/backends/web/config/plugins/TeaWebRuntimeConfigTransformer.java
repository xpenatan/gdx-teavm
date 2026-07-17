package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.web.config.TeaWebRuntimeConfig;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodHolder;
import org.teavm.model.emit.ProgramEmitter;

/** Embeds web-backend configuration values into {@link TeaWebRuntimeConfig}. */
public class TeaWebRuntimeConfigTransformer implements ClassHolderTransformer {
    private static final MethodDescriptor GET_STARTUP_LOGO =
            new MethodDescriptor("getStartupLogo", String.class);

    private final String startupLogo;

    public TeaWebRuntimeConfigTransformer(String startupLogo) {
        this.startupLogo = startupLogo;
    }

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        if(!cls.getName().equals(TeaWebRuntimeConfig.class.getName())) {
            return;
        }
        MethodHolder method = cls.getMethod(GET_STARTUP_LOGO);
        if(method == null) {
            return;
        }

        ProgramEmitter pe = ProgramEmitter.create(method, context.getHierarchy());
        pe.constant(startupLogo).returnValue();
    }
}
