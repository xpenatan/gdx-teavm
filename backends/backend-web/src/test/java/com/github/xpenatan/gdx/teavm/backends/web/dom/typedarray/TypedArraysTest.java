package com.github.xpenatan.gdx.teavm.backends.web.dom.typedarray;

import static com.google.common.truth.Truth.assertThat;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.junit.Test;

public class TypedArraysTest {

    @Test
    public void calculatesByteRangeWithoutChangingBufferState() {
        assertRange(ByteBuffer.allocate(8), 2, 5, 2, 3);
        assertRange(ShortBuffer.allocate(8), 2, 5, 4, 6);
        assertRange(IntBuffer.allocate(8), 2, 5, 8, 12);
        assertRange(FloatBuffer.allocate(8), 2, 5, 8, 12);
    }

    @Test
    public void supportsEmptyRangeAtEndOfBuffer() {
        assertRange(FloatBuffer.allocate(8), 8, 8, 32, 0);
    }

    @Test
    public void includesBackingArrayOffsetForSlicedHeapBuffers() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.position(1);
        assertRange(byteBuffer.slice(), 2, 5, 3, 3);

        ShortBuffer shortBuffer = ShortBuffer.allocate(8);
        shortBuffer.position(1);
        assertRange(shortBuffer.slice(), 2, 5, 6, 6);

        IntBuffer intBuffer = IntBuffer.allocate(8);
        intBuffer.position(1);
        assertRange(intBuffer.slice(), 2, 5, 12, 12);

        FloatBuffer floatBuffer = FloatBuffer.allocate(8);
        floatBuffer.position(1);
        assertRange(floatBuffer.slice(), 2, 5, 12, 12);
    }

    @Test
    public void duplicateProtectsSourceStateDuringFallbackConversion() {
        assertDuplicateProtectsState(ByteBuffer.allocate(16).asReadOnlyBuffer());
        assertDuplicateProtectsState(ByteBuffer.allocate(32).asShortBuffer());
        assertDuplicateProtectsState(ByteBuffer.allocate(32).asIntBuffer());
        assertDuplicateProtectsState(ByteBuffer.allocate(32).asFloatBuffer());
    }

    @Test
    public void reusesBuffersWhenConversionDoesNotMutateThem() {
        ByteBuffer heapBuffer = ByteBuffer.allocate(8);
        assertThat(TypedArrays.getConversionBuffer(heapBuffer)).isSameInstanceAs(heapBuffer);

        ByteBuffer directBuffer = ByteBuffer.allocateDirect(8);
        assertThat(TypedArrays.getConversionBuffer(directBuffer)).isSameInstanceAs(directBuffer);
    }

    private static void assertRange(Buffer buffer, int position, int limit, int bytePosition, int remainingBytes) {
        buffer.position(position);
        buffer.limit(limit);

        assertThat(TypedArrays.getRangeByteOffset(buffer)).isEqualTo(bytePosition);
        assertThat(TypedArrays.getRangeByteLength(buffer)).isEqualTo(remainingBytes);
        assertThat(buffer.position()).isEqualTo(position);
        assertThat(buffer.limit()).isEqualTo(limit);
    }

    private static void assertDuplicateProtectsState(Buffer buffer) {
        int originalLimit = buffer.capacity() - 1;
        buffer.limit(originalLimit);
        buffer.position(1);
        buffer.mark();
        buffer.position(2);

        Buffer duplicate = TypedArrays.getConversionBuffer(buffer);
        assertThat(duplicate).isNotSameInstanceAs(buffer);
        duplicate.position(0);
        duplicate.limit(duplicate.capacity());

        assertThat(buffer.position()).isEqualTo(2);
        assertThat(buffer.limit()).isEqualTo(originalLimit);
        buffer.reset();
        assertThat(buffer.position()).isEqualTo(1);
    }
}
