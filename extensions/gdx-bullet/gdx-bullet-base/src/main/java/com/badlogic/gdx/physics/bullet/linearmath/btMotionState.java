package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.BulletBase;
import netscape.javascript.JSObject;

/**
 * @author xpenatan
 */
public abstract class btMotionState extends BulletBase {
    Matrix4 tempMat = new Matrix4();

    protected btMotionState(String className) {
        super(className);
        initJavaObject();

    }

    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface SetWorldTransform extends org.teavm.jso.JSObject {
        void setWorldTransformJS(int worldTrans);
    }
    @org.teavm.jso.JSFunctor
    public interface GetWorldTransform extends org.teavm.jso.JSObject {
        void getWorldTransformJS(int worldTrans);
    }
     */
    /*[-teaVM;-REPLACE]
    private void initJavaObject() {
       SetWorldTransform setWorldTransform = new SetWorldTransform() {
            @Override
            public void setWorldTransformJS (int worldTransAddr) {
                tempMat.idt();
                btTransform.convert(worldTransAddr, tempMat);
                setWorldTransform(tempMat);
            }
        };
       GetWorldTransform getWorldTransform = new GetWorldTransform() {
            @Override
            public void getWorldTransformJS (int worldTransAddr) {
                tempMat.idt();
                getWorldTransform(tempMat);
                btTransform.convert(tempMat, worldTransAddr);
            }
        };
        int pointer = createNative(setWorldTransform, getWorldTransform);
        initObject(pointer, true);
    }
     */
    private void initJavaObject() {
        initObject(createNative(), true);
    }

    /*[-teaVM;-REPLACE]
    @org.teavm.jso.JSBody(params = { "setWorldTransform", "getWorldTransform" }, script = "var jsMotionState = new Bullet.MyMotionState(); jsMotionState.setWorldTransform = setWorldTransform; jsMotionState.getWorldTransform = getWorldTransform; return Bullet.getPointer(jsMotionState);")
    private static native int createNative(SetWorldTransform setWorldTransform, GetWorldTransform getWorldTransform);
     */
    private static native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-teaVM;-NATIVE]
        var jsObj = Bullet.wrapPointer(addr, Bullet.MyMotionState);
        Bullet.destroy(jsObj);
     */
    private static native void deleteNative(long addr);


    /**
     * Called to initialize body position. Modify worldTrans.
     */
    public void getWorldTransform(Matrix4 worldTrans) {
    }

    /**
     * Called when rigid body change position. Update your render matrix with worldTrans.
     */
    public void setWorldTransform(Matrix4 worldTrans) {
    }
}