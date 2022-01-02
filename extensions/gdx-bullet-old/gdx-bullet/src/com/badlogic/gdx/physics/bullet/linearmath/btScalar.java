package com.badlogic.gdx.physics.bullet.linearmath;

/** @author xpenatan */
public class btScalar {
	/*JNI
		#include <src/bullet/LinearMath/btScalar.h>
	 */

	public static native int btGetVersion(); /*
		return btGetVersion();
	*/
	/*[0;X;L]
		return Bullet.MyClassHelper.prototype.getBTVersion();
	*/
}
