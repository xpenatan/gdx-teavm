package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;

/** @author xpenatan */
public class LocalShapeInfo extends BulletBase {
	/*JNI 
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionWorld.h>
	*/
	
	public LocalShapeInfo(long cPtr, boolean cMemoryOwn) {
		resetObj(cPtr, cMemoryOwn);
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.LocalShapeInfo);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var vec = Bullet.wrapPointer(addr, Bullet.LocalShapeInfo);
		Bullet.destroy(vec);
	*/
	
	private static native void deletePointer(long addr); /*
		btCollisionWorld::LocalShapeInfo * cobj = (btCollisionWorld::LocalShapeInfo *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/

	public void setShapePart(int value) {
		checkPointer();
		setShapePart(cPointer, value);
	}
	/*[0;X;L]
	 	checkPointer(); #J
		this.$$$jsObj.set_m_shapePart(value);
	*/

	private static native void setShapePart(long addr, int value); /*
		btCollisionWorld::LocalShapeInfo * cobj = (btCollisionWorld::LocalShapeInfo *)addr;
		cobj->m_shapePart = value;
	 */
	/*[0;X;D]*/
	
	public int getShapePart() {
		checkPointer();
		return getShapePart(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
		return this.$$$jsObj.get_m_shapePart();
	*/
	
	private static native int getShapePart(long addr); /*
		btCollisionWorld::LocalShapeInfo * cobj = (btCollisionWorld::LocalShapeInfo *)addr;
		return cobj->m_shapePart;
	 */
	/*[0;X;D]*/

	public void setTriangleIndex(int value) {
		checkPointer();
		setTriangleIndex(cPointer, value);
	}
	/*[0;X;L]
	 	checkPointer(); #J
		this.$$$jsObj.set_m_triangleIndex(value);
	*/
	
	private static native void setTriangleIndex(long addr, int value); /*
		btCollisionWorld::LocalShapeInfo * cobj = (btCollisionWorld::LocalShapeInfo *)addr;
		cobj->m_triangleIndex = value;
	 */
	/*[0;X;D]*/

	public int getTriangleIndex() {
		checkPointer();
		return getTriangleIndex(cPointer);
	}
	
	/*[0;X;L]
	 	checkPointer(); #J
		return this.$$$jsObj.get_m_triangleIndex();
	*/
	
	private static native int getTriangleIndex(long addr); /*
		btCollisionWorld::LocalShapeInfo * cobj = (btCollisionWorld::LocalShapeInfo *)addr;
		return cobj->m_triangleIndex;
	 */
	/*[0;X;D]*/
}
