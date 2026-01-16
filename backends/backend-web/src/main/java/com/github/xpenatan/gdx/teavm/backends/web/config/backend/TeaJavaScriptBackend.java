package com.github.xpenatan.gdx.teavm.backends.web.config.backend;

import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompilerData;
import org.teavm.tooling.TeaVMTargetType;

public class TeaJavaScriptBackend extends TeaWebBackend {
    @Override
    protected void setup(TeaCompilerData data) {
        targetType = TeaVMTargetType.JAVASCRIPT;
        tool.setTargetFileName(data.outputName + ".js");
        super.setup(data);
    }
}
