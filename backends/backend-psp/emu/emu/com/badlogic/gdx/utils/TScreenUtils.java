package emu.com.badlogic.gdx.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.github.xpenatan.gdx.teavm.backends.psp.natives.PSPGraphicsApi;

public final class TScreenUtils {

    public static void clear(Color color) {
        clear(color.r, color.g, color.b, color.a, false);
    }

    public static void clear(float r, float g, float b, float a) {
        clear(r, g, b, a, false);
    }

    public static void clear(Color color, boolean clearDepth) {
        clear(color.r, color.g, color.b, color.a, clearDepth);
    }

    public static void clear(float r, float g, float b, float a, boolean clearDepth) {
        clear(r, g, b, a, clearDepth, false);
    }

    public static void clear(float r, float g, float b, float a, boolean clearDepth, boolean applyAntialiasing) {
        int mask = PSPGraphicsApi.GU_COLOR_BUFFER_BIT;
        if(clearDepth) mask = mask | PSPGraphicsApi.GU_DEPTH_BUFFER_BIT;
        int color = Color.argb8888(a, b, g, r); // Inverted. ABGR
        PSPGraphicsApi.sceGuClearColor(color);
        PSPGraphicsApi.sceGuClear(mask);
    }

    public static TextureRegion getFrameBufferTexture() {
        final int w = Gdx.graphics.getBackBufferWidth();
        final int h = Gdx.graphics.getBackBufferHeight();
        return getFrameBufferTexture(0, 0, w, h);
    }

    public static TextureRegion getFrameBufferTexture(int x, int y, int w, int h) {
//        final int potW = MathUtils.nextPowerOfTwo(w);
//        final int potH = MathUtils.nextPowerOfTwo(h);
//
//        final Pixmap pixmap = Pixmap.createFromFrameBuffer(x, y, w, h);
//        final Pixmap potPixmap = new Pixmap(potW, potH, Format.RGBA8888);
//        potPixmap.setBlending(Blending.None);
//        potPixmap.drawPixmap(pixmap, 0, 0);
//        Texture texture = new Texture(potPixmap);
//        TextureRegion textureRegion = new TextureRegion(texture, 0, h, w, -h);
//        potPixmap.dispose();
//        pixmap.dispose();
//
//        return textureRegion;
        return null; //TODO WIP
    }

    @Deprecated
    public static Pixmap getFrameBufferPixmap(int x, int y, int w, int h) {
        return Pixmap.createFromFrameBuffer(x, y, w, h);
    }

    public static byte[] getFrameBufferPixels(boolean flipY) {
        final int w = Gdx.graphics.getBackBufferWidth();
        final int h = Gdx.graphics.getBackBufferHeight();
        return getFrameBufferPixels(0, 0, w, h, flipY);
    }

    public static byte[] getFrameBufferPixels(int x, int y, int w, int h, boolean flipY) {
        final int numBytes = w * h * 4;
//        Gdx.gl.glPixelStorei(GL20.GL_PACK_ALIGNMENT, 1);
//        final ByteBuffer pixels = BufferUtils.newByteBuffer(w * h * 4);
//        Gdx.gl.glReadPixels(x, y, w, h, GL20.GL_RGBA, GL20.GL_UNSIGNED_BYTE, pixels);
//        byte[] lines = new byte[numBytes];
//        if(flipY) {
//            final int numBytesPerLine = w * 4;
//            for(int i = 0; i < h; i++) {
//                ((Buffer)pixels).position((h - i - 1) * numBytesPerLine);
//                pixels.get(lines, i * numBytesPerLine, numBytesPerLine);
//            }
//        }
//        else {
//            ((Buffer)pixels).clear();
//            pixels.get(lines);
//        }
//        return lines;
        return null;
    }
}