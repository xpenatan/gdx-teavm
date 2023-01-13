package com.badlogic.gdx.physics.bullet.linearmath;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.physics.bullet.BulletBase;

/**
 * @author xpenatan
 */
public class btMotionState extends BulletBase {

    /*[-C++;-NATIVE]
        #include "LinearMath/btMotionState.h"
    */

    /*[-C++;-NATIVE]
        static jclass motionClass = 0;
        static jmethodID getWorldTransformID = 0;
        static jmethodID setWorldTransformID = 0;

        class CustomMotionState : public btMotionState
        {

        private:
            JNIEnv* env;
            jobject obj;

        public:
            CustomMotionState( JNIEnv* env, jobject obj )
            {
                this->env = env;
                this->obj = obj;
            }

            virtual void getWorldTransform(btTransform & centerOfMassWorldTrans) const
            {
                env->CallVoidMethod(obj, getWorldTransformID, (jlong)&centerOfMassWorldTrans);
            }

            virtual void setWorldTransform(const btTransform& centerOfMassWorldTrans)
            {
                env->CallVoidMethod(obj, setWorldTransformID, (jlong)&centerOfMassWorldTrans);
            }
        };

    */

    Matrix4 tempMat = new Matrix4();

    public btMotionState() {
        initJavaObject();
    }

    /*[-teaVM;-ADD]
    @org.teavm.jso.JSFunctor
    public interface SetWorldTransform extends org.teavm.jso.JSObject {
        void setWorldTransformJS(int worldTrans);
    }
     */
    /*[-teaVM;-ADD]
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

    /*[-C++;-NATIVE]
        if(motionClass == 0) {
            motionClass = (jclass)env->NewGlobalRef(env->GetObjectClass(object));
            getWorldTransformID = env->GetMethodID(motionClass, "getWorldTransformCPP", "(J)V");
            setWorldTransformID = env->GetMethodID(motionClass, "setWorldTransformCPP", "(J)V");
        }
        return (jlong)new CustomMotionState(env, env->NewGlobalRef(object));
    */
    /*[-teaVM;-REPLACE]
    @org.teavm.jso.JSBody(params = { "setWorldTransform", "getWorldTransform" }, script = "var jsMotionState = new Bullet.MyMotionState(); jsMotionState.setWorldTransform = setWorldTransform; jsMotionState.getWorldTransform = getWorldTransform; return Bullet.getPointer(jsMotionState);")
    private static native int createNative(SetWorldTransform setWorldTransform, GetWorldTransform getWorldTransform);
    */
    private native long createNative();

    @Override
    protected void deleteNative() {
        deleteNative(cPointer);
    }

    /*[-C++;-NATIVE]
        delete (CustomMotionState*)addr;
    */
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

    private void getWorldTransformCPP(long worldTransAddr) {
        btTransform.TEMP_GDX_01.idt();
        getWorldTransform(btTransform.TEMP_GDX_01);
        btTransform.convert(btTransform.TEMP_GDX_01, worldTransAddr);
    }

    private void setWorldTransformCPP(long worldTransAddr) {
        btTransform.convert(worldTransAddr, btTransform.TEMP_GDX_01);
        setWorldTransform(btTransform.TEMP_GDX_01);
    }

    /*[-IDL_SKIP]
     */
    private void getWorldTransform(btTransform worldTrans) {
    }

    /*[-IDL_SKIP]
     */
    private void setWorldTransform(btTransform worldTrans) {
    }
}