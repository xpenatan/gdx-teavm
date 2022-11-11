package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
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
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btDbvtBroadphase); #EVAL
		}
	*/

    @Override
    protected void delete() {
        deletePointer(cPointer);
    }
    /*[0;X;D]*/

    private static native void deletePointer(long addr); /*
		btDbvtBroadphase * cobj = (btDbvtBroadphase *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/
}
