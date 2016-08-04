package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/** @author xpenatan */
public class btShapeHull extends BulletBase {
	/*JNI 
		#include <src/bullet/BulletCollision/CollisionShapes/btShapeHull.h>
	*/
	
	public btShapeHull(btConvexShape shape) {
		
	}
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btShapeHull); #EVAL
		}
	*/

	public boolean buildHull(float margin) {
		checkPointer();
		return buildHull(cPointer, margin);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.buildHull(margin);
	*/
	
	private static native boolean buildHull(long addr, float margin); /*
		btShapeHull * cobj = (btShapeHull *)addr;
		return cobj->buildHull(margin);
	 */
	/*[0;X;D]*/
	
	public int numTriangles() {
		checkPointer();
		return numTriangles(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.numTriangles();
	*/
	
	private static native int numTriangles(long addr); /*
		btShapeHull * cobj = (btShapeHull *)addr;
		return cobj->numTriangles();
	 */
	/*[0;X;D]*/

	public int numVertices() {
		checkPointer();
		return numVertices(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.numVertices();
	*/
	
	private static native int numVertices(long addr); /*
		btShapeHull * cobj = (btShapeHull *)addr;
		return cobj->numVertices();
	 */
	/*[0;X;D]*/

	public int numIndices() {
		checkPointer();
		return numIndices(cPointer);
	}
	/*[0;X;L]
	 	checkPointer(); #J
	 	jsObj, this.jsObj #P
		return jsObj.numIndices();
	*/
	
	private static native int numIndices(long addr); /*
		btShapeHull * cobj = (btShapeHull *)addr;
		return cobj->numIndices();
	 */
	/*[0;X;D]*/

	public btVector3[] getVertexPointer() {
		throw new RuntimeException("getVertexPointer not implemented");
	}

	public java.nio.LongBuffer getIndexPointer() {
		throw new RuntimeException("getIndexPointer not implemented");
	}
}
