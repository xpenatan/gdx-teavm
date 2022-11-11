package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btCollisionDispatcher extends btDispatcher {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionDispatcher.h>
	*/

    public btCollisionDispatcher(btCollisionConfiguration collisionConfiguration) {
        resetObj(createNative(collisionConfiguration.cPointer), true);
    }

    private static native long createNative(long collisionConfigurationAddr); /*
		btCollisionConfiguration * conf = (btCollisionConfiguration *)collisionConfigurationAddr;
		return (jlong)new btCollisionDispatcher(conf);
	*/
	/*[0;X;L]
		var config = Bullet.wrapPointer(collisionConfigurationAddr, Bullet.btCollisionConfiguration);
		var disp = new Bullet.btCollisionDispatcher(config);
		return Bullet.getPointer(disp);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionDispatcher); #EVAL
		}
	*/

    @Override
    protected void delete() {
        deletePointer(cPointer);
    }
    /*[0;X;D]*/

    private static native void deletePointer(long addr); /*
		btCollisionDispatcher * cobj = (btCollisionDispatcher *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/
}
