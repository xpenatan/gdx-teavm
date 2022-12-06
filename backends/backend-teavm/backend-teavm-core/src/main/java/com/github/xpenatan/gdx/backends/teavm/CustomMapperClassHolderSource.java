package com.github.xpenatan.gdx.backends.teavm;

import java.util.function.Function;
import org.teavm.common.CachedFunction;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderSource;

public class CustomMapperClassHolderSource implements ClassHolderSource {
    private CachedFunction<String, ClassHolder> mapper;

    public CustomMapperClassHolderSource(Function<String, ClassHolder> mapper) {
        this.mapper = new CachedFunction(mapper);
    }

    public ClassHolder get(String name) {
        return (ClassHolder)this.mapper.apply(name);
    }

    public void remove(String name) {
        mapper.invalidate(name);
    }
}
