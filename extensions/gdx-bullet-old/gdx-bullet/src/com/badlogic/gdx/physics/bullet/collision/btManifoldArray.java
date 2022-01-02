package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btManifoldArray extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btCollisionAlgorithm.h>
	*/
	btPersistentManifold tmp = new btPersistentManifold(0, false);
	
	public btManifoldArray() {
		resetObj(createNative(), true);
	}
	
	private static native long createNative(); /*
		return (jlong)new btManifoldArray();
	*/
	/*[0;X;L]
		var cobj = new Bullet.btManifoldArray();
		return Bullet.getPointer(cobj);
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		btManifoldArray * cobj = (btManifoldArray *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	
	public btManifoldArray(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btManifoldArray); #EVAL
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
		btManifoldArray * array = (btManifoldArray *)addr;
		return array->size();
	*/
	/*[0;X;D]*/
	
	/**
	 * Dont keep a reference of any object.
	 */
	public btPersistentManifold at(int n) {
		checkPointer();
		long addr = at(cPointer, n);
		tmp.resetObj(addr, false);
		return tmp;
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		long addr = Bullet.getPointer(jsObj.at(n)); #EVALLONG
		tmp.resetObj(addr, false); #J
		return tmp; #J
	*/
	
	private static native long at(long addr, int n); /*
		btManifoldArray * array = (btManifoldArray *)addr;
		return (jlong)array->at(n);
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
		btManifoldArray * array = (btManifoldArray *)addr;
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
		btManifoldArray * array = (btManifoldArray *)addr;
		array->resize(newsize);
	*/
	/*[0;X;D]*/
}
