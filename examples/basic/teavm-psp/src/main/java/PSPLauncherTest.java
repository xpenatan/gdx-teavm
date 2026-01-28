import com.github.xpenatan.gdx.teavm.backends.psp.PSPApplication;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPCoreApi;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPDebugApi;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.EmptyApplicationTest;

public class PSPLauncherTest {

    private static boolean SIMPLE_LOOP = false;

    public static void main(String[] args) {
        if(SIMPLE_LOOP) {
            PSPCoreApi.init();
            PSPGraphicsApi.grInitGraphics();
            while(PSPCoreApi.isRunning()) {
                PSPDebugApi.logUsedMemory(1000);
                PSPGraphicsApi.grStartFrame(PSPGraphicsApi.GU_FALSE);
                PSPGraphicsApi.sceGuClearColor(0xFF00FFFF);
                PSPGraphicsApi.sceGuClear(PSPGraphicsApi.GU_COLOR_BUFFER_BIT | PSPGraphicsApi.GU_DEPTH_BUFFER_BIT);
                PSPGraphicsApi.grSwapBuffers(PSPGraphicsApi.GU_TRUE, PSPGraphicsApi.GU_FALSE);
            }
        }
        else {
            new PSPApplication(new EmptyApplicationTest());
    //        new PSPApplication(new HelloTriangle());
        }
    }
}
