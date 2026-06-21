package com.github.xpenatan.gdx.teavm.backends.web.config.plugins;

import com.github.xpenatan.gdx.teavm.backends.web.assetloader.TeaAssetManifest;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodHolder;
import org.teavm.model.emit.ProgramEmitter;

public class TeaAssetManifestTransformer implements ClassHolderTransformer {
    private static final MethodDescriptor GET_ASSETS = new MethodDescriptor("getAssets", String[].class);

    private final String[] manifestEntries;

    public TeaAssetManifestTransformer(String[] manifestEntries) {
        this.manifestEntries = manifestEntries != null ? manifestEntries.clone() : new String[0];
    }

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        if(!cls.getName().equals(TeaAssetManifest.class.getName())) {
            return;
        }
        MethodHolder method = cls.getMethod(GET_ASSETS);
        if(method == null) {
            return;
        }

        ProgramEmitter pe = ProgramEmitter.create(method, context.getHierarchy());
        var array = pe.constructArray(String.class, manifestEntries.length);
        for(int i = 0; i < manifestEntries.length; i++) {
            array.setElement(i, pe.constant(manifestEntries[i]));
        }
        array.returnValue();
    }
}
