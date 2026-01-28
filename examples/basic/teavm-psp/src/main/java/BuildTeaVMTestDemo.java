import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.psp.config.backend.TeaPSPBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) {
        try {
            AssetFileHandle assetsPath = new AssetFileHandle("../assets");
            TeaPSPBackend pspBackend = new TeaPSPBackend();

            pspBackend.debugMemory = true;

            new TeaCompiler(pspBackend)
                    .addAssets(assetsPath)
                    .setObfuscated(false)
                    .setDebugInformationGenerated(true) // Generates .prx file for debugging
                    .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                    .setMainClass(PSPLauncherTest.class.getName())
                    .build(new File("build/dist"));
            System.out.println("Build successful");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
