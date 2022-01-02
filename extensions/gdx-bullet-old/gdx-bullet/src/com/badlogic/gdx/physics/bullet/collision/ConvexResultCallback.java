package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class ConvexResultCallback extends BulletBase {

	public ConvexResultCallback(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.ConvexResultCallback); #EVAL
		}
	*/
}
