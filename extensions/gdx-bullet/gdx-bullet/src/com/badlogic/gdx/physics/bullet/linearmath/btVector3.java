package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btVector3 extends BulletBase {
	/*JNI
		#include <src/bullet/LinearMath/btVector3.h>
		#include <src/custom/gdx/common/envHelper.h>
	*/
	final public static Vector3 vector3_1 = new Vector3();
	final public static Vector3 vector3_2 = new Vector3();
	final public static Vector3 vector3_3 = new Vector3();

	final public static float [] localArr_1 = new float [3];
	final public static float [] localArr_2 = new float [3];
	
//	final public static btVector3 empty_btVec3_1 = new btVector3(0, false);
//	final public static btVector3 empty_btVec3_2 = new btVector3(0, false);
//	final public static btVector3 empty_btVec3_3 = new btVector3(0, false);
	
	/*[0;X]
	 	final public static btVector3 btVector3_1 = new btVector3(); // Pointer
	 	final public static btVector3 btVector3_2 = new btVector3(); // Pointer
	*/

	public btVector3() {
		create();
	}

	public btVector3(float x, float y, float z) {
		resetObj(createNative(x, y, z), true);
	}

	public btVector3(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}

	protected void create () {
		resetObj(createNative(0, 0, 0), true);
	}

	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/

	private static native void deletePointer(long addr); /*
		btVector3 * cobj = (btVector3 *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/

	private static native long createNative(float x, float y, float z); /*
		return (jlong)new btVector3(x,y,z);
	*/
	/*[0;X;L]
		var vec = new Bullet.btVector3(x,y,z);
		return Bullet.getPointer(vec);
	*/

	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btVector3); #EVAL
		}
	*/

	public void setValue(float x, float y, float z) {
		checkPointer();
		setValue(cPointer, x, y, z);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.setValue(x,y,z);
	*/

	private static native void setValue(long addr, float x, float y, float z); /*
		btVector3 * vec = (btVector3 *)addr;
		vec->setValue(x,y,z);
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
		btVector3 * vec = (btVector3 *)addr;
		return vec->x();
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
		btVector3 * vec = (btVector3 *)addr;
		return vec->y();
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
		btVector3 * vec = (btVector3 *)addr;
		return vec->z();
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
		btVector3 * vec = (btVector3 *)addr;
		vec->setX(x);
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
		btVector3 * vec = (btVector3 *)addr;
		vec->setY(y);
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
		btVector3 * vec = (btVector3 *)addr;
		vec->setZ(z);
	*/
	/*[0;X;D]*/

	public static void set(btVector3 vec, float[] value) {
		vec.checkPointer();
		btVector3.set(vec.cPointer, value);
	}
	/*[0;X;L]
		vec.checkPointer();  #J
		jsObj, vec.jsObj #P
		jsObj.setValue(value[0], value[1], value[2]);
	*/

	private static native void set(long addr, float[] value); /*
		btVector3 * vec = (btVector3 *)addr;
		vec->setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/

	public static void get(btVector3 vector, float[] value) {
		vector.checkPointer();
		btVector3.get(vector.cPointer, value);
	}
	/*[0;X;L]
	 	vector.checkPointer();  #J
		vec, vector.jsObj #P
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	
	private static native void get(long addr, float[] value); /*
		btVector3 * vec = (btVector3 *)addr;
		value[0] = vec->getX();
		value[1] = vec->getY();
		value[2] = vec->getZ();
	*/
	/*[0;X;D]*/

	public static void set(Vector3 in, btVector3 out) {
		out.checkPointer();
		btVector3.localArr_1[0] = in.x;
		btVector3.localArr_1[1] = in.y;
		btVector3.localArr_1[2] = in.z;
		btVector3.set(out.cPointer, btVector3.localArr_1);
	}
	/*[0;X]
	 	out.checkPointer();
	 	btVector3.localArr_1[0] = in.x;
		btVector3.localArr_1[1] = in.y;
		btVector3.localArr_1[2] = in.z;
		btVector3.set(out, btVector3.localArr_1);
	*/

	public static void get(btVector3 in, Vector3 out) {
		in.checkPointer();
		btVector3.get(in.cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X]
	 	in.checkPointer();
	 	btVector3.get(in, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	*/

	public float dot(Vector3 v) {
		checkPointer();
		btVector3.localArr_1[0] = v.x;
		btVector3.localArr_1[1] = v.y;
		btVector3.localArr_1[2] = v.z;
		return dot(cPointer, btVector3.localArr_1);
	}
	/*[0;X;D]*/

	public static native float dot(long addr, float[] value); /*
		btVector3 localVec;
		localVec.setX(value[0]);
		localVec.setY(value[1]);
		localVec.setZ(value[2]);
		btVector3 * vec = (btVector3 *)addr;
		return vec->dot(localVec);
	*/
	/*[0;X;D]*/

	public float length2() {
		checkPointer();
		return length2(cPointer);
	}
	/*[0;X;D]*/

	public static native float length2(long addr); /*
		btVector3 * vec = (btVector3 *)addr;
		return vec->length2();
	*/
	/*[0;X;D]*/

	public float length() {
		checkPointer();
		return length(cPointer);
	}
	/*[0;X;D]*/

	public static native float length(long addr); /*
		btVector3 * vec = (btVector3 *)addr;
		return vec->length();
	*/
	/*[0;X;D]*/

	public float norm() {
		checkPointer();
		return norm(cPointer);
	}
	/*[0;X;D]*/

	public static native float norm(long addr); /*
		btVector3 * vec = (btVector3 *)addr;
		return vec->norm();
	*/
	/*[0;X;D]*/

	public float distance2(Vector3 v) {
		checkPointer();
		btVector3.localArr_1[0] = v.x;
		btVector3.localArr_1[1] = v.y;
		btVector3.localArr_1[2] = v.z;
		return distance2(cPointer, btVector3.localArr_1);
	}
	/*[0;X;D]*/

	public static native float distance2(long addr, float[] value); /*
		btVector3 localVec;
		localVec.setX(value[0]);
		localVec.setY(value[1]);
		localVec.setZ(value[2]);
		btVector3 * vec = (btVector3 *)addr;
		return vec->distance2(localVec);
	*/
	/*[0;X;D]*/
	
	public float distance(Vector3 v) {
		checkPointer();
		btVector3.localArr_1[0] = v.x;
		btVector3.localArr_1[1] = v.y;
		btVector3.localArr_1[2] = v.z;
		return distance(cPointer, btVector3.localArr_1);
	}
	/*[0;X;D]*/

	public static native float distance(long addr, float[] value); /*
		btVector3 localVec;
		localVec.setX(value[0]);
		localVec.setY(value[1]);
		localVec.setZ(value[2]);
		btVector3 * vec = (btVector3 *)addr;
		return vec->distance(localVec);
	*/
	/*[0;X;D]*/
	

	public void safeNormalize(Vector3 v, Vector3 out) {
		checkPointer();
		safeNormalize(cPointer, btVector3.localArr_1);
		out.x = btVector3.localArr_1[0];
		out.y = btVector3.localArr_1[1];
		out.z = btVector3.localArr_1[2];
	}
	/*[0;X;D]*/

	public static native void safeNormalize(long addr, float[] value); /*
		btVector3 * vec = (btVector3 *)addr;
		vec->safeNormalize();
		value[0] = vec->getX();
		value[1] = vec->getY();
		value[2] = vec->getZ();
	*/
	/*[0;X;D]*/
	
	public void normalize(Vector3 v, Vector3 out) {
		checkPointer();
		normalize(cPointer, btVector3.localArr_1);
		out.x = btVector3.localArr_1[0];
		out.y = btVector3.localArr_1[1];
		out.z = btVector3.localArr_1[2];
	}
	/*[0;X;D]*/

	public static native void normalize(long addr, float[] value); /*
		btVector3 * vec = (btVector3 *)addr;
		vec->normalize();
		value[0] = vec->getX();
		value[1] = vec->getY();
		value[2] = vec->getZ();
	*/
	/*[0;X;D]*/

	/** Returns a temporary object. */
	public void normalized(Vector3 v, Vector3 out) {
		checkPointer();
		normalize(cPointer, btVector3.localArr_1);
		out.x = btVector3.localArr_1[0];
		out.y = btVector3.localArr_1[1];
		out.z = btVector3.localArr_1[2];
	}
	/*[0;X;D]*/
	
	public static native void normalized(long addr, float[] value); /*
		btVector3 * vec = (btVector3 *)addr;
		btVector3 tmpVec = vec->normalized();
		value[0] = tmpVec.getX();
		value[1] = tmpVec.getY();
		value[2] = tmpVec.getZ();
	*/
	/*[0;X;D]*/
}
