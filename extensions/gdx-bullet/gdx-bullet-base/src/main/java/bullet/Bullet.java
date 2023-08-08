package bullet;

import com.github.xpenatan.jparser.loader.JParserLibraryLoader;

/**
 * @author xpenatan
 */
/*[-IDL_SKIP]*/
public class Bullet {

    /*[-teaVM;-ADD]
        @org.teavm.jso.JSFunctor
        public interface OnInitFunction extends org.teavm.jso.JSObject {
            void onInit();
        }
    */

    /*[-teaVM;-REPLACE]
     public static void init(Runnable run) {
        JParserLibraryLoader libraryLoader = new JParserLibraryLoader();
        OnInitFunction onInitFunction = new OnInitFunction() {
            @Override
            public void onInit() {
                run.run();
            }
        };
        setOnLoadInit(onInitFunction);
        libraryLoader.load("bullet.wasm");
    }
    */
    public static void init(Runnable run) {
        JParserLibraryLoader libraryLoader = new JParserLibraryLoader();
        libraryLoader.load("bullet");
        run.run();
    }

    /*[-teaVM;-REPLACE]
        @org.teavm.jso.JSBody(params = { "onInitFunction" }, script = "window.BulletOnInit = onInitFunction;")
        private static native void setOnLoadInit(OnInitFunction onInitFunction);
    */
    /*[-C++;-REMOVE] */
    public static native void setOnLoadInit();


    /* [-C++;-NATIVE]
         return btGetVersion();
    */
    /* [-teaVM;-NATIVE]
        return bullet.MyClassHelper.prototype.getBTVersion();
    */
    public static native int btGetVersion();
}
