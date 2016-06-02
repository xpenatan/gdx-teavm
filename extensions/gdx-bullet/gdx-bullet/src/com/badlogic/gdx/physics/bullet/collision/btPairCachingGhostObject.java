package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
public class btPairCachingGhostObject extends btGhostObject{

	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btGhostObject.h>
	*/
	
	protected void create() {
		resetObj(createNative(), true);
	}
	
	private static native long createNative(); /*
		return (jlong)new btPairCachingGhostObject();
	*/
	/*[0;X;L]
		var cobj = new Bullet.btPairCachingGhostObject();
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btPairCachingGhostObject);
		}
	*/
}
