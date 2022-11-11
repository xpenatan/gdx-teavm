package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btQuaternion extends BulletBase {
	/*JNI
		#include <src/bullet/LinearMath/btQuaternion.h>
	*/

    final public static float[] localArr_1 = new float[4];
    final public static float[] localArr_2 = new float[4];

    public btQuaternion() {
        resetObj(createNative(0, 0, 0, 0), true);
    }

    public btQuaternion(float x, float y, float z, float w) {
        resetObj(createNative(x, y, z, w), true);
    }

    public btQuaternion(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }

    private static native long createNative(float x, float y, float z, float w); /*
		return (jlong)new btQuaternion(x,y,z,w);
	*/
	/*[0;X;L]
		var quat = new Bullet.btQuaternion(x,y,z,w);
		return Bullet.getPointer(quat);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			jsObj = Bullet.wrapPointer(addr, Bullet.btQuaternion); #EVAL
		}
	*/

    @Override
    protected void delete() {
        deletePointer(cPointer);
    }
    /*[0;X;D]*/

    private static native void deletePointer(long addr); /*
		btQuaternion * cobj = (btQuaternion *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/

    public static void set(btQuaternion quat, float[] value) {
        quat.checkPointer();
        btQuaternion.set(quat.cPointer, value);
    }
	/*[0;X;L]
	 	quat.checkPointer();  #J
	 	jsObj, quat.jsObj #P
		jsObj.setValue(value[0],value[1],value[2],value[3]);
	*/

    private static native void set(long addr, float[] value); /*
		btQuaternion * quat = (btQuaternion *)addr;
		quat->setValue(value[0],value[1],value[2],value[3]);
	*/
    /*[0;X;D]*/

    public static void get(btQuaternion quaternion, float[] value) {
        quaternion.checkPointer();
        btQuaternion.get(quaternion.cPointer, value);
    }
	/*[0;X;L]
	 	quaternion.checkPointer();  #J
		Object quat = quaternion.jsObj;  #J
		value[0] = quat.x();
		value[1] = quat.y();
		value[2] = quat.z();
		value[3] = quat.w();
	*/

    private static native void get(long addr, float[] value); /*
		btQuaternion * quat = (btQuaternion *)addr;
		value[0] = quat->x();
		value[1] = quat->y();
		value[2] = quat->z();
		value[3] = quat->w();
	*/
    /*[0;X;D]*/

    public static void set(Quaternion in, btQuaternion out) {
        out.checkPointer();
        btQuaternion.localArr_1[0] = in.x;
        btQuaternion.localArr_1[1] = in.y;
        btQuaternion.localArr_1[2] = in.z;
        btQuaternion.localArr_1[3] = in.w;
        btQuaternion.set(out.cPointer, btQuaternion.localArr_1);
    }
	/*[0;X]
	 	out.checkPointer();
		btQuaternion.localArr_1[0] = in.x;
		btQuaternion.localArr_1[1] = in.y;
		btQuaternion.localArr_1[2] = in.z;
		btQuaternion.localArr_1[3] = in.w;
		btQuaternion.set(out, btQuaternion.localArr_1);
	*/

    public static void get(btQuaternion in, Quaternion out) {
        in.checkPointer();
        btQuaternion.get(in.cPointer, btQuaternion.localArr_1);
        out.set(btQuaternion.localArr_1[0], btQuaternion.localArr_1[1], btQuaternion.localArr_1[2], btQuaternion.localArr_1[3]);
    }
	/*[0;X]
	 	in.checkPointer();
		btQuaternion.get(in, btQuaternion.localArr_1);
		out.set(btQuaternion.localArr_1[0], btQuaternion.localArr_1[1], btQuaternion.localArr_1[2],btQuaternion.localArr_1[3]);
	*/

    public void setValue(float x, float y, float z, float w) {
        checkPointer();
        setValue(cPointer, x, y, z, w);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setValue(x,y,z,w);
	*/

    private static native void setValue(long addr, float x, float y, float z, float w); /*
		btQuaternion * quat = (btQuaternion *)addr;
		quat->setValue(x,y,z,w);
	*/
    /*[0;X;D]*/

    public float getX() {
        checkPointer();
        return getX(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.x();
	*/

    private static native float getX(long addr); /*
		btQuaternion * quat = (btQuaternion *)addr;
		return quat->x();
	*/
    /*[0;X;D]*/

    public float getY() {
        checkPointer();
        return getY(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.y();
	*/

    private static native float getY(long addr); /*
		btVector3 * quat = (btVector3 *)addr;
		return quat->y();
	*/
    /*[0;X;D]*/

    public float getZ() {
        checkPointer();
        return getZ(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.z();
	*/

    private static native float getZ(long addr); /*
		btQuaternion * quat = (btQuaternion *)addr;
		return quat->z();
	*/
    /*[0;X;D]*/

    public float getW() {
        checkPointer();
        return getW(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.w();
	*/

    private static native float getW(long addr); /*
		btQuaternion * quat = (btQuaternion *)addr;
		return quat->w();
	 */
    /*[0;X;D]*/

    public void setX(float x) {
        checkPointer();
        setX(cPointer, x);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setX(x);
	*/

    private static native void setX(long addr, float x); /*
		btQuaternion * quat = (btQuaternion *)addr;
		quat->setX(x);
	 */
    /*[0;X;D]*/

    public void setY(float y) {
        checkPointer();
        setY(cPointer, y);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setY(y);
	*/

    private static native void setY(long addr, float y); /*
		btQuaternion * quat = (btQuaternion *)addr;
		quat->setY(y);
	*/
    /*[0;X;D]*/

    public void setZ(float z) {
        checkPointer();
        setZ(cPointer, z);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setZ(z);
	*/

    private static native void setZ(long addr, float z); /*
		btQuaternion * quat = (btQuaternion *)addr;
		quat->setZ(z);
	*/
    /*[0;X;D]*/

    public void setW(float w) {
        checkPointer();
        setW(cPointer, w);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setW(w);
	*/

    private static native void setW(long addr, float w); /*
		btQuaternion * quat = (btQuaternion *)addr;
		quat->setW(w);
	*/
    /*[0;X;D]*/
}
