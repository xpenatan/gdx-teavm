package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
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
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btDefaultCollisionConfiguration);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var conf = Bullet.wrapPointer(addr, Bullet.btDefaultCollisionConfiguration);
		Bullet.destroy(conf);
	*/
	
	private static native void deletePointer(long addr); /*
		btDefaultCollisionConfiguration * cobj = (btDefaultCollisionConfiguration *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
