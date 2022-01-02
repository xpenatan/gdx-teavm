package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btTransform extends BulletBase{

	/*JNI
		#include <src/bullet/LinearMath/btTransform.h>
	*/
	
	public static Matrix4 tmp_param1 = new Matrix4();
	
	/*[0;X]
	 	final public static btTransform btTransform_1 = new btTransform(); // Pointer
	*/
	
	public btTransform() {
		resetObj(createNative(), true);
	}

	private static native long createNative(); /*
		return (jlong)new btTransform();
	*/
	/*[0;X;L]
		return Bullet.getPointer(new Bullet.btTransform());
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btTransform); #EVAL
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		btTransform * cobj = (btTransform *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	public static void set(btTransform tra, float[] value) {
		tra.checkPointer();
		btTransform.set(tra.cPointer, value);
	}
	/*[0;X;L]
	 	tra.checkPointer(); #J
		transform, tra.jsObj #P
		transform.setFromOpenGLMatrix(value);
	*/

	private static native void set(long addr, float[] value); /*
		btTransform * tra = (btTransform *)addr;
		tra->setFromOpenGLMatrix(value);
	*/
	/*[0;X;D]*/

	public static void get(btTransform tra, float[] value) {
		tra.checkPointer();
		btTransform.get(tra.cPointer, value);
	}
	/*[0;X;L]
	 	tra.checkPointer(); #J
		getOpenGLMatrix(tra.jsObj, value); #J	
	*/
	
	/*[0;X;F;L]
		public static void getOpenGLMatrix(Object transform, float[] value){
			var ptr=Bullet.ensureFloat32(value);
			transform.getOpenGLMatrix(ptr);
			ptr=ptr>>2;
			for(i=0;i<16;i++){
				value[i]=Bullet.HEAPF32[(ptr+i)];
			}; #E
		}
	*/
		
	private static native void get(long addr, float[] value); /*
		btTransform * tra = (btTransform *)addr;
		tra->getOpenGLMatrix(value);
	*/
	/*[0;X;D]*/
	
	public static void set(Matrix4 in, btTransform out) {
		out.checkPointer();
		btTransform.set(out.cPointer, in.val);
	}
	/*[0;X]
	 	out.checkPointer();
		btTransform.set(out, in.val);
	*/
	
	public static void get(btTransform in, Matrix4 out) {
		in.checkPointer();
		btTransform.get(in.cPointer, out.val);
	}
	/*[0;X]
	 	in.checkPointer();
		btTransform.get(in, out.val);
	*/
	
	public void set(Matrix4 in) {
		btTransform.set(this, in.val);
	}
	
	public void get(Matrix4 out) {
		btTransform.get(this, out.val);
	}
}
