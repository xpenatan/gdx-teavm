package emu.com.badlogic.gdx.utils.reflect;

public final class TAnnotation {

    private java.lang.annotation.Annotation annotation;

    TAnnotation(java.lang.annotation.Annotation annotation) {
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
