package java.nio.channels;

import com.github.xpenatan.gdx.backends.web.gen.Emulate;

@Emulate(FileChannel.class)
public abstract class FileChannelEmu {

    public static class MapMode {
        public static final MapMode READ_ONLY = new MapMode();
        public static final MapMode READ_WRITE = new MapMode();
        public static final MapMode PRIVATE = new MapMode();
    }
}
