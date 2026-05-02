package emu.com.badlogic.gdx.utils.reflect;

import com.badlogic.gdx.utils.reflect.ArrayGen;

public class TArrayReflection {

    /** Creates a new array with the specified component type and length. */
    static public Object newInstance (Class c, int size) {
        return java.lang.reflect.Array.newInstance(c, size);
    }

    /** Returns the length of the supplied array. */
    static public int getLength (Object array) {
        return ArrayGen.getLength(array);
    }

    /** Returns the value of the indexed component in the supplied array. */
    static public Object get (Object array, int index) {
        return ArrayGen.get(array.getClass().getComponentType(), array, index);
    }

    /** Sets the value of the indexed component in the supplied array to the supplied value. */
    static public void set (Object array, int index, Object value) {
        ArrayGen.set(array.getClass().getComponentType(), array, index, value);
    }
}
