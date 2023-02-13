package com.badlogic.gdx.utils.reflect;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

/**
 * Provides information about, and access to, an annotation of a field, class or interface.
 *
 * @author dludwig
 */
@Emulate(Annotation.class)
public final class AnnotationEmu {

    private java.lang.annotation.Annotation annotation;

    AnnotationEmu(java.lang.annotation.Annotation annotation) {
        this.annotation = annotation;
    }

    @SuppressWarnings("unchecked")
    public <T extends java.lang.annotation.Annotation> T getAnnotation(Class<T> annotationType) {
        if(annotation.annotationType().equals(annotationType)) {
            return (T)annotation;
        }
        return null;
    }

    public Class<? extends java.lang.annotation.Annotation> getAnnotationType() {
        return annotation.annotationType();
    }
}
