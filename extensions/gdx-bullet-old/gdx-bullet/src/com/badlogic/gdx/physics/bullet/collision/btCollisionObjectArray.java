package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btCollisionObjectArray extends BulletBase {

	/*JNI
		#include <src/bullet/LinearMath/btAlignedObjectArray.h>
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionObject.h>
	*/

    public btCollisionObjectArray(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.MyCollisionObjectArray); #EVAL
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
		btAlignedObjectArray<const btCollisionObject *> * array = (btAlignedObjectArray<const btCollisionObject *> *)addr;
		return array->size();
	*/
    /*[0;X;D]*/

    public btCollisionObject at(int n, btCollisionWorld world) {
        checkPointer();
        long at = at(cPointer, n);
        return world.bodies.get(at);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		long addr = Bullet.getPointer(jsObj.at(n)); #EVALLONG
		return world.bodies.get(addr); #J
	*/

    private static native long at(long addr, int n); /*
		btAlignedObjectArray<const btCollisionObject *> * array = (btAlignedObjectArray<const btCollisionObject *> *)addr;
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
		btAlignedObjectArray<const btCollisionObject *> * array = (btAlignedObjectArray<const btCollisionObject *> *)addr;
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
		btAlignedObjectArray<const btCollisionObject *> * array = (btAlignedObjectArray<const btCollisionObject *> *)addr;
		array->resize(newsize);
	*/
    /*[0;X;D]*/
}
