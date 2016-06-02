package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class btDispatcher extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/BroadphaseCollision/btDispatcher.h>
	*/
	
	private btPersistentManifold manifold = new btPersistentManifold(0, false);
	
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btDispatcher);
		}
	*/
	
	public int getNumManifolds() {
		checkPointer();
		return getNumManifolds(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		return this.$$$jsObj.getNumManifolds();
	*/
	
	private static native int getNumManifolds(long addr); /*
		btDispatcher * cobj = (btDispatcher *)addr;
		return cobj->getNumManifolds();
	 */
	/*[0;X;D]*/
	
	public btPersistentManifold getManifoldByIndexInternal(int index) {
		checkPointer();
		long addr = getManifoldByIndexInternal(cPointer, index);
		manifold.resetObj(addr, false);
		return manifold;
	}
	/*[0;X;L]
		checkPointer();  #J
		long addr = 0; #J
		addr = Bullet.getPointer(this.$$$jsObj.getManifoldByIndexInternal(index));
		manifold.resetObj(addr, false); #J
		return manifold; #J
	*/
	
	private static native long getManifoldByIndexInternal(long addr, int index); /*
		btDispatcher * cobj = (btDispatcher *)addr;
		return (jlong)cobj->getManifoldByIndexInternal(index);
	 */
	/*[0;X;D]*/
	
}
