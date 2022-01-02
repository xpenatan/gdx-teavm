package com.badlogic.gdx.physics.bullet.collision;

/** @author xpenatan */
public class btSphereShape extends btConvexInternalShape {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionShapes/btSphereShape.h>
	*/
	
	public btSphereShape(float radius) {
		resetObj(createNative(radius), true);
	}
	
	public static native long createNative(float radius); /*
		return (jlong)new btSphereShape(radius);
	*/
	/*[0;X;L]
		return Bullet.getPointer(new Bullet.btSphereShape(radius));
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btSphereShape); #EVAL
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;D]*/
	
	private static native void deletePointer(long addr); /*
		btSphereShape * cobj = (btSphereShape *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	public float getRadius() {
		return getRadius(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.getRadius();
	*/
	
	private static native float getRadius(long addr); /*
		btSphereShape * cobj = (btSphereShape *)addr;
		return cobj->getRadius();
	*/
	/*[0;X;D]*/
	
	public void setUnscaledRadius(float radius) {
		setUnscaledRadius(cPointer, radius);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.setUnscaledRadius(radius);
	 */
	
	private static native void setUnscaledRadius(long addr, float radius); /*
		btSphereShape * cobj = (btSphereShape *)addr;
		cobj->setUnscaledRadius(radius);
	 */
	/*[0;X;D]*/
	
	public void setMargin(float margin) {
		setMargin(cPointer, margin);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		jsObj.setMargin(margin);
	 */
	
	private static native void setMargin(long addr, float margin); /*
		btSphereShape * cobj = (btSphereShape *)addr;
		cobj->setMargin(margin);
	 */
	/*[0;X;D]*/
	
	public float getMargin() {
		return getMargin(cPointer);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		return jsObj.getMargin();
	 */
	
	private static native float getMargin(long addr); /*
		btSphereShape * cobj = (btSphereShape *)addr;
		return cobj->getMargin();
	 */
	/*[0;X;D]*/
}
