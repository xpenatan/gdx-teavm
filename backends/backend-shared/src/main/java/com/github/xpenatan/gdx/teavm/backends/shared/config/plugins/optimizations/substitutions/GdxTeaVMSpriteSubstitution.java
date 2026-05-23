package com.github.xpenatan.gdx.teavm.backends.shared.config.plugins.optimizations.substitutions;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import org.teavm.interop.Address;

public final class GdxTeaVMSpriteSubstitution extends Sprite {

    private static final int X1 = 0;
    private static final int Y1 = 4;
    private static final int X2 = 20;
    private static final int Y2 = 24;
    private static final int X3 = 40;
    private static final int Y3 = 44;
    private static final int X4 = 60;
    private static final int Y4 = 64;

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
            dirty = false;

            float[] vertices = this.vertices;
            float localX = -originX;
            float localY = -originY;
            float localX2 = localX + width;
            float localY2 = localY + height;
            float worldOriginX = x - localX;
            float worldOriginY = y - localY;
            float scaleX = this.scaleX;
            float scaleY = this.scaleY;
            if (scaleX != 1 || scaleY != 1) {
                localX *= scaleX;
                localY *= scaleY;
                localX2 *= scaleX;
                localY2 *= scaleY;
            }

            Address out = Address.ofData(vertices);
            float rotation = this.rotation;
            if (rotation != 0) {
                Address sinTable = Address.ofData(GdxTeaVMFastMath.SIN_TABLE);
                float cos = sinTable.add((((int)((rotation + 90) * GdxTeaVMFastMath.DEG_TO_INDEX))
                        & GdxTeaVMFastMath.SIN_MASK) * 4).getFloat();
                float sin = sinTable.add((((int)(rotation * GdxTeaVMFastMath.DEG_TO_INDEX))
                        & GdxTeaVMFastMath.SIN_MASK) * 4).getFloat();
                float localXCos = localX * cos;
                float localXSin = localX * sin;
                float localYCos = localY * cos;
                float localYSin = localY * sin;
                float localX2Cos = localX2 * cos;
                float localX2Sin = localX2 * sin;
                float localY2Cos = localY2 * cos;
                float localY2Sin = localY2 * sin;

                float x1 = localXCos - localYSin + worldOriginX;
                float y1 = localYCos + localXSin + worldOriginY;
                out.add(X1).putFloat(x1);
                out.add(Y1).putFloat(y1);

                float x2 = localXCos - localY2Sin + worldOriginX;
                float y2 = localY2Cos + localXSin + worldOriginY;
                out.add(X2).putFloat(x2);
                out.add(Y2).putFloat(y2);

                float x3 = localX2Cos - localY2Sin + worldOriginX;
                float y3 = localY2Cos + localX2Sin + worldOriginY;
                out.add(X3).putFloat(x3);
                out.add(Y3).putFloat(y3);

                out.add(X4).putFloat(x1 + (x3 - x2));
                out.add(Y4).putFloat(y3 - (y2 - y1));
            }
            else {
                float x1 = localX + worldOriginX;
                float y1 = localY + worldOriginY;
                float x2 = localX2 + worldOriginX;
                float y2 = localY2 + worldOriginY;

                out.add(X1).putFloat(x1);
                out.add(Y1).putFloat(y1);
                out.add(X2).putFloat(x1);
                out.add(Y2).putFloat(y2);
                out.add(X3).putFloat(x2);
                out.add(Y3).putFloat(y2);
                out.add(X4).putFloat(x2);
                out.add(Y4).putFloat(y1);
            }
        }
        return vertices;
    }
}
