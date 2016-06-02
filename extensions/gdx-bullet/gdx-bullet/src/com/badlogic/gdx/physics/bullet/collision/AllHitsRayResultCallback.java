package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btScalarArray;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3Array;

/** @author xpenatan */
public class AllHitsRayResultCallback extends RayResultCallback {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionWorld.h>
		#include <src/custom/gdx/common/envHelper.h>
//		#include <iostream>
//		using namespace std;
		
		static jmethodID addSingleResultID = 0;
		
		class MyAllHitsRayResultCallback : public btCollisionWorld::AllHitsRayResultCallback {
			private:
				jobject obj; //weak Global reference
				bool overridden;
			public:
				MyAllHitsRayResultCallback(const btVector3&	rayFromWorld,const btVector3& rayToWorld, bool overridden, jobject obj) : btCollisionWorld::AllHitsRayResultCallback(rayFromWorld, rayToWorld){
					this->obj = obj;
					this->overridden = overridden;
				}
		
				float addSingleResult(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
					if(overridden) {
//						cout << "CALLED 1: " << EnvHelper::env << endl;
//						fflush(stdout);
						return EnvHelper::env->CallFloatMethod(obj, addSingleResultID, (jlong)&arg0, arg1);
					}
					else {
						return btCollisionWorld::AllHitsRayResultCallback::addSingleResult(arg0, arg1);
					}
				}
								
				float addSingleResultSuper(btCollisionWorld::LocalRayResult & arg0, bool arg1) {
					return btCollisionWorld::AllHitsRayResultCallback::addSingleResult(arg0, arg1);
				}
		};
	*/
	
	btCollisionObjectArray objArray = new btCollisionObjectArray(0, false);
	btVector3Array hitnormalArray = new btVector3Array(0, false);
	btVector3Array hitpointArray = new btVector3Array(0, false);
	btScalarArray hitfractionArray = new btScalarArray(0, false);
	
	public AllHitsRayResultCallback(Vector3 rayFromWorld, Vector3 rayToWorld) {
		resetObj(createNative(rayFromWorld.x, rayFromWorld.y,rayFromWorld.z, rayToWorld.x, rayToWorld.y, rayToWorld.z, false), true);
	}
	
	public AllHitsRayResultCallback(Vector3 rayFromWorld, Vector3 rayToWorld, boolean toOverride) {
		resetObj(createNative(rayFromWorld.x, rayFromWorld.y,rayFromWorld.z, rayToWorld.x, rayToWorld.y, rayToWorld.z, toOverride), true);
	}
	
	private native long createNative(float x1, float y1, float z1, float x2, float y2, float z2, boolean overriden); /*
		btVector3 from(x1,y1,z1);
		btVector3 to(x2,y2,z2);
		
		if(!addSingleResultID) {
			jclass cls = env->GetObjectClass(object);
			addSingleResultID = env->GetMethodID(cls, "addSingleResultt", "(JZ)F");
		}
		jobject weakRef = env->NewWeakGlobalRef(object);
		return (jlong)new MyAllHitsRayResultCallback(from,to, overriden, weakRef);
	*/
	/*[0;X;L]
		LocalRayResult tmpLocal = RayResultCallback.tmpLocalRes; #J
		var from = new Bullet.btVector3(x1,y1,z1);
		var to = new Bullet.btVector3(x2,y2,z2);
		var cobj = new Bullet.MyAllHitsRayResultCallback(from,to);
		var self = this; #B
		cobj.addSingleResult=function(rayResult,normalInWorldSpace) {
			tmpLocal.$resetObj___long__boolean$void(rayResult, false);
			return self.$addSingleResult___com_badlogic_gdx_physics_bullet_collision_LocalRayResult__boolean$float(tmpLocal,normalInWorldSpace);  #I
		}; #E
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			this.$$$jsObj = Bullet.wrapPointer(this.$$$cPointer, Bullet.MyAllHitsRayResultCallback);
		}
	*/
	
	@Override
	protected void delete() {
		super.delete();
		deletePointer(cPointer);
		objArray.resetObj(0, false);
		hitnormalArray.resetObj(0, false);
		hitpointArray.resetObj(0, false);
		hitfractionArray.resetObj(0, false);
	}
	/*[0;X;L]
		long addr = cPointer;  #J
		var cobj = Bullet.wrapPointer(addr, Bullet.MyAllHitsRayResultCallback);
		Bullet.destroy(cobj);
	*/
	
	private static native void deletePointer(long addr); /*
		MyAllHitsRayResultCallback * cobj = (MyAllHitsRayResultCallback *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/

	public btCollisionObjectArray getCollisionObjects() {
		checkPointer();
		objArray.resetObj(getCollisionObjects(cPointer), false);
		return objArray;
	}
	/*[0;X;L]
		checkPointer();  #J
		long ptr = 0; #J
		ptr = Bullet.getPointer(this.$$$jsObj.get_m_collisionObjects());
		objArray.resetObj(ptr, false); #J
		return objArray; #J
	*/
	
	private static native long getCollisionObjects(long addr); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		return (jlong)&callback->m_collisionObjects;
	*/
	/*[0;X;D]*/
	
	public void getRayFromWorld(Vector3 out) {
		getRayFromWorld(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.get_m_rayFromWorld();
		x=vec.x(); z=vec.y(); y=vec.z();
		out.set(x,y,z); #J
	*/
	
	private static native void getRayFromWorld(long addr, float [] value); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
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
		float x=value.x,y=value.y,z=value.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3();
		vec.setValue(x,y,z);
		this.$$$jsObj.set_m_rayFromWorld(vec);
	*/
	
	private static native void setRayFromWorld(long addr, float [] value); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		callback->m_rayFromWorld.setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/
	
	public void getRayToWorld(Vector3 out) {
		getRayToWorld(cPointer, btVector3.localArr_1);
		out.set(btVector3.localArr_1);
	}
	/*[0;X;L]
		checkPointer();  #J
		float x=0,y=0,z=0; #J
		var vec = this.$$$jsObj.get_m_rayToWorld();
		x=vec.x(); z=vec.y(); y=vec.z();
		out.set(x,y,z); #J
	*/
	
	private static native void getRayToWorld(long addr, float [] value); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
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
		float x=value.x,y=value.y,z=value.z; #J
		var vec = Bullet.MyTemp.prototype.btVec3();
		vec.setValue(x,y,z);
		this.$$$jsObj.set_m_rayToWorld(vec);
	*/
	
	private static native void setRayToWorld(long addr, float [] value); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		callback->m_rayToWorld.setValue(value[0], value[1], value[2]);
	*/
	/*[0;X;D]*/
	
	public btVector3Array getHitNormalWorld() {
		checkPointer();
		hitnormalArray.resetObj(getHitNormalWorld(cPointer), false);
		return hitnormalArray;
	}
	/*[0;X;L]
		checkPointer();  #J
		long ptr = 0; #J
		ptr = Bullet.getPointer(this.$$$jsObj.get_m_hitNormalWorld());
		hitnormalArray.resetObj(ptr, false); #J
		return hitnormalArray; #J
	*/

	private static native long getHitNormalWorld(long addr); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		return (jlong)&callback->m_hitNormalWorld;
	*/
	/*[0;X;D]*/
	
	public btVector3Array getHitPointWorld() {
		checkPointer();
		hitpointArray.resetObj(getHitPointWorld(cPointer), false);
		return hitpointArray;
	}
	/*[0;X;L]
		checkPointer();  #J
		long ptr = 0; #J
		ptr = Bullet.getPointer(this.$$$jsObj.get_m_hitPointWorld());
		hitpointArray.resetObj(ptr, false); #J
		return hitpointArray; #J
	*/
	
	private static native long getHitPointWorld(long addr); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		return (jlong)&callback->m_hitPointWorld;
	*/
	/*[0;X;D]*/

	public btScalarArray getHitFractions() {
		checkPointer();
		hitfractionArray.resetObj(getHitFractions(cPointer), false);
		return hitfractionArray;
	}
	/*[0;X;L]
		checkPointer();  #J
		long ptr = 0; #J
		ptr = Bullet.getPointer(this.$$$jsObj.get_m_hitFractions());
		hitfractionArray.resetObj(ptr, false); #J
		return hitfractionArray; #J
	*/

	private static native long getHitFractions(long addr); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		return (jlong)&callback->m_hitFractions;
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
		Object locRay = rayResult.jsObj;  #J
		return this.$$$jsObj.addSingleResultSuper(locRay, normalInWorldSpace);
	*/
	
	private static native float addSingleResult(long addr, long rayResultAddr, boolean normalInWorldSpace); /*
		MyAllHitsRayResultCallback * callback = (MyAllHitsRayResultCallback *)addr;
		btCollisionWorld::LocalRayResult * rayResult = (btCollisionWorld::LocalRayResult *)rayResultAddr;
		return callback->addSingleResultSuper(*rayResult, normalInWorldSpace);
	*/
	/*[0;X;D]*/

	@Override
	public void clear() {
		super.clear();
		getHitNormalWorld().resize(0);
		getHitPointWorld().resize(0);
		getHitFractions().resize(0);
		getCollisionObjects().resize(0);
	}

}
