package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;

/** @author xpenatan */
public class btCylinderShape extends btConvexInternalShape {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionShapes/btCylinderShape.h>
	*/
	
	public btCylinderShape(Vector3 halfExtents) {
		resetObj(createNative(halfExtents.x, halfExtents.y, halfExtents.z), true);
	}
	
	public static native long createNative(float x, float y, float z); /*
		btVector3 vec(x,y,z);
		return (jlong)new btCylinderShape(vec);
	*/
	/*[0;X;L]
		var vec = Bullet.Temp.prototype.btVec3();
		vec.setValue(x,y,z);
		return Bullet.getPointer(new Bullet.btCylinderShape(vec));
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btCylinderShape);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var vec = Bullet.wrapPointer(addr, Bullet.btCylinderShape);
		Bullet.destroy(vec);
	*/
	
	private static native void deletePointer(long addr); /*
		btCylinderShape * cobj = (btCylinderShape *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
