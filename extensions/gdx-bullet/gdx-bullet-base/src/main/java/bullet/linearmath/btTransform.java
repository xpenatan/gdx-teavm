package bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.github.xpenatan.jparser.base.IDLBase;

public class btTransform extends IDLBase {

    public static btTransform emptyTransform = new btTransform((byte)1);

    public btTransform(byte temp) {
    }

    public void setFromOpenGLMatrix(float[] m) {
        setFromOpenGLMatrix(getCPointer(), m);
    }

    /*[-C++;-NATIVE]
        float * m = (float*)env->GetPrimitiveArrayCritical(array, 0);
        ((btTransform*)addr)->setFromOpenGLMatrix(m);
        env->ReleasePrimitiveArrayCritical(array, m, 0);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = bullet.wrapPointer(addr, bullet.btTransform);
        jsObj.setFromOpenGLMatrix(array);
    */
    private static native void setFromOpenGLMatrix(long addr, float[] array);

    public void getOpenGLMatrix(float[] m) {
        getOpenGLMatrix(getCPointer(), m);
    }

    /*[-C++;-NATIVE]
        float * m = (float*)env->GetPrimitiveArrayCritical(array, 0);
        btTransform * transform = (btTransform*)addr;
        btVector3 & origin = transform->getOrigin();
        btMatrix3x3 & matrix3x3 = transform->getBasis();
        const btVector3 & row0 = matrix3x3.getRow(0);
        const btVector3 & row1 = matrix3x3.getRow(1);
        const btVector3 & row2 = matrix3x3.getRow(2);
        m[0] = row0.getX();
        m[1] = row1.getX();
        m[2] = row2.getX();
        m[3] = 0;
        m[4] = row0.getY();
        m[5] = row1.getY();
        m[6] = row2.getY();
        m[7] = 0;
        m[8] = row0.getZ();
        m[9] = row1.getZ();
        m[10] = row2.getZ();
        m[11] = 0;
        m[12] = origin.getX();
        m[13] = origin.getY();
        m[14] = origin.getZ();
        m[15] = 1.0;

        env->ReleasePrimitiveArrayCritical(array, m, 0);
    */
    /*[-teaVM;-NATIVE]
        var worldTrans = bullet.wrapPointer(addr, bullet.btTransform);
        var origin = worldTrans.getOrigin();
        var matrix3x3 = worldTrans.getBasis();
        var row0 = matrix3x3.getRow(0);
        var row1 = matrix3x3.getRow(1);
        var row2 = matrix3x3.getRow(2);
        var m = array;
        m[0] = row0.getX();
        m[1] = row1.getX();
        m[2] = row2.getX();
        m[3] = 0;
        m[4] = row0.getY();
        m[5] = row1.getY();
        m[6] = row2.getY();
        m[7] = 0;
        m[8] = row0.getZ();
        m[9] = row1.getZ();
        m[10] = row2.getZ();
        m[11] = 0;
        m[12] = origin.getX();
        m[13] = origin.getY();
        m[14] = origin.getZ();
        m[15] = 1.0;
    */
    private static native void getOpenGLMatrix(long addr, float[] array);

    public static void convert(Matrix4 in, btTransform out) {
        out.setFromOpenGLMatrix(in.val);
    }

    public static void convert(btTransform in, Matrix4 out) {
        in.getOpenGLMatrix(out.val);
    }

    public static void convert(Matrix4 in, long outAddr) {
        emptyTransform.setPointer(outAddr);
        convert(in, emptyTransform);
    }

    public static void convert(long inAddr, Matrix4 out) {
        emptyTransform.setPointer(inAddr);
        convert(emptyTransform, out);
    }
}