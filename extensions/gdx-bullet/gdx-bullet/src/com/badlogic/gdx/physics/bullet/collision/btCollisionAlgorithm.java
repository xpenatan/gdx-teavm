package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btCollisionAlgorithm extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btCollisionAlgorithm.h>
	*/
	
	public btCollisionAlgorithm(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btCollisionAlgorithm);
		}
	*/
	
	public void getAllContactManifolds(btManifoldArray manifoldArray) {
		checkPointer();
		getAllContactManifolds(cPointer, manifoldArray.cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		this.$$$jsObj.getAllContactManifolds(manifoldArray.$$$jsObj);
	*/
	
	private static native void getAllContactManifolds(long addr, long manifoldArrayAddr); /*
		btCollisionAlgorithm * algorithm = (btCollisionAlgorithm *)addr;
		btManifoldArray * array = (btManifoldArray *)manifoldArrayAddr;
		algorithm->getAllContactManifolds(*array);
	*/
	/*[0;X;D]*/
}
