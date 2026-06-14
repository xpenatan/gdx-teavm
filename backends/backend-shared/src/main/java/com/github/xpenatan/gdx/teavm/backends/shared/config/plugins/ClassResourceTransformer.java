package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins;

import java.net.URL;
import org.teavm.model.AccessLevel;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderTransformer;
import org.teavm.model.ClassHolderTransformerContext;
import org.teavm.model.MethodDescriptor;
import org.teavm.model.MethodHolder;
import org.teavm.model.emit.ProgramEmitter;

public class ClassResourceTransformer implements ClassHolderTransformer {

    private static final String CLASS_NAME = Class.class.getName();
    private static final MethodDescriptor GET_RESOURCE = new MethodDescriptor("getResource", String.class, URL.class);

    @Override
    public void transformClass(ClassHolder cls, ClassHolderTransformerContext context) {
        if(!CLASS_NAME.equals(cls.getName()) || cls.getMethod(GET_RESOURCE) != null) {
            return;
        }

        MethodHolder method = new MethodHolder(GET_RESOURCE);
        method.setLevel(AccessLevel.PUBLIC);
        cls.addMethod(method);

        ProgramEmitter pe = ProgramEmitter.create(method, context.getHierarchy());
        pe.constantNull(URL.class).returnValue();
    }
}
