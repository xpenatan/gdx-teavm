package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/** @author xpenatan */
public class btCollisionShape extends BulletBase{
	/*JNI
		#include <src/bullet/BulletCollision/CollisionShapes/btCollisionShape.h>
	*/
	
	public void getAabb(Matrix4 t, Vector3 aabbMin, Vector3 aabbMax) {
		checkPointer();
		getAabb(cPointer, t.val, btVector3.localArr_1, btVector3.localArr_2);
		aabbMin.set(btVector3.localArr_1);
		aabbMax.set(btVector3.localArr_2);
	}
	/*[0;X;D]

	*/	
	
	
	private static native void getAabb(long addr, float [] mat, float [] value1, float [] value2); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		btVector3 min;
		btVector3 max;
		btTransform tra;
		cobj->getAabb(tra, min, max);
		value1[0] = min.x();
		value1[1] = min.y();
		value1[2] = min.z();
		value2[0] = max.x();
		value2[1] = max.y();
		value2[2] = max.z();
		tra.getOpenGLMatrix(mat);
	*/
	/*[0;X;D]*/
	
	/**
	 * 
	 * @param value A size 4 array for index 0,1,2 (center), and 4 (radius) 
	 */
	public void getBoundingSphere(float [] value) {
		checkPointer();
		getBoundingSphere(cPointer, value);
	} //TODO need to fix js radius
	/*[0;X;L]	
		checkPointer(); #J
		jsObj, this.jsObj #P
		center, com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_1.jsObj #P
		var radius = 0;
		jsObj.getBoundingSphere(center, radius);
		value[0] = center.x();
		value[1] = center.y();
		value[2] = center.z();
		value[3] = radius;
	*/

	
	private static native void getBoundingSphere(long addr, float [] value); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		btVector3 center;
		btScalar radius;
		cobj->getBoundingSphere(center, radius);
		value[0] = center.x();
		value[1] = center.y();
		value[2] = center.z();
		value[3] = radius;
	*/
	/*[0;X;D]*/
	
	
	public float getAngularMotionDisc() {
		checkPointer();
		return getAngularMotionDisc(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.getAngularMotionDisc();
	*/
	
	private static native float getAngularMotionDisc(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->getAngularMotionDisc();
	*/
	/*[0;X;D]*/
	
	public boolean isPolyhedral() {
		checkPointer();
		return isPolyhedral(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.isPolyhedral();
	*/
	
	private static native boolean isPolyhedral(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isPolyhedral();
	*/
	/*[0;X;D]*/
	
	public boolean isConvex2d() {
		checkPointer();
		return isConvex2d(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.isConvex2d();
	*/
	
	private static native boolean isConvex2d(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isConvex2d();
	*/
	/*[0;X;D]*/

	public boolean isConvex() {
		checkPointer();
		return isConvex(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.isConvex();
	*/
	
	private static native boolean isConvex(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isConvex();
	*/
	/*[0;X;D]*/

	public boolean isNonMoving() {
		checkPointer();
		return isNonMoving(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.isNonMoving();
	*/
	
	private static native boolean isNonMoving(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isNonMoving();
	*/
	/*[0;X;D]*/

	public boolean isConcave() {
		checkPointer();
		return isConcave(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.isConcave();
	*/
	
	private static native boolean isConcave(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isConcave();
	*/
	/*[0;X;D]*/
	
	public boolean isCompound() {
		checkPointer();
		return isCompound(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.isCompound();
	*/
	
	private static native boolean isCompound(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isCompound();
	*/
	/*[0;X;D]*/

	public boolean isSoftBody() {
		checkPointer();
		return isSoftBody(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.isSoftBody();
	*/
	
	private static native boolean isSoftBody(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isSoftBody();
	*/
	/*[0;X;D]*/
	
	public boolean isInfinite() {
		checkPointer();
		return isInfinite(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.isInfinite();
	*/
	
	private static native boolean isInfinite(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->isInfinite();
	*/
	/*[0;X;D]*/
	
	
	public void setLocalScaling(Vector3 scaling) {
		checkPointer();
		setLocalScaling(cPointer, scaling.x, scaling.y, scaling.z);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		x, scaling.x #P
		y, scaling.y #P
		z, scaling.z #P
		vec,  com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_1.jsObj #P
		vec.setValue(x,y,z);
		jsObj.setLocalScaling(vec);
	*/
	
	private static native void setLocalScaling(long addr, float x, float y, float z); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		btVector3 vec(x,y,z);
		cobj->setLocalScaling(vec);
	*/
	/*[0;X;D]*/
	
	public void getLocalScaling(Vector3 out) {
		checkPointer();
		getLocalScaling(cPointer, btVector3.localArr_1);
		out.x = btVector3.localArr_1[0];
		out.y = btVector3.localArr_1[1];
		out.z = btVector3.localArr_1[2];
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		var vec = jsObj.getLocalScaling();
		out.x = vec.x(); #EVALFLOAT
		out.y = vec.y(); #EVALFLOAT
		out.z = vec.z(); #EVALFLOAT
	*/
	
	private static native void getLocalScaling(long addr, float [] value); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		const btVector3 & vec = cobj->getLocalScaling();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/
	
	public void calculateLocalInertia(float mass, Vector3 inertia) {
		checkPointer();
		calculateLocalInertia(cPointer, mass, btVector3.localArr_1);
		inertia.x = btVector3.localArr_1[0];
		inertia.y = btVector3.localArr_1[1];
		inertia.z = btVector3.localArr_1[2];
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		vec3, com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_1.jsObj #P
		jsObj.calculateLocalInertia(mass, vec3);
		inertia.x = vec3.x(); #EVALFLOAT
		inertia.y = vec3.y(); #EVALFLOAT
		inertia.z = vec3.z(); #EVALFLOAT
	*/
	
	private static native void calculateLocalInertia(long addr, float mass, float [] value); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		btVector3 vec;
		cobj->calculateLocalInertia(mass, vec);
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/
	
	public int getShapeType() {
		return getShapeType(cPointer);
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.getShapeType();
	*/
	
	private static native int getShapeType(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->getShapeType();
	*/
	/*[0;X;D]*/
	
	public void getAnisotropicRollingFrictionDirection(Vector3 out) {
		checkPointer();
		getAnisotropicRollingFrictionDirection(cPointer, btVector3.localArr_1);
		out.x = btVector3.localArr_1[0];
		out.y = btVector3.localArr_1[1];
		out.z = btVector3.localArr_1[2];
	}
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		var vec = jsObj.getAnisotropicRollingFrictionDirection();
		out.x = vec.x(); #EVALFLOAT
		out.y = vec.y(); #EVALFLOAT
		out.z = vec.z(); #EVALFLOAT
	 */

	private static native void getAnisotropicRollingFrictionDirection(long addr, float [] value); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		btVector3 vec = cobj->getAnisotropicRollingFrictionDirection();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
	/*[0;X;D]*/
	
	public void setMargin(float margin) {
		checkPointer();
		setMargin(cPointer, margin);
	}
	/*[0;X;D]
	 	checkPointer(); #J
		jsObj, this.jsObj #P
		jsObj.setMargin(margin);
	 */
	
	private static native void setMargin(long addr, float margin); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		cobj->setMargin(margin);
	*/
	/*[0;X;D]*/
	
	public float getMargin() {
		checkPointer();
		return getMargin(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.getMargin();
	*/
	
	private static native float getMargin(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->getMargin();
	*/
	/*[0;X;D]*/
	
	public int getUserIndex() {
		checkPointer();
		return getUserIndex(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.getUserIndex();
	*/

	private static native int getUserIndex(long addr); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		return cobj->getUserIndex();
	*/
	/*[0;X;D]*/
	
	public void setUserIndex(int index) {
		checkPointer();
		setUserIndex(cPointer, index);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		jsObj.setUserIndex(index);
	*/
	
	private static native void setUserIndex(long addr, int index); /*
		btCollisionShape * cobj = (btCollisionShape *)addr;
		cobj->setUserIndex(index);
	*/
	/*[0;X;D]*/
}
