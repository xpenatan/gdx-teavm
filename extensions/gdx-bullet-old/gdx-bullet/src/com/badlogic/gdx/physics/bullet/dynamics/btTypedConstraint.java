package com.badlogic.gdx.physics.bullet.dynamics;

import com.badlogic.gdx.physics.bullet.linearmath.btTypedObject;

/**
 * @author xpenatan
 */
public class btTypedConstraint extends btTypedObject {

    public btTypedConstraint(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }
	
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btTypedConstraint); #EVAL
		}
	*/
}
