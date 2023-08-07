package com.badlogic.gdx.physics.bullet.linearmath;


import com.badlogic.gdx.math.Matrix4;
import com.github.xpenatan.jparser.base.IDLBase;

public class btMotionState extends IDLBase {

    private final Matrix4 TEMP_GDX_01 = new Matrix4();

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

    public btMotionState() {
        long addr = createNATIVE();
        initObject(addr, true);
    }

    /*[-C++;-NATIVE]
        if(motionClass == 0) {
            motionClass = (jclass)env->NewGlobalRef(env->GetObjectClass(object));
            getWorldTransformID = env->GetMethodID(motionClass, "getWorldTransformCPP", "(J)V");
            setWorldTransformID = env->GetMethodID(motionClass, "setWorldTransformCPP", "(J)V");
        }
        return (jlong)new CustomMotionState(env, env->NewGlobalRef(object));
    */
    private native long createNATIVE();

    private void getWorldTransformCPP(long worldTransAddr) {
        TEMP_GDX_01.idt();
        getWorldTransform(TEMP_GDX_01);
        btTransform.convert(TEMP_GDX_01, worldTransAddr);
    }

    private void setWorldTransformCPP(long worldTransAddr) {
        btTransform.convert(worldTransAddr, TEMP_GDX_01);
        setWorldTransform(TEMP_GDX_01);
    }

    public void getWorldTransform(Matrix4 worldTrans) {
    }

    public void setWorldTransform(Matrix4 worldTrans) {
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
