import com.badlogic.gdx.Files.FileType;
import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMVisUI {

    public static void main(String[] args) {
        AssetFileHandle assetsPath = new AssetFileHandle("../../../assets");

        new TeaBuilder(new WebBackend().setStartJettyAfterBuild(true))
                .addAssets(assetsPath)
                .addAssets(new AssetFileHandle("com/kotcrab/vis/ui/skin/x1", FileType.Classpath))
                .addAssets(new AssetFileHandle("com/kotcrab/vis/ui/widget/color/internal", FileType.Classpath))
                .addAssets(new AssetFileHandle("com/kotcrab/vis/ui/i18n/ColorPicker", FileType.Classpath))
                .addReflectionClass("com.kotcrab.vis.ui.**Style")
                .addReflectionClass("com.kotcrab.vis.ui.Sizes")
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(TestWebLauncher.class.getName())
                .setObfuscated(false)
                .build(new File("build/dist"));
    }
}
