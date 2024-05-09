package com.badlogic.gdx.tests;

import com.badlogic.gdx.tests.bench.TiledMapBench;
import com.badlogic.gdx.tests.conformance.AudioSoundAndMusicIsolationTest;
import com.badlogic.gdx.tests.conformance.DisplayModeTest;
import com.badlogic.gdx.tests.examples.MoveSpriteExample;
import com.badlogic.gdx.tests.extensions.FreeTypeAtlasTest;
import com.badlogic.gdx.tests.extensions.FreeTypeDisposeTest;
import com.badlogic.gdx.tests.extensions.FreeTypeFontLoaderTest;
import com.badlogic.gdx.tests.extensions.FreeTypeIncrementalTest;
import com.badlogic.gdx.tests.extensions.FreeTypeMetricsTest;
import com.badlogic.gdx.tests.extensions.FreeTypePackTest;
import com.badlogic.gdx.tests.extensions.FreeTypeTest;
import com.badlogic.gdx.tests.extensions.InternationalFontsTest;
import com.badlogic.gdx.tests.g3d.Animation3DTest;
import com.badlogic.gdx.tests.g3d.AnisotropyTest;
import com.badlogic.gdx.tests.g3d.Basic3DSceneTest;
import com.badlogic.gdx.tests.g3d.Basic3DTest;
import com.badlogic.gdx.tests.g3d.Benchmark3DTest;
import com.badlogic.gdx.tests.g3d.FogTest;
import com.badlogic.gdx.tests.g3d.FrameBufferCubemapTest;
import com.badlogic.gdx.tests.g3d.HeightMapTest;
import com.badlogic.gdx.tests.g3d.LightsTest;
import com.badlogic.gdx.tests.g3d.MaterialEmissiveTest;
import com.badlogic.gdx.tests.g3d.MaterialTest;
import com.badlogic.gdx.tests.g3d.MeshBuilderTest;
import com.badlogic.gdx.tests.g3d.ModelCacheTest;
import com.badlogic.gdx.tests.g3d.ModelTest;
import com.badlogic.gdx.tests.g3d.MultipleRenderTargetTest;
import com.badlogic.gdx.tests.g3d.ParticleControllerInfluencerSingleTest;
import com.badlogic.gdx.tests.g3d.ParticleControllerTest;
import com.badlogic.gdx.tests.g3d.PolarAccelerationTest;
import com.badlogic.gdx.tests.g3d.ShaderCollectionTest;
import com.badlogic.gdx.tests.g3d.ShaderTest;
import com.badlogic.gdx.tests.g3d.ShadowMappingTest;
import com.badlogic.gdx.tests.g3d.SkeletonTest;
import com.badlogic.gdx.tests.g3d.TangentialAccelerationTest;
import com.badlogic.gdx.tests.g3d.TextureArrayTest;
import com.badlogic.gdx.tests.g3d.TextureRegion3DTest;
import com.badlogic.gdx.tests.g3d.utils.DefaultTextureBinderTest;
import com.badlogic.gdx.tests.gles2.HelloTriangle;
import com.badlogic.gdx.tests.gles2.SimpleVertexShader;
import com.badlogic.gdx.tests.gles2.VertexArrayTest;
import com.badlogic.gdx.tests.gles3.GL30Texture3DTest;
import com.badlogic.gdx.tests.gles3.InstancedRenderingTest;
import com.badlogic.gdx.tests.gles3.ModelInstancedRenderingTest;
import com.badlogic.gdx.tests.gles3.NonPowerOfTwoTest;
import com.badlogic.gdx.tests.gles3.PixelBufferObjectTest;
import com.badlogic.gdx.tests.gles3.UniformBufferObjectsTest;
import com.badlogic.gdx.tests.gles31.GL31FrameBufferMultisampleTest;
import com.badlogic.gdx.tests.gles31.GL31IndirectDrawingIndexedTest;
import com.badlogic.gdx.tests.gles31.GL31IndirectDrawingNonIndexedTest;
import com.badlogic.gdx.tests.gles31.GL31ProgramIntrospectionTest;
import com.badlogic.gdx.tests.gles32.GL32AdvancedBlendingTest;
import com.badlogic.gdx.tests.gles32.GL32DebugControlTest;
import com.badlogic.gdx.tests.gles32.GL32MultipleRenderTargetsBlendingTest;
import com.badlogic.gdx.tests.gles32.GL32OffsetElementsTest;
import com.badlogic.gdx.tests.gwt.GwtInputTest;
import com.badlogic.gdx.tests.gwt.GwtWindowModeTest;
import com.badlogic.gdx.tests.math.CollisionPlaygroundTest;
import com.badlogic.gdx.tests.math.OctreeTest;
import com.badlogic.gdx.tests.math.collision.OrientedBoundingBoxTest;
import com.badlogic.gdx.tests.net.NetAPITest;
import com.badlogic.gdx.tests.net.OpenBrowserExample;
import com.badlogic.gdx.tests.superkoalio.SuperKoalio;
import com.badlogic.gdx.tests.utils.GdxTest;
import com.badlogic.gdx.tests.utils.IssueTest;
import java.util.ArrayList;

public class TeaVMGdxTests {

    public static TeaVMInstancer[] getTestList() {

        ArrayList<TeaVMInstancer> tests = new ArrayList<>();

        // QUICK TEST ###################################
        add(tests, SoundTest::new);
        add(tests, MusicTest::new);
        add(tests, TextureDataTest::new);
        add(tests, FloatTextureTest::new);
        add(tests, TextureRegion3DTest::new);
        add(tests, GL30Texture3DTest::new);
        add(tests, InstancedRenderingTest::new);
        add(tests, TextureArrayTest::new);
        add(tests, MultipleRenderTargetTest::new);
        // ###################################

        add(tests, GwtInputTest::new);
        add(tests, GwtWindowModeTest::new);
        add(tests, OpenBrowserExample::new);

        // #### START BUILD ERRORS ####
//        add(tests, Box2DTest::new);

//        add(tests, Box2DTestCollection::new);
//        add(tests, KinematicBodyTest::new);
//        add(tests, BulletTestCollection::new);
//        add(tests, PixelBufferObjectTest::new);
//        add(tests, ETC1Test::new);
//        add(tests, Gdx2DTest::new);
//        add(tests, MatrixJNITest::new);
//        add(tests, KTXTest::new);
//        add(tests, VBOWithVAOPerformanceTest::new);
//        add(tests, FreeTypeIncrementalTest::new);

        // #### END BUILD ERRORS ####

        add(tests, IssueTest::new);
        add(tests, AccelerometerTest::new);
        add(tests, ActionSequenceTest::new);
        add(tests, ActionTest::new);
        add(tests, Affine2Test::new);
        add(tests, AlphaTest::new);
        add(tests, Animation3DTest::new);
        add(tests, AnimationTest::new);
        add(tests, AnisotropyTest::new);
        add(tests, AnnotationTest::new);
        add(tests, AssetManagerTest::new);
        add(tests, AtlasIssueTest::new);
        add(tests, AudioChangeDeviceTest::new);
        add(tests, AudioDeviceTest::new);
        add(tests, AudioRecorderTest::new);
        add(tests, AudioSoundAndMusicIsolationTest::new);
        add(tests, Basic3DSceneTest::new);
        add(tests, Basic3DTest::new);
        add(tests, Benchmark3DTest::new);
        add(tests, BigMeshTest::new);
        add(tests, BitmapFontAlignmentTest::new);
        add(tests, BitmapFontDistanceFieldTest::new);
        add(tests, BitmapFontFlipTest::new);
        add(tests, BitmapFontMetricsTest::new);
        add(tests, BitmapFontTest::new);
        add(tests, BitmapFontAtlasRegionTest::new);
        add(tests, BlitTest::new);
        add(tests, Bresenham2Test::new);
        add(tests, BufferUtilsTest::new);
        add(tests, ClipboardTest::new);
        add(tests, CollectionsTest::new);
        add(tests, CollisionPlaygroundTest::new);
        add(tests, ColorTest::new);
        add(tests, ContainerTest::new);
        add(tests, CoordinatesTest::new);
        add(tests, CpuSpriteBatchTest::new);
        add(tests, CullTest::new);
        add(tests, CursorTest::new);
        add(tests, DecalTest::new);
        add(tests, DefaultTextureBinderTest::new);
        add(tests, DelaunayTriangulatorTest::new);
        add(tests, DeltaTimeTest::new);
        add(tests, DirtyRenderingTest::new);
        add(tests, DisplayModeTest::new);
        add(tests, DownloadTest::new);
        add(tests, DragAndDropTest::new);
        add(tests, EdgeDetectionTest::new);
        add(tests, ExitTest::new);
        add(tests, ExternalMusicTest::new);
        add(tests, FilesTest::new);
        add(tests, FilterPerformanceTest::new);
        add(tests, FogTest::new);
        add(tests, FrameBufferCubemapTest::new);
        add(tests, FrameBufferTest::new);
        add(tests, FramebufferToTextureTest::new);
        add(tests, FullscreenTest::new);
        add(tests, GestureDetectorTest::new);
        add(tests, GLES30Test::new);
        add(tests, GL31IndirectDrawingIndexedTest::new);
        add(tests, GL31IndirectDrawingNonIndexedTest::new);
        add(tests, GL31FrameBufferMultisampleTest::new);
        add(tests, GL31ProgramIntrospectionTest::new);
        add(tests, GL32AdvancedBlendingTest::new);
        add(tests, GL32DebugControlTest::new);
        add(tests, GL32MultipleRenderTargetsBlendingTest::new);
        add(tests, GL32OffsetElementsTest::new);
        add(tests, GLProfilerErrorTest::new);
        add(tests, GroupCullingTest::new);
        add(tests, GroupFadeTest::new);
        add(tests, GroupTest::new);
        add(tests, HeightMapTest::new);
        add(tests, HelloTriangle::new);
        add(tests, HexagonalTiledMapTest::new);
        add(tests, I18NMessageTest::new);
        add(tests, I18NSimpleMessageTest::new);
        add(tests, ImageScaleTest::new);
        add(tests, ImageTest::new);
        add(tests, ImmediateModeRendererTest::new);
        add(tests, IndexBufferObjectShaderTest::new);
        add(tests, InputTest::new);
        add(tests, IntegerBitmapFontTest::new);
        add(tests, InterpolationTest::new);
        add(tests, IntersectorOverlapConvexPolygonsTest::new);
        add(tests, InverseKinematicsTest::new);
        add(tests, IsometricTileTest::new);
        add(tests, LabelScaleTest::new);
        add(tests, LabelTest::new);
        add(tests, LifeCycleTest::new);
        add(tests, LightsTest::new);
        add(tests, MaterialTest::new);
        add(tests, MaterialEmissiveTest::new);
        add(tests, MeshBuilderTest::new);
        add(tests, MeshShaderTest::new);
        add(tests, MeshWithCustomAttributesTest::new);
        add(tests, MipMapTest::new);
        add(tests, ModelTest::new);
        add(tests, ModelCacheTest::new);
        add(tests, ModelInstancedRenderingTest::new);
        add(tests, MoveSpriteExample::new);
        add(tests, MultitouchTest::new);
        add(tests, NetAPITest::new);
        add(tests, NinePatchTest::new);
        add(tests, NoncontinuousRenderingTest::new);
        add(tests, NonPowerOfTwoTest::new);
        add(tests, OctreeTest::new);
        add(tests, OnscreenKeyboardTest::new);
        add(tests, OrientedBoundingBoxTest::new);
        add(tests, PathTest::new);
        add(tests, ParallaxTest::new);
        add(tests, ParticleControllerInfluencerSingleTest::new);
        add(tests, ParticleControllerTest::new);
        add(tests, ParticleEmitterTest::new);
        add(tests, ParticleEmittersTest::new);
        add(tests, ParticleEmitterChangeSpriteTest::new);
        add(tests, PixelsPerInchTest::new);
        add(tests, PixmapBlendingTest::new);
        add(tests, PixmapPackerTest::new);
        add(tests, PixmapPackerIOTest::new);
        add(tests, PixmapTest::new);
        add(tests, PolarAccelerationTest::new);
        add(tests, PolygonRegionTest::new);
        add(tests, PolygonSpriteTest::new);
        add(tests, PreferencesTest::new);
        add(tests, ProjectTest::new);
        add(tests, ProjectiveTextureTest::new);
        add(tests, ReflectionTest::new);
        add(tests, ReflectionCorrectnessTest::new);
        add(tests, RotationTest::new);
        add(tests, RunnablePostTest::new);
        add(tests, Scene2dTest::new);
        add(tests, ScrollPane2Test::new);
        add(tests, ScrollPaneScrollBarsTest::new);
        add(tests, ScrollPaneTest::new);
        add(tests, ScrollPaneTextAreaTest::new);
        add(tests, ScrollPaneWithDynamicScrolling::new);
        add(tests, SelectTest::new);
        add(tests, SensorTest::new);
        add(tests, ShaderCollectionTest::new);
        add(tests, ShaderMultitextureTest::new);
        add(tests, ShaderTest::new);
        add(tests, ShadowMappingTest::new);
        add(tests, ShapeRendererTest::new);
        add(tests, ShapeRendererAlphaTest::new);
        add(tests, SimpleAnimationTest::new);
        add(tests, SimpleDecalTest::new);
        add(tests, SimpleStageCullingTest::new);
        add(tests, SimpleVertexShader::new);
        add(tests, SkeletonTest::new);
        add(tests, SoftKeyboardTest::new);
        add(tests, SortedSpriteTest::new);
        add(tests, SpriteBatchRotationTest::new);
        add(tests, SpriteBatchShaderTest::new);
        add(tests, SpriteBatchTest::new);
        add(tests, SpriteCacheOffsetTest::new);
        add(tests, SpriteCacheTest::new);
        add(tests, StageDebugTest::new);
        add(tests, StagePerformanceTest::new);
        add(tests, StageTest::new);
        add(tests, SuperKoalio::new);
        add(tests, SystemCursorTest::new);
        add(tests, TableLayoutTest::new);
        add(tests, TableTest::new);
        add(tests, TangentialAccelerationTest::new);
        add(tests, TextAreaTest::new);
        add(tests, TextAreaTest2::new);
        add(tests, TextAreaTest3::new);
        add(tests, TextButtonTest::new);
        add(tests, TextInputDialogTest::new);
        add(tests, TextureAtlasTest::new);
        add(tests, TextureDownloadTest::new);
        add(tests, TextureFormatTest::new);
        add(tests, TideMapAssetManagerTest::new);
        add(tests, TideMapDirectLoaderTest::new);
        add(tests, TiledDrawableTest::new);
        add(tests, TileTest::new);
        add(tests, TiledMapAnimationLoadingTest::new);
        add(tests, TiledMapAssetManagerTest::new);
        add(tests, TiledMapGroupLayerTest::new);
        add(tests, TiledMapAtlasAssetManagerTest::new);
        add(tests, TiledMapDirectLoaderTest::new);
        add(tests, TiledMapModifiedExternalTilesetTest::new);
        add(tests, TiledMapObjectLoadingTest::new);
        add(tests, TiledMapObjectPropertyTest::new);
        add(tests, TiledMapBench::new);
        add(tests, TiledMapLayerOffsetTest::new);
        add(tests, TimerTest::new);
        add(tests, TimeUtilsTest::new);
        add(tests, TouchpadTest::new);
        add(tests, TreeTest::new);
        add(tests, UISimpleTest::new);
        add(tests, UITest::new);
        add(tests, UniformBufferObjectsTest::new);
        add(tests, UtfFontTest::new);
        add(tests, Vector2dTest::new);
        add(tests, VertexArrayTest::new);
        add(tests, VertexBufferObjectShaderTest::new);
        add(tests, VibratorTest::new);
        add(tests, ViewportTest1::new);
        add(tests, ViewportTest2::new);
        add(tests, ViewportTest3::new);
        add(tests, YDownTest::new);
        add(tests, FreeTypeFontLoaderTest::new);
        add(tests, FreeTypeDisposeTest::new);
        add(tests, FreeTypeMetricsTest::new);
        add(tests, FreeTypePackTest::new);
        add(tests, FreeTypeAtlasTest::new);
        add(tests, FreeTypeTest::new);
        add(tests, InternationalFontsTest::new);
        add(tests, PngTest::new);
        add(tests, JsonTest::new);
        add(tests, QuadTreeFloatTest::new);
        add(tests, QuadTreeFloatNearestTest::new);

        TeaVMInstancer[] t = new TeaVMInstancer[tests.size()];
        return tests.toArray(t);
    }

    private static void add(ArrayList<TeaVMInstancer> tests, GdxRunnable instance) {
        tests.add(new TeaVMInstancer() {
            public GdxTest instance() {
                return instance.run();
            }
        });
    }

    public interface GdxRunnable {
        GdxTest run();
    }

    public abstract static class TeaVMInstancer implements AbstractTestWrapper.Instancer {

        public String getSimpleName() {
            return instance().getClass().getSimpleName();
        }
    }
}
