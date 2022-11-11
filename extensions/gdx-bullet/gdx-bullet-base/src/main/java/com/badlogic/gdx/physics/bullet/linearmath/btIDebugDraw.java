package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btIDebugDraw extends BulletBase {

    private final Vector3 tempVec01 = new Vector3();
    private final Vector3 tempVec02 = new Vector3();
    private final Vector3 tempVec03 = new Vector3();

    public btIDebugDraw() {
        initJavaObject();
    }

    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface DrawLineFunction extends org.teavm.jso.JSObject {
        void drawLineJS(int vec3FromAddr, int vec3ToAddr, int vec3ColorAddr);
    }
     */
    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface DrawContactPointFunction extends org.teavm.jso.JSObject {
        void drawContactPointJS(int vec3PointOnBAddr,int vec3NormalOnBAddr, float distanceAddr, int lifeTimeAddr, int vec3ColorAddr);
    }
     */
    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface GetDebugFunction extends org.teavm.jso.JSObject {
        int getDebugFunctionJS();
    }
     */

    /*[-teaVM;-REPLACE]
    private void initJavaObject() {
       DrawLineFunction drawLineFunction = new DrawLineFunction() {
            @Override
            public void drawLineJS(int vec3FromAddr, int vec3ToAddr, int vec3ColorAddr) {
                btVector3.convert(vec3FromAddr, tempVec01);
                btVector3.convert(vec3ToAddr, tempVec02);
                btVector3.convert(vec3ColorAddr, tempVec03);
                drawLine(tempVec01, tempVec02, tempVec03);
            }
        };
       DrawContactPointFunction drawContactPointFunction = new DrawContactPointFunction() {
            @Override
            public void drawContactPointJS(int vec3PointOnBAddr, int vec3NormalOnBAddr, float distance, int lifeTime, int vec3ColorAddr) {
                btVector3.convert(vec3PointOnBAddr, tempVec01);
                btVector3.convert(vec3NormalOnBAddr, tempVec02);
                btVector3.convert(vec3ColorAddr, tempVec03);
                drawContactPoint(tempVec01, tempVec02, distance, lifeTime, tempVec03);
            }
        };
       GetDebugFunction getDebugFunction = new GetDebugFunction() {
            @Override
            public int getDebugFunctionJS() {
                return getDebugMode();
            }
        };
        int pointer = createNative(drawLineFunction, drawContactPointFunction, getDebugFunction);
        initObject(pointer, true);
    }
     */
    private void initJavaObject() {
        initObject(createNative(), true);
    }

    /*[-teaVM;-REPLACE]
    @org.teavm.jso.JSBody(params = { "drawLineFunction", "drawContactPointFunction", "getDebugFunction" }, script = "var callback = new Bullet.MyDebugDraw(); callback.drawLine = drawLineFunction; callback.drawContactPoint = drawContactPointFunction; callback.getDebugMode = getDebugFunction; return Bullet.getPointer(callback);")
    private static native int createNative(DrawLineFunction drawLineFunction, DrawContactPointFunction drawContactPointFunction, GetDebugFunction getDebugFunction);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyDebugDraw);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);

    public void drawLine(Vector3 from, Vector3 to, Vector3 color) {
    }

    public void drawContactPoint(Vector3 pointOnB, Vector3 normalOnB, float distance, int lifeTime, Vector3 color) {
    }

    public void reportErrorWarning(String warningString) {
        //FIXME not impl
    }

    public void draw3dText(Vector3 location, String textString) {
        //FIXME not impl
    }

    public void setDebugMode(int debugMode) {
    }

    public int getDebugMode() {
        return 0;
    }

    public static final class DebugDrawModes {
        public static final int DBG_NoDebug = 0;
        public static final int DBG_DrawWireframe = 1;
        public static final int DBG_DrawAabb = 2;
        public static final int DBG_DrawFeaturesText = 4;
        public static final int DBG_DrawContactPoints = 8;
        public static final int DBG_NoDeactivation = 16;
        public static final int DBG_NoHelpText = 32;
        public static final int DBG_DrawText = 64;
        public static final int DBG_ProfileTimings = 128;
        public static final int DBG_EnableSatComparison = 256;
        public static final int DBG_DisableBulletLCP = 512;
        public static final int DBG_EnableCCD = 1024;
        public static final int DBG_DrawConstraints = (1 << 11);
        public static final int DBG_DrawConstraintLimits = (1 << 12);
        public static final int DBG_FastWireframe = (1 << 13);
        public static final int DBG_DrawNormals = (1 << 14);
        public static final int DBG_DrawFrames = (1 << 15);
        public static final int DBG_MAX_DEBUG_DRAW_MODE = DBG_DrawFrames + 1;
    }
}