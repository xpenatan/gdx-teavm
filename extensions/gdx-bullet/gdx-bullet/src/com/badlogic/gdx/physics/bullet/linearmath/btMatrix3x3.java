package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btMatrix3x3 extends BulletBase{
	/*JNI
		#include <src/bullet/LinearMath/btMatrix3x3.h>
	*/
	
	public btMatrix3x3() {
		resetObj(createNative(), true);
	}

	private static native long createNative(); /*
		return (jlong)new btMatrix3x3();
	*/
	/*[0;X;L]
		return Bullet.getPointer(new Bullet.btMatrix3x3());
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btMatrix3x3);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var mat = Bullet.wrapPointer(addr, Bullet.btMatrix3x3);
		Bullet.destroy(mat);
	*/
	
	private static native void deletePointer(long addr); /*
		btMatrix3x3 * cobj = (btMatrix3x3 *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	public static void set(btMatrix3x3 mat, float[] value) {
		mat.checkPointer();
		btMatrix3x3.set(mat.cPointer, value);
	}
	/*[0;X;L]
	 	mat.checkPointer(); #J
		Object mat3 = mat.jsObj;  #J
		mat3.setValue(value[0],value[3],value[6],value[1],value[4],value[7],value[2],value[5],value[8]);	*/

	// Convert to column-major
	private static native void set(long addr, float[] value); /*
		btMatrix3x3 * mat3 = (btMatrix3x3 *)addr;
		mat3->setValue(
		value[0], value[3], value[6],
		value[1], value[4], value[7],
		value[2], value[5], value[8]);
	*/
	/*[0;X;D]*/

	public static void get(btMatrix3x3 mat, float[] value) {
		mat.checkPointer();
		btMatrix3x3.get(mat.cPointer, value);
	}
	/*[0;X;L]
	 	mat.checkPointer(); #J
		Object mat3 = mat.jsObj;  #J
		value[0] = mat3.getColumn(0).getX();
		value[1] = mat3.getColumn(0).getY();
		value[2] = mat3.getColumn(0).getZ();
		value[3] = mat3.getColumn(1).getX();
		value[4] = mat3.getColumn(1).getY();
		value[5] = mat3.getColumn(1).getZ();
		value[6] = mat3.getColumn(2).getX();
		value[7] = mat3.getColumn(2).getY();
		value[8] = mat3.getColumn(2).getZ();
	*/
	
	// Convert to column-major
	private static native void get(long addr, float[] value); /*
		btMatrix3x3 * mat3 = (btMatrix3x3 *)addr;
		value[0] = (jfloat) mat3->getColumn(0).getX();
		value[1] = (jfloat) mat3->getColumn(0).getY();
		value[2] = (jfloat) mat3->getColumn(0).getZ();
		value[3] = (jfloat) mat3->getColumn(1).getX();
		value[4] = (jfloat) mat3->getColumn(1).getY();
		value[5] = (jfloat) mat3->getColumn(1).getZ();
		value[6] = (jfloat) mat3->getColumn(2).getX();
		value[7] = (jfloat) mat3->getColumn(2).getY();
		value[8] = (jfloat) mat3->getColumn(2).getZ();
	*/
	/*[0;X;D]*/
	
	public static void set(Matrix3 in, btMatrix3x3 out) {
		out.checkPointer();
		btMatrix3x3.set(out.cPointer, in.val);
	}
	/*[0;X;L]
	 	out.checkPointer(); #J
		btMatrix3x3.set(out, in.val); #J
	*/

	public static void get(btMatrix3x3 in, Matrix3 out) {
		in.checkPointer();
		btMatrix3x3.get(in.cPointer, out.val);
	}
	/*[0;X;L]
	 	in.checkPointer(); #J
		btMatrix3x3.get(in, out.val); #J
	*/
}
