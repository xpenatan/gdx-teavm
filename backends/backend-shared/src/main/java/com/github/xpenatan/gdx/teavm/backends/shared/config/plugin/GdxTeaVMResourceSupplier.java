package com.github.xpenatan.gdx.teavm.backends.shared.config.plugin;

import org.teavm.classlib.ResourceSupplier;
import org.teavm.classlib.ResourceSupplierContext;

public class GdxTeaVMResourceSupplier implements ResourceSupplier {
    private static final String SENTINEL_RESOURCE = "gdx-teavm/wasm-resource-sentinel.bin";

    @Override
    public String[] supplyResources(ResourceSupplierContext context) {
        return new String[] { SENTINEL_RESOURCE };
    }
}
