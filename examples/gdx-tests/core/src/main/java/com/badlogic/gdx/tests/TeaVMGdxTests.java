package com.badlogic.gdx.tests;

import com.badlogic.gdx.tests.conformance.DisplayModeTest;
import com.badlogic.gdx.tests.g3d.ModelCacheTest;
import com.badlogic.gdx.tests.g3d.MultipleRenderTargetTest;
import com.badlogic.gdx.tests.g3d.ShadowMappingTest;
import com.badlogic.gdx.tests.g3d.TextureArrayTest;
import com.badlogic.gdx.tests.g3d.TextureRegion3DTest;
import com.badlogic.gdx.tests.gles2.VertexArrayTest;
import com.badlogic.gdx.tests.gles3.GL30Texture3DTest;
import com.badlogic.gdx.tests.gles3.InstancedRenderingTest;
import com.badlogic.gdx.tests.gwt.GwtInputTest;
import com.badlogic.gdx.tests.gwt.GwtWindowModeTest;
import com.badlogic.gdx.tests.math.OctreeTest;
import com.badlogic.gdx.tests.net.OpenBrowserExample;
import com.badlogic.gdx.tests.superkoalio.SuperKoalio;
import com.badlogic.gdx.tests.utils.GdxTest;
import java.util.ArrayList;

public class TeaVMGdxTests {

    public static TeaVMInstancer[] getTestList() {

        ArrayList<TeaVMInstancer> test = new ArrayList<>();

        // QUICK TEST ###################################
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new SoundTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new MusicTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new TextureDataTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new FloatTextureTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new TextureRegion3DTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new GL30Texture3DTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new InstancedRenderingTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new TextureArrayTest();
                    }
                }
        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new MultipleRenderTargetTest();
                    }
                }
        );
        // ###################################


//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new AccelerometerTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ActionTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ActionSequenceTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new AlphaTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new AnimationTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new AnnotationTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new AssetManagerTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new AtlasIssueTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BigMeshTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BitmapFontAlignmentTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BitmapFontFlipTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BitmapFontTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BitmapFontMetricsTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BlitTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new BufferUtilsTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ClipboardTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ColorTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ComplexActionTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new CustomShaderSpriteBatchTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new DecalTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new DisplayModeTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new LabelScaleTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new EdgeDetectionTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new FilesTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new FilterPerformanceTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new FrameBufferTest();
//                    }
//                }
//        );
        test.add(
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new DownloadTest();
                    }
                }
        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new FramebufferToTextureTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GestureDetectorTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GLProfilerErrorTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GroupCullingTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GroupFadeTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GwtInputTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GwtWindowModeTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new I18NSimpleMessageTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ImageScaleTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new IndexBufferObjectShaderTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new IntegerBitmapFontTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new InterpolationTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new InverseKinematicsTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new IsometricTileTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new KinematicBodyTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new LifeCycleTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new LabelTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new MeshShaderTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new MeshWithCustomAttributesTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new MipMapTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ModelCacheTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new MultitouchTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new OctreeTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new OpenBrowserExample();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ParallaxTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ParticleEmitterTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new PixelsPerInchTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new PixmapPackerTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new PixmapTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new PreferencesTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ProjectiveTextureTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new RotationTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ReflectionCorrectnessTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new Scene2dTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ShadowMappingTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ShapeRendererTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SimpleAnimationTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SimpleDecalTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SimpleStageCullingTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SortedSpriteTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SpriteBatchShaderTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SpriteCacheOffsetTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SpriteCacheTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new StageTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SystemCursorTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TableTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TextButtonTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TextureAtlasTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TiledMapObjectLoadingTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new UITest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new VertexBufferObjectShaderTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new YDownTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new SuperKoalio();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new ReflectionTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TiledMapAtlasAssetManagerTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TimeUtilsTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new GWTLossyPremultipliedAlphaTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new QuadTreeFloatTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new QuadTreeFloatNearestTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TextAreaTest();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TextAreaTest2();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new TextAreaTest3();
//                    }
//                }
//        );
//        test.add(
//                new TeaVMInstancer() {
//                    public GdxTest instance() {
//                        return new VertexArrayTest();
//                    }
//                }
//        );

        TeaVMInstancer[] t = new TeaVMInstancer[test.size()];
        return test.toArray(t);
    }

    public abstract static class TeaVMInstancer implements AbstractTestWrapper.Instancer {

        public String getSimpleName() {
            return instance().getClass().getSimpleName();
        }
    }
}
