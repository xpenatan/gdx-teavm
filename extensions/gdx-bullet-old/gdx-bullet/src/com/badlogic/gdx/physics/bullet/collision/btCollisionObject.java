package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btCollisionObject extends BulletBase {
	/*JNI
		#include <src/bullet/BulletCollision/CollisionDispatch/btCollisionObject.h>
	*/

    btCollisionWorld world;

    int userValue;

    public Object userData;

    protected btCollisionShape shape;

    private btBroadphaseProxy broadphaseProxy;

    public btCollisionObject() {
        create();
    }

    protected void create() {
        resetObj(createNative(), true);
    }

    public btCollisionObject(long cPtr, boolean cMemoryOwn) {
        resetObj(cPtr, cMemoryOwn);
    }

    @Override
    protected void delete() {
        deletePointer(cPointer);
        userData = null;
        if(shape != null)
            shape.dispose();
        shape = null;
    }
	/*[0;X;L]
		super.delete(); #J
		userData = null; #J
		if(shape != null) #J
			shape.dispose(); #J
		shape = null; #J
	*/

    private static native void deletePointer(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		delete cobj;
	*/
    /*[0;X;D]*/

    private static native long createNative(); /*
		return (jlong)new btCollisionObject();
	*/
	/*[0;X;L]
		var cobj = new Bullet.btCollisionObject();
		return Bullet.getPointer(cobj);
	*/
	
	/*[0;X;F;L]
		protected void cacheObj() {
			addr, this.cPointer #P
			this.jsObj = Bullet.wrapPointer(addr, Bullet.btCollisionObject); #EVAL
		}
	*/

    public btCollisionWorld getWorld() {
        checkPointer();
        return world;
    }

    public boolean mergesSimulationIslands() {
        checkPointer();
        return mergesSimulationIslands(cPointer);
    }
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		return jsObj.mergesSimulationIslands();
	*/

    private static native boolean mergesSimulationIslands(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->mergesSimulationIslands();
	*/
    /*[0;X;D]*/

    public void getAnisotropicFriction(Vector3 out) {
        checkPointer();
        getAnisotropicFriction(cPointer, btVector3.localArr_1);
        out.set(btVector3.localArr_1);
    }
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		var vec = jsObj.getAnisotropicFriction();
		out.x = vec.x(); #EVALFLOAT
		out.z = vec.y(); #EVALFLOAT
		out.y = vec.z(); #EVALFLOAT
	*/

    private static native void getAnisotropicFriction(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		const btVector3 & vec = cobj->getAnisotropicFriction();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
    /*[0;X;D]*/

    public void setAnisotropicFriction(Vector3 anisotropicFriction, int frictionMode) {
        checkPointer();
        btVector3.localArr_1[0] = anisotropicFriction.x;
        btVector3.localArr_1[1] = anisotropicFriction.y;
        btVector3.localArr_1[2] = anisotropicFriction.z;
        setAnisotropicFriction(cPointer, btVector3.localArr_1, frictionMode);
    }
	/*[0;X;L]
		checkPointer(); #J
		jsObj, this.jsObj #P
		x, anisotropicFriction.x #P
		y, anisotropicFriction.y #P
		z, anisotropicFriction.z #P
		vec, btVector3.btVector3_1.jsObj #P
		vec.setValue(x,y,z);
		jsObj.setAnisotropicFriction(vec, frictionMode);
	*/

    private static native void setAnisotropicFriction(long addr, float[] value, int frictionMode); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btVector3 vec(value[0], value[1], value[2]);
		cobj->setAnisotropicFriction(vec, frictionMode);
	*/
    /*[0;X;D]*/

    public void setAnisotropicFriction(Vector3 anisotropicFriction) {
        checkPointer();
        btVector3.localArr_1[0] = anisotropicFriction.x;
        btVector3.localArr_1[1] = anisotropicFriction.y;
        btVector3.localArr_1[2] = anisotropicFriction.z;
        setAnisotropicFriction(cPointer, btVector3.localArr_1);
    }
	/*[0;X;L]
	 	checkPointer(); #J
	 	setAnisotropicFriction(anisotropicFriction, AnisotropicFrictionFlags.CF_ANISOTROPIC_FRICTION); #J
	*/

    private static native void setAnisotropicFriction(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btVector3 vec(value[0], value[1], value[2]);
		cobj->setAnisotropicFriction(vec);
	*/
    /*[0;X;D]*/

    public boolean hasAnisotropicFriction(int frictionMode) {
        checkPointer();
        return hasAnisotropicFriction(cPointer, frictionMode);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.hasAnisotropicFriction(frictionMode);
	*/

    private static native boolean hasAnisotropicFriction(long addr, int frictionMode); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->hasAnisotropicFriction(frictionMode);
	*/
    /*[0;X;D]*/

    public boolean hasAnisotropicFriction() {
        checkPointer();
        return hasAnisotropicFriction(cPointer);
    }
	/*[0;X;L]
		checkPointer(); #J
		return hasAnisotropicFriction(AnisotropicFrictionFlags.CF_ANISOTROPIC_FRICTION); #J
	*/

    private static native boolean hasAnisotropicFriction(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->hasAnisotropicFriction();
	*/
    /*[0;X;D]*/

    public void setContactProcessingThreshold(float contactProcessingThreshold) {
        checkPointer();
        setContactProcessingThreshold(cPointer, contactProcessingThreshold);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setContactProcessingThreshold(contactProcessingThreshold);
	*/

    private static native void setContactProcessingThreshold(long addr, float contactProcessingThreshold); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setContactProcessingThreshold(contactProcessingThreshold);
	*/
    /*[0;X;D]*/

    public float getContactProcessingThreshold() {
        checkPointer();
        return getContactProcessingThreshold(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getContactProcessingThreshold();
	*/

    private static native float getContactProcessingThreshold(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getContactProcessingThreshold();
	*/
    /*[0;X;D]*/

    public boolean isStaticObject() {
        checkPointer();
        return isStaticObject(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.isStaticObject();
	*/

    private static native boolean isStaticObject(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->isStaticObject();
	*/
    /*[0;X;D]*/

    public boolean isKinematicObject() {
        checkPointer();
        return isKinematicObject(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.isKinematicObject();
	*/

    private static native boolean isKinematicObject(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->isKinematicObject();
	*/
    /*[0;X;D]*/

    public boolean isStaticOrKinematicObject() {
        checkPointer();
        return isStaticOrKinematicObject(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.isStaticOrKinematicObject();
	*/

    private static native boolean isStaticOrKinematicObject(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->isStaticOrKinematicObject();
	*/
    /*[0;X;D]*/

    public boolean hasContactResponse() {
        checkPointer();
        return hasContactResponse(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.hasContactResponse();
	*/

    private static native boolean hasContactResponse(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->hasContactResponse();
	*/
    /*[0;X;D]*/

    public void setCollisionShape(btCollisionShape shape) {
        checkPointer();
        this.shape = shape;
        setCollisionShape(cPointer, shape != null ? shape.cPointer : 0);
    }
	/*[0;X;L]
	 	checkPointer();  #J
		this.shape = shape; #J
		jsShape, shape.jsObj #P
		jsObj, this.jsObj #P
		jsObj.setCollisionShape(jsShape);
	*/

    private static native void setCollisionShape(long addr, long shapeAddr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btCollisionShape * cshape = (btCollisionShape *)shapeAddr;
		cobj->setCollisionShape(cshape);
	*/
    /*[0;X;D]*/

    public btCollisionShape getCollisionShape() {
        checkPointer();
        return shape;
    }

    public void setIgnoreCollisionCheck(btCollisionObject co, boolean ignoreCollisionCheck) {
        checkPointer();
        setIgnoreCollisionCheck(cPointer, co.cPointer, ignoreCollisionCheck);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
	 	coObj, co.jsObj #P
		jsObj.setIgnoreCollisionCheck(coObj);
	*/

    private static native void setIgnoreCollisionCheck(long addr, long collAddr, boolean ignoreCollisionCheck); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collAddr;
		cobj->setIgnoreCollisionCheck(collObj, ignoreCollisionCheck);
	*/
    /*[0;X;D]*/

    public boolean checkCollideWithOverride(btCollisionObject co) {
        checkPointer();
        return checkCollideWithOverride(cPointer, co.cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
	 	coObj, co.jsObj #P
		return jsObj.checkCollideWithOverride(coObj);
	*/

    private static native boolean checkCollideWithOverride(long addr, long collAddr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collAddr;
		return cobj->checkCollideWithOverride(collObj);
	*/
    /*[0;X;D]*/

    public int getActivationState() {
        checkPointer();
        return getActivationState(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getActivationState();
	*/

    private static native int getActivationState(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getActivationState();
	*/
    /*[0;X;D]*/

    public void setActivationState(int newState) {
        checkPointer();
        setActivationState(cPointer, newState);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setActivationState(newState);
	*/

    private static native void setActivationState(long addr, int state); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setActivationState(state);
	*/
    /*[0;X;D]*/

    public void setDeactivationTime(float time) {
        checkPointer();
        setDeactivationTime(cPointer, time);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setDeactivationTime(time);
	*/

    private static native void setDeactivationTime(long addr, float time); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setDeactivationTime(time);
	*/
    /*[0;X;D]*/

    public float getDeactivationTime() {
        checkPointer();
        return getDeactivationTime(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getDeactivationTime();
	*/

    private static native float getDeactivationTime(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getDeactivationTime();
	*/
    /*[0;X;D]*/

    public void forceActivationState(int newState) {
        checkPointer();
        forceActivationState(cPointer, newState);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.forceActivationState(newState);
	*/

    private static native void forceActivationState(long addr, int state); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->forceActivationState(state);
	*/
    /*[0;X;D]*/

    public void activate(boolean forceActivation) {
        checkPointer();
        activate(cPointer, forceActivation);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.activate(forceActivation);
	*/

    private static native void activate(long addr, boolean forceActivation); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->activate(forceActivation);
	*/
    /*[0;X;D]*/

    public void activate() {
        checkPointer();
        activate(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.activate();
	*/

    private static native void activate(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->activate();
	*/
    /*[0;X;D]*/

    public boolean isActive() {
        checkPointer();
        return isActive(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.isActive();
	*/

    private static native boolean isActive(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->isActive();
	*/
    /*[0;X;D]*/

    public void setRestitution(float rest) {
        checkPointer();
        setRestitution(cPointer, rest);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setRestitution(rest);
	*/

    private static native void setRestitution(long addr, float rest); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setRestitution(rest);
	*/
    /*[0;X;D]*/

    public float getRestitution() {
        checkPointer();
        return getRestitution(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getRestitution();
	*/

    private static native float getRestitution(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getRestitution();
	*/
    /*[0;X;D]*/

    public void setFriction(float frict) {
        checkPointer();
        setFriction(cPointer, frict);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setFriction(frict);
	*/

    private static native void setFriction(long addr, float frict); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setFriction(frict);
	*/
    /*[0;X;D]*/

    public float getFriction() {
        checkPointer();
        return getFriction(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getFriction();
	*/

    private static native float getFriction(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getFriction();
	*/
    /*[0;X;D]*/

    public void setRollingFriction(float frict) {
        checkPointer();
        setRollingFriction(cPointer, frict);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setRollingFriction(frict);
	*/

    private static native void setRollingFriction(long addr, float frict); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setRollingFriction(frict);
	*/
    /*[0;X;D]*/

    public float getRollingFriction() {
        checkPointer();
        return getRollingFriction(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getRollingFriction();
	*/

    private static native float getRollingFriction(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getRollingFriction();
	*/
    /*[0;X;D]*/

    public int getInternalType() {
        checkPointer();
        return getInternalType(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getInternalType();
	*/

    private static native int getInternalType(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getInternalType();
	*/
    /*[0;X;D]*/

    public void getWorldTransform(Matrix4 out) {
        checkPointer();
        getWorldTransform(cPointer, out.val);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		Object btTra = jsObj.getWorldTransform(); #EVAL
		com.badlogic.gdx.physics.bullet.linearmath.btTransform.getOpenGLMatrix(btTra, out.val); #J
	*/

    private static native void getWorldTransform(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btTransform & tra = cobj->getWorldTransform();
		tra.getOpenGLMatrix(value);
	*/
    /*[0;X;D]*/

    public void setWorldTransform(Matrix4 worldTrans) {
        checkPointer();
        setWorldTransform(cPointer, worldTrans.val);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		value, worldTrans.val #P
		btTran, com.badlogic.gdx.physics.bullet.linearmath.btTransform.btTransform_1.jsObj #P
		btTran.setFromOpenGLMatrix(value);
		jsObj.setWorldTransform(btTran);
	*/

    private static native void setWorldTransform(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btTransform tra; 
		tra.setFromOpenGLMatrix(value);
		cobj->setWorldTransform(tra);
	*/
    /*[0;X;D]*/

    public btBroadphaseProxy getBroadphaseHandle() {
        checkPointer();
        return broadphaseProxy;
    }

    public void setBroadphaseHandle(btBroadphaseProxy handle) {
        checkPointer();
        this.broadphaseProxy = handle;
        setBroadphaseHandle(cPointer, handle != null ? handle.cPointer : 0);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
	 	this.broadphaseProxy = handle; #J
		handler, handle.jsObj #P
		jsObj.setBroadphaseHandle(handler);
	*/

    private static native void setBroadphaseHandle(long addr, long proxyAddr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btBroadphaseProxy * proxyObj = (btBroadphaseProxy *)proxyAddr;
		cobj->setBroadphaseHandle(proxyObj);
	*/
    /*[0;X;D]*/

    public void getInterpolationWorldTransform(Matrix4 out) {
        checkPointer();
        getWorldTransform(cPointer, out.val);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		Object btTra = jsObj.getInterpolationWorldTransform(); #EVAL
		com.badlogic.gdx.physics.bullet.linearmath.btTransform.getOpenGLMatrix(btTra, out.val); #J
	*/

    private static native void getInterpolationWorldTransform(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btTransform & tra = cobj->getInterpolationWorldTransform();
		tra.getOpenGLMatrix(value);
	*/
    /*[0;X;D]*/

    public void setInterpolationWorldTransform(Matrix4 trans) {
        checkPointer();
        setWorldTransform(cPointer, trans.val);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		value, trans.val #P
		btTran, com.badlogic.gdx.physics.bullet.linearmath.btTransform.btTransform_1.jsObj #P
		btTran.setFromOpenGLMatrix(value);
		jsObj.setInterpolationWorldTransform(btTran);
	*/

    private static native void setInterpolationWorldTransform(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btTransform tra; 
		tra.setFromOpenGLMatrix(value);
		cobj->setInterpolationWorldTransform(tra);
	*/
    /*[0;X;D]*/

    public void setInterpolationLinearVelocity(Vector3 linvel) {
        checkPointer();
        btVector3.localArr_1[0] = linvel.x;
        btVector3.localArr_1[1] = linvel.y;
        btVector3.localArr_1[2] = linvel.z;
        setInterpolationLinearVelocity(cPointer, btVector3.localArr_1);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		x, linvel.x #P
		y, linvel.y #P
		z, linvel.z #P
		btVec, com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_1.jsObj #P
		btVec.setValue(x,y,z);
		jsObj.setInterpolationLinearVelocity(btVec);
	*/

    private static native void setInterpolationLinearVelocity(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btVector3 vec; 
		vec.setValue(value[0],value[1],value[2]);
		cobj->setInterpolationLinearVelocity(vec);
	*/
    /*[0;X;D]*/

    public void setInterpolationAngularVelocity(Vector3 angvel) {
        checkPointer();
        btVector3.localArr_1[0] = angvel.x;
        btVector3.localArr_1[1] = angvel.y;
        btVector3.localArr_1[2] = angvel.z;
        setInterpolationAngularVelocity(cPointer, btVector3.localArr_1);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		x, angvel.x #P
		y, angvel.y #P
		z, angvel.z #P
		btVec, com.badlogic.gdx.physics.bullet.linearmath.btVector3.btVector3_1.jsObj #P
		btVec.setValue(x,y,z);
		jsObj.setInterpolationAngularVelocity(btVec);
	*/

    private static native void setInterpolationAngularVelocity(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btVector3 vec; 
		vec.setValue(value[0],value[1],value[2]);
		cobj->setInterpolationAngularVelocity(vec);
	*/
    /*[0;X;D]*/

    public void getInterpolationLinearVelocity(Vector3 out) {
        checkPointer();
        getInterpolationLinearVelocity(cPointer, btVector3.localArr_1);
        out.set(btVector3.localArr_1);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		var btVec = jsObj.getInterpolationLinearVelocity();
		out.x = btVec.x(); #EVALFLOAT
		out.y = btVec.y(); #EVALFLOAT
		out.z = btVec.z(); #EVALFLOAT
	*/

    private static native void getInterpolationLinearVelocity(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		const btVector3 & vec = cobj->getInterpolationLinearVelocity();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
    /*[0;X;D]*/

    public void getInterpolationAngularVelocity(Vector3 out) {
        checkPointer();
        getInterpolationAngularVelocity(cPointer, btVector3.localArr_1);
        out.set(btVector3.localArr_1);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		var btVec = jsObj.getInterpolationAngularVelocity();
		out.x = btVec.x(); #EVALFLOAT
		out.y = btVec.y(); #EVALFLOAT
		out.z = btVec.z(); #EVALFLOAT
	*/

    private static native void getInterpolationAngularVelocity(long addr, float[] value); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		const btVector3 & vec = cobj->getInterpolationAngularVelocity();
		value[0] = vec.x();
		value[1] = vec.y();
		value[2] = vec.z();
	*/
    /*[0;X;D]*/

    public int getIslandTag() {
        checkPointer();
        return getIslandTag(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getIslandTag();
	*/

    private static native int getIslandTag(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getIslandTag();
	*/
    /*[0;X;D]*/

    public void setIslandTag(int tag) {
        checkPointer();
        setIslandTag(cPointer, tag);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setIslandTag(tag);
	*/

    private static native void setIslandTag(long addr, int tag); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setIslandTag(tag);
	*/
    /*[0;X;D]*/

    public int getCompanionId() {
        checkPointer();
        return getCompanionId(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getCompanionId();
	*/

    private static native int getCompanionId(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getCompanionId();
	*/
    /*[0;X;D]*/

    public void setCompanionId(int id) {
        checkPointer();
        setCompanionId(cPointer, id);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setCompanionId(id);
	*/

    private static native void setCompanionId(long addr, int id); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setCompanionId(id);
	*/
    /*[0;X;D]*/

    public float getHitFraction() {
        checkPointer();
        return getHitFraction(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getHitFraction();
	*/

    private static native float getHitFraction(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getHitFraction();
	*/
    /*[0;X;D]*/

    public void setHitFraction(float hitFraction) {
        checkPointer();
        setHitFraction(cPointer, hitFraction);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setHitFraction(hitFraction);
	*/

    private static native void setHitFraction(long addr, float hitFraction); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setHitFraction(hitFraction);
	*/
    /*[0;X;D]*/

    public int getCollisionFlags() {
        checkPointer();
        return getCollisionFlags(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getCollisionFlags();
	*/

    private static native int getCollisionFlags(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getCollisionFlags();
	*/
    /*[0;X;D]*/

    public void setCollisionFlags(int flags) {
        checkPointer();
        setCollisionFlags(cPointer, flags);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setCollisionFlags(flags);
	*/

    private static native void setCollisionFlags(long addr, int flags); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setCollisionFlags(flags);
	*/
    /*[0;X;D]*/

    public float getCcdSweptSphereRadius() {
        checkPointer();
        return getCcdSweptSphereRadius(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getCcdSweptSphereRadius();
	*/

    private static native float getCcdSweptSphereRadius(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getCcdSweptSphereRadius();
	*/
    /*[0;X;D]*/

    public void setCcdSweptSphereRadius(float radius) {
        checkPointer();
        setCcdSweptSphereRadius(cPointer, radius);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setCcdSweptSphereRadius(radius);
	*/

    private static native void setCcdSweptSphereRadius(long addr, float radius); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setCcdSweptSphereRadius(radius);
	*/
    /*[0;X;D]*/

    public float getCcdMotionThreshold() {
        checkPointer();
        return getCcdMotionThreshold(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getCcdMotionThreshold();
	*/

    private static native float getCcdMotionThreshold(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getCcdMotionThreshold();
	*/
    /*[0;X;D]*/

    public float getCcdSquareMotionThreshold() {
        checkPointer();
        return getCcdSquareMotionThreshold(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getCcdSquareMotionThreshold();
	*/

    private static native float getCcdSquareMotionThreshold(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getCcdSquareMotionThreshold();
	*/
    /*[0;X;D]*/

    public void setCcdMotionThreshold(float ccdMotionThreshold) {
        checkPointer();
        setCcdMotionThreshold(cPointer, ccdMotionThreshold);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setCcdMotionThreshold(ccdMotionThreshold);
	*/

    private static native void setCcdMotionThreshold(long addr, float ccdMotionThreshold); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setCcdMotionThreshold(ccdMotionThreshold);
	*/
    /*[0;X;D]*/

    public int getUserIndex() {
        checkPointer();
        return getUserIndex(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getUserIndex();
	*/

    private static native int getUserIndex(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getUserIndex();
	*/
    /*[0;X;D]*/

    public int getUserValue() {
        return this.userValue;
    }

    public void setUserIndex(int index) {
        checkPointer();
        setUserIndex(cPointer, index);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		jsObj.setUserIndex(index);
	*/

    private static native void setUserIndex(long addr, int index); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		cobj->setUserIndex(index);
	*/
    /*[0;X;D]*/

    public void setUserValue(int userValue) {
        this.userValue = userValue;
    }

    public int getUpdateRevisionInternal() {
        checkPointer();
        return getUpdateRevisionInternal(cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
	 	jsObj, this.jsObj #P
		return jsObj.getUpdateRevisionInternal();
	*/

    private static native int getUpdateRevisionInternal(long addr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		return cobj->getUpdateRevisionInternal();
	*/
    /*[0;X;D]*/

    public boolean checkCollideWith(btCollisionObject co) {
        checkPointer();
        return checkCollideWith(cPointer, co.cPointer);
    }
	/*[0;X;L]
	 	checkPointer();  #J
		jsObj, this.jsObj #P
		cobj, co.jsObj #P
		return jsObj.checkCollideWith(cobj);
	*/

    private static native boolean checkCollideWith(long addr, long collObjAddr); /*
		btCollisionObject * cobj = (btCollisionObject *)addr;
		btCollisionObject * collObj = (btCollisionObject *)collObjAddr;
		cobj->checkCollideWith(collObj);
	*/
    /*[0;X;D]*/

    public final static class CollisionFlags {
        public final static int CF_STATIC_OBJECT = 1;
        public final static int CF_KINEMATIC_OBJECT = 2;
        public final static int CF_NO_CONTACT_RESPONSE = 4;
        public final static int CF_CUSTOM_MATERIAL_CALLBACK = 8;
        public final static int CF_CHARACTER_OBJECT = 16;
        public final static int CF_DISABLE_VISUALIZE_OBJECT = 32;
        public final static int CF_DISABLE_SPU_COLLISION_PROCESSING = 64;
    }

    public final static class CollisionObjectTypes {
        public final static int CO_COLLISION_OBJECT = 1;
        public final static int CO_RIGID_BODY = 2;
        public final static int CO_GHOST_OBJECT = 4;
        public final static int CO_SOFT_BODY = 8;
        public final static int CO_HF_FLUID = 16;
        public final static int CO_USER_TYPE = 32;
        public final static int CO_FEATHERSTONE_LINK = 64;
    }

    public final static class AnisotropicFrictionFlags {
        public final static int CF_ANISOTROPIC_FRICTION_DISABLED = 0;
        public final static int CF_ANISOTROPIC_FRICTION = 1;
        public final static int CF_ANISOTROPIC_ROLLING_FRICTION = 2;
    }
}
