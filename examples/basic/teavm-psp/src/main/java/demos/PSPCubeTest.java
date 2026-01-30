package demos;

import com.github.xpenatan.gdx.teavm.backends.psp.types.ScePspFQuaternion;
import com.github.xpenatan.gdx.teavm.backends.psp.types.ScePspFVector3;
import com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPDebugApi;
import org.teavm.backend.c.runtime.Memory;
import org.teavm.interop.Address;
import org.teavm.interop.Structure;
import static com.github.xpenatan.gdx.teavm.backends.psp.utils.PSPGraphicsApi.*;

public class PSPCubeTest implements PSPTest {

    int val = 0;

    Address logo_start; // Texture buffer pointer

//    Vertex[] vertices;

    @Override
    public void create() {
//        vertices = new Vertex[12*3];
//        int i=0;
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff7f0000,-1,-1, 1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 0, 0xff7f0000,-1, 1, 1); // 4
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff7f0000, 1, 1, 1); // 5
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff7f0000,-1,-1, 1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff7f0000, 1, 1, 1); // 5
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 1, 0xff7f0000, 1,-1, 1); // 1
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff7f0000,-1,-1,-1); // 3
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 0, 0xff7f0000, 1,-1,-1); // 2
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff7f0000, 1, 1,-1); // 6
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff7f0000,-1,-1,-1); // 3
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff7f0000, 1, 1,-1); // 6
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 1, 0xff7f0000,-1, 1,-1); // 7
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff007f00, 1,-1,-1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 0, 0xff007f00, 1,-1, 1); // 3
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff007f00, 1, 1, 1); // 7
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff007f00, 1,-1,-1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff007f00, 1, 1, 1); // 7
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 1, 0xff007f00, 1, 1,-1); // 4
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff007f00,-1,-1,-1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 0, 0xff007f00,-1, 1,-1); // 3
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff007f00,-1, 1, 1); // 7
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff007f00,-1,-1,-1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff007f00,-1, 1, 1); // 7
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 1, 0xff007f00,-1,-1, 1); // 4
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff00007f,-1, 1,-1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 0, 0xff00007f, 1, 1,-1); // 1
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff00007f, 1, 1, 1); // 2
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff00007f,-1, 1,-1); // 0
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff00007f, 1, 1, 1); // 2
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 1, 0xff00007f,-1, 1, 1); // 3
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff00007f,-1,-1,-1); // 4
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 0, 0xff00007f,-1,-1, 1); // 7
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff00007f, 1,-1, 1); // 6
//
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 0, 0xff00007f,-1,-1,-1); // 4
//        vertices[i++] = Vertex.set(Vertex.malloc(), 1, 1, 0xff00007f, 1,-1, 1); // 6
//        vertices[i++] = Vertex.set(Vertex.malloc(), 0, 1, 0xff00007f, 1,-1,-1); // 5
    }

    @Override
    public void render() {
        sceGuClearColor(0xff554433);
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
//            ScePspFVector3 pos = ScePspFVector3.malloc();
//            ScePspFVector3 rot = ScePspFVector3.malloc();
//            ScePspFVector3.set(pos, 0, 0, -2.5f);
//            ScePspFVector3.set(rot, val * 0.79f * (GU_PI / 180.0f), val * 0.98f * (GU_PI / 180.0f), val * 1.32f * (GU_PI / 180.0f));
//            sceGumTranslate(pos.toAddress());
//            sceGumRotateXYZ(rot.toAddress());

            PSPDebugApi.rotateCube(val);
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

//        Address address = Address.ofData(vertices);
//        Address address = PSPDebugApi.getVertices();
//        sceGumDrawArray(GU_TRIANGLES,GU_TEXTURE_32BITF|GU_COLOR_8888|GU_VERTEX_32BITF|GU_TRANSFORM_3D,12*3, null, address);
        PSPDebugApi.drawArray2(GU_TRIANGLES,GU_TEXTURE_32BITF|GU_COLOR_8888|GU_VERTEX_32BITF|GU_TRANSFORM_3D,12*3, null);

//        PSPDebugApi.drawArray();
        val++;
    }

    public static class Vertex extends Structure {
        public float u, v;
        public int color;
        public float x,y,z;

        public static Vertex set(Vertex obj, float u, float v, int color, float x, float y, float z) {
            obj.u = u;
            obj.v = v;
            obj.color = color;
            obj.x = x;
            obj.y = y;
            obj.z = z;
            return obj;
        }

        public static Vertex malloc() {
            return Memory.malloc(sizeOf(Vertex.class)).toStructure();
        }
    }
}