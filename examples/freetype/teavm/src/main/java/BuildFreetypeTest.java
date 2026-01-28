import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.TeaWebBackend;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildFreetypeTest {

    public static void main(String[] args) throws IOException {
        new TeaCompiler(new TeaWebBackend())
                .addAssets(new AssetFileHandle("../desktop/assets"))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(FreetypeTestLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
