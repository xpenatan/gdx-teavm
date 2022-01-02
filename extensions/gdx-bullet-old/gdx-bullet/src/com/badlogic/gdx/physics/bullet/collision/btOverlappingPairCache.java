package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
public class btOverlappingPairCache extends btOverlappingPairCallback {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btOverlappingPairCache.h>
	*/
	
	btBroadphasePairArray tmp = new btBroadphasePairArray(0, false);
	
	public btOverlappingPairCache(long cPtr, boolean cMemoryOwn) {
		super(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btOverlappingPairCache); #EVAL
		}
	*/
	
	public btBroadphasePairArray getOverlappingPairArray() {
		checkPointer();
		long addr = getOverlappingPairArray(cPointer);
		tmp.resetObj(addr, false);
		return tmp;
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		long addr = Bullet.getPointer(jsObj.getOverlappingPairArray()); #EVALLONG
		tmp.resetObj(addr, false); #J
		return tmp; #J
	*/
	
	private static native long getOverlappingPairArray(long addr); /*
		btOverlappingPairCache * pairCache = (btOverlappingPairCache *)addr;
		return (jlong)&pairCache->getOverlappingPairArray();
	*/
	/*[0;X;D]*/
}
