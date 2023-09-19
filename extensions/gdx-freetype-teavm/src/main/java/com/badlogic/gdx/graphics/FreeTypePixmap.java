package com.badlogic.gdx.graphics;

import com.github.xpenatan.gdx.backends.teavm.dom.CanvasRenderingContext2DWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.ArrayBufferViewWrapper;
import java.nio.ByteBuffer;
import org.teavm.jso.JSBody;

/**
 * @author Simon Gerst
 */
public class FreeTypePixmap extends PixmapEmu {

    ByteBuffer buffer;

    public FreeTypePixmap(int width, int height, PixmapEmu.FormatEmu format) {
        super(width, height, format);
    }

    public void setPixelsNull() {
        pixels = null;
    }

    public static ByteBuffer getRealPixels(PixmapEmu pixmap) {
        if(pixmap.getWidth() == 0 || pixmap.getHeight() == 0) {
            return FreeTypeUtil.newDirectReadWriteByteBuffer();
        }
        if(pixmap.pixels == null) {
            pixmap.pixels = pixmap.getContext().getImageData(0, 0, pixmap.getWidth(), pixmap.getHeight()).getData();
            pixmap.buffer = FreeTypeUtil.newDirectReadWriteByteBuffer(pixmap.pixels);
            return pixmap.buffer;
        }
        return pixmap.buffer;
    }

    public static void putPixelsBack(PixmapEmu pixmap, ByteBuffer pixels) {
        if(pixmap.getWidth() == 0 || pixmap.getHeight() == 0) return;
        ArrayBufferViewWrapper typedArray = FreeTypeUtil.getTypedArray(pixels);
        putPixelsBack(typedArray, pixmap.getWidth(), pixmap.getHeight(), pixmap.getContext());
    }

    @JSBody(params = {"pixels", "width", "height", "ctx"}, script = "" +
            "var imgData = ctx.createImageData(width, height);" +
            "var data = imgData.data;" +
            "for (var i = 0, len = width * height * 4; i < len; i++) {" +
            "data[i] = pixels[i] & 0xff;" +
            "}" +
            "ctx.putImageData(imgData, 0, 0);")
    private static native void putPixelsBack(ArrayBufferViewWrapper pixels, int width, int height, CanvasRenderingContext2DWrapper ctx);
}