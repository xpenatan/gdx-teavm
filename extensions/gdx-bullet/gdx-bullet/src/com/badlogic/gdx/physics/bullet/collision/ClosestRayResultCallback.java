package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
/*[0;X] com.dragome.commons.compiler.annotations.MethodAlias */

/** @author xpenatan */
public class ClosestRayResultCallback extends RayResultCallback {
	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionWorld.h>
		#include <src/custom/gdx/common/envHelper.h>

		static jmethodID addSingleResultID = 0;

		class MyClosestRayResultCallback : public btCollisionWorld::ClosestRayResultCallback {
			private:
				jobject obj; //weak Global reference
				bool overridden;
			public:
				MyClosestRayResultCallback(const btVector3&	rayFromWorld,const btVector3& rayToWorld, bool overridden, jobject obj) : btCollisionWorld::ClosestRayResultCallback(rayFromWorld, rayToWorld){
					this->obj = obj;
					this->overridden = overridden;
				}
				float addSingleResult(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
					if(overridden) {
						return EnvHelper::env->CallFloatMethod(obj, addSingleResultID, (jlong)&arg0, arg1);
					}
					else {
						return btCollisionWorld::ClosestRayResultCallback::addSingleResult(arg0, arg1);
					}
				}
				float addSingleResultSuper(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
					return btCollisionWorld::ClosestRayResultCallback::addSingleResult(arg0, arg1);
				}
		};
	*/

	public ClosestRayResultCallback(Vector3 rayFromWorld, Vector3 rayToWorld) {
		resetObj(createNative(rayFromWorld.x, rayFromWorld.y,rayFromWorld.z, rayToWorld.x, rayToWorld.y, rayToWorld.z, false), true);
	}

	private native long createNative(float x1, float y1, float z1, float x2, float y2, float z2, boolean overriden); /*
		btVector3 from(x1,y1,z1);
		btVector3 to(x2,y2,z2);
		
		if(!addSingleResultID) {
			jclass cls = env->GetObjectClass(object);
			addSingleResultID = env->GetMethodID(cls, "addSingleResultt", "(JZ)F");
		}
		jobject weakRef = env->NewWeakGlobalRef(object);
		return (jlong)new MyClosestRayResultCallback(from, to, overriden, weakRef);
	*/
	/*[0;X;L]
		var from = new Bullet.btVector3(x1,y1,z1);
		var to = new Bullet.btVector3(x2,y2,z2);
		var cobj = new Bullet.MyClosestRayResultCallback(from,to);
		var self = this; #B
		cobj.addSingleResult = this.addSR;
		return Bullet.getPointer(cobj);
	*/

	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback); #EVAL
		}
	*/

	/*[0;X;F;L]
		@MethodAlias(local_alias= "addSR")
		private float addSingleR(long rayResult, boolean normalInWorldSpace) {
			LocalRayResult tmpLocal = RayResultCallback.tmpLocalRes; #J
			tmpLocal.resetObj(rayResult, false); #J
			ClosestRayResultCallback callback = this.self = ClosestRayResultCallback #EVAL
			return callback.addSingleResult(tmpLocal, normalInWorldSpace); #J
		}
	*/

	@Override
	protected void delete() {
		super.delete();
		deletePointer(cPointer);
	}
	/*[0;X;D]*/

	private static native void deletePointer(long addr); /*
		MyClosestRayResultCallback * cobj = (MyClosestRayResultCallback *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/

	private float addSingleResultt(long rayResultAddr, boolean normalInWorldSpace) {
		tmpLocalRes.resetObj(rayResultAddr, false);
		return addSingleResult(tmpLocalRes, normalInWorldSpace);
	}
	/*[0;X;D]*/

	public float addSingleResult(LocalRayResult rayResult, boolean normalInWorldSpace) {
		return addSingleResult(cPointer, rayResult.cPointer, normalInWorldSpace);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		rObj, rayResult.jsObj #P
		return jsObj.addSingleResultSuper(rObj, normalInWorldSpace);
	*/

	private static native float addSingleResult(long addr, long rayResultAddr, boolean normalInWorldSpace); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		btCollisionWorld::LocalRayResult * rayResult = (btCollisionWorld::LocalRayResult *)rayResultAddr;
		return callback->addSingleResultSuper(*rayResult, normalInWorldSpace);
	*/
	/*[0;X;D]*/

	public void getRayFromWorld(Vector3 out) {
		getRayFromWorld(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		var vec = jsObj.get_m_rayFromWorld();
		out.x = vec.x(); #EVALFLOAT
		out.z = vec.y(); #EVALFLOAT
		out.y = vec.z(); #EVALFLOAT
	*/

	private static native void getRayFromWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		value[0] = callback->m_rayFromWorld.x();
		value[1] = callback->m_rayFromWorld.y();
		value[2] = callback->m_rayFromWorld.z();
	*/
	/*[0;X;D]*/

	public void setRayFromWorld(Vector3 value) {
		btVector3.localArr_1[0] = value.x;
		btVector3.localArr_1[1] = value.y;
		btVector3.localArr_1[2] = value.z;
		setRayFromWorld(cPointer, btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		x, value.x #P
		y, value.y #P
		z, value.z #P
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		jsObj.set_m_rayFromWorld(vec);
	*/

	private static native void setRayFromWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		callback->m_rayFromWorld.setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/

	public void getRayToWorld(Vector3 out) {
		getRayToWorld(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		var vec = jsObj.get_m_rayToWorld();
		out.x = vec.x(); #EVALFLOAT
		out.z = vec.y(); #EVALFLOAT
		out.y = vec.z(); #EVALFLOAT
	*/
	
	private static native void getRayToWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		value[0] = callback->m_rayToWorld.x();
		value[1] = callback->m_rayToWorld.y();
		value[2] = callback->m_rayToWorld.z();
	*/
	/*[0;X;D]*/

	public void setRayToWorld(Vector3 value) {
		btVector3.localArr_1[0] = value.x;
		btVector3.localArr_1[1] = value.y;
		btVector3.localArr_1[2] = value.z;
		setRayToWorld(cPointer, btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		x, value.x #P
		y, value.y #P
		z, value.z #P
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		jsObj.set_m_rayToWorld(vec);
	*/
	
	private static native void setRayToWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		callback->m_rayToWorld.setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/

	public void getHitNormalWorld(Vector3 out) {
		getHitNormalWorld(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}

	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		var vec = jsObj.get_m_hitNormalWorld();
		out.x = vec.x(); #EVALFLOAT
		out.z = vec.y(); #EVALFLOAT
		out.y = vec.z(); #EVALFLOAT
	*/
	
	private static native void getHitNormalWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		value[0] = callback->m_hitNormalWorld.x();
		value[1] = callback->m_hitNormalWorld.y();
		value[2] = callback->m_hitNormalWorld.z();
	*/
	/*[0;X;D]*/

	public void setHitNormalWorld(Vector3 value) {
		btVector3.localArr_1[0] = value.x;
		btVector3.localArr_1[1] = value.y;
		btVector3.localArr_1[2] = value.z;
		setHitNormalWorld(cPointer, btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		x, value.x #P
		y, value.y #P
		z, value.z #P
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		jsObj.set_m_hitNormalWorld(vec);
	*/

	private static native void setHitNormalWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		callback->m_hitNormalWorld.setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/
	
	public void getHitPointWorld(Vector3 out) {
		getHitPointWorld(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		var vec = jsObj.get_m_hitPointWorld();
		out.x = vec.x(); #EVALFLOAT
		out.z = vec.y(); #EVALFLOAT
		out.y = vec.z(); #EVALFLOAT
	*/

	private static native void getHitPointWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		value[0] = callback->m_hitPointWorld.x();
		value[1] = callback->m_hitPointWorld.y();
		value[2] = callback->m_hitPointWorld.z();
	*/
	/*[0;X;D]*/

	public void setHitPointWorld(Vector3 value) {
		btVector3.localArr_1[0] = value.x;
		btVector3.localArr_1[1] = value.y;
		btVector3.localArr_1[2] = value.z;
		setHitPointWorld(cPointer, btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		jsObj, this.jsObj #P
		x, value.x #P
		y, value.y #P
		z, value.z #P
		var vec = Bullet.MyTemp.prototype.btVec3_1(x,y,z);
		jsObj.set_m_hitPointWorld(vec);
	*/

	private static native void setHitPointWorld(long addr, float [] value); /*
		MyClosestRayResultCallback * callback = (MyClosestRayResultCallback *)addr;
		callback->m_hitPointWorld.setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/
}
