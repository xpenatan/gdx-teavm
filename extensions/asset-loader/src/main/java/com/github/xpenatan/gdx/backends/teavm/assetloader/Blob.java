package com.github.xpenatan.gdx.backends.teavm.assetloader;

import java.io.IOException;
import java.io.InputStream;
import org.teavm.jso.typedarrays.ArrayBuffer;
import org.teavm.jso.typedarrays.Int8Array;

/**
 * @author xpenatan
 */
public final class Blob {

    private ArrayBuffer response;
    private final Int8Array data;

    public Blob(ArrayBuffer response, Int8Array data) {
        this.data = data;
        this.response = response;
    }

    public Int8Array getData() {
        return data;
    }

    public ArrayBuffer getResponse() {
        return response;
    }

    public int length() {
        return data.getLength();
    }

    public byte get(int i) {
        return data.get(i);
    }

    public InputStream read() {
        return new InputStream() {

            @Override
            public int read() throws IOException {
                if(pos == length()) return -1;
                return get(pos++) & 0xff;
            }

            @Override
            public int available() {
                return length() - pos;
            }

            int pos;
        };
    }
}
