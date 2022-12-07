package emu.java.nio;

import com.github.xpenatan.gdx.backends.web.gen.Emulate;
import java.nio.ByteBuffer;

@Emulate(value = ByteBuffer.class, updateCode = true)
public abstract class ByteBufferEmu {

    public abstract byte[] array();

    @Emulate
    public float getFloat() {
        byte[] bytes = array();
        float value = bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF);
        return value;
    }
}