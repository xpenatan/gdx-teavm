package com.github.xpenatan.gdx.teavm.backends.glfw.config.plugins.substitutions;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import org.teavm.interop.Address;

public final class GdxTeaVMSpriteBatchSubstitution extends SpriteBatch {

    final float[] vertices = null;
    int idx;
    Texture lastTexture;
    boolean drawing;

    private GdxTeaVMSpriteBatchSubstitution() {
        super(1);
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
            dstAddress.add(0).putFloat(srcAddress.add(0).getFloat());
            dstAddress.add(4).putFloat(srcAddress.add(4).getFloat());
            dstAddress.add(8).putFloat(srcAddress.add(8).getFloat());
            dstAddress.add(12).putFloat(srcAddress.add(12).getFloat());
            dstAddress.add(16).putFloat(srcAddress.add(16).getFloat());
            dstAddress.add(20).putFloat(srcAddress.add(20).getFloat());
            dstAddress.add(24).putFloat(srcAddress.add(24).getFloat());
            dstAddress.add(28).putFloat(srcAddress.add(28).getFloat());
            dstAddress.add(32).putFloat(srcAddress.add(32).getFloat());
            dstAddress.add(36).putFloat(srcAddress.add(36).getFloat());
            dstAddress.add(40).putFloat(srcAddress.add(40).getFloat());
            dstAddress.add(44).putFloat(srcAddress.add(44).getFloat());
            dstAddress.add(48).putFloat(srcAddress.add(48).getFloat());
            dstAddress.add(52).putFloat(srcAddress.add(52).getFloat());
            dstAddress.add(56).putFloat(srcAddress.add(56).getFloat());
            dstAddress.add(60).putFloat(srcAddress.add(60).getFloat());
            dstAddress.add(64).putFloat(srcAddress.add(64).getFloat());
            dstAddress.add(68).putFloat(srcAddress.add(68).getFloat());
            dstAddress.add(72).putFloat(srcAddress.add(72).getFloat());
            dstAddress.add(76).putFloat(srcAddress.add(76).getFloat());
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
