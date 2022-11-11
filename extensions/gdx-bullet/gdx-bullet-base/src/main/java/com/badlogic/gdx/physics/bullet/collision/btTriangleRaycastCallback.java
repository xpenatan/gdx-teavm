package com.badlogic.gdx.physics.bullet.collision;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.linearmath.btVector3;

/**
 * @author xpenatan
 */
public class btTriangleRaycastCallback extends btTriangleCallback {
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

    /*[-teaVM;-REPLACE]
    @org.teavm.jso.JSBody(params = { "fromAddr", "toAddr", "hitFunction" }, script = "var callback = new Bullet.MybtTriangleRaycastCallback(fromAddr, toAddr); callback.reportHit = hitFunction; return Bullet.getPointer(callback);")
    private static native int createNative(long fromAddr, long toAddr, ReportHitFunction hitFunction);
     */
    private static native long createNative(long fromAddr, long toAddr);

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public float reportHit(Vector3 hitNormalLocal, float hitFraction, int partId, int triangleIndex) {
        return 0;
    }

    public void setFrom(btVector3 value) {
        setFrom(cPointer, value.getCPointer());
    }

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

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        return Bullet.getPointer(jsObj.get_m_from());
     */
    private static native long getFrom(long addr);

    public void setTo(btVector3 value) {
        setTo(cPointer, value.getCPointer());
    }

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

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        jsObj.set_m_flags(value);
     */
    private static native void setFlags(long addr, long value);

    public long getFlags() {
        return getFlags(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        return jsObj.get_m_flags();
     */
    private static native long getFlags(long addr);

    public void setHitFraction(float value) {
        setHitFraction(cPointer, value);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MybtTriangleRaycastCallback);
        jsObj.set_m_hitFraction(value);
     */
    private static native void setHitFraction(long addr, float value);

    public float getHitFraction() {
        return getHitFraction(cPointer);
    }

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