package com.github.xpenatan.gdx.backends.teavm.config.backend;

import com.github.xpenatan.gdx.backends.teavm.config.compiler.TeaCompilerData;
import org.teavm.tooling.TeaVMTargetType;

public class TeaWebAssemblyBackend extends TeaWebBackend {
    @Override
    protected void setup(TeaCompilerData data) {
        targetType = TeaVMTargetType.WEBASSEMBLY_GC;
        tool.setTargetFileName(data.outputName + ".wasm");
        super.setup(data);
    }
}
