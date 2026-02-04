package emu.net.mgsx.gltf.loaders.shared.texture;

import com.badlogic.gdx.graphics.Pixmap;

public class TPixmapBinaryLoaderHack {
    public static Pixmap load(byte [] encodedData, int offset, int len){
        return new Pixmap(encodedData, offset, len);
    }
}