package demos;

import com.github.xpenatan.gdx.teavm.backends.psp.types.ScePspFVector3;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPDebugApi;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import org.teavm.interop.Address;
import static com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi.*;

public class PSPCubeTest implements PSPTest {

    private int val = 0;
    private ScePspFVector3 pos;
    private ScePspFVector3 rot;
    private ByteBuffer vertices;
    private Address logo_start; // Texture buffer pointer

    @Override
    public void create() {
        pos = ScePspFVector3.malloc();
        rot = ScePspFVector3.malloc();

        int vertexSize = 6 * 4; // u,v,color,x,y,z
        vertices = ByteBuffer.allocateDirect((12 * 3) * vertexSize);
        vertices.order(ByteOrder.LITTLE_ENDIAN);

        set(vertices, 0, 0, 0xff7f0000,-1,-1, 1); // 0
        set(vertices, 1, 0, 0xff7f0000,-1, 1, 1); // 4
        set(vertices, 1, 1, 0xff7f0000, 1, 1, 1); // 5

        set(vertices, 0, 0, 0xff7f0000,-1,-1, 1); // 0
        set(vertices, 1, 1, 0xff7f0000, 1, 1, 1); // 5
        set(vertices, 0, 1, 0xff7f0000, 1,-1, 1); // 1

        set(vertices, 0, 0, 0xff7f0000,-1,-1,-1); // 3
        set(vertices, 1, 0, 0xff7f0000, 1,-1,-1); // 2
        set(vertices, 1, 1, 0xff7f0000, 1, 1,-1); // 6

        set(vertices, 0, 0, 0xff7f0000,-1,-1,-1); // 3
        set(vertices, 1, 1, 0xff7f0000, 1, 1,-1); // 6
        set(vertices, 0, 1, 0xff7f0000,-1, 1,-1); // 7

        set(vertices, 0, 0, 0xff007f00, 1,-1,-1); // 0
        set(vertices, 1, 0, 0xff007f00, 1,-1, 1); // 3
        set(vertices, 1, 1, 0xff007f00, 1, 1, 1); // 7

        set(vertices, 0, 0, 0xff007f00, 1,-1,-1); // 0
        set(vertices, 1, 1, 0xff007f00, 1, 1, 1); // 7
        set(vertices, 0, 1, 0xff007f00, 1, 1,-1); // 4

        set(vertices, 0, 0, 0xff007f00,-1,-1,-1); // 0
        set(vertices, 1, 0, 0xff007f00,-1, 1,-1); // 3
        set(vertices, 1, 1, 0xff007f00,-1, 1, 1); // 7

        set(vertices, 0, 0, 0xff007f00,-1,-1,-1); // 0
        set(vertices, 1, 1, 0xff007f00,-1, 1, 1); // 7
        set(vertices, 0, 1, 0xff007f00,-1,-1, 1); // 4

        set(vertices, 0, 0, 0xff00007f,-1, 1,-1); // 0
        set(vertices, 1, 0, 0xff00007f, 1, 1,-1); // 1
        set(vertices, 1, 1, 0xff00007f, 1, 1, 1); // 2

        set(vertices, 0, 0, 0xff00007f,-1, 1,-1); // 0
        set(vertices, 1, 1, 0xff00007f, 1, 1, 1); // 2
        set(vertices, 0, 1, 0xff00007f,-1, 1, 1); // 3

        set(vertices, 0, 0, 0xff00007f,-1,-1,-1); // 4
        set(vertices, 1, 0, 0xff00007f,-1,-1, 1); // 7
        set(vertices, 1, 1, 0xff00007f, 1,-1, 1); // 6

        set(vertices, 0, 0, 0xff00007f,-1,-1,-1); // 4
        set(vertices, 1, 1, 0xff00007f, 1,-1, 1); // 6
        set(vertices, 0, 1, 0xff00007f, 1,-1,-1); // 5
    }

    @Override
    public void render() {
        sceGuClearColor(0xFFFFFFFF); // ABGR
        sceGuClearDepth(0);
        sceGuClear(GU_COLOR_BUFFER_BIT | GU_DEPTH_BUFFER_BIT);

        // setup matrices for cube

        sceGumMatrixMode(GU_PROJECTION);
        sceGumLoadIdentity();
        sceGumPerspective(75.0f, 16.0f / 9.0f, 0.5f, 1000.0f);

        sceGumMatrixMode(GU_VIEW);
        sceGumLoadIdentity();

        sceGumMatrixMode(GU_MODEL);
        sceGumLoadIdentity();

        {
            pos = ScePspFVector3.malloc();
            rot = ScePspFVector3.malloc();
            ScePspFVector3.set(pos, 0, 0, -2.5f);
            ScePspFVector3.set(rot, val * 0.79f * (GU_PI / 180.0f), val * 0.98f * (GU_PI / 180.0f), val * 1.32f * (GU_PI / 180.0f));
            sceGumTranslate(pos);
            sceGumRotateXYZ(rot);
        }

        // setup texture

        sceGuTexMode(GU_PSM_4444, 0, 0, 0);
        sceGuTexImage(0, 64, 64, 64, logo_start);
        sceGuTexFunc(GU_TFX_ADD, GU_TCC_RGB);
        sceGuTexEnvColor(0xffff00);
        sceGuTexFilter(GU_LINEAR, GU_LINEAR);
        sceGuTexScale(1.0f, 1.0f);
        sceGuTexOffset(0.0f, 0.0f);
        sceGuAmbientColor(0xffffffff);

        // draw cube

        sceGumDrawArray(GU_TRIANGLES,GU_TEXTURE_32BITF|GU_COLOR_8888|GU_VERTEX_32BITF|GU_TRANSFORM_3D,12*3, null, vertices);
        val++;
    }

    public static void set(ByteBuffer obj, float u, float v, int color, float x, float y, float z) {
        obj.putFloat(u);
        obj.putFloat(v);
        obj.putInt(color);
        obj.putFloat(x);
        obj.putFloat(y);
        obj.putFloat(z);
    }
}