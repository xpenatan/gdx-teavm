import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.TeaWebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) {
        AssetFileHandle assetsPath = new AssetFileHandle("../assets");
        new TeaCompiler(new TeaWebBackend().setStartJettyAfterBuild(true))
                .addAssets(assetsPath)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestWebLauncher.class.getName())
                .setObfuscated(false)
                .addReflectionPattern("com.badlogic.gdx.math.Vector2")
                .build(new File("build/dist"));
    }
}
