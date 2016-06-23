package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
public class btCapsuleShape extends btConvexInternalShape {
	/*JNI
		#include <src/bullet/BulletCollision/CollisionShapes/btCapsuleShape.h>
	*/

	public btCapsuleShape(float radius, float height) {
		resetObj(createNative(radius, height), true);
	}
	
	public static native long createNative(float radius, float height); /*
		return (jlong)new btCapsuleShape(radius, height);
	*/
	/*[0;X;L]
		return Bullet.getPointer(new Bullet.btCapsuleShape(radius, height));
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btCapsuleShape);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var jsObj = Bullet.wrapPointer(addr, Bullet.btCapsuleShape);
		Bullet.destroy(jsObj);
	*/
	
	private static native void deletePointer(long addr); /*
		btCapsuleShape * cobj = (btCapsuleShape *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
