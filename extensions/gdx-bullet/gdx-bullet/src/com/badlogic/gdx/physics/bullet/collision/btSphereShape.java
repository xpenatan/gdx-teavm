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
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.btSphereShape);
		}
	*/
	
	@Override
	protected void delete() {
		deletePointer(cPointer);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var vec = Bullet.wrapPointer(addr, Bullet.btSphereShape);
		Bullet.destroy(vec);
	*/
	
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
		return this.$$$jsObj.getRadius();
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
		this.$$$jsObj.setUnscaledRadius(radius);
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
		this.$$$jsObj.setMargin(margin);
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
		return this.$$$jsObj.getMargin();
	 */
	
	private static native float getMargin(long addr); /*
		btSphereShape * cobj = (btSphereShape *)addr;
		return cobj->getMargin();
	 */
	/*[0;X;D]*/
}
