package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btBroadphasePairArray extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btOverlappingPairCache.h>
	*/

    btBroadphasePair tmp = new btBroadphasePair(0, false);

    public btBroadphasePairArray(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btBroadphasePairArray); #EVAL
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
		btBroadphasePairArray * array = (btBroadphasePairArray *)addr;
		return array->size();
	*/
    /*[0;X;D]*/

    /**
     * Dont keep a reference of any object.
     */
    public btBroadphasePair at(int n) {
        checkPointer();
        long addr = at(cPointer, n);
        tmp.resetObj(addr, false);
        return tmp;
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		long addr = Bullet.getPointer(jsObj.at(n)) #EVALLONG
		tmp.resetObj(addr, false); #J
		return tmp; #J
	*/

    private static native long at(long addr, int n); /*
		btBroadphasePairArray * array = (btBroadphasePairArray *)addr;
		return (jlong)&array->at(n);
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
		btBroadphasePairArray * array = (btBroadphasePairArray *)addr;
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
		btBroadphasePairArray * array = (btBroadphasePairArray *)addr;
		array->resize(newsize);
	*/
    /*[0;X;D]*/

//	public int getCollisionObjects(int[] result, int max, int other) {
//		return 0;
//	}
//	
//	private static native void getCollisionObjects(long addr, int[] result, int max, int other); /*
//		btBroadphasePairArray * array = (btBroadphasePairArray *)addr;
//		
//	*/
//	/*[0;X;D]*/
//
//	public int getCollisionObjectsValue(int[] result, int max, int other) {
//		return 0;
//	}
}
