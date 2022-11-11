package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btBroadphaseInterface extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btBroadphaseInterface.h>
	*/

    btOverlappingPairCache overlappingPairCache = new btOverlappingPairCache(0, false);


	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btBroadphaseInterface); #EVAL
		}
	*/

    public btOverlappingPairCache getOverlappingPairCache() {
        checkPointer();
        if(overlappingPairCache.cPointer == 0)
            overlappingPairCache.resetObj(getOverlappingPairCache(cPointer), false);
        return overlappingPairCache;
    }
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
	 	if(overlappingPairCache.cPointer == 0) { #J
	 		long addr = Bullet.getPointer(jsObj.getOverlappingPairCache()); #EVALLONG
	 		overlappingPairCache.resetObj(addr,false); #J
	 	} #J
	 	return overlappingPairCache; #J
	*/

    private static native long getOverlappingPairCache(long addr); /*
		btBroadphaseInterface * cobj = (btBroadphaseInterface *)addr;
		return (jlong)cobj->getOverlappingPairCache();
	*/
    /*[0;X;D]*/
}
