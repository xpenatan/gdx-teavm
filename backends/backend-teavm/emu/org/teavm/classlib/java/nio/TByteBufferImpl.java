/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.classlib.java.nio;

class TByteBufferImpl extends org.teavm.classlib.java.nio.TByteBuffer {
    private boolean direct;
    private boolean readOnly;

    public TByteBufferImpl(int capacity, boolean direct) {
        this(0, capacity, new byte[capacity], 0, capacity, direct, false);
    }

    public TByteBufferImpl(int start, int capacity, byte[] array, int position, int limit,
            boolean direct, boolean readOnly) {
        super(start, capacity, array, position, limit);
        this.direct = direct;
        this.readOnly = readOnly;
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer slice() {
        return new TByteBufferImpl(position + start, limit - position,  array(), 0, limit - position,
                direct, readOnly);
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer duplicate() {
        return new TByteBufferImpl(start, capacity, array(), position, limit, direct, readOnly);
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer asReadOnlyBuffer() {
        return new TByteBufferImpl(start, capacity, array(), position, limit, direct, true);
    }

    @Override
    public byte get() {
        if (position >= limit) {
            throw new TBufferUnderflowException();
        }
        return array.get(start + position++);
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer put(byte b) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (position >= limit) {
            throw new TBufferOverflowException();
        }
        array.set(start + position++, b);
        return this;
    }

    @Override
    public byte get(int index) {
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + limit + ")");
        }
        return array.get(start + index);
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer put(int index, byte b) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (index < 0 || index >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + limit + ")");
        }
        array.set(start + index, b);
        return this;
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer compact() {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        int sz = remaining();
        if (position > 0) {
            int dst = start;
            int src = start + position;
            for (int i = 0; i < sz; ++i) {
                array.set(dst++, array.get(src++));
            }
        }
        position = sz;
        limit = capacity;
        mark = -1;
        return this;
    }

    @Override
    public boolean isDirect() {
        return direct;
    }

    @Override
    public boolean isReadOnly() {
        return readOnly;
    }

    @Override
    public char getChar() {
        if (position + 1 >= limit) {
            throw new TBufferUnderflowException();
        }
        int a = array.get(start + position) & 0xFF;
        int b = array.get(start + position + 1) & 0xFF;
        position += 2;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (char) ((a << 8) | b);
        } else {
            return (char) ((b << 8) | a);
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putChar(char value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (position + 1 >= limit) {
            throw new TBufferOverflowException();
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + position++, (byte) (value >> 8));
            array.set(start + position++, (byte) value);
        } else {
            array.set(start + position++, (byte) value);
            array.set(start + position++, (byte) (value >> 8));
        }
        return this;
    }

    @Override
    public char getChar(int index) {
        if (index < 0 || index + 1 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 1) + ")");
        }
        int a = array.get(start + index) & 0xFF;
        int b = array.get(start + index + 1) & 0xFF;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (char) ((a << 8) | b);
        } else {
            return (char) ((b << 8) | a);
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putChar(int index, char value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (index < 0 || index + 1 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 1) + ")");
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + index, (byte) (value >> 8));
            array.set(start + index + 1, (byte) value);
        } else {
            array.set(start + index, (byte) value);
            array.set(start + index + 1, (byte) (value >> 8));
        }
        return this;
    }

    @Override
    public TCharBuffer asCharBuffer() {
        int sz = remaining() / 2;
        if (order == TByteOrder.BIG_ENDIAN) {
            return new org.teavm.classlib.java.nio.TCharBufferOverByteBufferBigEndian(start + position, sz, this, 0, sz, isReadOnly());
        } else {
            return new org.teavm.classlib.java.nio.TCharBufferOverByteBufferLittleEndian(start + position, sz, this, 0, sz, isReadOnly());
        }
    }

    @Override
    public short getShort() {
        if (position + 1 >= limit) {
            throw new TBufferUnderflowException();
        }
        int a = array.get(start + position) & 0xFF;
        int b = array.get(start + position + 1) & 0xFF;
        position += 2;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (short) ((a << 8) | b);
        } else {
            return (short) ((b << 8) | a);
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putShort(short value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (position + 1 >= limit) {
            throw new TBufferOverflowException();
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + position++, (byte) (value >> 8));
            array.set(start + position++, (byte) value);
        } else {
            array.set(start + position++, (byte) value);
            array.set(start + position++, (byte) (value >> 8));
        }
        return this;
    }

    @Override
    public short getShort(int index) {
        if (index < 0 || index + 1 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 1) + ")");
        }
        int a = array.get(start + index) & 0xFF;
        int b = array.get(start + index + 1) & 0xFF;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (short) ((a << 8) | b);
        } else {
            return (short) ((b << 8) | a);
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putShort(int index, short value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (index < 0 || index + 1 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 1) + ")");
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + index, (byte) (value >> 8));
            array.set(start + index + 1, (byte) value);
        } else {
            array.set(start + index, (byte) value);
            array.set(start + index + 1, (byte) (value >> 8));
        }
        return this;
    }

    @Override
    public TShortBuffer asShortBuffer() {
        int sz = remaining() / 2;
        if (order == TByteOrder.BIG_ENDIAN) {
            return new org.teavm.classlib.java.nio.TShortBufferOverByteBufferBigEndian(start + position, sz, this, 0, sz, isReadOnly());
        } else {
            return new org.teavm.classlib.java.nio.TShortBufferOverByteBufferLittleEndian(start + position, sz, this, 0, sz, isReadOnly());
        }
    }

    @Override
    public int getInt() {
        if (position + 3 >= limit) {
            throw new TBufferUnderflowException();
        }
        int a = array.get(start + position) & 0xFF;
        int b = array.get(start + position + 1) & 0xFF;
        int c = array.get(start + position + 2) & 0xFF;
        int d = array.get(start + position + 3) & 0xFF;
        position += 4;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (a << 24) | (b << 16) | (c << 8) | d;
        } else {
            return (d << 24) | (c << 16) | (b << 8) | a;
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putInt(int value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (position + 3 >= limit) {
            throw new TBufferOverflowException();
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + position++, (byte) (value >> 24));
            array.set(start + position++, (byte) (value >> 16));
            array.set(start + position++, (byte) (value >> 8));
            array.set(start + position++, (byte) value);
        } else {
            array.set(start + position++, (byte) value);
            array.set(start + position++, (byte) (value >> 8));
            array.set(start + position++, (byte) (value >> 16));
            array.set(start + position++, (byte) (value >> 24));
        }
        return this;
    }

    @Override
    public int getInt(int index) {
        if (index < 0 || index + 3 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 3) + ")");
        }
        int a = array.get(start + index) & 0xFF;
        int b = array.get(start + index + 1) & 0xFF;
        int c = array.get(start + index + 2) & 0xFF;
        int d = array.get(start + index + 3) & 0xFF;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (a << 24) | (b << 16) | (c << 8) | d;
        } else {
            return (d << 24) | (c << 16) | (b << 8) | a;
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putInt(int index, int value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (index < 0 || index + 3 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 3) + ")");
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + index, (byte) (value >> 24));
            array.set(start + index + 1, (byte) (value >> 16));
            array.set(start + index + 2, (byte) (value >> 8));
            array.set(start + index + 3, (byte) value);
        } else {
            array.set(start + index, (byte) value);
            array.set(start + index + 1, (byte) (value >> 8));
            array.set(start + index + 2, (byte) (value >> 16));
            array.set(start + index + 3, (byte) (value >> 24));
        }
        return this;
    }

    @Override
    public TIntBuffer asIntBuffer() {
        int sz = remaining() / 4;
        if (order == TByteOrder.BIG_ENDIAN) {
            return new TIntBufferOverByteBufferBigEndian(start + position, sz, this, 0, sz, isReadOnly());
        } else {
            return new TIntBufferOverByteBufferLittleEndian(start + position, sz, this, 0, sz, isReadOnly());
        }
    }

    @Override
    public float getFloat() {
        return Float.intBitsToFloat(getInt());
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putFloat(float value) {
        return putInt(Float.floatToRawIntBits(value));
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putFloat(int index, float value) {
        return putInt(index, Float.floatToRawIntBits(value));
    }

    @Override
    public float getFloat(int index) {
        return Float.intBitsToFloat(getInt(index));
    }

    @Override
    public double getDouble() {
        return Double.longBitsToDouble(getLong());
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putDouble(double value) {
        return putLong(Double.doubleToRawLongBits(value));
    }

    @Override
    public double getDouble(int index) {
        return Double.longBitsToDouble(getLong(index));
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putDouble(int index, double value) {
        return putLong(index, Double.doubleToRawLongBits(value));
    }

    @Override
    public long getLong() {
        if (position + 7 >= limit) {
            throw new TBufferUnderflowException();
        }
        long a = array.get(start + position) & 0xFF;
        long b = array.get(start + position + 1) & 0xFF;
        long c = array.get(start + position + 2) & 0xFF;
        long d = array.get(start + position + 3) & 0xFF;
        long e = array.get(start + position + 4) & 0xFF;
        long f = array.get(start + position + 5) & 0xFF;
        long g = array.get(start + position + 6) & 0xFF;
        long h = array.get(start + position + 7) & 0xFF;
        position += 8;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (a << 56) | (b << 48) | (c << 40) | (d << 32) | (e << 24) | (f << 16) | (g << 8) | h;
        } else {
            return (h << 56) | (g << 48) | (f << 40) | (e << 32) | (d << 24) | (c << 16) | (b << 8) | a;
        }
    }

    @Override
    public org.teavm.classlib.java.nio.TByteBuffer putLong(long value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (position + 7 >= limit) {
            throw new TBufferOverflowException();
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + position++, (byte) (value >> 56));
            array.set(start + position++, (byte) (value >> 48));
            array.set(start + position++, (byte) (value >> 40));
            array.set(start + position++, (byte) (value >> 32));
            array.set(start + position++, (byte) (value >> 24));
            array.set(start + position++, (byte) (value >> 16));
            array.set(start + position++, (byte) (value >> 8));
            array.set(start + position++, (byte) value);
        } else {
            array.set(start + position++, (byte) value);
            array.set(start + position++, (byte) (value >> 8));
            array.set(start + position++, (byte) (value >> 16));
            array.set(start + position++, (byte) (value >> 24));
            array.set(start + position++, (byte) (value >> 32));
            array.set(start + position++, (byte) (value >> 40));
            array.set(start + position++, (byte) (value >> 48));
            array.set(start + position++, (byte) (value >> 56));
        }
        return this;
    }

    @Override
    public long getLong(int index) {
        if (index < 0 || index + 7 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 7) + ")");
        }
        long a = array.get(start + index) & 0xFF;
        long b = array.get(start + index + 1) & 0xFF;
        long c = array.get(start + index + 2) & 0xFF;
        long d = array.get(start + index + 3) & 0xFF;
        long e = array.get(start + index + 4) & 0xFF;
        long f = array.get(start + index + 5) & 0xFF;
        long g = array.get(start + index + 6) & 0xFF;
        long h = array.get(start + index + 7) & 0xFF;
        position += 8;
        if (order == TByteOrder.BIG_ENDIAN) {
            return (a << 56) | (b << 48) | (c << 40) | (d << 32) | (e << 24) | (f << 16) | (g << 8) | h;
        } else {
            return (h << 56) | (g << 48) | (f << 40) | (e << 32) | (d << 24) | (c << 16) | (b << 8) | a;
        }
    }

    @Override
    public TByteBuffer putLong(int index, long value) {
        if (readOnly) {
            throw new TReadOnlyBufferException();
        }
        if (index < 0 || index + 3 >= limit) {
            throw new IndexOutOfBoundsException("Index " + index + " is outside of range [0;" + (limit - 3) + ")");
        }
        if (order == TByteOrder.BIG_ENDIAN) {
            array.set(start + index + 0, (byte) (value >> 56));
            array.set(start + index + 1, (byte) (value >> 48));
            array.set(start + index + 2, (byte) (value >> 40));
            array.set(start + index + 3, (byte) (value >> 32));
            array.set(start + index + 4, (byte) (value >> 24));
            array.set(start + index + 5, (byte) (value >> 16));
            array.set(start + index + 6, (byte) (value >> 8));
            array.set(start + index + 7, (byte) value);
        } else {
            array.set(start + index + 0, (byte) value);
            array.set(start + index + 1, (byte) (value >> 8));
            array.set(start + index + 2, (byte) (value >> 16));
            array.set(start + index + 3, (byte) (value >> 24));
            array.set(start + index + 4, (byte) (value >> 32));
            array.set(start + index + 5, (byte) (value >> 40));
            array.set(start + index + 6, (byte) (value >> 48));
            array.set(start + index + 7, (byte) (value >> 56));
        }
        return this;
    }

    @Override
    public TLongBuffer asLongBuffer() {
        int sz = remaining() / 8;
        if (order == TByteOrder.BIG_ENDIAN) {
            return new TLongBufferOverByteBufferBigEndian(start + position, sz, this, 0, sz, isReadOnly());
        } else {
            return new org.teavm.classlib.java.nio.TLongBufferOverByteBufferLittleEndian(start + position, sz, this, 0, sz, isReadOnly());
        }
    }

    @Override
    public TFloatBuffer asFloatBuffer() {
        int sz = remaining() / 4;
        if (order == TByteOrder.LITTLE_ENDIAN) {
            return new org.teavm.classlib.java.nio.TFloatBufferOverByteBufferBigEndian(start + position, sz, this, 0, sz, isReadOnly());
        } else {
            return new org.teavm.classlib.java.nio.TFloatBufferOverByteBufferLittleEndian(start + position, sz, this, 0, sz, isReadOnly());
        }
    }

    @Override
    public TDoubleBuffer asDoubleBuffer() {
        int sz = remaining() / 8;
        TDoubleBufferOverByteBuffer result = new TDoubleBufferOverByteBuffer(start + position, sz, this, 0, sz,
                isReadOnly());
        result.byteOrder = order;
        return result;
    }
}
