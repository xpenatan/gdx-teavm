package emu.java.nio.channels;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import java.nio.channels.FileChannel;

@Emulate(FileChannel.class)
public abstract class FileChannelEmu {

    @Emulate(FileChannel.MapMode.class)
    public static class MapMode {
        public static final MapMode READ_ONLY = new MapMode();
        public static final MapMode READ_WRITE = new MapMode();
        public static final MapMode PRIVATE = new MapMode();
    }
}
