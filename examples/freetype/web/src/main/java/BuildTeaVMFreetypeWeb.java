import com.github.xpenatan.gdx.teavm.backends.shared.config.AssetFileHandle;
import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMFreetypeWeb {

    public static void main(String[] args) {
        boolean wasm = args.length > 0 && "wasm".equalsIgnoreCase(args[0]);

        WebBackend backend = new WebBackend()
                .setWebAssembly(wasm)
                .setStartJettyAfterBuild(true)
                .setHtmlTitle("gdx-teavm FreeType");

        new TeaBuilder(backend)
                .addAssets(new AssetFileHandle("../desktop/assets"))
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(FreetypeTestLauncher.class.getName())
                .setObfuscated(false)
                .build(new File(wasm ? "build/dist/wasm" : "build/dist/web"));
    }
}
