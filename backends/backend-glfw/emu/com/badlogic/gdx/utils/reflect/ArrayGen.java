package com.badlogic.gdx.utils.reflect;

import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;
import org.teavm.metaprogramming.Metaprogramming;

@CompileTime
public class ArrayGen {

    @Meta
    public static native void set(Class<?> arrayType, Object array, int index, Object value);
    private static void set(ReflectClass<?> arrayType, Value<Object> arrayValue, Value<Integer> indexValue, Value<Object> valueValue) {
        ReflectClass<?> componentType = arrayType.getComponentType();
        if(componentType == null) {
            Metaprogramming.unsupportedCase();
            return;
        }
        String componentName = componentType.getName();
        if("boolean".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((boolean[])arrayValue.get())[indexValue.get()] = (Boolean)valueValue.get();
            });
        }
        else if("byte".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((byte[])arrayValue.get())[indexValue.get()] = ((Number)valueValue.get()).byteValue();
            });
        }
        else if("short".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((short[])arrayValue.get())[indexValue.get()] = ((Number)valueValue.get()).shortValue();
            });
        }
        else if("char".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((char[])arrayValue.get())[indexValue.get()] = (Character)valueValue.get();
            });
        }
        else if("int".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((int[])arrayValue.get())[indexValue.get()] = ((Number)valueValue.get()).intValue();
            });
        }
        else if("long".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((long[])arrayValue.get())[indexValue.get()] = ((Number)valueValue.get()).longValue();
            });
        }
        else if("float".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((float[])arrayValue.get())[indexValue.get()] = ((Number)valueValue.get()).floatValue();
            });
        }
        else if("double".equals(componentName)) {
            Metaprogramming.emit(() -> {
                ((double[])arrayValue.get())[indexValue.get()] = ((Number)valueValue.get()).doubleValue();
            });
        }
        else {
            Metaprogramming.emit(() -> {
                ((Object[])arrayValue.get())[indexValue.get()] = valueValue.get();
            });
        }
    }

    @Meta
    public static native Object get(Class<?> arrayType, Object array, int index);
    private static void get(ReflectClass<?> arrayType, Value<Object> arrayValue, Value<Integer> indexValue) {
        ReflectClass<?> componentType = arrayType.getComponentType();
        if(componentType == null) {
            Metaprogramming.unsupportedCase();
            return;
        }
        String componentName = componentType.getName();
        if("boolean".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((boolean[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("byte".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((byte[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("short".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((short[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("char".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((char[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("int".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((int[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("long".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((long[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("float".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((float[])arrayValue.get())[indexValue.get()];
            });
        }
        else if("double".equals(componentName)) {
            Metaprogramming.exit(() -> {
                return ((double[])arrayValue.get())[indexValue.get()];
            });
        }
        else {
            Metaprogramming.exit(() -> {
                return ((Object[])arrayValue.get())[indexValue.get()];
            });
        }
    }

    @Meta
    public static native int getLength(Class<?> arrayType, Object array);
    private static void getLength(ReflectClass<?> arrayType, Value<Object> arrayValue) {
        if(!arrayType.isArray()) {
            Metaprogramming.unsupportedCase();
            return;
        }
        Metaprogramming.exit(() -> {
            return arrayType.getArrayLength(arrayValue.get());
        });
    }

    @Meta
    public static native Object newInstance(Class<?> componentType, int size);
    private static void newInstance(ReflectClass<?> componentType, Value<Integer> sizeValue) {
        Metaprogramming.exit(() -> {
            return componentType.createArray(sizeValue.get());
        });
    }
}
