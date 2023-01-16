package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class ClosestRayResultCallback extends RayResultCallback {

    /*[-C++;-NATIVE]
        #include "btBulletCollisionCommon.h"
    */

    /*[-C++;-NATIVE]

        static jclass closestRayResultClass = 0;
        static jmethodID onAddSingleResultID = 0;

        class CustomClosestRayResultCallback : public btCollisionWorld::ClosestRayResultCallback
        {

        private:
            JNIEnv* env;
            jobject obj;

        public:
            CustomClosestRayResultCallback(JNIEnv* env, jobject obj, const btVector3& rayFromWorld, const btVector3& rayToWorld)
                : btCollisionWorld::ClosestRayResultCallback(rayFromWorld, rayToWorld)
            {
                this->env = env;
                this->obj = obj;
            }

            virtual btScalar addSingleResult(btCollisionWorld::LocalRayResult& rayResult, bool normalInWorldSpace)
            {
                return env->CallFloatMethod(obj, onAddSingleResultID, (jlong)&rayResult, normalInWorldSpace);
            }
        };
    */

    public ClosestRayResultCallback(Vector3 rayFromWorld, Vector3 rayToWorld) {
        btVector3 btFrom = btVector3.TEMP_0;
        btVector3 btTo = btVector3.TEMP_1;
        btVector3.convert(rayFromWorld, btFrom);
        btVector3.convert(rayToWorld, btTo);
        initJavaObject(btFrom, btTo);
    }
    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface AddSingleResultFunction extends org.teavm.jso.JSObject {
        float addSingleResultJS(int rayResultAddr, boolean normalInWorldSpace);
    }
    */

    /*[-teaVM;-REPLACE]
    private void initJavaObject(btVector3 from, btVector3 to) {
       AddSingleResultFunction addSingleResultFunction = new AddSingleResultFunction() {
            @Override
            public float addSingleResultJS(int rayResultAddr, boolean normalInWorldSpace) {
                LocalRayResult.temp01.setPointer(rayResultAddr);
                return addSingleResult(LocalRayResult.temp01, normalInWorldSpace);
            }
        };
        int pointer = createNative(from.getCPointer(), to.getCPointer(), addSingleResultFunction);
        initObject(pointer, true);
    }
    */
    private void initJavaObject(btVector3 from, btVector3 to) {
        initObject(createNative(from.getCPointer(), to.getCPointer()), true);
    }

    /*[-C++;-NATIVE]
        if(closestRayResultClass == 0) {
            closestRayResultClass = (jclass)env->NewGlobalRef(env->GetObjectClass(object));
            onAddSingleResultID = env->GetMethodID(closestRayResultClass, "onAddSingleResult", "(JZ)F");
        }
        return (jlong)new CustomClosestRayResultCallback(env, env->NewGlobalRef(object), *((btVector3*)fromAddr), *((btVector3*)toAddr));
    */
    /*[-teaVM;-REPLACE]
    @org.teavm.jso.JSBody(params = { "rayFromWorldAddr", "rayToWorldAddr", "addSingleResultFunction" }, script = "var callback = new Bullet.MyClosestRayResultCallback(rayFromWorldAddr, rayToWorldAddr); callback.addSingleResult = addSingleResultFunction; return Bullet.getPointer(callback);")
    private static native int createNative(long rayFromWorldAddr, long rayToWorldAddr, AddSingleResultFunction addSingleResultFunction);
    */
    private native long createNative(long fromAddr, long toAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (CustomClosestRayResultCallback*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);

    public void getRayFromWorld(Vector3 out) {
        long outAddr = getRayFromWorld(cPointer);
        btVector3.convert(outAddr, out);
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        return (jlong)&nativeObject->m_rayFromWorld;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        return Bullet.getPointer(jsObj.get_m_rayFromWorld());
    */
    private static native long getRayFromWorld(long addr);

    public void setRayFromWorld(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_1);
        setRayFromWorld(cPointer, btVector3.TEMP_1.getCPointer());
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        nativeObject->m_rayFromWorld = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        var jsVec3Obj = Bullet.wrapPointer(valueAddr, Bullet.btVector3);
        jsObj.set_m_rayFromWorld(jsVec3Obj);
    */
    private static native long setRayFromWorld(long addr, long valueAddr);

    public void getRayToWorld(Vector3 out) {
        long outAddr = getRayToWorld(cPointer);
        btVector3.convert(outAddr, out);
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        return (jlong)&nativeObject->m_rayToWorld;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        return Bullet.getPointer(jsObj.get_m_rayToWorld());
    */
    private static native long getRayToWorld(long addr);

    public void setRayToWorld(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_1);
        setRayToWorld(cPointer, btVector3.TEMP_1.getCPointer());
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        nativeObject->m_rayToWorld = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        var jsVec3Obj = Bullet.wrapPointer(valueAddr, Bullet.btVector3);
        jsObj.set_m_rayToWorld(jsVec3Obj);
    */
    private static native long setRayToWorld(long addr, long valueAddr);

    public void getHitNormalWorld(Vector3 out) {
        long outAddr = getHitNormalWorld(cPointer);
        btVector3.convert(outAddr, out);
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        return (jlong)&nativeObject->m_hitNormalWorld;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        return Bullet.getPointer(jsObj.get_m_hitNormalWorld());
    */
    private static native long getHitNormalWorld(long addr);

    public void setHitNormalWorld(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_1);
        setHitNormalWorld(cPointer, btVector3.TEMP_1.getCPointer());
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        nativeObject->m_hitNormalWorld = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        var jsVec3Obj = Bullet.wrapPointer(valueAddr, Bullet.btVector3);
        jsObj.set_m_hitNormalWorld(jsVec3Obj);
    */
    private static native long setHitNormalWorld(long addr, long valueAddr);

    public void getHitPointWorld(Vector3 out) {
        long outAddr = getHitPointWorld(cPointer);
        btVector3.convert(outAddr, out);
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        return (jlong)&nativeObject->m_hitPointWorld;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        return Bullet.getPointer(jsObj.get_m_hitPointWorld());
    */
    private static native long getHitPointWorld(long addr);

    public void setHitPointWorld(Vector3 value) {
        btVector3.convert(value, btVector3.TEMP_1);
        setHitPointWorld(cPointer, btVector3.TEMP_1.getCPointer());
    }

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        nativeObject->m_hitPointWorld = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        var jsVec3Obj = Bullet.wrapPointer(valueAddr, Bullet.btVector3);
        jsObj.set_m_hitPointWorld(jsVec3Obj);
    */
    private static native long setHitPointWorld(long addr, long valueAddr);

    public float addSingleResult(LocalRayResult rayResult, boolean normalInWorldSpace) {
        return addSingleResult(cPointer, rayResult.getCPointer(), normalInWorldSpace);
    }

    //TODO native code must call single result

    /*[-C++;-NATIVE]
        CustomClosestRayResultCallback* nativeObject = (CustomClosestRayResultCallback*)addr;
        return nativeObject->ClosestRayResultCallback::addSingleResult(*((btCollisionWorld::LocalRayResult*)rayResultAddr), normalInWorldSpace);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyClosestRayResultCallback);
        return jsObj.addSingleResultSuper(rayResultAddr, normalInWorldSpace);
    */
    private static native float addSingleResult(long addr, long rayResultAddr, boolean normalInWorldSpace);

    private float onAddSingleResult(long rayResultAddr, boolean normalInWorldSpace) {
        LocalRayResult.temp01.setPointer(rayResultAddr);
        return addSingleResult(LocalRayResult.temp01, normalInWorldSpace);
    }
}