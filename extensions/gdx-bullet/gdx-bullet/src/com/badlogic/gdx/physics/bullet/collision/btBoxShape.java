package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
/*[0;X;D]*/

/** @author xpenatan */
public class btBoxShape extends btPolyhedralConvexShape {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionShapes/btBoxShape.h>
	*/

	public btBoxShape(Vector3 boxHalfExtents) {
		resetObj(createNative(boxHalfExtents.x, boxHalfExtents.y, boxHalfExtents.z), true);
	}
	
	public static native long createNative(float x, float y, float z); /*
		btVector3 tmp;
		tmp.setValue(x,y,z);
		return (jlong)new btBoxShape(tmp);
	*/
	/*[0;X;L]
		var tmp = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		return Bullet.getPointer(new Bullet.btBoxShape(tmp));
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btBoxShape); #EVAL
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		btBoxShape * cobj = (btBoxShape *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	public void getHalfExtentsWithMargin(Vector3 out) {
		checkPointer();
		getHalfExtentsWithMargin(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]	
		checkPointer(); #J
		jsObj, this.jsObj #P
		var vec = jsObj.getHalfExtentsWithMargin();
		out.x = vec.x(); #EVALFLOAT
		out.y = vec.y(); #EVALFLOAT
		out.z = vec.z(); #EVALFLOAT
	*/
	
	private static native void getHalfExtentsWithMargin(long addr, float [] value); /*
		btBoxShape * shape = (btBoxShape *)addr;
		btVector3 vec = shape->getHalfExtentsWithMargin();
		value[0] = vec.getX();
		value[1] = vec.getY();
		value[2] = vec.getZ();
	*/
	/*[0;X;D]*/

	public void getHalfExtentsWithoutMargin(Vector3 out) {
		checkPointer();
		getHalfExtentsWithoutMargin(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]	
		checkPointer(); #J
		jsObj, this.jsObj #P
		var vec = jsObj.getHalfExtentsWithoutMargin();
		out.x = vec.x(); #EVALFLOAT
		out.y = vec.y(); #EVALFLOAT
		out.z = vec.z(); #EVALFLOAT
	*/
	
	private static native void getHalfExtentsWithoutMargin(long addr, float [] value); /*
		btBoxShape * shape = (btBoxShape *)addr;
		btVector3 vec = shape->getHalfExtentsWithoutMargin();
		value[0] = vec.getX();
		value[1] = vec.getY();
		value[2] = vec.getZ();
	*/
	/*[0;X;D]*/
}
