package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.linearmath.btTypedObject;

/** @author xpenatan */
public class btPersistentManifold extends btTypedObject {

	/*JNI
		#include <src/bullet/BulletCollision/NarrowPhaseCollision/btPersistentManifold.h>
	*/
	
	private btManifoldPoint maniFoldPoint = new btManifoldPoint(0, false);
	
	public btPersistentManifold(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btPersistentManifold);
		}
	*/
	
	public btManifoldPoint getContactPoint(int index) {
		checkPointer();
		long addr = getContactPoint(cPointer, index);
		maniFoldPoint.resetObj(addr, false);
		return maniFoldPoint;
	}
	/*[0;X;L]
		checkPointer();  #J
		long addr = 0; #J
		addr = Bullet.getPointer(this.$$$jsObj.getContactPoint(index));
		maniFoldPoint.resetObj(addr, false); #J
		return maniFoldPoint; #J
	*/
	
	private static native long getContactPoint(long addr, int index); /*
		btPersistentManifold * cobj = (btPersistentManifold *)addr;
		return (jlong)&cobj->getContactPoint(index);
	 */
	/*[0;X;D]*/
	
	
	public int getNumContacts() {
		checkPointer();
		return getNumContacts(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		return this.$$$jsObj.getNumContacts();
	*/
	
	private static native int getNumContacts(long addr); /*
		btPersistentManifold * cobj = (btPersistentManifold *)addr;
		return cobj->getNumContacts();
	 */
	/*[0;X;D]*/
	
	public btCollisionObject getBody0(btCollisionWorld world) {
		checkPointer();
		long addr = getBody0(cPointer);
		return world.bodies.get(addr);
	}
	/*[0;X;L]
		checkPointer();  #J
		long addr = 0; #J
		addr = Bullet.getPointer(this.$$$jsObj.getBody0());
		return world.bodies.get(addr); #J
	*/
	
	private static native long getBody0(long addr); /*
		btPersistentManifold * cobj = (btPersistentManifold *)addr;
		return (jlong)cobj->getBody0();
	 */
	/*[0;X;D]*/
	
	public btCollisionObject getBody1(btCollisionWorld world) {
		checkPointer();
		long addr = getBody1(cPointer);
		return world.bodies.get(addr);
	}
	/*[0;X;L]
		checkPointer();  #J
		long addr = 0; #J
		addr = Bullet.getPointer(this.$$$jsObj.getBody1());
		return world.bodies.get(addr); #J
	*/
	
	private static native long getBody1(long addr); /*
		btPersistentManifold * cobj = (btPersistentManifold *)addr;
		return (jlong)cobj->getBody1();
	 */
	/*[0;X;D]*/
}
