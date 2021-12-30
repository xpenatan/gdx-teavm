package java.nio;

/** Provide factory service of buffer classes.
 * <p>
 * Since all buffer impl classes are package private (except DirectByteBuffer), this factory is the only entrance to access buffer
 * functions from outside of the impl package.
 * </p> */
final class BufferFactory {

    /** Returns a new byte buffer based on the specified byte array.
     *
     * @param array The byte array
     * @return A new byte buffer based on the specified byte array. */
    public static XByteBuffer newByteBuffer (byte array[]) {
        return new ReadWriteHeapByteBuffer(array);
    }

    /** Returns a new array based byte buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based byte buffer with the specified capacity. */
    public static XByteBuffer newByteBuffer (int capacity) {
        return new ReadWriteHeapByteBuffer(capacity);
    }

    /** Returns a new direct byte buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new direct byte buffer with the specified capacity. */
    public static XByteBuffer newDirectByteBuffer (int capacity) {
        return new DirectReadWriteByteBuffer(capacity);
    }

    /** Returns a new char buffer based on the specified char array.
     *
     * @param array The char array
     * @return A new char buffer based on the specified char array. */
    public static XCharBuffer newCharBuffer (char array[]) {
        return new ReadWriteCharArrayBuffer(array);
    }

    /** Returns a new readonly char buffer based on the specified char sequence.
     *
     * @param chseq The char sequence
     * @return A new readonly char buffer based on the specified char sequence. */
    public static XCharBuffer newCharBuffer (CharSequence chseq) {
        return new CharSequenceAdapter(chseq);
    }

    /** Returns a new array based char buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based char buffer with the specified capacity. */
    public static XCharBuffer newCharBuffer (int capacity) {
        return new ReadWriteCharArrayBuffer(capacity);
    }

    /** Returns a new double buffer based on the specified double array.
     *
     * @param array The double array
     * @return A new double buffer based on the specified double array. */
    public static XDoubleBuffer newDoubleBuffer (double array[]) {
        return new ReadWriteDoubleArrayBuffer(array);
    }

    /** Returns a new array based double buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based double buffer with the specified capacity. */
    public static XDoubleBuffer newDoubleBuffer (int capacity) {
        return new ReadWriteDoubleArrayBuffer(capacity);
    }

    /** Returns a new float buffer based on the specified float array.
     *
     * @param array The float array
     * @return A new float buffer based on the specified float array. */
    public static XFloatBuffer newFloatBuffer (float array[]) {
        return new ReadWriteFloatArrayBuffer(array);
    }

    /** Returns a new array based float buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based float buffer with the specified capacity. */
    public static XFloatBuffer newFloatBuffer (int capacity) {
        return new ReadWriteFloatArrayBuffer(capacity);
    }

    /** Returns a new array based int buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based int buffer with the specified capacity. */
    public static XIntBuffer newIntBuffer (int capacity) {
        return new ReadWriteIntArrayBuffer(capacity);
    }

    /** Returns a new int buffer based on the specified int array.
     *
     * @param array The int array
     * @return A new int buffer based on the specified int array. */
    public static XIntBuffer newIntBuffer (int array[]) {
        return new ReadWriteIntArrayBuffer(array);
    }

    /** Returns a new array based long buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based long buffer with the specified capacity. */
    public static XLongBuffer newLongBuffer (int capacity) {
        return new ReadWriteLongArrayBuffer(capacity);
    }

    /** Returns a new long buffer based on the specified long array.
     *
     * @param array The long array
     * @return A new long buffer based on the specified long array. */
    public static XLongBuffer newLongBuffer (long array[]) {
        return new ReadWriteLongArrayBuffer(array);
    }

    /** Returns a new array based short buffer with the specified capacity.
     *
     * @param capacity The capacity of the new buffer
     * @return A new array based short buffer with the specified capacity. */
    public static XShortBuffer newShortBuffer (int capacity) {
        return new ReadWriteShortArrayBuffer(capacity);
    }

    /** Returns a new short buffer based on the specified short array.
     *
     * @param array The short array
     * @return A new short buffer based on the specified short array. */
    public static XShortBuffer newShortBuffer (short array[]) {
        return new ReadWriteShortArrayBuffer(array);
    }

}