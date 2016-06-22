package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;

/** @author xpenatan */
public class btConvexHullShape extends btPolyhedralConvexAabbCachingShape{

	/*JNI
		#include <src/bullet/BulletCollision/CollisionShapes/btConvexHullShape.h>
	*/
	
	public btConvexHullShape() {
		resetObj(createNative(), true);
	}
	
	public static native long createNative(); /*
		return (jlong)new btConvexHullShape();
	*/
	/*[0;X;L]
		return Bullet.getPointer(new Bullet.btConvexHullShape());
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btConvexHullShape);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var vec = Bullet.wrapPointer(addr, Bullet.btConvexHullShape);
		Bullet.destroy(vec);
	*/
	
	private static native void deletePointer(long addr); /*
		btConvexHullShape * cobj = (btConvexHullShape *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	public void addPoint(Vector3 point) {
		checkPointer();
		addPoint(cPointer, point.x, point.y, point.z, true);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=point.x, y=point.y,z=point.z; #J
		var vec = Bullet.Temp.prototype.btVec3_1(x,y,z);
		vec.setValue(x,y,z);
		this.$$$jsObj.addPoint(vec, true);
	*/
	
	public void addPoint(Vector3 point, boolean recalculateLocalAabb) {
		checkPointer();
		addPoint(cPointer, point.x, point.y, point.z, recalculateLocalAabb);
	}
	/*[0;X;L]
		checkPointer(); #J
		float x=point.x, y=point.y,z=point.z; #J
		var vec = Bullet.Temp.prototype.btVec3_1(x,y,z);
		this.$$$jsObj.addPoint(vec, recalculateLocalAabb);
	*/
	
	private static native void addPoint(long addr, float x, float y, float z, boolean recalculateLocalAabb); /*
		btConvexHullShape * cobj = (btConvexHullShape *)addr;
		btVector3 vec(x,y,z);
		cobj->addPoint(vec, recalculateLocalAabb);
	*/
	/*[0;X;D]*/
}
