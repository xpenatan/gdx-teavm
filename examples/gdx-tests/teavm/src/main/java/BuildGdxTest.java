import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.compiler.TeaCompiler;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import java.io.IOException;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildGdxTest {

    public static void main(String[] args) throws IOException {
        String gdxAssetsPath = args[0];
        System.out.println("gdxAssetsPath: " + gdxAssetsPath);

        new TeaCompiler(new WebBackend()
                .setWebAssembly(true)
                .setStartJettyAfterBuild(true))
                .addAssets(new AssetFileHandle(gdxAssetsPath))
                .addAssets(new AssetFileHandle("../../basic/assets"))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(GdxTestLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
