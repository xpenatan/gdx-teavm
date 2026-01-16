import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.glfw.config.backend.TeaGLFWBackend;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) throws IOException {
        AssetFileHandle assetsPath = new AssetFileHandle("../desktop/assets");
        TeaGLFWBackend cBackend = new TeaGLFWBackend();
//        cBackend.shouldGenerateSource = false;
        new TeaCompiler()
                .addAssets(assetsPath)
                .setBackend(cBackend)
                .setObfuscated(false)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestCLauncher.class.getName())
                .build(new File("build/dist"));
    }
}
