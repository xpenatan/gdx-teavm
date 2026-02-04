package emu.com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.ConstructorGen;
import com.badlogic.gdx.utils.reflect.InstanceGen;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import java.lang.reflect.InvocationTargetException;

public final class TConstructor {

    private Class type;
    private final java.lang.reflect.Constructor constructor;

    public TConstructor(Class type, java.lang.reflect.Constructor constructor) {
        this.constructor = constructor;
        this.type = type;
    }

    public Class[] getParameterTypes() {
        return constructor.getParameterTypes();
    }

    public Class getDeclaringClass() {
        return constructor.getDeclaringClass();
    }

    public boolean isAccessible() {
        return constructor.isAccessible();
    }

    public void setAccessible(boolean accessible) {
        constructor.setAccessible(accessible);
    }

    public Object newInstance(Object... args) throws ReflectionException {
//        return ConstructorGen.newInstance(type, args);
        return InstanceGen.newInstance(type);

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
