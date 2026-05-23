package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.teavm.interop.Import;

public final class GdxTeaVMSpriteSubstitution extends Sprite {

    final float[] vertices = null;
    float x;
    float y;
    float width;
    float height;
    float originX;
    float originY;
    float rotation;
    float scaleX = 1;
    float scaleY = 1;
    boolean dirty = true;

    private GdxTeaVMSpriteSubstitution() {
        super();
    }

    @Override
    public void draw(Batch batch) {
        if (batch instanceof GdxTeaVMSpriteBatchSubstitution) {
            ((GdxTeaVMSpriteBatchSubstitution)batch).drawSprite(this);
        }
        else {
            float[] vertices = getVertices();
            batch.draw(getTexture(), vertices, 0, 20);
        }
    }

    public float[] getVertices() {
        if (dirty) {
            updateVerticesNative(this);
        }
        return vertices;
    }

    @Import(name = "teavm_sprite_update_vertices")
    private static native void updateVerticesNative(GdxTeaVMSpriteSubstitution sprite);
}
