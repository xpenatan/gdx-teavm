package com.github.xpenatan.gdx.teavm.backends.shared.utils;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;
import org.teavm.classlib.java.nio.TBuffer;
import org.teavm.classlib.java.nio.TNativeBufferUtil;
import org.teavm.interop.Address;

public final class BufferAddressUtils {
    private BufferAddressUtils() {
    }

    public static Address of(Buffer buffer) {
        if(buffer == null) {
            return Address.fromInt(0);
        }
        if(buffer instanceof ByteBuffer) {
            return ofInternal((ByteBuffer)buffer);
        }
        if(buffer instanceof ShortBuffer) {
            return ofInternal((ShortBuffer)buffer);
        }
        if(buffer instanceof IntBuffer) {
            return ofInternal((IntBuffer)buffer);
        }
        if(buffer instanceof LongBuffer) {
            return ofInternal((LongBuffer)buffer);
        }
        if(buffer instanceof FloatBuffer) {
            return ofInternal((FloatBuffer)buffer);
        }
        if(buffer instanceof DoubleBuffer) {
            return ofInternal((DoubleBuffer)buffer);
        }
        if(buffer instanceof CharBuffer) {
            return ofInternal((CharBuffer)buffer);
        }
        throw new IllegalArgumentException("Unsupported buffer type: " + buffer.getClass());
    }

    public static Address ofInternal(ByteBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 1);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add(buffer.arrayOffset() + buffer.position());
        }
        byte[] data = new byte[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    public static Address ofInternal(ShortBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 2);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 2);
        }
        short[] data = new short[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    public static Address ofInternal(IntBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 4);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 4);
        }
        int[] data = new int[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    public static Address ofInternal(LongBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 8);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 8);
        }
        long[] data = new long[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    public static Address ofInternal(FloatBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 4);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 4);
        }
        float[] data = new float[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    public static Address ofInternal(DoubleBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 8);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 8);
        }
        double[] data = new double[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    public static Address ofInternal(CharBuffer buffer) {
        if(buffer.isDirect()) {
            return nativeAddress(buffer, 2);
        }
        if(buffer.hasArray()) {
            return Address.ofData(buffer.array()).add((buffer.arrayOffset() + buffer.position()) * 2);
        }
        char[] data = new char[buffer.remaining()];
        int pos = buffer.position();
        buffer.get(data);
        buffer.position(pos);
        return Address.ofData(data);
    }

    private static Address nativeAddress(Buffer buffer, int elementSize) {
        Address address = TNativeBufferUtil.getAddress((TBuffer)(Object)buffer);
        return address.add(buffer.position() * elementSize);
    }
}
