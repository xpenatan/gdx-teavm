package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
public class btGhostObject extends btCollisionObject {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btGhostObject.h>
	*/
	
	protected void create() {
		resetObj(createNative(), true);
	}
	
	private static native long createNative(); /*
		return (jlong)new btGhostObject();
	*/
	/*[0;X;L]
		var cobj = new Bullet.btGhostObject();
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btGhostObject);
		}
	*/
	
	public int getNumOverlappingObjects() {
		checkPointer();
		return getNumOverlappingObjects(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		return this.$$$jsObj.getNumOverlappingObjects();
	*/
	
	private static native int getNumOverlappingObjects(long addr); /*
		btGhostObject * cobj = (btGhostObject *)addr;
		return cobj->getNumOverlappingObjects();
	*/
	/*[0;X;D]*/
	
	public btCollisionObject getOverlappingObject(int index, btCollisionWorld world) {
		checkPointer();
		long addr =getOverlappingObject(cPointer, index);
		return world.bodies.get(addr);
	}
	/*[0;X;L]
		checkPointer(); #J
		long addr = 0; #J
		addr = Bullet.getPointer(this.$$$jsObj.getOverlappingObject(index));
		return world.bodies.get(addr); #J
	*/
	
	private static native long getOverlappingObject(long addr, int index); /*
		btGhostObject * cobj = (btGhostObject *)addr;
		return (jlong)cobj->getOverlappingObject(index);
	*/
	/*[0;X;D]*/
}
