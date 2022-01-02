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
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btSequentialImpulseConstraintSolver); #EVAL
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		btSequentialImpulseConstraintSolver * cobj = (btSequentialImpulseConstraintSolver *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
}
