package com.badlogic.gdx.utils.reflect;

import org.teavm.metaprogramming.CompileTime;
import org.teavm.metaprogramming.Meta;
import org.teavm.metaprogramming.ReflectClass;
import org.teavm.metaprogramming.Value;
import org.teavm.metaprogramming.Metaprogramming;

@CompileTime
public class ArrayGen {

    @Meta
    public static native void set(Class<?> type, Object array, int index, Object value);
    private static void set(ReflectClass<?> type, Value<Object> arrayValue, Value<Integer> indexValue, Value<Object> valueValue) {
        Metaprogramming.emit(() -> {
            ((Object[]) arrayValue.get())[indexValue.get()] = valueValue.get();
        });
    }

    @Meta
    public static native Object get(Class<?> type, Object array, int index);
    private static void get(ReflectClass<?> type, Value<Object> arrayValue, Value<Integer> indexValue) {
        Metaprogramming.exit(() -> {
            return type.getArrayElement(arrayValue.get(), indexValue.get());
        });
    }

    @Meta
    public static native int getLength(Object array);
    private static void getLength(Value<Object> arrayValue) {
        Metaprogramming.exit(() -> {
            return ((Object[]) arrayValue.get()).length;
        });
    }
}