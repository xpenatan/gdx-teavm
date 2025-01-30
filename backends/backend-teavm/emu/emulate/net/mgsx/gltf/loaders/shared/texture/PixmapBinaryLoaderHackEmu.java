package emulate.net.mgsx.gltf.loaders.shared.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;

@Emulate(valueStr = "net.mgsx.gltf.loaders.shared.texture.PixmapBinaryLoaderHack")
public class PixmapBinaryLoaderHackEmu {
    public static Pixmap load(byte [] encodedData, int offset, int len){
        return new Pixmap(encodedData, offset, len);
    }
}