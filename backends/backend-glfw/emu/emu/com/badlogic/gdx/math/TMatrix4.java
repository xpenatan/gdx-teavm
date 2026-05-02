package emu.com.badlogic.gdx.math;

import com.badlogic.gdx.math.Affine2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import java.io.Serializable;

/**
 * Encapsulates a <a href="http://en.wikipedia.org/wiki/Row-major_order#Column-major_order">column major</a> 4 by 4 matrix. Like
 * the {@link Vector3} class it allows the chaining of methods by returning a reference to itself. For example:
 *
 * <pre>
 * Matrix4 mat = new Matrix4().trn(position).mul(camera.combined);
 * </pre>
 *
 * @author badlogicgames@gmail.com
 */
public class TMatrix4 implements Serializable {
    private static final long serialVersionUID = -2717655254359579617L;
    /**
     * XX: Typically the unrotated X component for scaling, also the cosine of the angle when rotated on the Y and/or Z axis. On
     * Vector3 multiplication this value is multiplied with the source X component and added to the target X component.
     */
    public static final int M00 = 0;
    /**
     * XY: Typically the negative sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied
     * with the source Y component and added to the target X component.
     */
    public static final int M01 = 4;
    /**
     * XZ: Typically the sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied with the
     * source Z component and added to the target X component.
     */
    public static final int M02 = 8;
    /**
     * XW: Typically the translation of the X component. On Vector3 multiplication this value is added to the target X component.
     */
    public static final int M03 = 12;
    /**
     * YX: Typically the sine of the angle when rotated on the Z axis. On Vector3 multiplication this value is multiplied with the
     * source X component and added to the target Y component.
     */
    public static final int M10 = 1;
    /**
     * YY: Typically the unrotated Y component for scaling, also the cosine of the angle when rotated on the X and/or Z axis. On
     * Vector3 multiplication this value is multiplied with the source Y component and added to the target Y component.
     */
    public static final int M11 = 5;
    /**
     * YZ: Typically the negative sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied
     * with the source Z component and added to the target Y component.
     */
    public static final int M12 = 9;
    /**
     * YW: Typically the translation of the Y component. On Vector3 multiplication this value is added to the target Y component.
     */
    public static final int M13 = 13;
    /**
     * ZX: Typically the negative sine of the angle when rotated on the Y axis. On Vector3 multiplication this value is multiplied
     * with the source X component and added to the target Z component.
     */
    public static final int M20 = 2;
    /**
     * ZY: Typical the sine of the angle when rotated on the X axis. On Vector3 multiplication this value is multiplied with the
     * source Y component and added to the target Z component.
     */
    public static final int M21 = 6;
    /**
     * ZZ: Typically the unrotated Z component for scaling, also the cosine of the angle when rotated on the X and/or Y axis. On
     * Vector3 multiplication this value is multiplied with the source Z component and added to the target Z component.
     */
    public static final int M22 = 10;
    /**
     * ZW: Typically the translation of the Z component. On Vector3 multiplication this value is added to the target Z component.
     */
    public static final int M23 = 14;
    /**
     * WX: Typically the value zero. On Vector3 multiplication this value is ignored.
     */
    public static final int M30 = 3;
    /**
     * WY: Typically the value zero. On Vector3 multiplication this value is ignored.
     */
    public static final int M31 = 7;
    /**
     * WZ: Typically the value zero. On Vector3 multiplication this value is ignored.
     */
    public static final int M32 = 11;
    /**
     * WW: Typically the value one. On Vector3 multiplication this value is ignored.
     */
    public static final int M33 = 15;

    /**
     * @deprecated Do not use this member, instead use a temporary Matrix4 instance, or create a temporary float array.
     */
    @Deprecated
    public static final float tmp[] = new float[16]; // FIXME Change to private access
    public final float val[] = new float[16];

    /**
     * Constructs an identity matrix
     */
    public TMatrix4() {
        val[M00] = 1f;
        val[M11] = 1f;
        val[M22] = 1f;
        val[M33] = 1f;
    }

    /**
     * Constructs a matrix from the given matrix.
     *
     * @param matrix The matrix to copy. (This matrix is not modified)
     */
    public TMatrix4(TMatrix4 matrix) {
        this.set(matrix);
    }

    /**
     * Constructs a matrix from the given float array. The array must have at least 16 elements; the first 16 will be copied.
     *
     * @param values The float array to copy. Remember that this matrix is in <a
     *               href="http://en.wikipedia.org/wiki/Row-major_order">column major</a> order. (The float array is not modified)
     */
    public TMatrix4(float[] values) {
        this.set(values);
    }

    /**
     * Constructs a rotation matrix from the given {@link Quaternion}.
     *
     * @param quaternion The quaternion to be copied. (The quaternion is not modified)
     */
    public TMatrix4(Quaternion quaternion) {
        this.set(quaternion);
    }

    /**
     * Construct a matrix from the given translation, rotation and scale.
     *
     * @param position The translation
     * @param rotation The rotation, must be normalized
     * @param scale    The scale
     */
    public TMatrix4(Vector3 position, Quaternion rotation, Vector3 scale) {
        set(position, rotation, scale);
    }

    /**
     * Sets the matrix to the given matrix.
     *
     * @param matrix The matrix that is to be copied. (The given matrix is not modified)
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 set(TMatrix4 matrix) {
        return this.set(matrix.val);
    }

    /**
     * Sets the matrix to the given matrix as a float array. The float array must have at least 16 elements; the first 16 will be
     * copied.
     *
     * @param values The matrix, in float form, that is to be copied. Remember that this matrix is in <a
     *               href="http://en.wikipedia.org/wiki/Row-major_order">column major</a> order.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 set(float[] values) {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets the matrix to a rotation matrix representing the quaternion.
     *
     * @param quaternion The quaternion that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 set(Quaternion quaternion) {
        return set(quaternion.x, quaternion.y, quaternion.z, quaternion.w);
    }

    /**
     * Sets the matrix to a rotation matrix representing the quaternion.
     *
     * @param quaternionX The X component of the quaternion that is to be used to set this matrix.
     * @param quaternionY The Y component of the quaternion that is to be used to set this matrix.
     * @param quaternionZ The Z component of the quaternion that is to be used to set this matrix.
     * @param quaternionW The W component of the quaternion that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 set(float quaternionX, float quaternionY, float quaternionZ, float quaternionW) {
        return set(0f, 0f, 0f, quaternionX, quaternionY, quaternionZ, quaternionW);
    }

    /**
     * Set this matrix to the specified translation and rotation.
     *
     * @param position    The translation
     * @param orientation The rotation, must be normalized
     * @return This matrix for chaining
     */
    public TMatrix4 set(Vector3 position, Quaternion orientation) {
        return set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w);
    }

    /**
     * Sets the matrix to a rotation matrix representing the translation and quaternion.
     *
     * @param translationX The X component of the translation that is to be used to set this matrix.
     * @param translationY The Y component of the translation that is to be used to set this matrix.
     * @param translationZ The Z component of the translation that is to be used to set this matrix.
     * @param quaternionX  The X component of the quaternion that is to be used to set this matrix.
     * @param quaternionY  The Y component of the quaternion that is to be used to set this matrix.
     * @param quaternionZ  The Z component of the quaternion that is to be used to set this matrix.
     * @param quaternionW  The W component of the quaternion that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY,
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Set this matrix to the specified translation, rotation and scale.
     *
     * @param position    The translation
     * @param orientation The rotation, must be normalized
     * @param scale       The scale
     * @return This matrix for chaining
     */
    public TMatrix4 set(Vector3 position, Quaternion orientation, Vector3 scale) {
        return set(position.x, position.y, position.z, orientation.x, orientation.y, orientation.z, orientation.w, scale.x,
                scale.y, scale.z);
    }

    /**
     * Sets the matrix to a rotation matrix representing the translation and quaternion.
     *
     * @param translationX The X component of the translation that is to be used to set this matrix.
     * @param translationY The Y component of the translation that is to be used to set this matrix.
     * @param translationZ The Z component of the translation that is to be used to set this matrix.
     * @param quaternionX  The X component of the quaternion that is to be used to set this matrix.
     * @param quaternionY  The Y component of the quaternion that is to be used to set this matrix.
     * @param quaternionZ  The Z component of the quaternion that is to be used to set this matrix.
     * @param quaternionW  The W component of the quaternion that is to be used to set this matrix.
     * @param scaleX       The X component of the scaling that is to be used to set this matrix.
     * @param scaleY       The Y component of the scaling that is to be used to set this matrix.
     * @param scaleZ       The Z component of the scaling that is to be used to set this matrix.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 set(float translationX, float translationY, float translationZ, float quaternionX, float quaternionY,
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets the four columns of the matrix which correspond to the x-, y- and z-axis of the vector space this matrix creates as
     * well as the 4th column representing the translation of any point that is multiplied by this matrix.
     *
     * @param xAxis The x-axis.
     * @param yAxis The y-axis.
     * @param zAxis The z-axis.
     * @param pos   The translation vector.
     */
    public TMatrix4 set(Vector3 xAxis, Vector3 yAxis, Vector3 zAxis, Vector3 pos) {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * @return a copy of this matrix
     */
    public TMatrix4 cpy() {
        return new TMatrix4((TMatrix4)(Object)this);
    }

    /**
     * Adds a translational component to the matrix in the 4th column. The other columns are untouched.
     *
     * @param vector The translation vector to add to the current matrix. (This vector is not modified)
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 trn(Vector3 vector) {
        val[M03] += vector.x;
        val[M13] += vector.y;
        val[M23] += vector.z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Adds a translational component to the matrix in the 4th column. The other columns are untouched.
     *
     * @param x The x-component of the translation vector.
     * @param y The y-component of the translation vector.
     * @param z The z-component of the translation vector.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 trn(float x, float y, float z) {
        val[M03] += x;
        val[M13] += y;
        val[M23] += z;
        return (TMatrix4)(Object)this;
    }

    /**
     * @return the backing float array
     */
    public float[] getValues() {
        return val;
    }

    /**
     * Postmultiplies this matrix with the given matrix, storing the result in this matrix. For example:
     *
     * <pre>
     * A.mul(B) results in A := AB.
     * </pre>
     *
     * @param matrix The other matrix to multiply by.
     * @return This matrix for the purpose of chaining operations together.
     */
    public TMatrix4 mul(TMatrix4 matrix) {
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

    /**
     * Premultiplies this matrix with the given matrix, storing the result in this matrix. For example:
     *
     * <pre>
     * A.mulLeft(B) results in A := BA.
     * </pre>
     *
     * @param matrix The other matrix to multiply by.
     * @return This matrix for the purpose of chaining operations together.
     */
    public TMatrix4 mulLeft(TMatrix4 matrix) {
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

    /**
     * Transposes the matrix.
     *
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 tra() {
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

    /**
     * Sets the matrix to an identity matrix.
     *
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 idt() {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Inverts the matrix. Stores the result in this matrix.
     *
     * @return This matrix for the purpose of chaining methods together.
     * @throws RuntimeException if the matrix is singular (not invertible)
     */
    public TMatrix4 inv() {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * @return The determinant of this matrix
     */
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

    /**
     * @return The determinant of the 3x3 upper left matrix
     */
    public float det3x3() {
        return val[M00] * val[M11] * val[M22] + val[M01] * val[M12] * val[M20] + val[M02] * val[M10] * val[M21] - val[M00]
                * val[M12] * val[M21] - val[M01] * val[M10] * val[M22] - val[M02] * val[M11] * val[M20];
    }

    /**
     * Sets the matrix to a projection matrix with a near- and far plane, a field of view in degrees and an aspect ratio. Note that
     * the field of view specified is the angle in degrees for the height, the field of view for the width will be calculated
     * according to the aspect ratio.
     *
     * @param near        The near plane
     * @param far         The far plane
     * @param fovy        The field of view of the height in degrees
     * @param aspectRatio The "width over height" aspect ratio
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToProjection(float near, float far, float fovy, float aspectRatio) {
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

        return (TMatrix4)(Object)this;
    }

    /**
     * Sets the matrix to a projection matrix with a near/far plane, and left, bottom, right and top specifying the points on the
     * near plane that are mapped to the lower left and upper right corners of the viewport. This allows to create projection
     * matrix with off-center vanishing point.
     *
     * @param left
     * @param right
     * @param bottom
     * @param top
     * @param near   The near plane
     * @param far    The far plane
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToProjection(float left, float right, float bottom, float top, float near, float far) {
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

        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to an orthographic projection matrix with the origin at (x,y) extending by width and height. The near plane
     * is set to 0, the far plane is set to 1.
     *
     * @param x      The x-coordinate of the origin
     * @param y      The y-coordinate of the origin
     * @param width  The width
     * @param height The height
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToOrtho2D(float x, float y, float width, float height) {
        setToOrtho(x, x + width, y, y + height, 0, 1);
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to an orthographic projection matrix with the origin at (x,y) extending by width and height, having a near
     * and far plane.
     *
     * @param x      The x-coordinate of the origin
     * @param y      The y-coordinate of the origin
     * @param width  The width
     * @param height The height
     * @param near   The near plane
     * @param far    The far plane
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToOrtho2D(float x, float y, float width, float height, float near, float far) {
        setToOrtho(x, x + width, y, y + height, near, far);
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets the matrix to an orthographic projection like glOrtho (http://www.opengl.org/sdk/docs/man/xhtml/glOrtho.xml) following
     * the OpenGL equivalent
     *
     * @param left   The left clipping plane
     * @param right  The right clipping plane
     * @param bottom The bottom clipping plane
     * @param top    The top clipping plane
     * @param near   The near clipping plane
     * @param far    The far clipping plane
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToOrtho(float left, float right, float bottom, float top, float near, float far) {

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

        return (TMatrix4)(Object)this;
    }

    /**
     * Sets the 4th column to the translation vector.
     *
     * @param vector The translation vector
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setTranslation(Vector3 vector) {
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets the 4th column to the translation vector.
     *
     * @param x The X coordinate of the translation vector
     * @param y The Y coordinate of the translation vector
     * @param z The Z coordinate of the translation vector
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setTranslation(float x, float y, float z) {
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to a translation matrix, overwriting it first by an identity matrix and then setting the 4th column to the
     * translation vector.
     *
     * @param vector The translation vector
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToTranslation(Vector3 vector) {
        idt();
        val[M03] = vector.x;
        val[M13] = vector.y;
        val[M23] = vector.z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to a translation matrix, overwriting it first by an identity matrix and then setting the 4th column to the
     * translation vector.
     *
     * @param x The x-component of the translation vector.
     * @param y The y-component of the translation vector.
     * @param z The z-component of the translation vector.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToTranslation(float x, float y, float z) {
        idt();
        val[M03] = x;
        val[M13] = y;
        val[M23] = z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to a translation and scaling matrix by first overwriting it with an identity and then setting the
     * translation vector in the 4th column and the scaling vector in the diagonal.
     *
     * @param translation The translation vector
     * @param scaling     The scaling vector
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToTranslationAndScaling(Vector3 translation, Vector3 scaling) {
        idt();
        val[M03] = translation.x;
        val[M13] = translation.y;
        val[M23] = translation.z;
        val[M00] = scaling.x;
        val[M11] = scaling.y;
        val[M22] = scaling.z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to a translation and scaling matrix by first overwriting it with an identity and then setting the
     * translation vector in the 4th column and the scaling vector in the diagonal.
     *
     * @param translationX The x-component of the translation vector
     * @param translationY The y-component of the translation vector
     * @param translationZ The z-component of the translation vector
     * @param scalingX     The x-component of the scaling vector
     * @param scalingY     The x-component of the scaling vector
     * @param scalingZ     The x-component of the scaling vector
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToTranslationAndScaling(float translationX, float translationY, float translationZ, float scalingX,
                                               float scalingY, float scalingZ) {
        idt();
        val[M03] = translationX;
        val[M13] = translationY;
        val[M23] = translationZ;
        val[M00] = scalingX;
        val[M11] = scalingY;
        val[M22] = scalingZ;
        return (TMatrix4)(Object)this;
    }

    static Quaternion quat = new Quaternion();
    static Quaternion quat2 = new Quaternion();

    /**
     * Sets the matrix to a rotation matrix around the given axis.
     *
     * @param axis    The axis
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToRotation(Vector3 axis, float degrees) {
        if(degrees == 0) {
            idt();
            return (TMatrix4)(Object)this;
        }
        return set(quat.set(axis, degrees));
    }

    /**
     * Sets the matrix to a rotation matrix around the given axis.
     *
     * @param axis    The axis
     * @param radians The angle in radians
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToRotationRad(Vector3 axis, float radians) {
        if(radians == 0) {
            idt();
            return (TMatrix4)(Object)this;
        }
        return set(quat.setFromAxisRad(axis, radians));
    }

    /**
     * Sets the matrix to a rotation matrix around the given axis.
     *
     * @param axisX   The x-component of the axis
     * @param axisY   The y-component of the axis
     * @param axisZ   The z-component of the axis
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToRotation(float axisX, float axisY, float axisZ, float degrees) {
        if(degrees == 0) {
            idt();
            return (TMatrix4)(Object)this;
        }
        return set(quat.setFromAxis(axisX, axisY, axisZ, degrees));
    }

    /**
     * Sets the matrix to a rotation matrix around the given axis.
     *
     * @param axisX   The x-component of the axis
     * @param axisY   The y-component of the axis
     * @param axisZ   The z-component of the axis
     * @param radians The angle in radians
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToRotationRad(float axisX, float axisY, float axisZ, float radians) {
        if(radians == 0) {
            idt();
            return (TMatrix4)(Object)this;
        }
        return set(quat.setFromAxisRad(axisX, axisY, axisZ, radians));
    }

    /**
     * Set the matrix to a rotation matrix between two vectors.
     *
     * @param v1 The base vector
     * @param v2 The target vector
     * @return This matrix for the purpose of chaining methods together
     */
    public TMatrix4 setToRotation(final Vector3 v1, final Vector3 v2) {
        return set(quat.setFromCross(v1, v2));
    }

    /**
     * Set the matrix to a rotation matrix between two vectors.
     *
     * @param x1 The base vectors x value
     * @param y1 The base vectors y value
     * @param z1 The base vectors z value
     * @param x2 The target vector x value
     * @param y2 The target vector y value
     * @param z2 The target vector z value
     * @return This matrix for the purpose of chaining methods together
     */
    public TMatrix4 setToRotation(final float x1, final float y1, final float z1, final float x2, final float y2, final float z2) {
        return set(quat.setFromCross(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Sets this matrix to a rotation matrix from the given euler angles.
     *
     * @param yaw   the yaw in degrees
     * @param pitch the pitch in degrees
     * @param roll  the roll in degrees
     * @return This matrix
     */
    public TMatrix4 setFromEulerAngles(float yaw, float pitch, float roll) {
        quat.setEulerAngles(yaw, pitch, roll);
        return set(quat);
    }

    /**
     * Sets this matrix to a rotation matrix from the given euler angles.
     *
     * @param yaw   the yaw in radians
     * @param pitch the pitch in radians
     * @param roll  the roll in radians
     * @return This matrix
     */
    public TMatrix4 setFromEulerAnglesRad(float yaw, float pitch, float roll) {
        quat.setEulerAnglesRad(yaw, pitch, roll);
        return set(quat);
    }

    /**
     * Sets this matrix to a scaling matrix
     *
     * @param vector The scaling vector
     * @return This matrix for chaining.
     */
    public TMatrix4 setToScaling(Vector3 vector) {
        idt();
        val[M00] = vector.x;
        val[M11] = vector.y;
        val[M22] = vector.z;
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to a scaling matrix
     *
     * @param x The x-component of the scaling vector
     * @param y The y-component of the scaling vector
     * @param z The z-component of the scaling vector
     * @return This matrix for chaining.
     */
    public TMatrix4 setToScaling(float x, float y, float z) {
        idt();
        val[M00] = x;
        val[M11] = y;
        val[M22] = z;
        return (TMatrix4)(Object)this;
    }

    static final Vector3 l_vez = new Vector3();
    static final Vector3 l_vex = new Vector3();
    static final Vector3 l_vey = new Vector3();

    /**
     * Sets the matrix to a look at matrix with a direction and an up vector. Multiply with a translation matrix to get a camera
     * model view matrix.
     *
     * @param direction The direction vector
     * @param up        The up vector
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 setToLookAt(Vector3 direction, Vector3 up) {
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

        return (TMatrix4)(Object)this;
    }

    static final Vector3 tmpVec = new Vector3();
    static final TMatrix4 tmpMat = new TMatrix4();

    /**
     * Sets this matrix to a look at matrix with the given position, target and up vector.
     *
     * @param position the position
     * @param target   the target
     * @param up       the up vector
     * @return This matrix
     */
    public TMatrix4 setToLookAt(Vector3 position, Vector3 target, Vector3 up) {
        tmpVec.set(target).sub(position);
        setToLookAt(tmpVec, up);
        this.mul(tmpMat.setToTranslation(-position.x, -position.y, -position.z));

        return (TMatrix4)(Object)this;
    }

    static final Vector3 right = new Vector3();
    static final Vector3 tmpForward = new Vector3();
    static final Vector3 tmpUp = new Vector3();

    public TMatrix4 setToWorld(Vector3 position, Vector3 forward, Vector3 up) {
        tmpForward.set(forward).nor();
        right.set(tmpForward).crs(up).nor();
        tmpUp.set(right).crs(tmpForward).nor();

        this.set(right, tmpUp, tmpForward.scl(-1), position);
        return (TMatrix4)(Object)this;
    }

    public String toString() {
        return "[" + val[M00] + "|" + val[M01] + "|" + val[M02] + "|" + val[M03] + "]\n" + "[" + val[M10] + "|" + val[M11] + "|"
                + val[M12] + "|" + val[M13] + "]\n" + "[" + val[M20] + "|" + val[M21] + "|" + val[M22] + "|" + val[M23] + "]\n" + "["
                + val[M30] + "|" + val[M31] + "|" + val[M32] + "|" + val[M33] + "]\n";
    }

    /**
     * Linearly interpolates between this matrix and the given matrix mixing by alpha
     *
     * @param matrix the matrix
     * @param alpha  the alpha value in the range [0,1]
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 lerp(TMatrix4 matrix, float alpha) {
        for(int i = 0; i < 16; i++)
            this.val[i] = this.val[i] * (1 - alpha) + matrix.val[i] * alpha;
        return (TMatrix4)(Object)this;
    }

    /**
     * Averages the given transform with this one and stores the result in this matrix.
     * Translations and scales are lerped while rotations are slerped.
     *
     * @param other The other transform
     * @param w     Weight of this transform; weight of the other transform is (1 - w)
     * @return This matrix for chaining
     */
    public TMatrix4 avg(TMatrix4 other, float w) {
        getScale(tmpVec);
        other.getScale(tmpForward);

        getRotation(quat);
        other.getRotation(quat2);

        getTranslation(tmpUp);
        other.getTranslation(right);

        setToScaling(tmpVec.scl(w).add(tmpForward.scl(1 - w)));
        rotate(quat.slerp(quat2, 1 - w));
        setTranslation(tmpUp.scl(w).add(right.scl(1 - w)));

        return (TMatrix4)(Object)this;
    }

    /**
     * Averages the given transforms and stores the result in this matrix.
     * Translations and scales are lerped while rotations are slerped.
     * Does not destroy the data contained in t.
     *
     * @param t List of transforms
     * @return This matrix for chaining
     */
    public TMatrix4 avg(TMatrix4[] t) {
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

        return (TMatrix4)(Object)this;
    }

    /**
     * Averages the given transforms with the given weights and stores the result in this matrix.
     * Translations and scales are lerped while rotations are slerped.
     * Does not destroy the data contained in t or w;
     * Sum of w_i must be equal to 1, or unexpected results will occur.
     *
     * @param t List of transforms
     * @param w List of weights
     * @return This matrix for chaining
     */
    public TMatrix4 avg(TMatrix4[] t, float[] w) {
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

        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to the given 3x3 matrix. The third column of this matrix is set to (0,0,1,0).
     *
     * @param mat the matrix
     */
    public TMatrix4 set(Matrix3 mat) {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Sets this matrix to the given affine matrix. The values are mapped as follows:
     *
     * <pre>
     *      [  M00  M01   0   M02  ]
     *      [  M10  M11   0   M12  ]
     *      [   0    0    1    0   ]
     *      [   0    0    0    1   ]
     * </pre>
     *
     * @param affine the affine matrix
     * @return This matrix for chaining
     */
    public TMatrix4 set(Affine2 affine) {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Assumes that this matrix is a 2D affine transformation, copying only the relevant components. The values are mapped as
     * follows:
     *
     * <pre>
     *      [  M00  M01   _   M02  ]
     *      [  M10  M11   _   M12  ]
     *      [   _    _    _    _   ]
     *      [   _    _    _    _   ]
     * </pre>
     *
     * @param affine the source matrix
     * @return This matrix for chaining
     */
    public TMatrix4 setAsAffine(Affine2 affine) {
        val[M00] = affine.m00;
        val[M10] = affine.m10;
        val[M01] = affine.m01;
        val[M11] = affine.m11;
        val[M03] = affine.m02;
        val[M13] = affine.m12;
        return (TMatrix4)(Object)this;
    }

    /**
     * Assumes that both matrices are 2D affine transformations, copying only the relevant components. The copied values are:
     *
     * <pre>
     *      [  M00  M01   _   M03  ]
     *      [  M10  M11   _   M13  ]
     *      [   _    _    _    _   ]
     *      [   _    _    _    _   ]
     * </pre>
     *
     * @param mat the source matrix
     * @return This matrix for chaining
     */
    public TMatrix4 setAsAffine(TMatrix4 mat) {
        val[M00] = mat.val[M00];
        val[M10] = mat.val[M10];
        val[M01] = mat.val[M01];
        val[M11] = mat.val[M11];
        val[M03] = mat.val[M03];
        val[M13] = mat.val[M13];
        return (TMatrix4)(Object)this;
    }

    public TMatrix4 scl(Vector3 scale) {
        val[M00] *= scale.x;
        val[M11] *= scale.y;
        val[M22] *= scale.z;
        return (TMatrix4)(Object)this;
    }

    public TMatrix4 scl(float x, float y, float z) {
        val[M00] *= x;
        val[M11] *= y;
        val[M22] *= z;
        return (TMatrix4)(Object)this;
    }

    public TMatrix4 scl(float scale) {
        val[M00] *= scale;
        val[M11] *= scale;
        val[M22] *= scale;
        return (TMatrix4)(Object)this;
    }

    public Vector3 getTranslation(Vector3 position) {
        position.x = val[M03];
        position.y = val[M13];
        position.z = val[M23];
        return position;
    }

    /**
     * Gets the rotation of this matrix.
     *
     * @param rotation      The {@link Quaternion} to receive the rotation
     * @param normalizeAxes True to normalize the axes, necessary when the matrix might also include scaling.
     * @return The provided {@link Quaternion} for chaining.
     */
    public Quaternion getRotation(Quaternion rotation, boolean normalizeAxes) {
        return rotation.setFromMatrix(normalizeAxes, (Matrix4)(Object)this);
    }

    /**
     * Gets the rotation of this matrix.
     *
     * @param rotation The {@link Quaternion} to receive the rotation
     * @return The provided {@link Quaternion} for chaining.
     */
    public Quaternion getRotation(Quaternion rotation) {
        return rotation.setFromMatrix((Matrix4)(Object)this);
    }

    /**
     * @return the squared scale factor on the X axis
     */
    public float getScaleXSquared() {
        return val[TMatrix4.M00] * val[TMatrix4.M00] + val[TMatrix4.M01] * val[TMatrix4.M01] + val[TMatrix4.M02] * val[TMatrix4.M02];
    }

    /**
     * @return the squared scale factor on the Y axis
     */
    public float getScaleYSquared() {
        return val[TMatrix4.M10] * val[TMatrix4.M10] + val[TMatrix4.M11] * val[TMatrix4.M11] + val[TMatrix4.M12] * val[TMatrix4.M12];
    }

    /**
     * @return the squared scale factor on the Z axis
     */
    public float getScaleZSquared() {
        return val[TMatrix4.M20] * val[TMatrix4.M20] + val[TMatrix4.M21] * val[TMatrix4.M21] + val[TMatrix4.M22] * val[TMatrix4.M22];
    }

    /**
     * @return the scale factor on the X axis (non-negative)
     */
    public float getScaleX() {
        return (MathUtils.isZero(val[TMatrix4.M01]) && MathUtils.isZero(val[TMatrix4.M02])) ? Math.abs(val[TMatrix4.M00])
                : (float)Math.sqrt(getScaleXSquared());
    }

    /**
     * @return the scale factor on the Y axis (non-negative)
     */
    public float getScaleY() {
        return (MathUtils.isZero(val[TMatrix4.M10]) && MathUtils.isZero(val[TMatrix4.M12])) ? Math.abs(val[TMatrix4.M11])
                : (float)Math.sqrt(getScaleYSquared());
    }

    /**
     * @return the scale factor on the X axis (non-negative)
     */
    public float getScaleZ() {
        return (MathUtils.isZero(val[TMatrix4.M20]) && MathUtils.isZero(val[TMatrix4.M21])) ? Math.abs(val[TMatrix4.M22])
                : (float)Math.sqrt(getScaleZSquared());
    }

    /**
     * @param scale The vector which will receive the (non-negative) scale components on each axis.
     * @return The provided vector for chaining.
     */
    public Vector3 getScale(Vector3 scale) {
        return scale.set(getScaleX(), getScaleY(), getScaleZ());
    }

    /**
     * removes the translational part and transposes the matrix.
     */
    public TMatrix4 toNormalMatrix() {
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

    /**
     * Multiplies the matrix mata with matrix matb, storing the result in mata. The arrays are assumed to hold 4x4 column major
     * matrices as you can get from {@link TMatrix4#val}. This is the same as {@link TMatrix4#mul(TMatrix4)}.
     *
     * @param mata the first matrix.
     * @param matb the second matrix.
     */
    public static void mul(float[] mata, float[] matb) {
        matrix4_mul(mata, matb);
    }

    /**
     * Multiplies the vector with the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get
     * from {@link TMatrix4#val}. The vector array is assumed to hold a 3-component vector, with x being the first element, y being
     * the second and z being the last component. The result is stored in the vector array. This is the same as
     * {@link Vector3#mul(TMatrix4)}.
     *
     * @param mat the matrix
     * @param vec the vector.
     */
    public static void mulVec(float[] mat, float[] vec) {
        matrix4_mulVec(mat, vec, 0);
    }

    /**
     * Multiplies the vectors with the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get
     * from {@link TMatrix4#val}. The vectors array is assumed to hold 3-component vectors. Offset specifies the offset into the
     * array where the x-component of the first vector is located. The numVecs parameter specifies the number of vectors stored in
     * the vectors array. The stride parameter specifies the number of floats between subsequent vectors and must be >= 3. This is
     * the same as {@link Vector3#mul(TMatrix4)} applied to multiple vectors.
     *
     * @param mat     the matrix
     * @param vecs    the vectors
     * @param offset  the offset into the vectors array
     * @param numVecs the number of vectors
     * @param stride  the stride between vectors in floats
     */
    public static void mulVec(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
        for(int i = 0; i < numVecs; i++) {
            matrix4_mulVec(mat, vecs, offset);
            offset += stride;
        }
    }

    /**
     * Multiplies the vector with the given matrix, performing a division by w. The matrix array is assumed to hold a 4x4 column
     * major matrix as you can get from {@link TMatrix4#val}. The vector array is assumed to hold a 3-component vector, with x being
     * the first element, y being the second and z being the last component. The result is stored in the vector array. This is the
     * same as {@link Vector3#prj(TMatrix4)}.
     *
     * @param mat the matrix
     * @param vec the vector.
     */
    public static void prj(float[] mat, float[] vec) {
        matrix4_proj(mat, vec, 0);
    }

    /**
     * Multiplies the vectors with the given matrix, , performing a division by w. The matrix array is assumed to hold a 4x4 column
     * major matrix as you can get from {@link TMatrix4#val}. The vectors array is assumed to hold 3-component vectors. Offset
     * specifies the offset into the array where the x-component of the first vector is located. The numVecs parameter specifies
     * the number of vectors stored in the vectors array. The stride parameter specifies the number of floats between subsequent
     * vectors and must be >= 3. This is the same as {@link Vector3#prj(TMatrix4)} applied to multiple vectors.
     *
     * @param mat     the matrix
     * @param vecs    the vectors
     * @param offset  the offset into the vectors array
     * @param numVecs the number of vectors
     * @param stride  the stride between vectors in floats
     */
    public static void prj(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
        for(int i = 0; i < numVecs; i++) {
            matrix4_proj(mat, vecs, offset);
            offset += stride;
        }
    }

    /**
     * Multiplies the vector with the top most 3x3 sub-matrix of the given matrix. The matrix array is assumed to hold a 4x4 column
     * major matrix as you can get from {@link TMatrix4#val}. The vector array is assumed to hold a 3-component vector, with x being
     * the first element, y being the second and z being the last component. The result is stored in the vector array. This is the
     * same as {@link Vector3#rot(TMatrix4)}.
     *
     * @param mat the matrix
     * @param vec the vector.
     */
    public static void rot(float[] mat, float[] vec) {
        matrix4_rot(mat, vec, 0);
    }

    /**
     * Multiplies the vectors with the top most 3x3 sub-matrix of the given matrix. The matrix array is assumed to hold a 4x4
     * column major matrix as you can get from {@link TMatrix4#val}. The vectors array is assumed to hold 3-component vectors.
     * Offset specifies the offset into the array where the x-component of the first vector is located. The numVecs parameter
     * specifies the number of vectors stored in the vectors array. The stride parameter specifies the number of floats between
     * subsequent vectors and must be >= 3. This is the same as {@link Vector3#rot(TMatrix4)} applied to multiple vectors.
     *
     * @param mat     the matrix
     * @param vecs    the vectors
     * @param offset  the offset into the vectors array
     * @param numVecs the number of vectors
     * @param stride  the stride between vectors in floats
     */
    public static void rot(float[] mat, float[] vecs, int offset, int numVecs, int stride) {
        for(int i = 0; i < numVecs; i++) {
            matrix4_rot(mat, vecs, offset);
            offset += stride;
        }
    }

    /**
     * Computes the inverse of the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get from
     * {@link TMatrix4#val}.
     *
     * @param values the matrix values.
     * @return false in case the inverse could not be calculated, true otherwise.
     */
    public static boolean inv(float[] values) {
        return matrix4_inv(values);
    }

    /**
     * Computes the determinante of the given matrix. The matrix array is assumed to hold a 4x4 column major matrix as you can get
     * from {@link TMatrix4#val}.
     *
     * @param values the matrix values.
     * @return the determinante.
     */
    public static float det(float[] values) {
        return matrix4_det(values);
    }

    /**
     * Postmultiplies this matrix by a translation matrix. Postmultiplication is also used by OpenGL ES'
     * glTranslate/glRotate/glScale
     *
     * @param translation
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 translate(Vector3 translation) {
        return translate(translation.x, translation.y, translation.z);
    }

    /**
     * Postmultiplies this matrix by a translation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param x Translation in the x-axis.
     * @param y Translation in the y-axis.
     * @param z Translation in the z-axis.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 translate(float x, float y, float z) {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param axis    The vector axis to rotate around.
     * @param degrees The angle in degrees.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 rotate(Vector3 axis, float degrees) {
        if(degrees == 0) return (TMatrix4)(Object)this;
        quat.set(axis, degrees);
        return rotate(quat);
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param axis    The vector axis to rotate around.
     * @param radians The angle in radians.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 rotateRad(Vector3 axis, float radians) {
        if(radians == 0) return (TMatrix4)(Object)this;
        quat.setFromAxisRad(axis, radians);
        return rotate(quat);
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale
     *
     * @param axisX   The x-axis component of the vector to rotate around.
     * @param axisY   The y-axis component of the vector to rotate around.
     * @param axisZ   The z-axis component of the vector to rotate around.
     * @param degrees The angle in degrees
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 rotate(float axisX, float axisY, float axisZ, float degrees) {
        if(degrees == 0) return (TMatrix4)(Object)this;
        quat.setFromAxis(axisX, axisY, axisZ, degrees);
        return rotate(quat);
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale
     *
     * @param axisX   The x-axis component of the vector to rotate around.
     * @param axisY   The y-axis component of the vector to rotate around.
     * @param axisZ   The z-axis component of the vector to rotate around.
     * @param radians The angle in radians
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 rotateRad(float axisX, float axisY, float axisZ, float radians) {
        if(radians == 0) return (TMatrix4)(Object)this;
        quat.setFromAxisRad(axisX, axisY, axisZ, radians);
        return rotate(quat);
    }

    /**
     * Postmultiplies this matrix with a (counter-clockwise) rotation matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param rotation
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 rotate(Quaternion rotation) {
        rotation.toMatrix(tmp);
        mul(val, tmp);
        return (TMatrix4)(Object)this;
    }

    /**
     * Postmultiplies this matrix by the rotation between two vectors.
     *
     * @param v1 The base vector
     * @param v2 The target vector
     * @return This matrix for the purpose of chaining methods together
     */
    public TMatrix4 rotate(final Vector3 v1, final Vector3 v2) {
        return rotate(quat.setFromCross(v1, v2));
    }

    /**
     * Postmultiplies this matrix with a scale matrix. Postmultiplication is also used by OpenGL ES' 1.x
     * glTranslate/glRotate/glScale.
     *
     * @param scaleX The scale in the x-axis.
     * @param scaleY The scale in the y-axis.
     * @param scaleZ The scale in the z-axis.
     * @return This matrix for the purpose of chaining methods together.
     */
    public TMatrix4 scale(float scaleX, float scaleY, float scaleZ) {
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
        return (TMatrix4)(Object)this;
    }

    /**
     * Copies the 4x3 upper-left sub-matrix into float array. The destination array is supposed to be a column major matrix.
     *
     * @param dst the destination matrix
     */
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

    /**
     * @return True if this matrix has any rotation or scaling, false otherwise
     */
    public boolean hasRotationOrScaling() {
        return !(MathUtils.isEqual(val[M00], 1) && MathUtils.isEqual(val[M11], 1) && MathUtils.isEqual(val[M22], 1)
                && MathUtils.isZero(val[M01]) && MathUtils.isZero(val[M02]) && MathUtils.isZero(val[M10]) && MathUtils.isZero(val[M12])
                && MathUtils.isZero(val[M20]) && MathUtils.isZero(val[M21]));
    }

    public TMatrix4 rotateTowardDirection(final Vector3 direction, final Vector3 up) {
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
        return (TMatrix4)(Object)this;
    }
}