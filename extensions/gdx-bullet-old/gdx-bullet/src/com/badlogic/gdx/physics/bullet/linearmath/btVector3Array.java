package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btVector3Array extends BulletBase {

	/*JNI
		#include <src/bullet/LinearMath/btAlignedObjectArray.h>
		#include <src/bullet/LinearMath/btVector3.h>
	*/
	
	public btVector3Array(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr,this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.MyVector3Array) #EVAL
		}
	*/
	
	public int size() {
		checkPointer();
		return size(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.size();
	*/
	
	private static native int size(long addr); /*
		btAlignedObjectArray<btVector3> * array = (btAlignedObjectArray<btVector3> *)addr;
		return array->size();
	*/
	/*[0;X;D]*/

	public void at(int n, Vector3 out) {
		checkPointer();
		at(cPointer, n, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		float x=0,y=0,z=0; #J
		jsObj, this.jsObj #P
		var vec = jsObj.at(n);
		x = vec.x();
		y = vec.y();
		z = vec.z();
		out.set(x,y,z);
	*/
	
	private static native void at(long addr, int n, float [] value); /*
		btAlignedObjectArray<btVector3> * array = (btAlignedObjectArray<btVector3> *)addr;
		btVector3 vec = array->at(n);
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/

	public int capacity() {
		checkPointer();
		return capacity(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.capacity();
	*/
	
	private static native int capacity(long addr); /*
		btAlignedObjectArray<btVector3> * array = (btAlignedObjectArray<btVector3> *)addr;
		return array->capacity();
	*/
	/*[0;X;D]*/

	public void resize(int newsize) {
		checkPointer();
		resize(cPointer, newsize);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.resize(newsize);
	*/
	
	private static native void resize(long addr, int newsize); /*
		btAlignedObjectArray<btVector3> * array = (btAlignedObjectArray<btVector3> *)addr;
		array->resize(newsize);
	*/
	/*[0;X;D]*/
}
