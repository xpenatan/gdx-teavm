package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btScalarArray extends BulletBase {

	/*JNI
		#include <src/bullet/LinearMath/btAlignedObjectArray.h>
		#include <src/bullet/LinearMath/btScalar.h>
	*/

    public btScalarArray(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.MyScalarArray); #EVAL
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
		btAlignedObjectArray<btScalar> * array = (btAlignedObjectArray<btScalar> *)addr;
		return array->size();
	*/
    /*[0;X;D]*/

    public float at(int n) {
        checkPointer();
        return at(cPointer, n);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.at(n);
	*/

    private static native float at(long addr, int n); /*
		btAlignedObjectArray<btScalar> * array = (btAlignedObjectArray<btScalar> *)addr;
		return array->at(n);
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
		btAlignedObjectArray<btScalar> * array = (btAlignedObjectArray<btScalar> *)addr;
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
		btAlignedObjectArray<btScalar> * array = (btAlignedObjectArray<btScalar> *)addr;
		array->resize(newsize);
	*/
    /*[0;X;D]*/
}
