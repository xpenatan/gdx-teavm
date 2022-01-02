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
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		return Bullet.getPointer(new Bullet.btCylinderShape(vec));
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btCylinderShape); #EVAL
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		btCylinderShape * cobj = (btCylinderShape *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
