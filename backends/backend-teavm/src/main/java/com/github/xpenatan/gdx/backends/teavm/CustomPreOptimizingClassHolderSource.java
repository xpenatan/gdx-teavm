package com.github.xpenatan.gdx.backends.teavm;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderSource;
import org.teavm.model.MethodHolder;
import org.teavm.model.optimization.GlobalValueNumbering;
import org.teavm.model.optimization.UnusedVariableElimination;
import org.teavm.model.transformation.NoSuchFieldCatchElimination;

public class CustomPreOptimizingClassHolderSource implements ClassHolderSource {
    private CustomClasspathClassHolderSource innerClassSource;
    private Map<String, ClassHolder> cache = new LinkedHashMap();

    public CustomPreOptimizingClassHolderSource(CustomClasspathClassHolderSource innerClassSource) {
        this.innerClassSource = innerClassSource;
    }

    public void remove(String name) {
        cache.remove(name);
        innerClassSource.remove(name);
    }

    public ClassHolder get(String name) {
        ClassHolder cls = (ClassHolder)this.cache.get(name);
        if (cls == null) {
            ClassHolderSource var10000 = this.innerClassSource;
            Objects.requireNonNull(var10000);
            cls = optimize(var10000::get, name);
            if (cls == null) {
                return null;
            }

            this.cache.put(name, cls);
        }

        return cls;
    }

    public static ClassHolder optimize(Function<String, ClassHolder> innerSource, String name) {
        ClassHolder cls = (ClassHolder)innerSource.apply(name);
        if (cls == null) {
            return cls;
        } else {
            NoSuchFieldCatchElimination noSuchFieldCatchElimination = new NoSuchFieldCatchElimination();
            Iterator var4 = cls.getMethods().iterator();

            while(var4.hasNext()) {
                MethodHolder method = (MethodHolder)var4.next();
                if (method.getProgram() != null) {
                    noSuchFieldCatchElimination.apply(method.getProgram());
                    (new GlobalValueNumbering(true)).optimize(method.getProgram());
                    (new UnusedVariableElimination()).optimize(method, method.getProgram());
                }
            }

            return cls;
        }
    }
}