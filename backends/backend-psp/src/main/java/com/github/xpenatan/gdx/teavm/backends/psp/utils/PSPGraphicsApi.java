package com.github.xpenatan.gdx.teavm.backends.psp.utils;

import java.nio.ByteBuffer;
import org.teavm.interop.Address;
import org.teavm.interop.Function;
import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

@Include("PSPGraphicsApi.h")
public class PSPGraphicsApi {

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
    public static int GU_TEXTURE_SHIFT(int n) { return (n) << 0; }
    public static final int GU_TEXTURE_8BIT = GU_TEXTURE_SHIFT(1);
    public static final int GU_TEXTURE_16BIT = GU_TEXTURE_SHIFT(2);
    public static final int GU_TEXTURE_32BITF = GU_TEXTURE_SHIFT(3);
    public static final int GU_TEXTURE_BITS = GU_TEXTURE_SHIFT(3);

    public static int GU_COLOR_SHIFT(int n) { return (n) << 2; }
    public static final int GU_COLOR_5650 = GU_COLOR_SHIFT(4);
    public static final int GU_COLOR_5551 = GU_COLOR_SHIFT(5);
    public static final int GU_COLOR_4444 = GU_COLOR_SHIFT(6);
    public static final int GU_COLOR_8888 = GU_COLOR_SHIFT(7);
    public static final int GU_COLOR_BITS = GU_COLOR_SHIFT(7);

    public static int GU_NORMAL_SHIFT(int n) { return (n) << 5; }
    public static final int GU_NORMAL_8BIT = GU_NORMAL_SHIFT(1);
    public static final int GU_NORMAL_16BIT = GU_NORMAL_SHIFT(2);
    public static final int GU_NORMAL_32BITF = GU_NORMAL_SHIFT(3);
    public static final int GU_NORMAL_BITS = GU_NORMAL_SHIFT(3);

    public static int GU_VERTEX_SHIFT(int n) { return (n) << 7; }
    public static final int GU_VERTEX_8BIT = GU_VERTEX_SHIFT(1);
    public static final int GU_VERTEX_16BIT = GU_VERTEX_SHIFT(2);
    public static final int GU_VERTEX_32BITF = GU_VERTEX_SHIFT(3);
    public static final int GU_VERTEX_BITS = GU_VERTEX_SHIFT(3);

    public static int GU_WEIGHT_SHIFT(int n) { return (n) << 9; }
    public static final int GU_WEIGHT_8BIT = GU_WEIGHT_SHIFT(1);
    public static final int GU_WEIGHT_16BIT = GU_WEIGHT_SHIFT(2);
    public static final int GU_WEIGHT_32BITF = GU_WEIGHT_SHIFT(3);
    public static final int GU_WEIGHT_BITS = GU_WEIGHT_SHIFT(3);

    public static int GU_INDEX_SHIFT(int n) { return (n) << 11; }
    public static final int GU_INDEX_8BIT = GU_INDEX_SHIFT(1);
    public static final int GU_INDEX_16BIT = GU_INDEX_SHIFT(2);
    public static final int GU_INDEX_BITS = GU_INDEX_SHIFT(3);

    public static int GU_WEIGHTS(int n) { return (((n)-1)&7)<<14; }
    public static final int GU_WEIGHTS_BITS = GU_WEIGHTS(8);
    public static int GU_VERTICES(int n) { return (((n)-1)&7)<<18; }
    public static final int GU_VERTICES_BITS = GU_VERTICES(8);

    public static int GU_TRANSFORM_SHIFT(int n) { return (n) << 23; }
    public static final int GU_TRANSFORM_3D = GU_TRANSFORM_SHIFT(0);
    public static final int GU_TRANSFORM_2D = GU_TRANSFORM_SHIFT(1);
    public static final int GU_TRANSFORM_BITS = GU_TRANSFORM_SHIFT(1);
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
    public static native void sceGuSetDither(Address matrix);

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

    @Import(name = "sceGuBoneMatrix")
    public static native void sceGuBoneMatrix(int index, Address matrix);

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

    // CUSTOM METHODS

    @Import(name = "initGraphics")
    public static native void initGraphics();

    @Import(name = "startFrame")
    public static native void startFrame(int dialog);

    @Import(name = "endFrame")
    public static native void endFrame(int vsync, int dialog);
}