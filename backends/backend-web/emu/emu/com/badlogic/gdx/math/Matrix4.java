package emu.com.badlogic.gdx.math;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

public class Matrix4 implements Serializable {
    private static final long serialVersionUID = -2717655254359579617L;
    public static final int M00 = 0;
    public static final int M01 = 4;
    public static final int M02 = 8;
    public static final int M03 = 12;
    public static final int M10 = 1;
    public static final int M11 = 5;
    public static final int M12 = 9;
    public static final int M13 = 13;
    public static final int M20 = 2;
    public static final int M21 = 6;
    public static final int M22 = 10;
    public static final int M23 = 14;
    public static final int M30 = 3;
    public static final int M31 = 7;
    public static final int M32 = 11;
    public static final int M33 = 15;

    @Deprecated
    public static final float tmp[] = new float[16]; // FIXME Change to private access
    public final float val[] = new float[16];

    public Matrix4() {
        val[M00] = 1f;
        val[M11] = 1f;
        val[M22] = 1f;
        val[M33] = 1f;
    }

    public Matrix4(Matrix4 matrix) {
        this.set(matrix);
    }

    public Matrix4(float[] values) {
        this.set(values);
    }

    public Matrix4(Quaternion quaternion) {
        this.set(quaternion);
    }

    public Matrix4(Vector3 position, Quaternion rotation, Vector3 scale) {
        set(position, rotation, scale);
    }

    public Matrix4 set(Matrix4 matrix) {
        return this.set(matrix.val);
    }

    public Matrix4 set(float[] values) {
        val[M00] = values[M00];
        val[M10] = values[M10];
        val[M20] = values[M20];
        val[M30] = values[M30];
        val[M01] = values[M01];
        val[M11] = values[M11];
        val[M21] = values[M21];
        val[M31] = values[M31];
        val[M02] = values[M02];
        val[M12] = values[M12];
        val[M22] = values[M22];
        val[M32] = values[M32];
        val[M03] = values[M03];
        val[M13] = values[M13];
        val[M23] = values[M23];
        val[M33] = values[M33];
        return (Matrix4)(Object)this;
    }

    public Matrix4 set(Quaternion quaternion) {
        return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    public Matrix4 set(float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        return set(0f, 0f, 0f, quaternionX, quaternionY, quaternionZ, quaternionW);
    }

    public Matrix4 set(Vector3 position, Quaternion orientation) {
        return set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w);
    }

    public Matrix4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY,
                       float quaternionZ, float quaternionW) {
        final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
        final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
        final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
        final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

        val[M00] = (1.0f - (yy + zz));
        val[M01] = (xy - wz);
        val[M02] = (xz + wy);
        val[M03] = translationX;

        val[M10] = (xy + wz);
        val[M11] = (1.0f - (xx + zz));
        val[M12] = (yz - wx);
        val[M13] = translationY;

        val[M20] = (xz - wy);
        val[M21] = (yz + wx);
        val[M22] = (1.0f - (xx + yy));
        val[M23] = translationZ;

        val[M30] = 0.f;
        val[M31] = 0.f;
        val[M32] = 0.f;
        val[M33] = 1.0f;
        return (Matrix4)(Object)this;
    }

    public Matrix4 set(Vector3 position, Quaternion orientation, Vector3 scale) {
        return set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w, scale.x,
                scale.y, scale.z);
    }

    public Matrix4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY,
                       float quaternionZ, float quaternionW, float scaleX, float scaleY, float scaleZ) {
        final float xs = quaternionX * 2f, ys = quaternionY * 2f, zs = quaternionZ * 2f;
        final float wx = quaternionW * xs, wy = quaternionW * ys, wz = quaternionW * zs;
        final float xx = quaternionX * xs, xy = quaternionX * ys, xz = quaternionX * zs;
        final float yy = quaternionY * ys, yz = quaternionY * zs, zz = quaternionZ * zs;

        val[M00] = scaleX * (1.0f - (yy + zz));
        val[M01] = scaleY * (xy - wz);
        val[M02] = scaleZ * (xz + wy);
        val[M03] = translationX;

        val[M10] = scaleX * (xy + wz);
        val[M11] = scaleY * (1.0f - (xx + zz));
        val[M12] = scaleZ * (yz - wx);
        val[M13] = translationY;

        val[M20] = scaleX * (xz - wy);
        val[M21] = scaleY * (yz + wx);
        val[M22] = scaleZ * (1.0f - (xx + yy));
        val[M23] = translationZ;

        val[M30] = 0.f;
        val[M31] = 0.f;
        val[M32] = 0.f;
        val[M33] = 1.0f;
        return (Matrix4)(Object)this;
    }

    public Matrix4 set(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
        val[M00] = xAxis.x;
        val[M01] = xAxis.y;
        val[M02] = xAxis.z;
        val[M10] = yAxis.x;
        val[M11] = yAxis.y;
        val[M12] = yAxis.z;
        val[M20] = zAxis.x;
        val[M21] = zAxis.y;
        val[M22] = zAxis.z;
        val[M03] = pos.x;
        val[M13] = pos.y;
        val[M23] = pos.z;
        val[M30] = 0;
        val[M31] = 0;
        val[M32] = 0;
        val[M33] = 1;
        return (Matrix4)(Object)this;
    }

    public Matrix4 cpy() {
        return new Matrix4((Matrix4)(Object)this);
    }

    public Matrix4 trn(Vector3 vector) {
        val[M03] += vector.x;
        val[M13] += vector.y;
        val[M23] += vector.z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 trn(float x, float y, float z) {
        val[M03] += x;
        val[M13] += y;
        val[M23] += z;
        return (Matrix4)(Object)this;
    }

    public float[] getValues() {
        return val;
    }

    public Matrix4 mul(Matrix4 matrix) {
        tmp[M00] = val[M00] * matrix.val[M00] + val[M01] * matrix.val[M10] + val[M02] * matrix.val[M20] + val[M03]
                * matrix.val[M30];
        tmp[M01] = val[M00] * matrix.val[M01] + val[M01] * matrix.val[M11] + val[M02] * matrix.val[M21] + val[M03]
                * matrix.val[M31];
        tmp[M02] = val[M00] * matrix.val[M02] + val[M01] * matrix.val[M12] + val[M02] * matrix.val[M22] + val[M03]
                * matrix.val[M32];
        tmp[M03] = val[M00] * matrix.val[M03] + val[M01] * matrix.val[M13] + val[M02] * matrix.val[M23] + val[M03]
                * matrix.val[M33];
        tmp[M10] = val[M10] * matrix.val[M00] + val[M11] * matrix.val[M10] + val[M12] * matrix.val[M20] + val[M13]
                * matrix.val[M30];
        tmp[M11] = val[M10] * matrix.val[M01] + val[M11] * matrix.val[M11] + val[M12] * matrix.val[M21] + val[M13]
                * matrix.val[M31];
        tmp[M12] = val[M10] * matrix.val[M02] + val[M11] * matrix.val[M12] + val[M12] * matrix.val[M22] + val[M13]
                * matrix.val[M32];
        tmp[M13] = val[M10] * matrix.val[M03] + val[M11] * matrix.val[M13] + val[M12] * matrix.val[M23] + val[M13]
                * matrix.val[M33];
        tmp[M20] = val[M20] * matrix.val[M00] + val[M21] * matrix.val[M10] + val[M22] * matrix.val[M20] + val[M23]
                * matrix.val[M30];
        tmp[M21] = val[M20] * matrix.val[M01] + val[M21] * matrix.val[M11] + val[M22] * matrix.val[M21] + val[M23]
                * matrix.val[M31];
        tmp[M22] = val[M20] * matrix.val[M02] + val[M21] * matrix.val[M12] + val[M22] * matrix.val[M22] + val[M23]
                * matrix.val[M32];
        tmp[M23] = val[M20] * matrix.val[M03] + val[M21] * matrix.val[M13] + val[M22] * matrix.val[M23] + val[M23]
                * matrix.val[M33];
        tmp[M30] = val[M30] * matrix.val[M00] + val[M31] * matrix.val[M10] + val[M32] * matrix.val[M20] + val[M33]
                * matrix.val[M30];
        tmp[M31] = val[M30] * matrix.val[M01] + val[M31] * matrix.val[M11] + val[M32] * matrix.val[M21] + val[M33]
                * matrix.val[M31];
        tmp[M32] = val[M30] * matrix.val[M02] + val[M31] * matrix.val[M12] + val[M32] * matrix.val[M22] + val[M33]
                * matrix.val[M32];
        tmp[M33] = val[M30] * matrix.val[M03] + val[M31] * matrix.val[M13] + val[M32] * matrix.val[M23] + val[M33]
                * matrix.val[M33];
        return this.set(tmp);
    }

    public Matrix4 mulLeft(Matrix4 matrix) {
        tmp[M00] = matrix.val[M00] * val[M00] + matrix.val[M01] * val[M10] + matrix.val[M02] * val[M20] + matrix.val[M03]
                * val[M30];
        tmp[M01] = matrix.val[M00] * val[M01] + matrix.val[M01] * val[M11] + matrix.val[M02] * val[M21] + matrix.val[M03]
                * val[M31];
        tmp[M02] = matrix.val[M00] * val[M02] + matrix.val[M01] * val[M12] + matrix.val[M02] * val[M22] + matrix.val[M03]
                * val[M32];
        tmp[M03] = matrix.val[M00] * val[M03] + matrix.val[M01] * val[M13] + matrix.val[M02] * val[M23] + matrix.val[M03]
                * val[M33];
        tmp[M10] = matrix.val[M10] * val[M00] + matrix.val[M11] * val[M10] + matrix.val[M12] * val[M20] + matrix.val[M13]
                * val[M30];
        tmp[M11] = matrix.val[M10] * val[M01] + matrix.val[M11] * val[M11] + matrix.val[M12] * val[M21] + matrix.val[M13]
                * val[M31];
        tmp[M12] = matrix.val[M10] * val[M02] + matrix.val[M11] * val[M12] + matrix.val[M12] * val[M22] + matrix.val[M13]
                * val[M32];
        tmp[M13] = matrix.val[M10] * val[M03] + matrix.val[M11] * val[M13] + matrix.val[M12] * val[M23] + matrix.val[M13]
                * val[M33];
        tmp[M20] = matrix.val[M20] * val[M00] + matrix.val[M21] * val[M10] + matrix.val[M22] * val[M20] + matrix.val[M23]
                * val[M30];
        tmp[M21] = matrix.val[M20] * val[M01] + matrix.val[M21] * val[M11] + matrix.val[M22] * val[M21] + matrix.val[M23]
                * val[M31];
        tmp[M22] = matrix.val[M20] * val[M02] + matrix.val[M21] * val[M12] + matrix.val[M22] * val[M22] + matrix.val[M23]
                * val[M32];
        tmp[M23] = matrix.val[M20] * val[M03] + matrix.val[M21] * val[M13] + matrix.val[M22] * val[M23] + matrix.val[M23]
                * val[M33];
        tmp[M30] = matrix.val[M30] * val[M00] + matrix.val[M31] * val[M10] + matrix.val[M32] * val[M20] + matrix.val[M33]
                * val[M30];
        tmp[M31] = matrix.val[M30] * val[M01] + matrix.val[M31] * val[M11] + matrix.val[M32] * val[M21] + matrix.val[M33]
                * val[M31];
        tmp[M32] = matrix.val[M30] * val[M02] + matrix.val[M31] * val[M12] + matrix.val[M32] * val[M22] + matrix.val[M33]
                * val[M32];
        tmp[M33] = matrix.val[M30] * val[M03] + matrix.val[M31] * val[M13] + matrix.val[M32] * val[M23] + matrix.val[M33]
                * val[M33];
        return this.set(tmp);
    }

    public Matrix4 tra() {
        tmp[M00] = val[M00];
        tmp[M01] = val[M10];
        tmp[M02] = val[M20];
        tmp[M03] = val[M30];
        tmp[M10] = val[M01];
        tmp[M11] = val[M11];
        tmp[M12] = val[M21];
        tmp[M13] = val[M31];
        tmp[M20] = val[M02];
        tmp[M21] = val[M12];
        tmp[M22] = val[M22];
        tmp[M23] = val[M32];
        tmp[M30] = val[M03];
        tmp[M31] = val[M13];
        tmp[M32] = val[M23];
        tmp[M33] = val[M33];
        return set(tmp);
    }

    public Matrix4 idt() {
        val[M00] = 1;
        val[M01] = 0;
        val[M02] = 0;
        val[M03] = 0;
        val[M10] = 0;
        val[M11] = 1;
        val[M12] = 0;
        val[M13] = 0;
        val[M20] = 0;
        val[M21] = 0;
        val[M22] = 1;
        val[M23] = 0;
        val[M30] = 0;
        val[M31] = 0;
        val[M32] = 0;
        val[M33] = 1;
        return (Matrix4)(Object)this;
    }

    public Matrix4 inv() {
        float l_det = val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03] - val[M30] * val[M11]
                * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03] + val[M20] * val[M11] * val[M32] * val[M03] - val[M10]
                * val[M21] * val[M32] * val[M03] - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13] - val[M20] * val[M01] * val[M32]
                * val[M13] + val[M00] * val[M21] * val[M32] * val[M13] + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31]
                * val[M02] * val[M23] - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23] + val[M10]
                * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23] - val[M20] * val[M11] * val[M02] * val[M33]
                + val[M10] * val[M21] * val[M02] * val[M33] + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12]
                * val[M33] - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
        if(l_det == 0f) throw new RuntimeException("non-invertible matrix");
        float inv_det = 1.0f / l_det;
        tmp[M00] = val[M12] * val[M23] * val[M31] - val[M13] * val[M22] * val[M31] + val[M13] * val[M21] * val[M32] - val[M11]
                * val[M23] * val[M32] - val[M12] * val[M21] * val[M33] + val[M11] * val[M22] * val[M33];
        tmp[M01] = val[M03] * val[M22] * val[M31] - val[M02] * val[M23] * val[M31] - val[M03] * val[M21] * val[M32] + val[M01]
                * val[M23] * val[M32] + val[M02] * val[M21] * val[M33] - val[M01] * val[M22] * val[M33];
        tmp[M02] = val[M02] * val[M13] * val[M31] - val[M03] * val[M12] * val[M31] + val[M03] * val[M11] * val[M32] - val[M01]
                * val[M13] * val[M32] - val[M02] * val[M11] * val[M33] + val[M01] * val[M12] * val[M33];
        tmp[M03] = val[M03] * val[M12] * val[M21] - val[M02] * val[M13] * val[M21] - val[M03] * val[M11] * val[M22] + val[M01]
                * val[M13] * val[M22] + val[M02] * val[M11] * val[M23] - val[M01] * val[M12] * val[M23];
        tmp[M10] = val[M13] * val[M22] * val[M30] - val[M12] * val[M23] * val[M30] - val[M13] * val[M20] * val[M32] + val[M10]
                * val[M23] * val[M32] + val[M12] * val[M20] * val[M33] - val[M10] * val[M22] * val[M33];
        tmp[M11] = val[M02] * val[M23] * val[M30] - val[M03] * val[M22] * val[M30] + val[M03] * val[M20] * val[M32] - val[M00]
                * val[M23] * val[M32] - val[M02] * val[M20] * val[M33] + val[M00] * val[M22] * val[M33];
        tmp[M12] = val[M03] * val[M12] * val[M30] - val[M02] * val[M13] * val[M30] - val[M03] * val[M10] * val[M32] + val[M00]
                * val[M13] * val[M32] + val[M02] * val[M10] * val[M33] - val[M00] * val[M12] * val[M33];
        tmp[M13] = val[M02] * val[M13] * val[M20] - val[M03] * val[M12] * val[M20] + val[M03] * val[M10] * val[M22] - val[M00]
                * val[M13] * val[M22] - val[M02] * val[M10] * val[M23] + val[M00] * val[M12] * val[M23];
        tmp[M20] = val[M11] * val[M23] * val[M30] - val[M13] * val[M21] * val[M30] + val[M13] * val[M20] * val[M31] - val[M10]
                * val[M23] * val[M31] - val[M11] * val[M20] * val[M33] + val[M10] * val[M21] * val[M33];
        tmp[M21] = val[M03] * val[M21] * val[M30] - val[M01] * val[M23] * val[M30] - val[M03] * val[M20] * val[M31] + val[M00]
                * val[M23] * val[M31] + val[M01] * val[M20] * val[M33] - val[M00] * val[M21] * val[M33];
        tmp[M22] = val[M01] * val[M13] * val[M30] - val[M03] * val[M11] * val[M30] + val[M03] * val[M10] * val[M31] - val[M00]
                * val[M13] * val[M31] - val[M01] * val[M10] * val[M33] + val[M00] * val[M11] * val[M33];
        tmp[M23] = val[M03] * val[M11] * val[M20] - val[M01] * val[M13] * val[M20] - val[M03] * val[M10] * val[M21] + val[M00]
                * val[M13] * val[M21] + val[M01] * val[M10] * val[M23] - val[M00] * val[M11] * val[M23];
        tmp[M30] = val[M12] * val[M21] * val[M30] - val[M11] * val[M22] * val[M30] - val[M12] * val[M20] * val[M31] + val[M10]
                * val[M22] * val[M31] + val[M11] * val[M20] * val[M32] - val[M10] * val[M21] * val[M32];
        tmp[M31] = val[M01] * val[M22] * val[M30] - val[M02] * val[M21] * val[M30] + val[M02] * val[M20] * val[M31] - val[M00]
                * val[M22] * val[M31] - val[M01] * val[M20] * val[M32] + val[M00] * val[M21] * val[M32];
        tmp[M32] = val[M02] * val[M11] * val[M30] - val[M01] * val[M12] * val[M30] - val[M02] * val[M10] * val[M31] + val[M00]
                * val[M12] * val[M31] + val[M01] * val[M10] * val[M32] - val[M00] * val[M11] * val[M32];
        tmp[M33] = val[M01] * val[M12] * val[M20] - val[M02] * val[M11] * val[M20] + val[M02] * val[M10] * val[M21] - val[M00]
                * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] + val[M00] * val[M11] * val[M22];
        val[M00] = tmp[M00] * inv_det;
        val[M01] = tmp[M01] * inv_det;
        val[M02] = tmp[M02] * inv_det;
        val[M03] = tmp[M03] * inv_det;
        val[M10] = tmp[M10] * inv_det;
        val[M11] = tmp[M11] * inv_det;
        val[M12] = tmp[M12] * inv_det;
        val[M13] = tmp[M13] * inv_det;
        val[M20] = tmp[M20] * inv_det;
        val[M21] = tmp[M21] * inv_det;
        val[M22] = tmp[M22] * inv_det;
        val[M23] = tmp[M23] * inv_det;
        val[M30] = tmp[M30] * inv_det;
        val[M31] = tmp[M31] * inv_det;
        val[M32] = tmp[M32] * inv_det;
        val[M33] = tmp[M33] * inv_det;
        return (Matrix4)(Object)this;
    }

    public float det() {
        return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03] - val[M30] * val[M11]
                * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03] + val[M20] * val[M11] * val[M32] * val[M03] - val[M10]
                * val[M21] * val[M32] * val[M03] - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13] - val[M20] * val[M01] * val[M32]
                * val[M13] + val[M00] * val[M21] * val[M32] * val[M13] + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31]
                * val[M02] * val[M23] - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23] + val[M10]
                * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23] - val[M20] * val[M11] * val[M02] * val[M33]
                + val[M10] * val[M21] * val[M02] * val[M33] + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12]
                * val[M33] - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
    }

    public float det3x3() {
        return val[M00] * val[M11] * val[M22] + val[M01] * val[M12] * val[M20] + val[M02] * val[M10] * val[M21] - val[M00]
                * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] - val[M02] * val[M11] * val[M20];
    }

    public Matrix4 setToProjection(float near, float far, float fovy, float aspectRatio) {
        idt();
        float l_fd = (float)(1.0 / Math.tan((fovy * (Math.PI / 180)) / 2.0));
        float l_a1 = (far + near) / (near - far);
        float l_a2 = (2 * far * near) / (near - far);
        val[M00] = l_fd / aspectRatio;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = l_fd;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = l_a1;
        val[M32] = -1;
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = l_a2;
        val[M33] = 0;

        return (Matrix4)(Object)this;
    }

    public Matrix4 setToProjection(float left, float right, float bottom, float top, float near, float far) {
        float x = 2.0f * near / (right - left);
        float y = 2.0f * near / (top - bottom);
        float a = (right + left) / (right - left);
        float b = (top + bottom) / (top - bottom);
        float l_a1 = (far + near) / (near - far);
        float l_a2 = (2 * far * near) / (near - far);
        val[M00] = x;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = y;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = a;
        val[M12] = b;
        val[M22] = l_a1;
        val[M32] = -1;
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = l_a2;
        val[M33] = 0;

        return (Matrix4)(Object)this;
    }

    public Matrix4 setToOrtho2D(float x, float y, float width, float height) {
        setToOrtho(x, x + width, y, y + height, 0, 1);
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToOrtho2D(float x, float y, float width, float height, float near, float far) {
        setToOrtho(x, x + width, y, y + height, near, far);
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToOrtho(float left, float right, float bottom, float top, float near, float far) {

        this.idt();
        float x_orth = 2 / (right - left);
        float y_orth = 2 / (top - bottom);
        float z_orth = -2 / (far - near);

        float tx = -(right + left) / (right - left);
        float ty = -(top + bottom) / (top - bottom);
        float tz = -(far + near) / (far - near);

        val[M00] = x_orth;
        val[M10] = 0;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = 0;
        val[M11] = y_orth;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = z_orth;
        val[M32] = 0;
        val[M03] = tx;
        val[M13] = ty;
        val[M23] = tz;
        val[M33] = 1;

        return (Matrix4)(Object)this;
    }

    public Matrix4 setTranslation(Vector3 vector) {
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setTranslation(float x, float y, float z) {
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToTranslation(Vector3 vector) {
        idt();
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToTranslation(float x, float y, float z) {
        idt();
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
        idt();
        val[M03] = translation.x;
        val[M13] = translation.y;
        val[M23] = translation.z;
        val[M00] = scaling.x;
        val[M11] = scaling.y;
        val[M22] = scaling.z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToTranslationAndScaling(float translationX, float translationY, float translationZ, float scalingX,
                                              float scalingY, float scalingZ) {
        idt();
        val[M03] = translationX;
        val[M13] = translationY;
        val[M23] = translationZ;
        val[M00] = scalingX;
        val[M11] = scalingY;
        val[M22] = scalingZ;
        return (Matrix4)(Object)this;
    }

    static Quaternion quat = new Quaternion();
    static Quaternion quat2 = new Quaternion();

    public Matrix4 setToRotation(Vector3 axis, float degrees) {
        if(degrees == 0) {
            idt();
            return (Matrix4)(Object)this;
        }
        return set(quat.set(axis, degrees));
    }

    public Matrix4 setToRotationRad(Vector3 axis, float radians) {
        if(radians == 0) {
            idt();
            return (Matrix4)(Object)this;
        }
        return set(quat.setFromAxisRad(axis, radians));
    }

    public Matrix4 setToRotation(float axisX, float axisY, float axisZ, float degrees) {
        if(degrees == 0) {
            idt();
            return (Matrix4)(Object)this;
        }
        return set(quat.setFromAxis(axisX, axisY, axisZ, degrees));
    }

    public Matrix4 setToRotationRad(float axisX, float axisY, float axisZ, float radians) {
        if(radians == 0) {
            idt();
            return (Matrix4)(Object)this;
        }
        return set(quat.setFromAxisRad(axisX, axisY, axisZ, radians));
    }

    public Matrix4 setToRotation(final Vector3 v1, final Vector3 v2) {
        return set(quat.setFromCross(v1, v2));
    }

    public Matrix4 setToRotation(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
    }

    public Matrix4 setFromEulerAngles(float yaw, float pitch, float roll) {
        quat.setEulerAngles(yaw, pitch, roll);
        return set(quat);
    }

    public Matrix4 setFromEulerAnglesRad(float yaw, float pitch, float roll) {
        quat.setEulerAnglesRad(yaw, pitch, roll);
        return set(quat);
    }

    public Matrix4 setToScaling(Vector3 vector) {
        idt();
        val[M00] = vector.x;
        val[M11] = vector.y;
        val[M22] = vector.z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setToScaling(float x, float y, float z) {
        idt();
        val[M00] = x;
        val[M11] = y;
        val[M22] = z;
        return (Matrix4)(Object)this;
    }

    static final Vector3 l_vez = new Vector3();
    static final Vector3 l_vex = new Vector3();
    static final Vector3 l_vey = new Vector3();

    public Matrix4 setToLookAt(Vector3 direction, Vector3 up) {
        l_vez.set(direction).nor();
        l_vex.set(direction).nor();
        l_vex.crs(up).nor();
        l_vey.set(l_vex).crs(l_vez).nor();
        idt();
        val[M00] = l_vex.x;
        val[M01] = l_vex.y;
        val[M02] = l_vex.z;
        val[M10] = l_vey.x;
        val[M11] = l_vey.y;
        val[M12] = l_vey.z;
        val[M20] = -l_vez.x;
        val[M21] = -l_vez.y;
        val[M22] = -l_vez.z;

        return (Matrix4)(Object)this;
    }

    static final Vector3 tmpVec = new Vector3();
    static final Matrix4 tmpMat = new Matrix4();

    public Matrix4 setToLookAt(Vector3 position, Vector3 target, Vector3 up) {
        tmpVec.set(target).sub(position);
        setToLookAt(tmpVec, up);
        this.mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));

        return (Matrix4)(Object)this;
    }

    static final Vector3 right = new Vector3();
    static final Vector3 tmpForward = new Vector3();
    static final Vector3 tmpUp = new Vector3();

    public Matrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
        tmpForward.set(forward).nor();
        right.set(tmpForward).crs(up).nor();
        tmpUp.set(right).crs(tmpForward).nor();

        this.set(right, tmpUp, tmpForward.scl(-1), position);
        return (Matrix4)(Object)this;
    }

    public String toString() {
        return "[" + val[M00] + "|" + val[M01] + "|" + val[M02] + "|" + val[M03] + "]\n" + "[" + val[M10] + "|" + val[M11] + "|"
                + val[M12] + "|" + val[M13] + "]\n" + "[" + val[M20] + "|" + val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" + "["
                + val[M30] + "|" + val[M31] + "|" + val[M32] + "|" + val[M33] + "]\n";
    }

    public Matrix4 lerp(Matrix4 matrix, float alpha) {
        for(int i = 0; i < 16; i++)
            this.val[i] = this.val[i] * (1 - alpha) + matrix.val[i] * alpha;
        return (Matrix4)(Object)this;
    }

    public Matrix4 avg(Matrix4 other, float w) {
        getScale(tmpVec);
        other.getScale(tmpForward);

        getRotation(quat);
        other.getRotation(quat2);

        getTranslation(tmpUp);
        other.getTranslation(right);

        setToScaling(tmpVec.scl(w).add(tmpForward.scl(1 - w)));
        rotate(quat.slerp(quat2, 1 - w));
        setTranslation(tmpUp.scl(w).add(right.scl(1 - w)));

        return (Matrix4)(Object)this;
    }

    public Matrix4 avg(Matrix4[] t) {
        final float w = 1.0f / t.length;

        tmpVec.set(t[0].getScale(tmpUp).scl(w));
        quat.set(t[0].getRotation(quat2).exp(w));
        tmpForward.set(t[0].getTranslation(tmpUp).scl(w));

        for(int i = 1; i < t.length; i++) {
            tmpVec.add(t[i].getScale(tmpUp).scl(w));
            quat.mul(t[i].getRotation(quat2).exp(w));
            tmpForward.add(t[i].getTranslation(tmpUp).scl(w));
        }
        quat.nor();

        setToScaling(tmpVec);
        rotate(quat);
        setTranslation(tmpForward);

        return (Matrix4)(Object)this;
    }

    public Matrix4 avg(Matrix4[] t, float[] w) {
        tmpVec.set(t[0].getScale(tmpUp).scl(w[0]));
        quat.set(t[0].getRotation(quat2).exp(w[0]));
        tmpForward.set(t[0].getTranslation(tmpUp).scl(w[0]));

        for(int i = 1; i < t.length; i++) {
            tmpVec.add(t[i].getScale(tmpUp).scl(w[i]));
            quat.mul(t[i].getRotation(quat2).exp(w[i]));
            tmpForward.add(t[i].getTranslation(tmpUp).scl(w[i]));
        }
        quat.nor();

        setToScaling(tmpVec);
        rotate(quat);
        setTranslation(tmpForward);

        return (Matrix4)(Object)this;
    }

    public Matrix4 set(Matrix3 mat) {
        val[0] = mat.val[0];
        val[1] = mat.val[1];
        val[2] = mat.val[2];
        val[3] = 0;
        val[4] = mat.val[3];
        val[5] = mat.val[4];
        val[6] = mat.val[5];
        val[7] = 0;
        val[8] = 0;
        val[9] = 0;
        val[10] = 1;
        val[11] = 0;
        val[12] = mat.val[6];
        val[13] = mat.val[7];
        val[14] = 0;
        val[15] = mat.val[8];
        return (Matrix4)(Object)this;
    }

    public Matrix4 set(Affine2 affine) {
        val[M00] = affine.m00;
        val[M10] = affine.m10;
        val[M20] = 0;
        val[M30] = 0;
        val[M01] = affine.m01;
        val[M11] = affine.m11;
        val[M21] = 0;
        val[M31] = 0;
        val[M02] = 0;
        val[M12] = 0;
        val[M22] = 1;
        val[M32] = 0;
        val[M03] = affine.m02;
        val[M13] = affine.m12;
        val[M23] = 0;
        val[M33] = 1;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setAsAffine(Affine2 affine) {
        val[M00] = affine.m00;
        val[M10] = affine.m10;
        val[M01] = affine.m01;
        val[M11] = affine.m11;
        val[M03] = affine.m02;
        val[M13] = affine.m12;
        return (Matrix4)(Object)this;
    }

    public Matrix4 setAsAffine(Matrix4 mat) {
        val[M00] = mat.val[M00];
        val[M10] = mat.val[M10];
        val[M01] = mat.val[M01];
        val[M11] = mat.val[M11];
        val[M03] = mat.val[M03];
        val[M13] = mat.val[M13];
        return (Matrix4)(Object)this;
    }

    public Matrix4 scl(Vector3 scale) {
        val[M00] *= scale.x;
        val[M11] *= scale.y;
        val[M22] *= scale.z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 scl(float x, float y, float z) {
        val[M00] *= x;
        val[M11] *= y;
        val[M22] *= z;
        return (Matrix4)(Object)this;
    }

    public Matrix4 scl(float scale) {
        val[M00] *= scale;
        val[M11] *= scale;
        val[M22] *= scale;
        return (Matrix4)(Object)this;
    }

    public Vector3 getTranslation(Vector3 position) {
        position.x = val[M03];
        position.y = val[M13];
        position.z = val[M23];
        return position;
    }

    public Quaternion getRotation(Quaternion rotation, boolean normalizeAxes) {
        return rotation.setFromMatrix(normalizeAxes, (com.badlogic.gdx.math.Matrix4)(Object)this);
    }

    public Quaternion getRotation(Quaternion rotation) {
        return rotation.setFromMatrix((com.badlogic.gdx.math.Matrix4)(Object)this);
    }

    public float getScaleXSquared() {
        return val[Matrix4.M00] * val[Matrix4.M00] + val[Matrix4.M01] * val[Matrix4.M01] + val[Matrix4.M02] * val[Matrix4.M02];
    }

    public float getScaleYSquared() {
        return val[Matrix4.M10] * val[Matrix4.M10] + val[Matrix4.M11] * val[Matrix4.M11] + val[Matrix4.M12] * val[Matrix4.M12];
    }

    public float getScaleZSquared() {
        return val[Matrix4.M20] * val[Matrix4.M20] + val[Matrix4.M21] * val[Matrix4.M21] + val[Matrix4.M22] * val[Matrix4.M22];
    }

    public float getScaleX() {
        return (MathUtils.isZero(val[Matrix4.M01]) && MathUtils.isZero(val[Matrix4.M02])) ? Math.abs(val[Matrix4.M00])
                : (float)Math.sqrt(getScaleXSquared());
    }

    public float getScaleY() {
        return (MathUtils.isZero(val[Matrix4.M10]) && MathUtils.isZero(val[Matrix4.M12])) ? Math.abs(val[Matrix4.M11])
                : (float)Math.sqrt(getScaleYSquared());
    }

    public float getScaleZ() {
        return (MathUtils.isZero(val[Matrix4.M20]) && MathUtils.isZero(val[Matrix4.M21])) ? Math.abs(val[Matrix4.M22])
                : (float)Math.sqrt(getScaleZSquared());
    }

    public Vector3 getScale(Vector3 scale) {
        return scale.set(getScaleX(), getScaleY(), getScaleZ());
    }

    public Matrix4 toNormalMatrix() {
        val[M03] = 0;
        val[M13] = 0;
        val[M23] = 0;
        return inv().tra();
    }

    static void matrix4_mul(float[] mata, float[] matb) {
        float tmp[] = new float[16];
        tmp[M00] = mata[M00] * matb[M00] + mata[M01] * matb[M10] + mata[M02] * matb[M20] + mata[M03] * matb[M30];
        tmp[M01] = mata[M00] * matb[M01] + mata[M01] * matb[M11] + mata[M02] * matb[M21] + mata[M03] * matb[M31];
        tmp[M02] = mata[M00] * matb[M02] + mata[M01] * matb[M12] + mata[M02] * matb[M22] + mata[M03] * matb[M32];
        tmp[M03] = mata[M00] * matb[M03] + mata[M01] * matb[M13] + mata[M02] * matb[M23] + mata[M03] * matb[M33];
        tmp[M10] = mata[M10] * matb[M00] + mata[M11] * matb[M10] + mata[M12] * matb[M20] + mata[M13] * matb[M30];
        tmp[M11] = mata[M10] * matb[M01] + mata[M11] * matb[M11] + mata[M12] * matb[M21] + mata[M13] * matb[M31];
        tmp[M12] = mata[M10] * matb[M02] + mata[M11] * matb[M12] + mata[M12] * matb[M22] + mata[M13] * matb[M32];
        tmp[M13] = mata[M10] * matb[M03] + mata[M11] * matb[M13] + mata[M12] * matb[M23] + mata[M13] * matb[M33];
        tmp[M20] = mata[M20] * matb[M00] + mata[M21] * matb[M10] + mata[M22] * matb[M20] + mata[M23] * matb[M30];
        tmp[M21] = mata[M20] * matb[M01] + mata[M21] * matb[M11] + mata[M22] * matb[M21] + mata[M23] * matb[M31];
        tmp[M22] = mata[M20] * matb[M02] + mata[M21] * matb[M12] + mata[M22] * matb[M22] + mata[M23] * matb[M32];
        tmp[M23] = mata[M20] * matb[M03] + mata[M21] * matb[M13] + mata[M22] * matb[M23] + mata[M23] * matb[M33];
        tmp[M30] = mata[M30] * matb[M00] + mata[M31] * matb[M10] + mata[M32] * matb[M20] + mata[M33] * matb[M30];
        tmp[M31] = mata[M30] * matb[M01] + mata[M31] * matb[M11] + mata[M32] * matb[M21] + mata[M33] * matb[M31];
        tmp[M32] = mata[M30] * matb[M02] + mata[M31] * matb[M12] + mata[M32] * matb[M22] + mata[M33] * matb[M32];
        tmp[M33] = mata[M30] * matb[M03] + mata[M31] * matb[M13] + mata[M32] * matb[M23] + mata[M33] * matb[M33];
        System.arraycopy(tmp, 0, mata, 0, 16);
    }

    static float matrix4_det(float[] val) {
        return val[M30] * val[M21] * val[M12] * val[M03] - val[M20] * val[M31] * val[M12] * val[M03] - val[M30] * val[M11]
                * val[M22] * val[M03] + val[M10] * val[M31] * val[M22] * val[M03] + val[M20] * val[M11] * val[M32] * val[M03] - val[M10]
                * val[M21] * val[M32] * val[M03] - val[M30] * val[M21] * val[M02] * val[M13] + val[M20] * val[M31] * val[M02] * val[M13]
                + val[M30] * val[M01] * val[M22] * val[M13] - val[M00] * val[M31] * val[M22] * val[M13] - val[M20] * val[M01] * val[M32]
                * val[M13] + val[M00] * val[M21] * val[M32] * val[M13] + val[M30] * val[M11] * val[M02] * val[M23] - val[M10] * val[M31]
                * val[M02] * val[M23] - val[M30] * val[M01] * val[M12] * val[M23] + val[M00] * val[M31] * val[M12] * val[M23] + val[M10]
                * val[M01] * val[M32] * val[M23] - val[M00] * val[M11] * val[M32] * val[M23] - val[M20] * val[M11] * val[M02] * val[M33]
                + val[M10] * val[M21] * val[M02] * val[M33] + val[M20] * val[M01] * val[M12] * val[M33] - val[M00] * val[M21] * val[M12]
                * val[M33] - val[M10] * val[M01] * val[M22] * val[M33] + val[M00] * val[M11] * val[M22] * val[M33];
    }

    static boolean matrix4_inv(float[] val) {
        float tmp[] = new float[16];
        float l_det = matrix4_det(val);
        if(l_det == 0) return false;
        tmp[M00] = val[M12] * val[M23] * val[M31] - val[M13] * val[M22] * val[M31] + val[M13] * val[M21] * val[M32] - val[M11]
                * val[M23] * val[M32] - val[M12] * val[M21] * val[M33] + val[M11] * val[M22] * val[M33];
        tmp[M01] = val[M03] * val[M22] * val[M31] - val[M02] * val[M23] * val[M31] - val[M03] * val[M21] * val[M32] + val[M01]
                * val[M23] * val[M32] + val[M02] * val[M21] * val[M33] - val[M01] * val[M22] * val[M33];
        tmp[M02] = val[M02] * val[M13] * val[M31] - val[M03] * val[M12] * val[M31] + val[M03] * val[M11] * val[M32] - val[M01]
                * val[M13] * val[M32] - val[M02] * val[M11] * val[M33] + val[M01] * val[M12] * val[M33];
        tmp[M03] = val[M03] * val[M12] * val[M21] - val[M02] * val[M13] * val[M21] - val[M03] * val[M11] * val[M22] + val[M01]
                * val[M13] * val[M22] + val[M02] * val[M11] * val[M23] - val[M01] * val[M12] * val[M23];
        tmp[M10] = val[M13] * val[M22] * val[M30] - val[M12] * val[M23] * val[M30] - val[M13] * val[M20] * val[M32] + val[M10]
                * val[M23] * val[M32] + val[M12] * val[M20] * val[M33] - val[M10] * val[M22] * val[M33];
        tmp[M11] = val[M02] * val[M23] * val[M30] - val[M03] * val[M22] * val[M30] + val[M03] * val[M20] * val[M32] - val[M00]
                * val[M23] * val[M32] - val[M02] * val[M20] * val[M33] + val[M00] * val[M22] * val[M33];
        tmp[M12] = val[M03] * val[M12] * val[M30] - val[M02] * val[M13] * val[M30] - val[M03] * val[M10] * val[M32] + val[M00]
                * val[M13] * val[M32] + val[M02] * val[M10] * val[M33] - val[M00] * val[M12] * val[M33];
        tmp[M13] = val[M02] * val[M13] * val[M20] - val[M03] * val[M12] * val[M20] + val[M03] * val[M10] * val[M22] - val[M00]
                * val[M13] * val[M22] - val[M02] * val[M10] * val[M23] + val[M00] * val[M12] * val[M23];
        tmp[M20] = val[M11] * val[M23] * val[M30] - val[M13] * val[M21] * val[M30] + val[M13] * val[M20] * val[M31] - val[M10]
                * val[M23] * val[M31] - val[M11] * val[M20] * val[M33] + val[M10] * val[M21] * val[M33];
        tmp[M21] = val[M03] * val[M21] * val[M30] - val[M01] * val[M23] * val[M30] - val[M03] * val[M20] * val[M31] + val[M00]
                * val[M23] * val[M31] + val[M01] * val[M20] * val[M33] - val[M00] * val[M21] * val[M33];
        tmp[M22] = val[M01] * val[M13] * val[M30] - val[M03] * val[M11] * val[M30] + val[M03] * val[M10] * val[M31] - val[M00]
                * val[M13] * val[M31] - val[M01] * val[M10] * val[M33] + val[M00] * val[M11] * val[M33];
        tmp[M23] = val[M03] * val[M11] * val[M20] - val[M01] * val[M13] * val[M20] - val[M03] * val[M10] * val[M21] + val[M00]
                * val[M13] * val[M21] + val[M01] * val[M10] * val[M23] - val[M00] * val[M11] * val[M23];
        tmp[M30] = val[M12] * val[M21] * val[M30] - val[M11] * val[M22] * val[M30] - val[M12] * val[M20] * val[M31] + val[M10]
                * val[M22] * val[M31] + val[M11] * val[M20] * val[M32] - val[M10] * val[M21] * val[M32];
        tmp[M31] = val[M01] * val[M22] * val[M30] - val[M02] * val[M21] * val[M30] + val[M02] * val[M20] * val[M31] - val[M00]
                * val[M22] * val[M31] - val[M01] * val[M20] * val[M32] + val[M00] * val[M21] * val[M32];
        tmp[M32] = val[M02] * val[M11] * val[M30] - val[M01] * val[M12] * val[M30] - val[M02] * val[M10] * val[M31] + val[M00]
                * val[M12] * val[M31] + val[M01] * val[M10] * val[M32] - val[M00] * val[M11] * val[M32];
        tmp[M33] = val[M01] * val[M12] * val[M20] - val[M02] * val[M11] * val[M20] + val[M02] * val[M10] * val[M21] - val[M00]
                * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] + val[M00] * val[M11] * val[M22];

        float inv_det = 1.0f / l_det;
        val[M00] = tmp[M00] * inv_det;
        val[M01] = tmp[M01] * inv_det;
        val[M02] = tmp[M02] * inv_det;
        val[M03] = tmp[M03] * inv_det;
        val[M10] = tmp[M10] * inv_det;
        val[M11] = tmp[M11] * inv_det;
        val[M12] = tmp[M12] * inv_det;
        val[M13] = tmp[M13] * inv_det;
        val[M20] = tmp[M20] * inv_det;
        val[M21] = tmp[M21] * inv_det;
        val[M22] = tmp[M22] * inv_det;
        val[M23] = tmp[M23] * inv_det;
        val[M30] = tmp[M30] * inv_det;
        val[M31] = tmp[M31] * inv_det;
        val[M32] = tmp[M32] * inv_det;
        val[M33] = tmp[M33] * inv_det;
        return true;
    }

    static void matrix4_mulVec(float[] mat, float[] vec, int offset) {
        float x = vec[offset + 0] * mat[M00] + vec[offset + 1] * mat[M01] + vec[offset + 2] * mat[M02] + mat[M03];
        float y = vec[offset + 0] * mat[M10] + vec[offset + 1] * mat[M11] + vec[offset + 2] * mat[M12] + mat[M13];
        float z = vec[offset + 0] * mat[M20] + vec[offset + 1] * mat[M21] + vec[offset + 2] * mat[M22] + mat[M23];
        vec[offset + 0] = x;
        vec[offset + 1] = y;
        vec[offset + 2] = z;
    }

    static void matrix4_proj(float[] mat, float[] vec, int offset) {
        float inv_w = 1.0f / (vec[offset + 0] * mat[M30] + vec[offset + 1] * mat[M31] + vec[offset + 2] * mat[M32] + mat[M33]);
        float x = (vec[offset + 0] * mat[M00] + vec[offset + 1] * mat[M01] + vec[offset + 2] * mat[M02] + mat[M03]) * inv_w;
        float y = (vec[offset + 0] * mat[M10] + vec[offset + 1] * mat[M11] + vec[offset + 2] * mat[M12] + mat[M13]) * inv_w;
        float z = (vec[offset + 0] * mat[M20] + vec[offset + 1] * mat[M21] + vec[offset + 2] * mat[M22] + mat[M23]) * inv_w;
        vec[offset + 0] = x;
        vec[offset + 1] = y;
        vec[offset + 2] = z;
    }

    static void matrix4_rot(float[] mat, float[] vec, int offset) {
        float x = vec[offset + 0] * mat[M00] + vec[offset + 1] * mat[M01] + vec[offset + 2] * mat[M02];
        float y = vec[offset + 0] * mat[M10] + vec[offset + 1] * mat[M11] + vec[offset + 2] * mat[M12];
        float z = vec[offset + 0] * mat[M20] + vec[offset + 1] * mat[M21] + vec[offset + 2] * mat[M22];
        vec[offset + 0] = x;
        vec[offset + 1] = y;
        vec[offset + 2] = z;
    }

    public static void mul(float[] mata, float[] matb) {
        matrix4_mul(mata, matb);
    }

    public static void mulVec(float[] mat, float[] vec) {
        matrix4_mulVec(mat, vec, 0);
    }

    public static void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
        for(int i = 0; i < numVecs; i++) {
            matrix4_mulVec(mat, vecs, offset);
            offset += stride;
        }
    }

    public static void prj(float[] mat, float[] vec) {
        matrix4_proj(mat, vec, 0);
    }

    public static void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
        for(int i = 0; i < numVecs; i++) {
            matrix4_proj(mat, vecs, offset);
            offset += stride;
        }
    }

    public static void rot(float[] mat, float[] vec) {
        matrix4_rot(mat, vec, 0);
    }

    public static void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
        for(int i = 0; i < numVecs; i++) {
            matrix4_rot(mat, vecs, offset);
            offset += stride;
        }
    }

    public static boolean inv(float[] values) {
        return matrix4_inv(values);
    }

    public static float det(float[] values) {
        return matrix4_det(values);
    }

    public Matrix4 translate(Vector3 translation) {
        return translate(translation.x, translation.y, translation.z);
    }

    public Matrix4 translate(float x, float y, float z) {
        tmp[M00] = 1;
        tmp[M01] = 0;
        tmp[M02] = 0;
        tmp[M03] = x;
        tmp[M10] = 0;
        tmp[M11] = 1;
        tmp[M12] = 0;
        tmp[M13] = y;
        tmp[M20] = 0;
        tmp[M21] = 0;
        tmp[M22] = 1;
        tmp[M23] = z;
        tmp[M30] = 0;
        tmp[M31] = 0;
        tmp[M32] = 0;
        tmp[M33] = 1;

        mul(val, tmp);
        return (Matrix4)(Object)this;
    }

    public Matrix4 rotate(Vector3 axis, float degrees) {
        if(degrees == 0) return (Matrix4)(Object)this;
        quat.set(axis, degrees);
        return rotate(quat);
    }

    public Matrix4 rotateRad(Vector3 axis, float radians) {
        if(radians == 0) return (Matrix4)(Object)this;
        quat.setFromAxisRad(axis, radians);
        return rotate(quat);
    }

    public Matrix4 rotate(float axisX, float axisY, float axisZ, float degrees) {
        if(degrees == 0) return (Matrix4)(Object)this;
        quat.setFromAxis(axisX, axisY, axisZ, degrees);
        return rotate(quat);
    }

    public Matrix4 rotateRad(float axisX, float axisY, float axisZ, float radians) {
        if(radians == 0) return (Matrix4)(Object)this;
        quat.setFromAxisRad(axisX, axisY, axisZ, radians);
        return rotate(quat);
    }

    public Matrix4 rotate(Quaternion rotation) {
        rotation.toMatrix(tmp);
        mul(val, tmp);
        return (Matrix4)(Object)this;
    }

    public Matrix4 rotate(final Vector3 v1, final Vector3 v2) {
        return rotate(quat.setFromCross(v1, v2));
    }

    public Matrix4 scale(float scaleX, float scaleY, float scaleZ) {
        tmp[M00] = scaleX;
        tmp[M01] = 0;
        tmp[M02] = 0;
        tmp[M03] = 0;
        tmp[M10] = 0;
        tmp[M11] = scaleY;
        tmp[M12] = 0;
        tmp[M13] = 0;
        tmp[M20] = 0;
        tmp[M21] = 0;
        tmp[M22] = scaleZ;
        tmp[M23] = 0;
        tmp[M30] = 0;
        tmp[M31] = 0;
        tmp[M32] = 0;
        tmp[M33] = 1;

        mul(val, tmp);
        return (Matrix4)(Object)this;
    }

    public void extract4x3Matrix(float[] dst) {
        dst[0] = val[M00];
        dst[1] = val[M10];
        dst[2] = val[M20];
        dst[3] = val[M01];
        dst[4] = val[M11];
        dst[5] = val[M21];
        dst[6] = val[M02];
        dst[7] = val[M12];
        dst[8] = val[M22];
        dst[9] = val[M03];
        dst[10] = val[M13];
        dst[11] = val[M23];
    }

    public boolean hasRotationOrScaling() {
        return !(MathUtils.isEqual(val[M00], 1) && MathUtils.isEqual(val[M11], 1) && MathUtils.isEqual(val[M22], 1)
                && MathUtils.isZero(val[M01]) && MathUtils.isZero(val[M02]) && MathUtils.isZero(val[M10]) && MathUtils.isZero(val[M12])
                && MathUtils.isZero(val[M20]) && MathUtils.isZero(val[M21]));
    }

    public Matrix4 rotateTowardDirection(final Vector3 direction, final Vector3 up) {
        l_vez.set(direction).nor();
        l_vex.set(direction).crs(up).nor();
        l_vey.set(l_vex).crs(l_vez).nor();
        float m00 = val[M00] * l_vex.x + val[M01] * l_vex.y + val[M02] * l_vex.z;
        float m01 = val[M00] * l_vey.x + val[M01] * l_vey.y + val[M02] * l_vey.z;
        float m02 = val[M00] * -l_vez.x + val[M01] * -l_vez.y + val[M02] * -l_vez.z;
        float m10 = val[M10] * l_vex.x + val[M11] * l_vex.y + val[M12] * l_vex.z;
        float m11 = val[M10] * l_vey.x + val[M11] * l_vey.y + val[M12] * l_vey.z;
        float m12 = val[M10] * -l_vez.x + val[M11] * -l_vez.y + val[M12] * -l_vez.z;
        float m20 = val[M20] * l_vex.x + val[M21] * l_vex.y + val[M22] * l_vex.z;
        float m21 = val[M20] * l_vey.x + val[M21] * l_vey.y + val[M22] * l_vey.z;
        float m22 = val[M20] * -l_vez.x + val[M21] * -l_vez.y + val[M22] * -l_vez.z;
        float m30 = val[M30] * l_vex.x + val[M31] * l_vex.y + val[M32] * l_vex.z;
        float m31 = val[M30] * l_vey.x + val[M31] * l_vey.y + val[M32] * l_vey.z;
        float m32 = val[M30] * -l_vez.x + val[M31] * -l_vez.y + val[M32] * -l_vez.z;
        val[M00] = m00;
        val[M10] = m10;
        val[M20] = m20;
        val[M30] = m30;
        val[M01] = m01;
        val[M11] = m11;
        val[M21] = m21;
        val[M31] = m31;
        val[M02] = m02;
        val[M12] = m12;
        val[M22] = m22;
        val[M32] = m32;
        return (Matrix4)(Object)this;
    }
}
