import com.github.xpenatan.gdx.teavm.backends.shared.config.builder.TeaBuilder;
import com.github.xpenatan.gdx.teavm.backends.web.config.backend.WebBackend;
import java.io.File;
import org.teavm.vm.TeaVMOptimizationLevel;

public class BuildTeaVMWebSocketsWeb {

    public static void main(String[] args) {
        boolean wasm = args.length > 0 && "wasm".equalsIgnoreCase(args[0]);

        WebBackend backend = new WebBackend()
                .setWebAssembly(wasm)
                .setStartJettyAfterBuild(true)
                .setHtmlTitle("gdx-teavm websockets");

        new TeaBuilder(backend)
                .setOptimizationLevel(TeaVMOptimizationLevel.SIMPLE)
                .setMainClass(WebSocketsWebLauncher.class.getName())
                .setObfuscated(false)
                .build(new File(wasm ? "build/dist/wasm" : "build/dist/web"));
    }
}
