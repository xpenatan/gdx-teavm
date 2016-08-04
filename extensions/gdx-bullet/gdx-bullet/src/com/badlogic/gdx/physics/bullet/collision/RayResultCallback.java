package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class RayResultCallback extends BulletBase{
	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionWorld.h>
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionObject.h>
	*/

	static LocalRayResult tmpLocalRes = new LocalRayResult(0, false); 

	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.RayResultCallback); #EVAL
		}
	*/

	@Override
	protected void delete() {
		clear();
		super.delete();
	}

	public boolean hasHit() {
		checkPointer();
		return hasHit(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.hasHit();
	*/

	private static native boolean hasHit(long addr); /*
		btCollisionWorld::RayResultCallback * callback = (btCollisionWorld::RayResultCallback *)addr;
		return callback->hasHit();
	*/
	/*[0;X;D]*/

	public void setClosestHitFraction(float value) {
		checkPointer();
		setClosestHitFraction(cPointer, value);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.set_m_closestHitFraction(value);
	*/
	
	private static native void setClosestHitFraction(long addr, float value); /*
		btCollisionWorld::RayResultCallback * callback = (btCollisionWorld::RayResultCallback *)addr;
		callback->m_closestHitFraction = value;
	*/
	/*[0;X;D]*/

	public void setCollisionObject(btCollisionObject value) {
		checkPointer();
		setCollisionObject(cPointer, value != null ? value.cPointer : 0);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		coladdr, (value != null ? value.cPointer : 0)  #P
		var colobj = Bullet.wrapPointer(coladdr, Bullet.btCollisionObject);
		jsObj.set_m_collisionObject(colobj);
	*/

	private static native void setCollisionObject(long addr, long objAddr); /*
		btCollisionWorld::RayResultCallback * callback = (btCollisionWorld::RayResultCallback *)addr;
		btCollisionObject * collObj = (btCollisionObject *)objAddr;
		callback->m_collisionObject = collObj;
	*/
	/*[0;X;D]*/

	public btCollisionObject getCollisionObject(btCollisionWorld world) {
		checkPointer(); 
		long cPtr = getCollisionObject(cPointer);
		return world.bodies.get(cPtr);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		var collObj = jsObj.get_m_collisionObject();
		var colAddr = 0; 
		if(collObj !== undefined) { #B
			var pointer=Bullet.getPointer(collObj);
			if(pointer !== undefined) 
				colAddr = pointer;
		} #E
		long colAddr = colAddr; #EVALLONG
		return world.bodies.get(colAddr); #J
	*/

	private static native long getCollisionObject(long addr); /*
		btCollisionWorld::RayResultCallback * callback = (btCollisionWorld::RayResultCallback *)addr;
		return (jlong)callback->m_collisionObject;
	*/
	/*[0;X;D]*/

	public float addSingleResult(LocalRayResult rayResult, boolean normalInWorldSpace) {
		return 0;
	}

	/**
	 * Use to remove objects after raycasting
	 */
	public void clear()
	{
		setCollisionObject(null);
	}
	//TODO need to finish
}
