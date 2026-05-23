package com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins.substitutions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.teavm.interop.Address;
import org.teavm.interop.Import;

public final class GdxTeaVMSpriteBatchSubstitution extends SpriteBatch {

    private static final int SPRITE_SIZE_BYTES = 20 * 4;

    final float[] vertices = null;
    int idx;
    float colorPacked;
    Texture lastTexture;
    boolean drawing;

    private GdxTeaVMSpriteBatchSubstitution() {
        super(1);
    }

    public void drawSprite(GdxTeaVMSpriteSubstitution sprite) {
        drawSpriteNative(this, sprite);
    }

    @Import(name = "gdx_teavm_spritebatch_draw_sprite")
    private static native void drawSpriteNative(GdxTeaVMSpriteBatchSubstitution batch,
            GdxTeaVMSpriteSubstitution sprite);

    @Import(name = "gdx_teavm_spritebatch_draw_sprite_array")
    public static native void drawSpriteArrayNative(GdxTeaVMSpriteBatchSubstitution batch,
            GdxTeaVMSpriteSubstitution[] sprites, int count, float rotationDelta, float scale);

    @Override
    public void draw(Texture texture, float x, float y, float originX, float originY, float width, float height,
            float scaleX, float scaleY, float rotation, int srcX, int srcY, int srcWidth, int srcHeight,
            boolean flipX, boolean flipY) {
        drawTextureTransformNative(this, texture, x, y, originX, originY, width, height, scaleX, scaleY, rotation,
                srcX, srcY, srcWidth, srcHeight, flipX, flipY);
    }

    @Import(name = "gdx_teavm_spritebatch_draw_texture_transform")
    private static native void drawTextureTransformNative(GdxTeaVMSpriteBatchSubstitution batch, Texture texture,
            float x, float y, float originX, float originY, float width, float height, float scaleX, float scaleY,
            float rotation, int srcX, int srcY, int srcWidth, int srcHeight, boolean flipX, boolean flipY);

    @Override
    public void draw(Texture texture, float x, float y, float width, float height) {
        drawTextureRectNative(this, texture, x, y, width, height);
    }

    @Import(name = "gdx_teavm_spritebatch_draw_texture_rect")
    private static native void drawTextureRectNative(GdxTeaVMSpriteBatchSubstitution batch, Texture texture,
            float x, float y, float width, float height);

    public void drawSpriteVertices(Texture texture, float[] spriteVertices) {
        if (!drawing)
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        float[] vertices = this.vertices;
        int currentIdx = idx;
        if (texture != lastTexture) {
            switchTexture(texture);
            currentIdx = idx;
        }
        if (currentIdx > vertices.length - 20) {
            flush();
            currentIdx = idx;
        }
        Address srcAddress = Address.ofData(spriteVertices);
        Address dstAddress = Address.ofData(vertices).add(currentIdx * 4);
        Address.moveMemoryBlock(srcAddress, dstAddress, SPRITE_SIZE_BYTES);
        idx = currentIdx + 20;
    }

    @Override
    public void draw(Texture texture, float[] spriteVertices, int offset, int count) {
        if (!drawing)
            throw new IllegalStateException("SpriteBatch.begin must be called before draw.");
        if (spriteVertices == null)
            throw new NullPointerException("spriteVertices");
        if (offset == 0 && count == 20 && spriteVertices.length >= 20) {
            float[] vertices = this.vertices;
            int currentIdx = idx;
            if (texture != lastTexture) {
                switchTexture(texture);
                currentIdx = idx;
            }
            if (currentIdx > vertices.length - 20) {
                flush();
                currentIdx = idx;
            }
            Address srcAddress = Address.ofData(spriteVertices);
            Address dstAddress = Address.ofData(vertices).add(currentIdx * 4);
            Address.moveMemoryBlock(srcAddress, dstAddress, SPRITE_SIZE_BYTES);
            idx = currentIdx + 20;
            return;
        }
        if (offset < 0 || count < 0 || offset > spriteVertices.length - count)
            throw new IndexOutOfBoundsException();

        int verticesLength = vertices.length;
        int remainingVertices = verticesLength;
        int currentIdx = idx;
        if (texture != lastTexture) {
            switchTexture(texture);
            currentIdx = idx;
        }
        else {
            remainingVertices -= currentIdx;
            if (remainingVertices == 0) {
                flush();
                remainingVertices = verticesLength;
                currentIdx = idx;
            }
        }

        int copyCount = remainingVertices < count ? remainingVertices : count;
        if (copyCount > 0) {
            GdxTeaVMFloatArrayCopy.copy(spriteVertices, offset, vertices, currentIdx, copyCount);
            currentIdx += copyCount;
            idx = currentIdx;
            count -= copyCount;
        }

        while (count > 0) {
            offset += copyCount;
            flush();
            copyCount = verticesLength < count ? verticesLength : count;
            GdxTeaVMFloatArrayCopy.copy(spriteVertices, offset, vertices, 0, copyCount);
            idx = copyCount;
            count -= copyCount;
        }
    }
}
