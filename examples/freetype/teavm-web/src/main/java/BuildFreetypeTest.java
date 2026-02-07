import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildFreetypeTest {

    public static void main(String[] args) {
        new TeaCompiler(new WebBackend().setStartJettyAfterBuild(true))
                .addAssets(new AssetFileHandle("../desktop/assets"))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(FreetypeTestLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
