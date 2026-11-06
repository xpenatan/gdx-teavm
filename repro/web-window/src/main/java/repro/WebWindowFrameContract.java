package repro;

import com.github.xpenatan.gdx.teavm.backends.web.dom.impl.WebWindow;
import java.util.concurrent.locks.LockSupport;
import org.teavm.jso.JSBody;

/** Browser contract for suspended, serialized WebWindow animation frames. */
public final class WebWindowFrameContract {
    private static final WebWindow window = WebWindow.get();
    private static volatile Thread renderThread;
    private static volatile boolean frameActive;
    private static volatile int completedFrames;

    private WebWindowFrameContract() {
    }

    public static void main(String[] args) {
        window.requestAnimationFrame(WebWindowFrameContract::firstFrame);
    }

    private static void firstFrame() {
        try {
            renderThread = Thread.currentThread();
            require("gdx-web-render".equals(renderThread.getName()), "RAF task did not use render worker");
            require(!frameActive, "first frame overlapped another frame");
            frameActive = true;

            Thread timer = new Thread(() -> {
                try {
                    Thread timerThread = Thread.currentThread();
                    LockSupport.parkNanos(10_000_000L);
                    require(Thread.currentThread() == timerThread, "timer thread identity changed after park");
                    window.requestAnimationFrame(WebWindowFrameContract::secondFrame);
                    LockSupport.parkNanos(50_000_000L);
                    require(completedFrames == 0 && frameActive, "suspended frame completed before it was unparked");
                    LockSupport.unpark(renderThread);
                } catch (Throwable e) {
                    fail(e);
                }
            }, "raf-contract-timer");
            timer.setDaemon(true);
            timer.start();

            LockSupport.park();
            require(Thread.currentThread() == renderThread, "render thread identity changed after resume");
            require(frameActive, "render frame state lost during suspension");
            frameActive = false;
            completedFrames++;
        } catch (Throwable e) {
            fail(e);
        }
    }

    private static void secondFrame() {
        try {
            require(Thread.currentThread() == renderThread, "RAF tasks used different Java threads");
            require(!frameActive, "next RAF ran before the suspended frame completed");
            require(completedFrames == 1, "next RAF ran before frame completion was recorded");
            completedFrames++;
            result("PASS");
        } catch (Throwable e) {
            fail(e);
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    private static void fail(Throwable error) {
        result("FAIL: " + error);
        throw new AssertionError(error);
    }

    @JSBody(params = "value", script = "document.body.setAttribute('data-result', value);")
    private static native void result(String value);
}
