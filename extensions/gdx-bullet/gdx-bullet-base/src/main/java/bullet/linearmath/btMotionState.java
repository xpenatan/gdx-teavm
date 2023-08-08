package bullet.linearmath;


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
        public btMotionState() {
           SetWorldTransform setWorldTransform = new SetWorldTransform() {
                @Override
                public void setWorldTransformJS (int worldTransAddr) {
                    TEMP_GDX_01.idt();
                    btTransform.convert(worldTransAddr, TEMP_GDX_01);
                    setWorldTransform(TEMP_GDX_01);
                }
            };
           GetWorldTransform getWorldTransform = new GetWorldTransform() {
                @Override
                public void getWorldTransformJS (int worldTransAddr) {
                    TEMP_GDX_01.idt();
                    getWorldTransform(TEMP_GDX_01);
                    btTransform.convert(TEMP_GDX_01, worldTransAddr);
                }
            };
            int pointer = createNative(setWorldTransform, getWorldTransform);
            initObject(pointer, true);
        }
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
    /*[-teaVM;-REPLACE]
        @org.teavm.jso.JSBody(params = { "setWorldTransform", "getWorldTransform" }, script = "var jsMotionState = new bullet.MotionStateImpl(); jsMotionState.setWorldTransform = setWorldTransform; jsMotionState.getWorldTransform = getWorldTransform; return bullet.getPointer(jsMotionState);")
        private static native int createNative(SetWorldTransform setWorldTransform, GetWorldTransform getWorldTransform);
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

    /*[-IDL_SKIP]*/
    private void getWorldTransform(btTransform worldTrans) {
    }

    /*[-IDL_SKIP]*/
    private void setWorldTransform(btTransform worldTrans) {
    }
}
