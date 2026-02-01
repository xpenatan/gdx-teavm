package com.github.xpenatan.gdx.teavm.backends.psp.natives;

import com.github.xpenatan.gdx.teavm.backends.psp.natives.types.ScePspFMatrix4;
import com.github.xpenatan.gdx.teavm.backends.psp.natives.types.ScePspFQuaternion;
import com.github.xpenatan.gdx.teavm.backends.psp.natives.types.ScePspFVector3;
import com.github.xpenatan.gdx.teavm.backends.psp.natives.types.ScePspIMatrix4;
import java.nio.ByteBuffer;
import org.teavm.interop.Address;
import org.teavm.interop.Function;
import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("PSPGraphicsApi.h")
public class PSPGraphicsApi {

    // pspgu.h

    /* PI, float-sized */
    public static final float GU_PI = 3.141593f;

    /* Boolean values for convenience */
    public static final int GU_FALSE = 0;
    public static final int GU_TRUE = 1;

    /* Primitive types */
    public static final int GU_POINTS = 0;
    public static final int GU_LINES = 1;
    public static final int GU_LINE_STRIP = 2;
    public static final int GU_TRIANGLES = 3;
    public static final int GU_TRIANGLE_STRIP = 4;
    public static final int GU_TRIANGLE_FAN = 5;
    public static final int GU_SPRITES = 6;

    /* States */
    public static final int GU_ALPHA_TEST = 0;
    public static final int GU_DEPTH_TEST = 1;
    public static final int GU_SCISSOR_TEST = 2;
    public static final int GU_STENCIL_TEST = 3;
    public static final int GU_BLEND = 4;
    public static final int GU_CULL_FACE = 5;
    public static final int GU_DITHER = 6;
    public static final int GU_FOG = 7;
    public static final int GU_CLIP_PLANES = 8;
    public static final int GU_TEXTURE_2D = 9;
    public static final int GU_LIGHTING = 10;
    public static final int GU_LIGHT0 = 11;
    public static final int GU_LIGHT1 = 12;
    public static final int GU_LIGHT2 = 13;
    public static final int GU_LIGHT3 = 14;
    public static final int GU_LINE_SMOOTH = 15;
    public static final int GU_PATCH_CULL_FACE = 16;
    public static final int GU_COLOR_TEST = 17;
    public static final int GU_COLOR_LOGIC_OP = 18;
    public static final int GU_FACE_NORMAL_REVERSE = 19;
    public static final int GU_PATCH_FACE = 20;
    public static final int GU_FRAGMENT_2X = 21;
    public static final int GU_MAX_STATUS = 22;

    /* Matrix modes */
    public static final int GU_PROJECTION = 0;
    public static final int GU_VIEW = 1;
    public static final int GU_MODEL = 2;
    public static final int GU_TEXTURE = 3;

    /* Vertex Declarations Begin */
    public static final int GU_TEXTURE_8BIT = 1 << 0;
    public static final int GU_TEXTURE_16BIT = 2 << 0;
    public static final int GU_TEXTURE_32BITF = 3 << 0;
    public static final int GU_TEXTURE_BITS = 3 << 0;

    public static final int GU_COLOR_5650 = 4 << 2;
    public static final int GU_COLOR_5551 = 5 << 2;
    public static final int GU_COLOR_4444 = 6 << 2;
    public static final int GU_COLOR_8888 = 7 << 2;
    public static final int GU_COLOR_BITS = 7 << 2;

    public static final int GU_NORMAL_8BIT = 1 << 5;
    public static final int GU_NORMAL_16BIT = 2 << 5;
    public static final int GU_NORMAL_32BITF = 3 << 5;
    public static final int GU_NORMAL_BITS = 3 << 5;

    public static final int GU_VERTEX_8BIT = 1 << 7;
    public static final int GU_VERTEX_16BIT = 2 << 7;
    public static final int GU_VERTEX_32BITF = 3 << 7;
    public static final int GU_VERTEX_BITS = 3 << 7;

    public static final int GU_WEIGHT_8BIT = 1 << 9;
    public static final int GU_WEIGHT_16BIT = 2 << 9;
    public static final int GU_WEIGHT_32BITF = 3 << 9;
    public static final int GU_WEIGHT_BITS = 3 << 9;

    public static final int GU_INDEX_8BIT = 1 << 11;
    public static final int GU_INDEX_16BIT = 2 << 11;
    public static final int GU_INDEX_BITS = 3 << 11;

    public static final int GU_WEIGHTS_BITS = 7 << 14;
    public static final int GU_VERTICES_BITS = 7 << 18;

    public static final int GU_TRANSFORM_3D = 0 << 23;
    public static final int GU_TRANSFORM_2D = 1 << 23;
    public static final int GU_TRANSFORM_BITS = 1 << 23;
    /* Vertex Declarations End */

    /* display ON/OFF switch */
    public static final int GU_DISPLAY_OFF = 0;
    public static final int GU_DISPLAY_ON = 1;

    /* screen size */
    public static final int GU_SCR_WIDTH = 480;
    public static final int GU_SCR_HEIGHT = 272;
    public static final float GU_SCR_ASPECT = ((float)GU_SCR_WIDTH / (float)GU_SCR_HEIGHT);
    public static final int GU_SCR_OFFSETX = ((4096 - GU_SCR_WIDTH) / 2);
    public static final int GU_SCR_OFFSETY = ((4096 - GU_SCR_HEIGHT) / 2);

    /* Frame buffer */
    public static final int GU_VRAM_TOP = 0x00000000;
    public static final int GU_VRAM_WIDTH = 512;
    /* 16bit mode */
    public static final int GU_VRAM_BUFSIZE = (GU_VRAM_WIDTH*GU_SCR_HEIGHT*2);
    public static final Address GU_VRAM_BP_0 = Address.fromInt(GU_VRAM_TOP);
    public static final Address GU_VRAM_BP_1 = Address.fromInt(GU_VRAM_TOP+GU_VRAM_BUFSIZE);
    public static final Address GU_VRAM_BP_2 = Address.fromInt(GU_VRAM_TOP+(GU_VRAM_BUFSIZE*2));
    /* 32bit mode */
    public static final int GU_VRAM_BUFSIZE32 = (GU_VRAM_WIDTH*GU_SCR_HEIGHT*4);
    public static final Address GU_VRAM_BP32_0 = Address.fromInt(GU_VRAM_TOP);
    public static final Address GU_VRAM_BP32_1 = Address.fromInt(GU_VRAM_TOP+GU_VRAM_BUFSIZE32);
    public static final Address GU_VRAM_BP32_2 = Address.fromInt(GU_VRAM_TOP+(GU_VRAM_BUFSIZE32*2));

    /* Pixel Formats */
    public static final int GU_PSM_5650 = 0; /* Display, Texture, Palette */
    public static final int GU_PSM_5551 = 1; /* Display, Texture, Palette */
    public static final int GU_PSM_4444 = 2; /* Display, Texture, Palette */
    public static final int GU_PSM_8888 = 3; /* Display, Texture, Palette */
    public static final int GU_PSM_T4 = 4; /* Texture */
    public static final int GU_PSM_T8 = 5; /* Texture */
    public static final int GU_PSM_T16 = 6; /* Texture */
    public static final int GU_PSM_T32 = 7; /* Texture */
    public static final int GU_PSM_DXT1 = 8; /* Texture */
    public static final int GU_PSM_DXT3 = 9; /* Texture */
    public static final int GU_PSM_DXT5 = 10; /* Texture */

    /* Spline Mode */
    public static final int GU_FILL_FILL = 0;
    public static final int GU_OPEN_FILL = 1;
    public static final int GU_FILL_OPEN = 2;
    public static final int GU_OPEN_OPEN = 3;

    /* Shading Model */
    public static final int GU_FLAT = 0;
    public static final int GU_SMOOTH = 1;

    /* Logical operation */
    public static final int GU_CLEAR = 0;
    public static final int GU_AND = 1;
    public static final int GU_AND_REVERSE = 2;
    public static final int GU_COPY = 3;
    public static final int GU_AND_INVERTED = 4;
    public static final int GU_NOOP = 5;
    public static final int GU_XOR = 6;
    public static final int GU_OR = 7;
    public static final int GU_NOR = 8;
    public static final int GU_EQUIV = 9;
    public static final int GU_INVERTED = 10;
    public static final int GU_OR_REVERSE = 11;
    public static final int GU_COPY_INVERTED = 12;
    public static final int GU_OR_INVERTED = 13;
    public static final int GU_NAND = 14;
    public static final int GU_SET = 15;

    /* Texture Filter */
    public static final int GU_NEAREST = 0;
    public static final int GU_LINEAR = 1;
    public static final int GU_NEAREST_MIPMAP_NEAREST = 4;
    public static final int GU_LINEAR_MIPMAP_NEAREST = 5;
    public static final int GU_NEAREST_MIPMAP_LINEAR = 6;
    public static final int GU_LINEAR_MIPMAP_LINEAR = 7;

    /* Texture Map Mode */
    public static final int GU_TEXTURE_COORDS = 0;
    public static final int GU_TEXTURE_MATRIX = 1;
    public static final int GU_ENVIRONMENT_MAP = 2;

    /* Texture Level Mode */
    public static final int GU_TEXTURE_AUTO = 0;
    public static final int GU_TEXTURE_CONST = 1;
    public static final int GU_TEXTURE_SLOPE = 2;

    /* Texture Projection Map Mode */
    public static final int GU_POSITION = 0;
    public static final int GU_UV = 1;
    public static final int GU_NORMALIZED_NORMAL = 2;
    public static final int GU_NORMAL = 3;

    /* Wrap Mode */
    public static final int GU_REPEAT = 0;
    public static final int GU_CLAMP = 1;

    /* Front Face Direction */
    public static final int GU_CW = 0;
    public static final int GU_CCW = 1;

    /* Test Function */
    public static final int GU_NEVER = 0;
    public static final int GU_ALWAYS = 1;
    public static final int GU_EQUAL = 2;
    public static final int GU_NOTEQUAL = 3;
    public static final int GU_LESS = 4;
    public static final int GU_LEQUAL = 5;
    public static final int GU_GREATER = 6;
    public static final int GU_GEQUAL = 7;

    /* Clear Buffer Mask */
    public static final int GU_COLOR_BUFFER_BIT = 1;
    public static final int GU_STENCIL_BUFFER_BIT = 2;
    public static final int GU_DEPTH_BUFFER_BIT = 4;
    public static final int GU_FAST_CLEAR_BIT = 16;

    /* Texture Effect */
    public static final int GU_TFX_MODULATE = 0;
    public static final int GU_TFX_DECAL = 1;
    public static final int GU_TFX_BLEND = 2;
    public static final int GU_TFX_REPLACE = 3;
    public static final int GU_TFX_ADD = 4;

    /* Texture Color Component */
    public static final int GU_TCC_RGB = 0;
    public static final int GU_TCC_RGBA = 1;

    /* Blending Op */
    public static final int GU_ADD = 0;
    public static final int GU_SUBTRACT = 1;
    public static final int GU_REVERSE_SUBTRACT = 2;
    public static final int GU_MIN = 3;
    public static final int GU_MAX = 4;
    public static final int GU_ABS = 5;

    /* Blending Factor */
    public static final int GU_OTHER_COLOR = 0;
    public static final int GU_ONE_MINUS_OTHER_COLOR = 1;
    public static final int GU_SRC_ALPHA = 2;
    public static final int GU_ONE_MINUS_SRC_ALPHA = 3;
    public static final int GU_DST_ALPHA = 4;
    public static final int GU_ONE_MINUS_DST_ALPHA = 5;
    public static final int GU_DOUBLE_SRC_ALPHA = 6;
    public static final int GU_ONE_MINUS_DOUBLE_SRC_ALPHA = 7;
    public static final int GU_DOUBLE_DST_ALPHA = 8;
    public static final int GU_ONE_MINUS_DOUBLE_DST_ALPHA = 9;
    public static final int GU_FIX = 10; /* Note: behavior of 11-15 blend factors is identical to GU_FIX */
    public static final int GU_SRC_COLOR = 0; /* Deprecated */
    public static final int GU_ONE_MINUS_SRC_COLOR = 1; /* Deprecated */
    public static final int GU_DST_COLOR = 0; /* Deprecated */
    public static final int GU_ONE_MINUS_DST_COLOR = 1; /* Deprecated */

    /* Stencil Operations */
    public static final int GU_KEEP = 0;
    public static final int GU_ZERO = 1;
    public static final int GU_REPLACE = 2;
    public static final int GU_INVERT = 3;
    public static final int GU_INCR = 4;
    public static final int GU_DECR = 5;

    /* Light Components */
    public static final int GU_AMBIENT = 1;
    public static final int GU_DIFFUSE = 2;
    public static final int GU_SPECULAR = 4;
    public static final int GU_AMBIENT_AND_DIFFUSE = (GU_AMBIENT|GU_DIFFUSE);
    public static final int GU_DIFFUSE_AND_SPECULAR = (GU_DIFFUSE|GU_SPECULAR);
    public static final int GU_POWERED_DIFFUSE = 8;

    /* Light modes */
    public static final int GU_SINGLE_COLOR = 0;
    public static final int GU_SEPARATE_SPECULAR_COLOR = 1;

    /* Light Type */
    public static final int GU_DIRECTIONAL = 0;
    public static final int GU_POINTLIGHT = 1;
    public static final int GU_SPOTLIGHT = 2;

    /* Contexts */
    public static final int GU_DIRECT = 0;
    public static final int GU_CALL = 1;
    public static final int GU_SEND = 2;

    /* List Queue */
    public static final int GU_TAIL = 0;
    public static final int GU_HEAD = 1;

    /* Sync behavior (mode) */
    public static final int GU_SYNC_FINISH = 0;
    public static final int GU_SYNC_SIGNAL = 1;
    public static final int GU_SYNC_DONE = 2;
    public static final int GU_SYNC_LIST = 3;
    public static final int GU_SYNC_SEND = 4;

    /* behavior (what) */
    public static final int GU_SYNC_WAIT = 0;
    public static final int GU_SYNC_NOWAIT = 1;

    /* Sync behavior (what) [see pspge.h] */
    public static final int GU_SYNC_WHAT_DONE = 0;
    public static final int GU_SYNC_WHAT_QUEUED = 1;
    public static final int GU_SYNC_WHAT_DRAW = 2;
    public static final int GU_SYNC_WHAT_STALL = 3;
    public static final int GU_SYNC_WHAT_CANCEL = 4;

    /* Call mode */
    public static final int GU_CALL_NORMAL = 0;
    public static final int GU_CALL_SIGNAL = 1;

    /* Signal models */
    public static final int GU_SIGNAL_WAIT = 1;
    public static final int GU_SIGNAL_NOWAIT = 2;
    public static final int GU_SIGNAL_PAUSE = 3;

    /* Signals */
    public static final int GU_CALLBACK_SIGNAL = 1;
    public static final int GU_CALLBACK_FINISH = 4;

    /* Signal behavior (deprecated) */
    public static final int GU_BEHAVIOR_SUSPEND = 1;
    public static final int GU_BEHAVIOR_CONTINUE = 2;

    /* Break mode */
    public static final int GU_BREAK_PAUSE = 0;
    public static final int GU_BREAK_CANCEL = 1;

    /* Color Macros, maps 8 bit unsigned channels into one 32-bit value */
    public static int GU_ABGR(int a, int b, int g, int r) { return (((a) << 24)|((b) << 16)|((g) << 8)|(r)); }
    public static int GU_ARGB(int a, int r, int g, int b) { return GU_ABGR((a),(b),(g),(r)); }
    public static int GU_RGBA(int r, int g, int b, int a) { return GU_ARGB((a),(r),(g),(b)); }

    @Import(name = "sceGuDepthBuffer")
    public static native void sceGuDepthBuffer(Address zbp, int zbw);

    @Import(name = "sceGuDispBuffer")
    public static native void sceGuDispBuffer(int width, int height, Address dispbp, int dispbw);

    @Import(name = "sceGuDrawBuffer")
    public static native void sceGuDrawBuffer(int psm, Address fbp, int fbw);

    @Import(name = "sceGuDrawBufferList")
    public static native void sceGuDrawBufferList(int psm, Address fbp, int fbw);

    @Import(name = "sceGuDisplay")
    public static native int sceGuDisplay(int state);

    @Import(name = "sceGuDepthFunc")
    public static native void sceGuDepthFunc(int function);

    @Import(name = "sceGuDepthMask")
    public static native void sceGuDepthMask(int mask);

    @Import(name = "sceGuDepthOffset")
    public static native void sceGuDepthOffset(int offset);

    @Import(name = "sceGuDepthRange")
    public static native void sceGuDepthRange(int near, int far);

    @Import(name = "sceGuFog")
    public static native void sceGuFog(float near, float far, int color);

    @Import(name = "sceGuInit")
    public static native int sceGuInit();

    @Import(name = "sceGuTerm")
    public static native void sceGuTerm();

    @Import(name = "sceGuBreak")
    public static native int sceGuBreak(int mode);

    @Import(name = "sceGuContinue")
    public static native int sceGuContinue();

    @Import(name = "sceGuSetCallback")
    public static native Address sceGuSetCallback(int signal, GuSetCallback callback);

    @Import(name = "sceGuSignal")
    public static native void sceGuSignal(int mode, int id);

    @Import(name = "sceGuSendCommandf")
    public static native void sceGuSendCommandf(int cmd, float argument);

    @Import(name = "sceGuSendCommandi")
    public static native void sceGuSendCommandi(int cmd, int argument);

    @Import(name = "sceGuGetMemory")
    public static native Address sceGuGetMemory(int size);

    @Import(name = "sceGuStart")
    public static native int sceGuStart(int ctype, Address list);

    @Import(name = "sceGuFinish")
    public static native int sceGuFinish();

    @Import(name = "sceGuFinishId")
    public static native int sceGuFinishId(int id);

    @Import(name = "sceGuCallList")
    public static native int sceGuCallList(Address list);

    @Import(name = "sceGuCallMode")
    public static native void sceGuCallMode(int mode);

    @Import(name = "sceGuCheckList")
    public static native int sceGuCheckList();

    @Import(name = "sceGuSendList")
    public static native int sceGuSendList(int mode, Address list, Address context);

    @Import(name = "sceGuSwapBuffers")
    public static native Address sceGuSwapBuffers();

    @Import(name = "sceGuSync")
    public static native int sceGuSync(int mode, int what);

    @Import(name = "sceGuDrawArray")
    public static native void sceGuDrawArray(int prim, int vtype, int count, Address indices, Address vertices);

    @Import(name = "sceGuDrawArray")
    public static native void sceGuDrawArray(int prim, int vtype, int count, Address indices, ByteBuffer vertices);

    @Import(name = "sceGuBeginObject")
    public static native void sceGuBeginObject(int vtype, int count, Address indices, Address vertices);

    @Import(name = "sceGuEndObject")
    public static native int sceGuEndObject();

    @Import(name = "sceGuSetStatus")
    public static native void sceGuSetStatus(int state, int status);

    @Import(name = "sceGuGetStatus")
    public static native int sceGuGetStatus(int state);

    @Import(name = "sceGuSetAllStatus")
    public static native void sceGuSetAllStatus(int status);

    @Import(name = "sceGuGetAllStatus")
    public static native int sceGuGetAllStatus();

    @Import(name = "sceGuEnable")
    public static native void sceGuEnable(int state);

    @Import(name = "sceGuDisable")
    public static native void sceGuDisable(int state);

    @Import(name = "sceGuLight")
    public static native void sceGuLight(int light, int type, int components, Address position);

    @Import(name = "sceGuLightAtt")
    public static native void sceGuLightAtt(int light, float atten0, float atten1, float atten2);

    @Import(name = "sceGuLightColor")
    public static native void sceGuLightColor(int light, int component, int color);

    @Import(name = "sceGuLightMode")
    public static native void sceGuLightMode(int mode);

    @Import(name = "sceGuLightSpot")
    public static native void sceGuLightSpot(int light, Address direction, float exponent, float cutoff);

    @Import(name = "sceGuClear")
    public static native void sceGuClear(int flags);

    @Import(name = "sceGuClearColor")
    public static native void sceGuClearColor(int color);

    @Import(name = "sceGuClearDepth")
    public static native void sceGuClearDepth(int depth);

    @Import(name = "sceGuClearStencil")
    public static native void sceGuClearStencil(int stencil);

    @Import(name = "sceGuPixelMask")
    public static native void sceGuPixelMask(int mask);

    @Import(name = "sceGuColor")
    public static native void sceGuColor(int color);

    @Import(name = "sceGuColorFunc")
    public static native void sceGuColorFunc(int func, int color, int mask);

    @Import(name = "sceGuColorMaterial")
    public static native void sceGuColorMaterial(int components);

    @Import(name = "sceGuAlphaFunc")
    public static native void sceGuAlphaFunc(int func, int value, int mask);

    @Import(name = "sceGuAmbient")
    public static native void sceGuAmbient(int color);

    @Import(name = "sceGuAmbientColor")
    public static native void sceGuAmbientColor(int color);

    @Import(name = "sceGuBlendFunc")
    public static native void sceGuBlendFunc(int op, int src, int dest, int srcfix, int destfix);

    @Import(name = "sceGuMaterial")
    public static native void sceGuMaterial(int mode, int color);

    @Import(name = "sceGuModelColor")
    public static native void sceGuModelColor(int emissive, int ambient, int diffuse, int specular);

    @Import(name = "sceGuStencilFunc")
    public static native void sceGuStencilFunc(int func, int ref, int mask);

    @Import(name = "sceGuStencilOp")
    public static native void sceGuStencilOp(int fail, int zfail, int zpass);

    @Import(name = "sceGuSpecular")
    public static native void sceGuSpecular(float power);

    @Import(name = "sceGuFrontFace")
    public static native void sceGuFrontFace(int order);

    @Import(name = "sceGuLogicalOp")
    public static native void sceGuLogicalOp(int op);

    @Import(name = "sceGuSetDither")
    public static native void sceGuSetDither(Address matrix4);

    @Import(name = "sceGuSetDither")
    public static native void sceGuSetDither(ScePspIMatrix4 matrix4);

    @Import(name = "sceGuShadeModel")
    public static native void sceGuShadeModel(int mode);

    @Import(name = "sceGuCopyImage")
    public static native void sceGuCopyImage(int psm, int sx, int sy, int width, int height, int srcw, Address src, int dx, int dy, int destw, Address dest);

    @Import(name = "sceGuTexEnvColor")
    public static native void sceGuTexEnvColor(int color);

    @Import(name = "sceGuTexFilter")
    public static native void sceGuTexFilter(int min, int mag);

    @Import(name = "sceGuTexFlush")
    public static native void sceGuTexFlush();

    @Import(name = "sceGuTexFunc")
    public static native void sceGuTexFunc(int tfx, int tcc);

    @Import(name = "sceGuTexImage")
    public static native void sceGuTexImage(int mipmap, int width, int height, int tbw, Address tbp);

    @Import(name = "sceGuTexLevelMode")
    public static native void sceGuTexLevelMode(int mode, float bias);

    @Import(name = "sceGuTexMapMode")
    public static native void sceGuTexMapMode(int mode, int lu, int lv);

    @Import(name = "sceGuTexMode")
    public static native void sceGuTexMode(int tpsm, int maxmips, int mc, int swizzle);

    @Import(name = "sceGuTexOffset")
    public static native void sceGuTexOffset(float u, float v);

    @Import(name = "sceGuTexProjMapMode")
    public static native void sceGuTexProjMapMode(int mode);

    @Import(name = "sceGuTexScale")
    public static native void sceGuTexScale(float u, float v);

    @Import(name = "sceGuTexSlope")
    public static native void sceGuTexSlope(float slope);

    @Import(name = "sceGuTexSync")
    public static native void sceGuTexSync();

    @Import(name = "sceGuTexWrap")
    public static native void sceGuTexWrap(int u, int v);

    @Import(name = "sceGuClutLoad")
    public static native void sceGuClutLoad(int num_blocks, Address cbp);

    @Import(name = "sceGuClutMode")
    public static native void sceGuClutMode(int cpsm, int shift, int mask, int csa);

    @Import(name = "sceGuOffset")
    public static native void sceGuOffset(int x, int y);

    @Import(name = "sceGuScissor")
    public static native void sceGuScissor(int x, int y, int w, int h);

    @Import(name = "sceGuViewport")
    public static native void sceGuViewport(int cx, int cy, int width, int height);

    @Import(name = "sceGuDrawBezier")
    public static native void sceGuDrawBezier(int vtype, int ucount, int vcount, Address indices, Address vertices);

    @Import(name = "sceGuPatchDivide")
    public static native void sceGuPatchDivide(int ulevel, int vlevel);

    @Import(name = "sceGuPatchFrontFace")
    public static native void sceGuPatchFrontFace(int mode);

    @Import(name = "sceGuPatchPrim")
    public static native void sceGuPatchPrim(int prim);

    @Import(name = "sceGuDrawSpline")
    public static native void sceGuDrawSpline(int vtype, int ucount, int vcount, int uedge, int vedge, Address indices, Address vertices);

    @Import(name = "sceGuSetMatrix")
    public static native void sceGuSetMatrix(int type, Address matrix);

    @Import(name = "sceGuSetMatrix")
    public static native void sceGuSetMatrix(int type, ScePspFMatrix4 matrix);

    @Import(name = "sceGuBoneMatrix")
    public static native void sceGuBoneMatrix(int index, Address matrix);

    @Import(name = "sceGuBoneMatrix")
    public static native void sceGuBoneMatrix(int index, ScePspFMatrix4 matrix);

    @Import(name = "sceGuMorphWeight")
    public static native void sceGuMorphWeight(int index, float weight);

    @Import(name = "sceGuDrawArrayN")
    public static native void sceGuDrawArrayN(int primitive_type, int vertex_type, int vcount, int primcount, Address indices, Address vertices);

    @Import(name = "guSwapBuffersBehaviour")
    public static native void guSwapBuffersBehaviour(int behaviour);

    @Import(name = "guSwapBuffersCallback")
    public static native void guSwapBuffersCallback(GuSwapBuffersCallback callback);

    @Import(name = "guGetStaticVramBuffer")
    public static native Address guGetStaticVramBuffer(int width, int height, int psm);

    @Import(name = "guGetStaticVramTexture")
    public static native Address guGetStaticVramTexture(int width, int height, int psm);

    @Import(name = "guGetDisplayState")
    public static native int guGetDisplayState();

    public static abstract class GuSwapBuffersCallback extends Function {
        public abstract void invoke(Address display, Address render);
    }

    public static abstract class GuSetCallback extends Function {
        public abstract void invoke(int value);
    }

    // pspgum.h

    @Import(name = "sceGumDrawArray")
    public static native void sceGumDrawArray(int prim, int vtype, int count, Address indices, Address vertices);

    @Import(name = "sceGumDrawArray")
    public static native void sceGumDrawArray(int prim, int vtype, int count, Address indices, ByteBuffer vertices);

    @Import(name = "sceGumDrawArrayN")
    public static native void sceGumDrawArrayN(int prim, int vtype, int count, int a3, Address indices, Address vertices);

    @Import(name = "sceGumDrawBezier")
    public static native void sceGumDrawBezier(int vtype, int ucount, int vcount, Address indices, Address vertices);

    @Import(name = "sceGumDrawSpline")
    public static native void sceGumDrawSpline(int vtype, int ucount, int vcount, int uedge, int vedge, Address indices, Address vertices);

    @Import(name = "sceGumLoadIdentity")
    public static native void sceGumLoadIdentity();

    @Import(name = "sceGumLoadMatrix")
    public static native void sceGumLoadMatrix(Address matrix4);

    @Import(name = "sceGumLoadMatrix")
    public static native void sceGumLoadMatrix(ScePspFMatrix4 matrix4);

    @Import(name = "sceGumLookAt")
    public static native void sceGumLookAt(Address eye_Vector3, Address center_Vector3, Address up_Vector3);

    @Import(name = "sceGumLookAt")
    public static native void sceGumLookAt(ScePspFVector3 eye_Vector3, ScePspFVector3 center_Vector3, ScePspFVector3 up_Vector3);

    @Import(name = "sceGumMatrixMode")
    public static native void sceGumMatrixMode(int mode);

    @Import(name = "sceGumMultMatrix")
    public static native void sceGumMultMatrix(Address matrix4);

    @Import(name = "sceGumMultMatrix")
    public static native void sceGumMultMatrix(ScePspFMatrix4 matrix4);

    @Import(name = "sceGumOrtho")
    public static native void sceGumOrtho(float left, float right, float bottom, float top, float near, float far);

    @Import(name = "sceGumPerspective")
    public static native void sceGumPerspective(float fovy, float aspect, float near, float far);

    @Import(name = "sceGumPopMatrix")
    public static native void sceGumPopMatrix();

    @Import(name = "sceGumPushMatrix")
    public static native void sceGumPushMatrix();

    @Import(name = "sceGumRotateX")
    public static native void sceGumRotateX(float angle);

    @Import(name = "sceGumRotateY")
    public static native void sceGumRotateY(float angle);

    @Import(name = "sceGumRotateZ")
    public static native void sceGumRotateZ(float angle);

    @Import(name = "sceGumRotateXYZ")
    public static native void sceGumRotateXYZ(Address vector3);

    @Import(name = "sceGumRotateXYZ")
    public static native void sceGumRotateXYZ(ScePspFVector3 vector3);

    @Import(name = "sceGumRotateZYX")
    public static native void sceGumRotateZYX(Address vector3);

    @Import(name = "sceGumRotateZYX")
    public static native void sceGumRotateZYX(ScePspFVector3 vector3);

    @Import(name = "sceGumRotate")
    public static native void sceGumRotate(Address quaternion);

    @Import(name = "sceGumRotate")
    public static native void sceGumRotate(ScePspFQuaternion quaternion);

    @Import(name = "sceGumScale")
    public static native void sceGumScale(Address vector3);

    @Import(name = "sceGumScale")
    public static native void sceGumScale(ScePspFVector3 vector3);

    @Import(name = "sceGumStoreMatrix")
    public static native void sceGumStoreMatrix(Address matrix4);

    @Import(name = "sceGumStoreMatrix")
    public static native void sceGumStoreMatrix(ScePspFMatrix4 matrix4);

    @Import(name = "sceGumTranslate")
    public static native void sceGumTranslate(Address vector3);

    @Import(name = "sceGumTranslate")
    public static native void sceGumTranslate(ScePspFVector3 vector3);

    @Import(name = "sceGumUpdateMatrix")
    public static native void sceGumUpdateMatrix();

    @Import(name = "sceGumFullInverse")
    public static native void sceGumFullInverse();

    @Import(name = "sceGumFastInverse")
    public static native void sceGumFastInverse();

    @Import(name = "sceGumBeginObject")
    public static native void sceGumBeginObject(int vtype, int count, Address indices, Address vertices);

    @Import(name = "sceGumEndObject")
    public static native void sceGumEndObject();

    @Import(name = "gumInit")
    public static native void gumInit();

    @Import(name = "gumInit")
    public static native void gumLoadIdentity(Address matrix4);

    @Import(name = "gumInit")
    public static native void gumLoadIdentity(ScePspFMatrix4 matrix4);

    @Import(name = "gumInit")
    public static native void gumLoadQuaternion(Address rMatrix4, Address quaternion);

    @Import(name = "gumInit")
    public static native void gumLoadQuaternion(ScePspFMatrix4 rMatrix4, ScePspFQuaternion quaternion);

    @Import(name = "gumInit")
    public static native void gumLoadMatrix(Address rMatrix4, Address aMatrix4);

    @Import(name = "gumInit")
    public static native void gumLoadMatrix(ScePspFMatrix4 rMatrix4, ScePspFMatrix4 aMatrix4);

    @Import(name = "gumInit")
    public static native void gumLookAt(Address matrix4, Address eyeVector3, Address centerVector3, Address upVector3);

    @Import(name = "gumInit")
    public static native void gumLookAt(ScePspFMatrix4 matrix4, ScePspFVector3 eyeVector3, ScePspFVector3 centerVector3, ScePspFVector3 upVector3);

    @Import(name = "gumInit")
    public static native void gumMultMatrix(Address resultMatrix4, Address aMatrix4, Address bMatrix4);

    @Import(name = "gumInit")
    public static native void gumMultMatrix(ScePspFMatrix4 resultMatrix4, ScePspFMatrix4 aMatrix4, ScePspFMatrix4 bMatrix4);

    @Import(name = "gumInit")
    public static native void gumOrtho(Address matrix4, float left, float right, float bottom, float top, float near, float far);

    @Import(name = "gumInit")
    public static native void gumOrtho(ScePspFMatrix4 matrix4, float left, float right, float bottom, float top, float near, float far);

    @Import(name = "gumInit")
    public static native void gumPerspective(Address matrix4, float fovy, float aspect, float near, float far);

    @Import(name = "gumInit")
    public static native void gumPerspective(ScePspFMatrix4 matrix4, float fovy, float aspect, float near, float far);

    @Import(name = "gumRotateX")
    public static native void gumRotateX(Address matrix4, float angle);

    @Import(name = "gumRotateX")
    public static native void gumRotateX(ScePspFMatrix4 matrix4, float angle);

    @Import(name = "gumRotateXYZ")
    public static native void gumRotateXYZ(Address matrix4, Address vector3);

    @Import(name = "gumRotateXYZ")
    public static native void gumRotateXYZ(ScePspFMatrix4 matrix4, ScePspFVector3 vector3);

    @Import(name = "gumRotateY")
    public static native void gumRotateY(Address matrix4, float angle);

    @Import(name = "gumRotateY")
    public static native void gumRotateY(ScePspFMatrix4 matrix4, float angle);

    @Import(name = "gumRotateZ")
    public static native void gumRotateZ(Address matrix4, float angle);

    @Import(name = "gumRotateZ")
    public static native void gumRotateZ(ScePspFMatrix4 matrix4, float angle);

    @Import(name = "gumRotateZYX")
    public static native void gumRotateZYX(Address matrix4, Address vector3);

    @Import(name = "gumRotateZYX")
    public static native void gumRotateZYX(ScePspFMatrix4 matrix4, ScePspFVector3 vector3);

    @Import(name = "gumRotateMatrix")
    public static native void gumRotateMatrix(Address matrix4, Address quaternion);

    @Import(name = "gumRotateMatrix")
    public static native void gumRotateMatrix(ScePspFMatrix4 matrix4, ScePspFQuaternion quaternion);

    @Import(name = "gumScale")
    public static native void gumScale(Address matrix4, Address vector3);

    @Import(name = "gumScale")
    public static native void gumScale(ScePspFMatrix4 matrix4, ScePspFVector3 vector3);

    @Import(name = "gumTranslate")
    public static native void gumTranslate(Address matrix4, Address vector3);

    @Import(name = "gumTranslate")
    public static native void gumTranslate(ScePspFMatrix4 matrix4, ScePspFVector3 vector3);

    @Import(name = "gumFullInverse")
    public static native void gumFullInverse(Address rMatrix4, Address aMatrix4);

    @Import(name = "gumFullInverse")
    public static native void gumFullInverse(ScePspFMatrix4 rMatrix4, ScePspFMatrix4 aMatrix4);

    @Import(name = "gumFastInverse")
    public static native void gumFastInverse(Address rMatrix4, Address aMatrix4);

    @Import(name = "gumFastInverse")
    public static native void gumFastInverse(ScePspFMatrix4 rMatrix4, ScePspFMatrix4 aMatrix4);

    @Import(name = "gumCrossProduct")
    public static native void gumCrossProduct(Address rVector3, Address aVector3, Address bVector3);

    @Import(name = "gumCrossProduct")
    public static native void gumCrossProduct(ScePspFVector3 rVector3, ScePspFVector3 aVector3, ScePspFVector3 bVector3);

    @Import(name = "gumDotProduct")
    public static native float gumDotProduct(Address aVector3, Address bVector3);

    @Import(name = "gumDotProduct")
    public static native float gumDotProduct(ScePspFVector3 aVector3, ScePspFVector3 bVector3);

    @Import(name = "gumNormalize")
    public static native void gumNormalize(Address vVector3);

    @Import(name = "gumNormalize")
    public static native void gumNormalize(ScePspFVector3 vVector3);

    @Import(name = "gumRotateVector")
    public static native void gumRotateVector(Address rVector3, Address quaternion, Address vector3);

    @Import(name = "gumRotateVector")
    public static native void gumRotateVector(ScePspFVector3 rVector3, ScePspFQuaternion quaternion, ScePspFVector3 vector3);

    @Import(name = "gumNormalizeQuaternion")
    public static native void gumNormalizeQuaternion(Address quaternion);

    @Import(name = "gumNormalizeQuaternion")
    public static native void gumNormalizeQuaternion(ScePspFQuaternion quaternion);

    @Import(name = "gumLoadAxisAngle")
    public static native void gumLoadAxisAngle(Address rQuaternion, Address axisVector3, float t);

    @Import(name = "gumLoadAxisAngle")
    public static native void gumLoadAxisAngle(ScePspFQuaternion rQuaternion, ScePspFVector3 axisVector3, float t);

    @Import(name = "gumMultQuaternion")
    public static native void gumMultQuaternion(Address resultQuaternion, Address aQuaternion, Address bQuaternion);

    @Import(name = "gumMultQuaternion")
    public static native void gumMultQuaternion(ScePspFQuaternion resultQuaternion, ScePspFQuaternion aQuaternion, ScePspFQuaternion bQuaternion);

    // CUSTOM METHODS

    @Import(name = "initGraphics")
    public static native void initGraphics();

    @Import(name = "beginFrame")
    public static native void beginFrame(int dialog);

    @Import(name = "endFrame")
    public static native void endFrame(int vsync, int dialog);
}