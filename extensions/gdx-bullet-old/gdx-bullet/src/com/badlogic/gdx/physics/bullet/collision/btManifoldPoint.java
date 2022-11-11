package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btManifoldPoint extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/NarrowPhaseCollision/btManifoldPoint.h>
	*/

    public btManifoldPoint(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }
	
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btManifoldPoint); #EVAL
		}
	*/

    public float getDistance() {
        checkPointer();
        return getDistance(cPointer);
    }
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.getDistance();
	*/

    private static native float getDistance(long addr); /*
		btManifoldPoint * cobj = (btManifoldPoint *)addr;
		return cobj->getDistance();
	 */
    /*[0;X;D]*/
}
