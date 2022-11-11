package com.badlogic.gdx.physics.bullet.collision;

/**
 * @author xpenatan
 */
public class btDefaultCollisionConfiguration extends btCollisionConfiguration {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btDefaultCollisionConfiguration.h>
	*/

//	public btDefaultCollisionConfiguration(btDefaultCollisionConstructionInfo constructionInfo) {
//	}

    public btDefaultCollisionConfiguration() {
        resetObj(createNative(), true);
    }

    private static native long createNative(); /*
		return (jlong)new btDefaultCollisionConfiguration();
	*/
	/*[0;X;L]
		var conf = new Bullet.btDefaultCollisionConfiguration();
		return Bullet.getPointer(conf);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btDefaultCollisionConfiguration); #EVAL
		}
	*/

    @Override
    protected void delete() {
        deletePointer(cPointer);
    }
    /*[0;X;D]*/

    private static native void deletePointer(long addr); /*
		btDefaultCollisionConfiguration * cobj = (btDefaultCollisionConfiguration *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/
}
