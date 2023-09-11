package com.badlogic.gdx.tests;

import com.badlogic.gdx.tests.conformance.DisplayModeTest;
import com.badlogic.gdx.tests.g3d.ModelCacheTest;
import com.badlogic.gdx.tests.g3d.ShadowMappingTest;
import com.badlogic.gdx.tests.gles2.VertexArrayTest;
import com.badlogic.gdx.tests.gwt.GwtInputTest;
import com.badlogic.gdx.tests.gwt.GwtWindowModeTest;
import com.badlogic.gdx.tests.math.OctreeTest;
import com.badlogic.gdx.tests.net.OpenBrowserExample;
import com.badlogic.gdx.tests.superkoalio.SuperKoalio;
import com.badlogic.gdx.tests.utils.GdxTest;

public class TeaVMGdxTests {

    public static TeaVMInstancer[] getTestList() {
        TeaVMInstancer[] tests = {new TeaVMInstancer() {
            public GdxTest instance() {
                return new AccelerometerTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ActionTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ActionSequenceTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new AlphaTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new AnimationTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new AnnotationTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new AssetManagerTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new AtlasIssueTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BigMeshTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BitmapFontAlignmentTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BitmapFontFlipTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BitmapFontTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BitmapFontMetricsTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BlitTest();
            }
        }, new TeaVMInstancer() {
//            public GdxTest instance() {
//                return new Box2DCharacterControllerTest();
//            }
//        }, new TeaVMInstancer() {
//            public GdxTest instance() {
//                return new Box2DTest();
//            }
//        }, new TeaVMInstancer() {
//            public GdxTest instance() {
//                return new Box2DTestCollection();
//            }
//        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new BufferUtilsTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ClipboardTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ColorTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ComplexActionTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new CustomShaderSpriteBatchTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new DecalTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new DisplayModeTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new LabelScaleTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new EdgeDetectionTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new FilesTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new FilterPerformanceTest();
            }
        },
// new GwtInstancer() {public GdxTest instance(){return new FlickScrollPaneTest();}}, // FIXME this messes up stuff, why?
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new FrameBufferTest();
                    }
                }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new DownloadTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new FramebufferToTextureTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GestureDetectorTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GLProfilerErrorTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GroupCullingTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GroupFadeTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GwtInputTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GwtWindowModeTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new I18NSimpleMessageTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ImageScaleTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ImageTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new IndexBufferObjectShaderTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new IntegerBitmapFontTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new InterpolationTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new InverseKinematicsTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new IsometricTileTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new KinematicBodyTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new LifeCycleTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new LabelTest();
            }
        },
                // new GwtInstancer() {public GdxTest instance(){return new MatrixJNITest();}}, // No purpose
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new MeshShaderTest();
                    }
                }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new MeshWithCustomAttributesTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new MipMapTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ModelCacheTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new MultitouchTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new MusicTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new OctreeTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new OpenBrowserExample();
            }
// }, new GwtInstancer() { public GdxTest instance () { return new NoncontinuousRenderingTest(); } // FIXME doesn't compile due to
// the use of Thread
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ParallaxTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ParticleEmitterTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new PixelsPerInchTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new PixmapPackerTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new PixmapTest();
            }
        },
                // new GwtInstancer() {public GdxTest instance(){return new PixmapBlendingTest();}}, // FIXME no idea why this doesn't
                // work
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new PreferencesTest();
                    }
                }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ProjectiveTextureTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new RotationTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ReflectionCorrectnessTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new Scene2dTest();
            }

// new GwtInstancer() {public GdxTest instance(){return new RunnablePostTest();}}, // Goes into infinite loop
// new GwtInstancer() {public GdxTest instance(){return new ScrollPaneTest();}}, // FIXME this messes up stuff, why?
// new GwtInstancer() {public GdxTest instance(){return new ShaderMultitextureTest();}}, // FIXME fucks up stuff
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ShadowMappingTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ShapeRendererTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SimpleAnimationTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SimpleDecalTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SimpleStageCullingTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SortedSpriteTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SpriteBatchShaderTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SpriteCacheOffsetTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SpriteCacheTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SoundTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new StageTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SystemCursorTest();
            }
        },
                // new GwtInstancer() {public GdxTest instance(){return new StagePerformanceTest();}}, // FIXME borks out
                new TeaVMInstancer() {
                    public GdxTest instance() {
                        return new TableTest();
                    }
                }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TextButtonTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TextButtonTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TextureAtlasTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TiledMapObjectLoadingTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new UITest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new VertexBufferObjectShaderTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new YDownTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new SuperKoalio();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new ReflectionTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TiledMapAtlasAssetManagerTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TimeUtilsTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new GWTLossyPremultipliedAlphaTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new QuadTreeFloatTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new QuadTreeFloatNearestTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TextAreaTest();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TextAreaTest2();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new TextAreaTest3();
            }
        }, new TeaVMInstancer() {
            public GdxTest instance() {
                return new VertexArrayTest();
            }
        } // these may have issues with tab getting intercepted by the browser
        };

        return tests;
    }

    public abstract static class TeaVMInstancer implements AbstractTestWrapper.Instancer {

        public String getSimpleName() {
            return instance().getClass().getSimpleName();
        }
    }
}
