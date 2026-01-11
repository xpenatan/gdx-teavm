package com.github.xpenatan.gdx.teavm.backend.web.config.backend;

import com.github.xpenatan.gdx.teavm.backend.shared.config.compiler.TeaCompilerData;
import org.teavm.tooling.TeaVMTargetType;

public class TeaWebAssemblyBackend extends TeaWebBackend {
    @Override
    protected void setup(TeaCompilerData data) {
        targetType = TeaVMTargetType.WEBASSEMBLY_GC;
        tool.setTargetFileName(data.outputName + ".wasm");
        super.setup(data);
    }
}
