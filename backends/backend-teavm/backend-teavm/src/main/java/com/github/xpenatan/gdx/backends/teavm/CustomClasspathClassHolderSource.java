package com.github.xpenatan.gdx.backends.teavm;

import java.util.Date;
import org.teavm.model.ClassHolder;
import org.teavm.model.ClassHolderSource;
import org.teavm.model.ReferenceCache;
import org.teavm.parsing.ClassDateProvider;
import org.teavm.parsing.ClasspathClassHolderSource;
import org.teavm.parsing.ClasspathResourceMapper;
import org.teavm.parsing.resource.ClasspathResourceReader;
import org.teavm.parsing.resource.MapperClassHolderSource;
import org.teavm.parsing.resource.ResourceClassHolderMapper;

public class CustomClasspathClassHolderSource implements ClassHolderSource, ClassDateProvider {
    private CustomMapperClassHolderSource innerClassSource;
    private ClasspathResourceMapper classPathMapper;

    public CustomClasspathClassHolderSource(ClassLoader classLoader, ReferenceCache referenceCache) {
        ClasspathResourceReader reader = new ClasspathResourceReader(classLoader);
        ResourceClassHolderMapper rawMapper = new ResourceClassHolderMapper(reader, referenceCache);
        this.classPathMapper = new ClasspathResourceMapper(classLoader, referenceCache, rawMapper);
        this.innerClassSource = new CustomMapperClassHolderSource(this.classPathMapper);
    }

    public CustomClasspathClassHolderSource(ReferenceCache referenceCache) {
        this(ClasspathClassHolderSource.class.getClassLoader(), referenceCache);
    }

    public ClassHolder get(String name) {
        return this.innerClassSource.get(name);
    }

    public void remove(String name) {
        this.innerClassSource.remove(name);
    }

    public Date getModificationDate(String className) {
        return this.classPathMapper.getModificationDate(className);
    }
}