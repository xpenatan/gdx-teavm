package com.badlogic.gdx.physics.bullet.collision;

import java.util.Iterator;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.LongMap;

/** @author xpenatan */
public class btCollisionWorld extends BulletBase {

	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionWorld.h>
		#include <src/custom/gdx/common/envHelper.h>
	*/
	
	/** Use only for iterating. Don't use it for adding or removing bodies. */
	public final LongMap<btCollisionObject> bodies = new LongMap<btCollisionObject>(100);
	
	protected btBroadphaseInterface broadphasePairCache;
	protected btDispatcher dispatcher;
	private btIDebugDraw debugDrawer;
	
	private btDispatcherInfo dispInfo = new btDispatcherInfo(0, false);
	
	public btCollisionWorld() {}
	
	public btCollisionWorld(btDispatcher dispatcher, btBroadphaseInterface broadphasePairCache, btCollisionConfiguration collisionConfiguration) {
		this.broadphasePairCache = broadphasePairCache;
		this.dispatcher = dispatcher;
		resetObj(createNative(dispatcher.cPointer, broadphasePairCache.cPointer, collisionConfiguration.cPointer), true);
	}
	
	private static native long createNative(long dispatcherAddr, long broadphaseAddr, long collConfAddr); /*
		btDispatcher * dispatcher = (btDispatcher *)dispatcherAddr;
		btBroadphaseInterface * broadphase = (btBroadphaseInterface *)broadphaseAddr;
		btCollisionConfiguration * config = (btCollisionConfiguration *)collConfAddr;
		return (jlong)new btCollisionWorld(dispatcher, broadphase, config);
	*/
	/*[0;X;L]
		var dispatch = Bullet.wrapPointer(dispatcherAddr, Bullet.btDispatcher);
		var cache = Bullet.wrapPointer(broadphaseAddr, Bullet.broadphasePairCache);
		var colConfig = Bullet.wrapPointer(collConfAddr, Bullet.btCollisionConfiguration);
		var cobj = new Bullet.btCollisionWorld(dispatch,cache,colConfig);
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionWorld); #EVAL
		}
	*/
	
	@Override
	protected void delete() {
		bodies.clear();
		deletePointer(cPointer);
	}
	/*[0;X;L]
		bodies.clear();  #J
		super.delete(); #J
	*/
	
	private static native void deletePointer(long addr); /*
		btCollisionWorld * cobj = (btCollisionWorld *)addr;
		delete cobj;
	*/
	/*[0;X;D]*/
	
	final protected void addBody(btCollisionObject body) {
		bodies.put(body.cPointer, body);
		body.world = this;
	}
	
	final protected void removeBody(btCollisionObject body) {
		body.world = null;
		bodies.remove(body.cPointer);
	}
	
	/** Copy collision objects to array*/
	public void getBodies (Array<btCollisionObject> bodies) {
		checkPointer();
		bodies.clear();
		bodies.ensureCapacity(this.bodies.size);
		for (Iterator<btCollisionObject> iter = this.bodies.values(); iter.hasNext();) {
			bodies.add(iter.next());
		}		
	}
	
	public void setBroadphase(btBroadphaseInterface pairCache) {
		checkPointer();
		broadphasePairCache = pairCache;
		setBroadphase(cPointer, pairCache.cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
	 	pObj, broadphasePairCache.jsObj #P
	 	broadphasePairCache = pairCache; #J
		jsObj.setBroadphase(pObj);
	*/
	
	private static native void setBroadphase(long addr, long pairCacheAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btBroadphaseInterface * pairCache = (btBroadphaseInterface *)pairCacheAddr;
		world->setBroadphase(pairCache);
	*/
	/*[0;X;D]*/
	
	public btBroadphaseInterface getBroadphase() {
		checkPointer();
		return broadphasePairCache;
	}
	
	public btOverlappingPairCache getPairCache() {
		checkPointer();
		return broadphasePairCache.getOverlappingPairCache();
	}

	public btDispatcher getDispatcher() {
		checkPointer();
		return dispatcher;
	}
	
	public void updateSingleAabb(btCollisionObject obj) {
		checkPointer();
		updateSingleAabb(cPointer, obj.cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
	 	oObj, obj.jsObj #P
		jsObj.updateSingleAabb(oObj);
	*/
	
	private static native void updateSingleAabb(long addr, long collObjAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		world->updateSingleAabb(collObj);
	*/
	/*[0;X;D]*/
	
	public void updateAabbs() {
		checkPointer();
		updateAabbs(cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.updateAabbs();
	*/
	
	private static native void updateAabbs(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		world->updateAabbs();
	*/
	/*[0;X;D]*/
	
	public void computeOverlappingPairs() {
		checkPointer();
		computeOverlappingPairs(cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.computeOverlappingPairs();
	*/
	
	private static native void computeOverlappingPairs(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		world->computeOverlappingPairs();
	*/
	/*[0;X;D]*/
	
	public void setDebugDrawer(btIDebugDraw debugDrawer) {
		checkPointer();
		this.debugDrawer = debugDrawer;
		setDebugDrawer(cPointer, debugDrawer.cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
	 	dObj, debugDrawer.jsObj #P
	 	this.debugDrawer = debugDrawer; #J
		jsObj.setDebugDrawer(dObj);
	*/

	private static native void setDebugDrawer(long addr, long debugDrawerAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btIDebugDraw * debugDrawer = (btIDebugDraw *)debugDrawerAddr;
		world->setDebugDrawer(debugDrawer);
	*/
	/*[0;X;D]*/
	
	public btIDebugDraw getDebugDrawer() {
		checkPointer();
		return this.debugDrawer;
	}
	
	public void debugDrawWorld() {
		checkPointer();
		debugDrawWorld(cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.debugDrawWorld();
	*/

	private static native void debugDrawWorld(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		EnvHelper::env = env;
		world->debugDrawWorld();
		EnvHelper::env = 0;
	*/
	/*[0;X;D]*/
	
	public void debugDrawObject(Matrix4 worldTransform, btCollisionShape shape, Vector3 color) {
		checkPointer();
		//TODO need impl
	}
	
	public int getNumCollisionObjects() {
		checkPointer();
		return getNumCollisionObjects(cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getNumCollisionObjects();
	*/

	private static native int getNumCollisionObjects(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		return (jint)world->getNumCollisionObjects();
	*/
	/*[0;X;D]*/
	
	public void rayTest(Vector3 rayFromWorld, Vector3 rayToWorld, RayResultCallback resultCallback) {
		checkPointer();
		btVector3.localArr_1[0] = rayFromWorld.x;
		btVector3.localArr_1[1] = rayFromWorld.y;
		btVector3.localArr_1[2] = rayFromWorld.z;
		btVector3.localArr_2[0] = rayToWorld.x;
		btVector3.localArr_2[1] = rayToWorld.y;
		btVector3.localArr_2[2] = rayToWorld.z;
		rayTest(cPointer, btVector3.localArr_1, btVector3.localArr_2, resultCallback.cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		vec1, com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_1.jsObj #P
		vec2, com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_2.jsObj #P
		x1, rayFromWorld.x #P
		y1, rayFromWorld.y #P
		z1, rayFromWorld.z #P
		x2, rayToWorld.x #P
		y2, rayToWorld.y #P
		z2, rayToWorld.z #P
		vec1.setValue(x1,y1,z1);
		vec2.setValue(x2,y2,z2);
		rObj, resultCallback.jsObj #P
		jsObj.rayTest(vec1, vec2, rObj);
	*/

	private static native void rayTest(long addr, float [] rayFromWorld, float [] rayToWorld, long resultCallbackAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionWorld::RayResultCallback * rayCallback = (btCollisionWorld::RayResultCallback *)resultCallbackAddr;
		btVector3 tmp1;
		btVector3 tmp2;
		tmp1.setValue(rayFromWorld[0], rayFromWorld[1], rayFromWorld[2]);
		tmp2.setValue(rayToWorld[0], rayToWorld[1], rayToWorld[2]);
		EnvHelper::env = env;
		world->rayTest(tmp1, tmp2, *rayCallback);
		EnvHelper::env = 0;
	*/
	/*[0;X;D]*/
	
	public void convexSweepTest(btConvexShape castShape, Matrix4 from, Matrix4 to, ConvexResultCallback resultCallback, float allowedCcdPenetration) {
		checkPointer();
		convexSweepTest(cPointer, castShape.cPointer, from.val, to.val, resultCallback.cPointer, allowedCcdPenetration);
	}
	/*[0;X;D]*/ //TODO need to impl
	
	private static native void convexSweepTest(long addr, long castShapeAddr, float [] fromArr, float [] toArr, long resultCallbackAddr, float allowedCcdPenetration); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btConvexShape * convShape = (btConvexShape *)castShapeAddr;
		btCollisionWorld::ConvexResultCallback * convCallback = (btCollisionWorld::ConvexResultCallback *)resultCallbackAddr;
		btTransform from;
		btTransform to;
		from.setFromOpenGLMatrix(fromArr);
		to.setFromOpenGLMatrix(toArr);
		world->convexSweepTest(convShape, from, to, *convCallback, allowedCcdPenetration);
	*/
	/*[0;X;D]*/
	
	public void contactTest(btCollisionObject collObj, ContactResultCallback resultCallback) {
		checkPointer();
		contactTest(cPointer, collObj.cPointer, resultCallback.cPointer);
	}
	/*[0;X;D]*/ //TODO need to impl
	
	private static native void contactTest(long addr, long collObjAddr, long resultCallbackAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		btCollisionWorld::ContactResultCallback * callback = (btCollisionWorld::ContactResultCallback *)resultCallbackAddr;
		world->contactTest(collObj, *callback);
	*/
	/*[0;X;D]*/
	
	public void contactPairTest(btCollisionObject colObjA, btCollisionObject colObjB, ContactResultCallback resultCallback) {
		checkPointer();
		contactPairTest(cPointer, colObjA.cPointer, colObjB.cPointer, resultCallback.cPointer);
	}
	/*[0;X;D]*/ //TODO need to impl
	
	private static native void contactPairTest(long addr, long collObjAAddr, long collObjBAddr, long resultCallbackAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObjA = (btCollisionObject *)collObjAAddr;
		btCollisionObject * collObjB = (btCollisionObject *)collObjBAddr;
		btCollisionWorld::ContactResultCallback * callback = (btCollisionWorld::ContactResultCallback *)resultCallbackAddr;
		world->contactPairTest(collObjA, collObjB, *callback);
	*/
	/*[0;X;D]*/
	
	public void addCollisionObject(btCollisionObject collisionObject) {
		checkPointer();
		addBody(collisionObject);
		addCollisionObject(cPointer, collisionObject.cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	addBody(collisionObject); #J
	 	jsObj, this.jsObj #P
	 	cObj, collisionObject.jsObj #P
		jsObj.addCollisionObject(cObj);
	*/
	
	private static native void addCollisionObject(long addr, long collObjAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		world->addCollisionObject(collObj);
	*/
	/*[0;X;D]*/
	
	public void addCollisionObject(btCollisionObject collisionObject, short collisionFilterGroup) {
		checkPointer();
		addBody(collisionObject);
		addCollisionObject(cPointer, collisionObject.cPointer, collisionFilterGroup);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	addBody(collisionObject); #J
	 	jsObj, this.jsObj #P
	 	cObj, collisionObject.jsObj #P
		jsObj.addCollisionObject(cObj, collisionFilterGroup);
	*/
	
	private static native void addCollisionObject(long addr, long collObjAddr, short collisionFilterGroup); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		world->addCollisionObject(collObj, collisionFilterGroup);
	*/
	/*[0;X;D]*/
	
	public void addCollisionObject(btCollisionObject collisionObject, short collisionFilterGroup, short collisionFilterMask) {
		checkPointer();
		addBody(collisionObject);
		addCollisionObject(cPointer, collisionObject.cPointer, collisionFilterGroup, collisionFilterMask);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	addBody(collisionObject); #J
	 	jsObj, this.jsObj #P
	 	cObj, collisionObject.jsObj #P
		jsObj.addCollisionObject(cObj, collisionFilterGroup, collisionFilterMask);
	*/
	
	private static native void addCollisionObject(long addr, long collObjAddr, short collisionFilterGroup, short collisionFilterMask); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		world->addCollisionObject(collObj, collisionFilterGroup, collisionFilterMask);
	*/
	/*[0;X;D]*/
	
	public void removeCollisionObject(btCollisionObject collisionObject) {
		checkPointer();
		removeBody(collisionObject);
		removeCollisionObject(cPointer, collisionObject.cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	removeBody(collisionObject); #J
	 	jsObj, this.jsObj #P
	 	cObj, collisionObject.jsObj #P
		jsObj.removeCollisionObject(cObj);
	*/
	
	private static native void removeCollisionObject(long addr, long collObjAddr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		world->removeCollisionObject(collObj);
	*/
	/*[0;X;D]*/
	
	public void performDiscreteCollisionDetection() {
		checkPointer();
		performDiscreteCollisionDetection(cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.performDiscreteCollisionDetection();
	*/
	
	private static native void performDiscreteCollisionDetection(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		EnvHelper::env = env;
		world->performDiscreteCollisionDetection();
		EnvHelper::env = 0;
	*/
	/*[0;X;D]*/
	
	public btDispatcherInfo getDispatchInfo() {
		checkPointer();
		if(dispInfo.cPointer == 0) {
			dispInfo.resetObj(getDispatchInfo(cPointer), false);
		}
		return dispInfo;
	}

	private native long getDispatchInfo(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		return (jlong)&world->getDispatchInfo();
	*/
	/*[0;X;L]
		jsObj, this.jsObj #P
		return Bullet.getPointer(jsObj.getDispatchInfo());
	*/
	
	public boolean getForceUpdateAllAabbs() {
		checkPointer();
		return getForceUpdateAllAabbs(cPointer);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getForceUpdateAllAabbs();
	*/
	
	private static native boolean getForceUpdateAllAabbs(long addr); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		return world->getForceUpdateAllAabbs();
	*/
	/*[0;X;D]*/
	
	public void setForceUpdateAllAabbs(boolean value) {
		checkPointer();
		setForceUpdateAllAabbs(cPointer, value);
	}
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setForceUpdateAllAabbs(value);
	*/
	
	private static native void setForceUpdateAllAabbs(long addr, boolean value); /*
		btCollisionWorld * world = (btCollisionWorld *)addr;
		world->setForceUpdateAllAabbs(value);
	*/
	/*[0;X;D]*/
}
