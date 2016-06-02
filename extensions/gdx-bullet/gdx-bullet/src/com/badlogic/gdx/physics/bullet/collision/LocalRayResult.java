package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class LocalRayResult extends BulletBase {
	/*JNI 
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionWorld.h>
	*/
	
	private LocalShapeInfo localShapeInfo = new LocalShapeInfo(0, false);
	
	public LocalRayResult(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.LocalRayResult);
		}
	*/
	
	
	public LocalShapeInfo getLocalShapeInfo() {
		checkPointer();
		long addr = getLocalShapeInfo(cPointer);
		if(addr != 0) {
			localShapeInfo.resetObj(addr, false);
			return localShapeInfo;
		}
		return null;
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	long addr = 0; #J
	 	addr = Bullet.getPointer(this.$$$jsObj.get_m_localShapeInfo());
		if(addr != 0) { #J
			localShapeInfo.resetObj(addr, false); #J
			return localShapeInfo; #J
		} #J
		return null; #J
	*/
	
	private static native long getLocalShapeInfo(long addr); /*
		btCollisionWorld::LocalRayResult * cobj = (btCollisionWorld::LocalRayResult *)addr;
		return (jlong)cobj->m_localShapeInfo;
	 */
	/*[0;X;D]*/
	
	public btCollisionObject getCollisionObject(btCollisionWorld world) {
		checkPointer();
		long addr = getCollisionObject(cPointer);
		return world.bodies.get(addr);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	long addr = 0; #J
	 	addr = Bullet.getPointer(this.$$$jsObj.get_m_collisionObject());
		return world.bodies.get(addr); #J
	*/
	
	private static native long getCollisionObject(long addr); /*
		btCollisionWorld::LocalRayResult * cobj = (btCollisionWorld::LocalRayResult *)addr;
		return (jlong)cobj->m_collisionObject;
	 */
	/*[0;X;D]*/
}
