package demos;

import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PSPShapeTest implements PSPTest {

    int vertexSize = (2 + 3) * Float.BYTES; // UV + Position

    ByteBuffer vertices;

    void drawRect(float x, float y, float w, float h) {
        vertices.position(0);
        vertices.putFloat(0);
        vertices.putFloat(0);
        vertices.putFloat(x);
        vertices.putFloat(y);
        vertices.putFloat(0);

        vertices.putFloat(0);
        vertices.putFloat(0);
        vertices.putFloat((x + w));
        vertices.putFloat((y + h));
        vertices.putFloat(0);

        int vType = PSPGraphicsApi.GU_TEXTURE_32BITF | PSPGraphicsApi.GU_VERTEX_32BITF | PSPGraphicsApi.GU_TRANSFORM_2D;
        PSPGraphicsApi.sceGuColor(0xFF0000FF); // Red, colors are ABGR
        PSPGraphicsApi.sceGuDrawArray(PSPGraphicsApi.GU_SPRITES, vType, 2, null, vertices);
    }

    @Override
    public void create() {
        vertices  = ByteBuffer.allocateDirect(2 * vertexSize);
        vertices.order(ByteOrder.LITTLE_ENDIAN);
    }

    @Override
    public void render() {
        PSPGraphicsApi.sceGuClearColor(0xFF00FF00);
//        PSPGraphicsApi.sceGuClear(PSPGraphicsApi.GU_COLOR_BUFFER_BIT | PSPGraphicsApi.GU_DEPTH_BUFFER_BIT);
        PSPGraphicsApi.sceGuClear(PSPGraphicsApi.GU_COLOR_BUFFER_BIT);
        drawRect(216, 96, 34, 64);
    }
}