package com.badlogic.gdx.math;

import com.github.xpenatan.gdx.backends.web.emu.Emulate;
import java.util.Random;

@Emulate(MathUtils.class)
public final class MathUtilsEmu {
    static public final float nanoToSec = 1 / 1000000000f;

    static public final float FLOAT_ROUNDING_ERROR = 0.000001f; // 32 bits
    static public final float PI = 3.1415927f;
    static public final float PI2 = PI * 2;

    static public final float E = 2.7182818f;

    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;

    static private final float radFull = PI * 2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;

    static public final float radiansToDegrees = 180f / PI;
    static public final float radDeg = radiansToDegrees;
    static public final float degreesToRadians = PI / 180;
    static public final float degRad = degreesToRadians;

    static private class Sin {
        static final float[] table = new float[SIN_COUNT];

        static {
            for(int i = 0; i < SIN_COUNT; i++)
                table[i] = (float)Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            for(int i = 0; i < 360; i += 90)
                table[(int)(i * degToIndex) & SIN_MASK] = (float)Math.sin(i * degreesToRadians);
        }
    }

    static public float sin(float radians) {
        return Sin.table[(int)(radians * radToIndex) & SIN_MASK];
    }

    static public float cos(float radians) {
        return Sin.table[(int)((radians + PI / 2) * radToIndex) & SIN_MASK];
    }

    static public float sinDeg(float degrees) {
        return Sin.table[(int)(degrees * degToIndex) & SIN_MASK];
    }

    static public float cosDeg(float degrees) {
        return Sin.table[(int)((degrees + 90) * degToIndex) & SIN_MASK];
    }

    static public float atan2(float y, float x) {
        if(x == 0f) {
            if(y > 0f) return PI / 2;
            if(y == 0f) return 0f;
            return -PI / 2;
        }
        final float atan, z = y / x;
        if(Math.abs(z) < 1f) {
            atan = z / (1f + 0.28f * z * z);
            if(x < 0f) return atan + (y < 0f ? -PI : PI);
            return atan;
        }
        atan = PI / 2 - z / (z * z + 0.28f);
        return y < 0f ? atan - PI : atan;
    }

    static public Random random = new Random();

    static public int random(int range) {
        return random.nextInt(range + 1);
    }

    static public int random(int start, int end) {
        return start + random.nextInt(end - start + 1);
    }

    static public long random(long range) {
        return (long)(random.nextDouble() * range);
    }

    static public long random(long start, long end) {
        return start + (long)(random.nextDouble() * (end - start));
    }

    static public boolean randomBoolean() {
        return random.nextBoolean();
    }

    static public boolean randomBoolean(float chance) {
        return MathUtils.random() < chance;
    }

    static public float random() {
        return random.nextFloat();
    }

    static public float random(float range) {
        return random.nextFloat() * range;
    }

    static public float random(float start, float end) {
        return start + random.nextFloat() * (end - start);
    }

    static public int randomSign() {
        return 1 | (random.nextInt() >> 31);
    }

    public static float randomTriangular() {
        return random.nextFloat() - random.nextFloat();
    }

    public static float randomTriangular(float max) {
        return (random.nextFloat() - random.nextFloat()) * max;
    }

    public static float randomTriangular(float min, float max) {
        return randomTriangular(min, max, (min + max) * 0.5f);
    }

    public static float randomTriangular(float min, float max, float mode) {
        float u = random.nextFloat();
        float d = max - min;
        if(u <= (mode - min) / d) return min + (float)Math.sqrt(u * d * (mode - min));
        return max - (float)Math.sqrt((1 - u) * d * (max - mode));
    }

    static public int nextPowerOfTwo(int value) {
        if(value == 0) return 1;
        value--;
        value |= value >> 1;
        value |= value >> 2;
        value |= value >> 4;
        value |= value >> 8;
        value |= value >> 16;
        return value + 1;
    }

    static public boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }

    static public short clamp(short value, short min, short max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    static public int clamp(int value, int min, int max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    static public long clamp(long value, long min, long max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    static public float clamp(float value, float min, float max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    static public double clamp(double value, double min, double max) {
        if(value < min) return min;
        if(value > max) return max;
        return value;
    }

    static public float lerp(float fromValue, float toValue, float progress) {
        return fromValue + (toValue - fromValue) * progress;
    }

    public static float lerpAngle(float fromRadians, float toRadians, float progress) {
        float delta = ((toRadians - fromRadians + PI2 + PI) % PI2) - PI;
        return (fromRadians + delta * progress + PI2) % PI2;
    }

    public static float lerpAngleDeg(float fromDegrees, float toDegrees, float progress) {
        float delta = ((toDegrees - fromDegrees + 360 + 180) % 360) - 180;
        return (fromDegrees + delta * progress + 360) % 360;
    }

    static private final int BIG_ENOUGH_INT = 16 * 1024;
    static private final double BIG_ENOUGH_FLOOR = BIG_ENOUGH_INT;
    static private final double CEIL = 0.9999999;
    static private final double BIG_ENOUGH_CEIL = 16384.999999999996;
    static private final double BIG_ENOUGH_ROUND = BIG_ENOUGH_INT + 0.5f;

    static public int floor(float value) {
        return (int)(value + BIG_ENOUGH_FLOOR) - BIG_ENOUGH_INT;
    }

    static public int floorPositive(float value) {
        return (int)value;
    }

    static public int ceil(float value) {
        return (int)(value + BIG_ENOUGH_CEIL) - BIG_ENOUGH_INT;
    }

    static public int ceilPositive(float value) {
        return (int)(value + CEIL);
    }

    static public int round(float value) {
        return (int)(value + BIG_ENOUGH_ROUND) - BIG_ENOUGH_INT;
    }

    static public int roundPositive(float value) {
        return (int)(value + 0.5f);
    }

    static public boolean isZero(float value) {
        return Math.abs(value) <= FLOAT_ROUNDING_ERROR;
    }

    static public boolean isZero(float value, float tolerance) {
        return Math.abs(value) <= tolerance;
    }

    static public boolean isEqual(float a, float b) {
        return Math.abs(a - b) <= FLOAT_ROUNDING_ERROR;
    }

    static public boolean isEqual(float a, float b, float tolerance) {
        return Math.abs(a - b) <= tolerance;
    }

    static public float log(float a, float value) {
        return (float)(Math.log(value) / Math.log(a));
    }

    static public float log2(float value) {
        return log(2, value);
    }
}
