import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMTestDemo {

    public static void main(String[] args) {
        AssetFileHandle assetsPath = new AssetFileHandle("../../../assets");

        new TeaBuilder(new WebBackend().setStartJettyAfterBuild(true))
                .addAssets(assetsPath)
                .addPreservedClass("com.badlogic.gdx.math.Vector2")
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestWebLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
