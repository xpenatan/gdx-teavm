import com.github.xpenatan.gdx.teavm.backends.psp.PSPApplication;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPCoreApi;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPDebugApi;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi;
import com.github.xpenatan.gdx.teavm.examples.basic.tests.webgl.EmptyApplicationTest;
import demos.PSPCubeTest;
import demos.PSPTest;

public class PSPLauncherTest {

    private static boolean SIMPLE_LOOP = true;

    public static void main(String[] args) {
        if(SIMPLE_LOOP) {
            setupSimpleLoop();
        }
        else {
            new PSPApplication(new EmptyApplicationTest());
    //        new PSPApplication(new HelloTriangle());
        }
    }

    private static PSPTest obtainSimpleTest() {
        return new PSPCubeTest();
    }

    private static void setupSimpleLoop() {
        PSPTest test = obtainSimpleTest();
        PSPCoreApi.init();
        PSPGraphicsApi.initGraphics();

        PSPTest app = null;
        while(PSPCoreApi.isRunning()) {
            PSPGraphicsApi.startFrame(PSPGraphicsApi.GU_FALSE);
            {
                PSPDebugApi.logUsedMemory(1000);
                if(app == null) {
                    app = test;
                    app.create();
                }
                else {
                    app.render();
                }
            }
            PSPGraphicsApi.endFrame(PSPGraphicsApi.GU_TRUE, PSPGraphicsApi.GU_FALSE);
        }
        PSPGraphicsApi.sceGuDisplay(PSPGraphicsApi.GU_FALSE);
        PSPGraphicsApi.sceGuTerm();
    }
}
