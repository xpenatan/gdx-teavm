package com.badlogic.gdx.physics.bullet.dynamics;

/** @author xpenatan */
public class btSequentialImpulseConstraintSolver extends btConstraintSolver {

	/*JNI
		#include <src/bullet/BulletDynamics/ConstraintSolver/btSequentialImpulseConstraintSolver.h>
	*/
	
	public btSequentialImpulseConstraintSolver() {
		resetObj(createNative(), true);
	}
	
	private static native long createNative(); /*
		return (jlong)new btSequentialImpulseConstraintSolver();
	*/
	/*[0;X;L]
		var obj = new Bullet.btSequentialImpulseConstraintSolver();
		return Bullet.getPointer(obj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btSequentialImpulseConstraintSolver);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var cobj = Bullet.wrapPointer(addr, Bullet.btSequentialImpulseConstraintSolver);
		Bullet.destroy(cobj);
	*/
	
	private static native void deletePointer(long addr); /*
		btSequentialImpulseConstraintSolver * cobj = (btSequentialImpulseConstraintSolver *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
