package emu.com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.ConstructorGen;
import com.badlogic.gdx.utils.reflect.ReflectionException;

public final class TConstructor {

    private Class type;
    private final java.lang.reflect.Constructor constructor;

    public TConstructor(Class type, java.lang.reflect.Constructor constructor) {
        this.constructor = constructor;
        this.type = type;
    }

    public Class[] getParameterTypes() {
        if(constructor == null) {
            return new Class[0];
        }
        return constructor.getParameterTypes();
    }

    public Class getDeclaringClass() {
        if(constructor == null) {
            return type;
        }
        return constructor.getDeclaringClass();
    }

    public boolean isAccessible() {
        if(constructor == null) {
            return true;
        }
        return constructor.isAccessible();
    }

    public void setAccessible(boolean accessible) {
        if(constructor != null) {
            constructor.setAccessible(accessible);
        }
    }

    public Object newInstance(Object... args) throws ReflectionException {
        if(args == null) {
            args = new Object[0];
        }
        try {
            return ConstructorGen.newInstance(type, args);
        }
        catch(IllegalArgumentException e) {
            throw new ReflectionException("Illegal argument(s) supplied to constructor for class: " + getDeclaringClass().getName(), e);
        }
        catch(Throwable e) {
            throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
        }
//        if(args == null) {
//            args = new Object[0];
//        }
//        try {
//            return constructor.newInstance(args);
//        }
//        catch(IllegalArgumentException e) {
//            throw new ReflectionException("Illegal argument(s) supplied to constructor for class: " + getDeclaringClass().getName(),
//                    e);
//        }
//        catch(InstantiationException e) {
//            throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
//        }
//        catch(IllegalAccessException e) {
//            throw new ReflectionException("Could not instantiate instance of class: " + getDeclaringClass().getName(), e);
//        }
//        catch(InvocationTargetException e) {
//            throw new ReflectionException("Exception occurred in constructor for class: " + getDeclaringClass().getName(), e);
//        }
    }
}
