package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btTriangleRaycastCallback extends btTriangleCallback {

    /*[-C++;-NATIVE]
        #include "BulletCollision/NarrowPhaseCollision/btRaycastCallback.h"
    */

    /*[-C++;-NATIVE]

        static jclass triangleRaycastClass = 0;
        static jmethodID onReportHitID = 0;

        class CustombtTriangleRaycastCallback : public btTriangleRaycastCallback
        {

        private:
            JNIEnv* env;
            jobject obj;

        public:
            CustombtTriangleRaycastCallback(JNIEnv* env, jobject obj, const btVector3& from, const btVector3& to, unsigned int flags)
                : btTriangleRaycastCallback(from, to, flags)
            {
                this->env = env;
                this->obj = obj;
            }

            virtual btScalar reportHit(const btVector3& hitNormalLocal, btScalar hitFraction, int partId, int triangleIndex)
            {
                return env->CallFloatMethod(obj, onReportHitID, (jlong)&hitNormalLocal, hitFraction, partId, triangleIndex);
            }
        };
    */

    Vector3 tempVec = new Vector3();

    public btTriangleRaycastCallback(Vector3 from, Vector3 to) {
        btVector3 btFrom = btVector3.TEMP_0;
        btVector3 btTo = btVector3.TEMP_1;
        btVector3.convert(from, btFrom);
        btVector3.convert(to, btTo);
        initJavaObject(btFrom, btTo);
    }

    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface ReportHitFunction extends org.teavm.jso.JSObject {
        float reportHitJS(int hitNormalLocalAddr, float hitFraction, int partId, int triangleIndex);
    }
    */

    /*[-teaVM;-REPLACE]
    private void initJavaObject(btVector3 from, btVector3 to) {
       ReportHitFunction reportHitFunction = new ReportHitFunction() {
            @Override
            public float reportHitJS(int hitNormalLocalAddr, float hitFraction, int partId, int triangleIndex) {
                btVector3.convert(hitNormalLocalAddr, tempVec);
                return reportHit(tempVec, hitFraction, partId, triangleIndex);
            }
        };
        int pointer = createNative(from.getCPointer(), to.getCPointer(), reportHitFunction);
        initObject(pointer, true);
    }
    */
    private void initJavaObject(btVector3 from, btVector3 to) {
        initObject(createNative(from.getCPointer(), to.getCPointer()), true);
    }

    /*[-C++;-NATIVE]
        if(triangleRaycastClass == 0) {
            triangleRaycastClass = (jclass)env->NewGlobalRef(env->GetObjectClass(object));
            onReportHitID = env->GetMethodID(triangleRaycastClass, "onReportHit", "(JFII)F");
        }
        return (jlong)new CustombtTriangleRaycastCallback(env, env->NewGlobalRef(object), *((btVector3*)fromAddr), *((btVector3*)toAddr), 0);
    */
    /*[-teaVM;-REPLACE]
    @org.teavm.jso.JSBody(params = { "fromAddr", "toAddr", "hitFunction" }, script = "var callback = new Bullet.MybtTriangleRaycastCallback(fromAddr, toAddr); callback.reportHit = hitFunction; return Bullet.getPointer(callback);")
    private static native int createNative(long fromAddr, long toAddr, ReportHitFunction hitFunction);
    */
    private native long createNative(long fromAddr, long toAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (CustombtTriangleRaycastCallback*)addr;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        Bullet.destroy(jsObj);
    */
    private static native void deleteNative(long addr);


    /*[-IDL_SKIP]
    */
    public float reportHit(Vector3 hitNormalLocal, float hitFraction, int partId, int triangleIndex) {
        return 0;
    }

    private float onReportHit(long hitNormalLocalAddr, float hitFraction, int partId, int triangleIndex) {
        btVector3.convert(hitNormalLocalAddr, btVector3.TEMP_GDX_01);
        return reportHit(btVector3.TEMP_GDX_01, hitFraction, partId, triangleIndex);
    }

    public void setFrom(btVector3 value) {
        setFrom(cPointer, value.getCPointer());
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        nativeObject->m_from = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        var jsVec3Obj = Bullet.wrapPointer(valueAddr, Bullet.btVector3);
        jsObj.set_m_from(jsVec3Obj);
    */
    private static native long setFrom(long addr, long valueAddr);

    public btVector3 getFrom() {
        long outAddr = getFrom(cPointer);
        btVector3.emptyTransform.setPointer(outAddr);
        return btVector3.emptyTransform;
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        return (jlong)&nativeObject->m_from;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        return Bullet.getPointer(jsObj.get_m_from());
    */
    private static native long getFrom(long addr);

    public void setTo(btVector3 value) {
        setTo(cPointer, value.getCPointer());
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        nativeObject->m_to = *((btVector3*)valueAddr);
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        var jsVec3Obj = Bullet.wrapPointer(valueAddr, Bullet.btVector3);
        jsObj.set_m_to(jsVec3Obj);
    */
    private static native long setTo(long addr, long valueAddr);

    public btVector3 getTo() {
        long outAddr = getTo(cPointer);
        btVector3.emptyTransform.setPointer(outAddr);
        return btVector3.emptyTransform;
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        return (jlong)&nativeObject->m_to;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        return Bullet.getPointer(jsObj.get_m_to());
    */
    private static native long getTo(long addr);

    /*[-teaVM;-REPLACE]
    public void setFlags (long value) {
        setFlags(cPointer, (int)value);
    }
    */
    public void setFlags(long value) {
        setFlags(cPointer, value);
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        nativeObject->m_flags = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        jsObj.set_m_flags(value);
    */
    private static native void setFlags(long addr, long value);

    public long getFlags() {
        return getFlags(cPointer);
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        return nativeObject->m_flags;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        return jsObj.get_m_flags();
    */
    private static native long getFlags(long addr);

    public void setHitFraction(float value) {
        setHitFraction(cPointer, value);
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        nativeObject->m_hitFraction = value;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        jsObj.set_m_hitFraction(value);
    */
    private static native void setHitFraction(long addr, float value);

    public float getHitFraction() {
        return getHitFraction(cPointer);
    }

    /*[-C++;-NATIVE]
        CustombtTriangleRaycastCallback* nativeObject = (CustombtTriangleRaycastCallback*)addr;
        return nativeObject->m_hitFraction;
    */
    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        return jsObj.get_m_hitFraction();
    */
    private static native float getHitFraction(long addr);

    public final static class EFlags {
        public final static int kF_None = 0;
        public final static int kF_FilterBackfaces = 1 << 0;
        public final static int kF_KeepUnflippedNormal = 1 << 1;
        public final static int kF_UseSubSimplexConvexCastRaytest = 1 << 2;
        public final static int kF_UseGjkConvexCastRaytest = 1 << 3;
        public final static int kF_Terminator = 0xFFFFFFFF;
    }
}