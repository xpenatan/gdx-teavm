import com.badlogic.gdx.graphics.Colors;
import org.teavm.interop.Import;

public class PSPLauncherTest {


    public static void main(String[] args) {
        int GL_DEPTH_BUFFER_BIT = 4;
        int GL_COLOR_BUFFER_BIT = 1;
        int GL_TRUE = 1;
        int GL_FALSE = 0;

        System.out.println("HELLO WORLD PSP");

        glInit();
        while(true) {
            glStartFrame(GL_FALSE);
            glClearColor(0xFF00FF00);

            glClearDepth(0);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            guglSwapBuffers(GL_TRUE, GL_FALSE);
        }
    }

    @Import(name = "glInit")
    private static native void glInit();

    @Import(name = "glStartFrame")
    private static native void glStartFrame(int dialog);

    @Import(name = "glDisable")
    private static native void glDisable(int flag);

    @Import(name = "glClearColor")
    private static native void glClearColor(int flag);

    @Import(name = "glClearDepth")
    private static native void glClearDepth(int flag);

    @Import(name = "glClear")
    private static native void glClear(int flag);

    @Import(name = "guglSwapBuffers")
    private static native void guglSwapBuffers(int param1, int param2);

//    @Import(name = "pspDebugScreenSetXY")
//    private static native void pspDebugScreenSetXY(int x, int y);
//
//    @Import(name = "pspDebugScreenPrintf")
//    private static native void pspDebugScreenPrintf(String text);
//
//    @Import(name = "sceDisplayWaitVblankStart")
//    private static native void sceDisplayWaitVblankStart();

//    @Import(name = "sceKernelCreateThread")
//    private static native int sceKernelCreateThread(int x, int y);
}
