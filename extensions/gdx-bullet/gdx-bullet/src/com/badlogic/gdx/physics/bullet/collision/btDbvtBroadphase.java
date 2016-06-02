package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
public class btDbvtBroadphase extends btBroadphaseInterface {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btDbvtBroadphase.h>
	*/

	public btDbvtBroadphase() {
		resetObj(createNative(), true);
	}

	private static native long createNative(); /*
		return (jlong)new btDbvtBroadphase();
	*/
	/*[0;X;L]
		var dbvt = new Bullet.btDbvtBroadphase();
		return Bullet.getPointer(dbvt);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btDbvtBroadphase);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var dbvt = Bullet.wrapPointer(addr, Bullet.btDbvtBroadphase);
		Bullet.destroy(dbvt);
	*/
	
	private static native void deletePointer(long addr); /*
		btDbvtBroadphase * cobj = (btDbvtBroadphase *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
