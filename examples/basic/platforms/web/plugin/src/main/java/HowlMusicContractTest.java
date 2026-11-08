import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.teavm.backends.web.webaudio.howler.HowlMusic;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSFunctor;
import org.teavm.jso.JSObject;

/** TeaVM browser regression fixture for HowlMusic's deferred-play lifecycle. */
public final class HowlMusicContractTest {
    private static HowlMusic active;
    private static HowlMusic pausedBeforeUnlock;
    private static HowlMusic stoppedBeforeUnlock;
    private static HowlMusic disposedBeforeUnlock;
    private static HowlMusic invalidAudio;
    private static HowlMusic retryAfterPlayError;
    private static HowlMusic terminalPlayError;

    private HowlMusicContractTest() { }

    public static void main(String[] args) {
        loadHowler(() -> runContract());
    }

    private static void runContract() {
        byte[] audio = wavAudio();
        active = new HowlMusic(new MemoryAudioFile(audio));
        active.setLooping(true);
        active.play();

        pausedBeforeUnlock = new HowlMusic(new MemoryAudioFile(audio));
        pausedBeforeUnlock.play();
        pausedBeforeUnlock.pause();

        stoppedBeforeUnlock = new HowlMusic(new MemoryAudioFile(audio));
        stoppedBeforeUnlock.play();
        stoppedBeforeUnlock.stop();

        disposedBeforeUnlock = new HowlMusic(new MemoryAudioFile(audio));
        disposedBeforeUnlock.play();
        disposedBeforeUnlock.dispose();

        invalidAudio = new HowlMusic(new MemoryAudioFile(new byte[] { 0, 1, 2, 3, 4, 5 }));
        invalidAudio.play();

        retryAfterPlayError = new HowlMusic(new MemoryAudioFile(audio));
        retryAfterPlayError.play();

        terminalPlayError = new HowlMusic(new MemoryAudioFile(audio));
        terminalPlayError.play();

        updateResult("beforeUnlock");
        onDocumentClick(() -> setTimeout(1800, () -> updateResult("afterUnlock")));
        setTimeout(1800, () -> updateResult("afterLoadError"));
    }

    private static byte[] wavAudio() {
        int sampleRate = 22050;
        int sampleCount = sampleRate * 5;
        byte[] wav = new byte[44 + sampleCount];
        copyAscii(wav, 0, "RIFF");
        writeInt(wav, 4, wav.length - 8);
        copyAscii(wav, 8, "WAVE");
        copyAscii(wav, 12, "fmt ");
        writeInt(wav, 16, 16);
        writeShort(wav, 20, 1);
        writeShort(wav, 22, 1);
        writeInt(wav, 24, sampleRate);
        writeInt(wav, 28, sampleRate);
        writeShort(wav, 32, 1);
        writeShort(wav, 34, 8);
        copyAscii(wav, 36, "data");
        writeInt(wav, 40, sampleCount);
        for (int i = 0; i < sampleCount; i++) {
            wav[44 + i] = (byte)(128 + Math.sin(2 * Math.PI * 440 * i / sampleRate) * 48);
        }
        return wav;
    }

    private static void copyAscii(byte[] target, int offset, String value) {
        for (int i = 0; i < value.length(); i++) target[offset + i] = (byte)value.charAt(i);
    }

    private static void writeShort(byte[] target, int offset, int value) {
        target[offset] = (byte)value;
        target[offset + 1] = (byte)(value >>> 8);
    }

    private static void writeInt(byte[] target, int offset, int value) {
        target[offset] = (byte)value;
        target[offset + 1] = (byte)(value >>> 8);
        target[offset + 2] = (byte)(value >>> 16);
        target[offset + 3] = (byte)(value >>> 24);
    }

    private static void updateResult(String phase) {
        String json = "{\"phase\":\"" + phase + "\",\"active\":" + active.isPlaying()
                + ",\"paused\":" + pausedBeforeUnlock.isPlaying()
                + ",\"stopped\":" + stoppedBeforeUnlock.isPlaying()
                + ",\"disposed\":" + disposedBeforeUnlock.isPlaying()
                + ",\"invalid\":" + invalidAudio.isPlaying()
                + ",\"retry\":" + retryAfterPlayError.isPlaying()
                + ",\"terminal\":" + terminalPlayError.isPlaying()
                + ",\"position\":" + active.getPosition()
                + ",\"retryPosition\":" + retryAfterPlayError.getPosition() + "}";
        setGlobalResult(json);
    }

    private static final class MemoryAudioFile extends FileHandle {
        private final byte[] bytes;

        MemoryAudioFile(byte[] bytes) {
            super("music.wav");
            this.bytes = bytes;
        }

        @Override
        public byte[] readBytes() {
            return bytes;
        }
    }

    @JSFunctor
    private interface Callback extends JSObject {
        void run();
    }

    @JSBody(params = {"callback"}, script = "" +
            "var script = document.createElement('script');" +
            "script.src = 'scripts/howler.js';" +
            "script.onload = callback;" +
            "document.head.appendChild(script);")
    private static native void loadHowler(Callback callback);

    @JSBody(params = {"callback"}, script = "document.addEventListener('click', callback);")
    private static native void onDocumentClick(Callback callback);

    @JSBody(params = {"delay", "callback"}, script = "window.setTimeout(callback, delay);")
    private static native void setTimeout(int delay, Callback callback);

    @JSBody(params = {"result"}, script = "window.__howlMusicContract = JSON.parse(result);")
    private static native void setGlobalResult(String result);
}
