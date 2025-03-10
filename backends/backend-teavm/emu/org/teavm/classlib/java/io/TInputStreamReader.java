package org.teavm.classlib.java.io;

import org.teavm.classlib.java.nio.TByteBuffer;
import java.io.IOException;
import org.teavm.classlib.java.nio.TCharBuffer;
import org.teavm.classlib.java.nio.charset.TCharset;
import org.teavm.classlib.java.nio.charset.TCharsetDecoder;
import org.teavm.classlib.java.nio.charset.TCodingErrorAction;
import org.teavm.classlib.java.nio.charset.TUnsupportedCharsetException;
import org.teavm.classlib.java.nio.charset.impl.TUTF8Charset;

public class TInputStreamReader extends TReader {
    private TInputStream stream;
    private TCharsetDecoder decoder;
    private byte[] inData = new byte[8192];
    private TByteBuffer inBuffer = TByteBuffer.wrap(inData);
    private char[] outData = new char[1024];
    private TCharBuffer outBuffer = TCharBuffer.wrap(outData);
    private boolean streamEof;
    private boolean eof;

    public TInputStreamReader(TInputStream in, String charsetName) throws TUnsupportedEncodingException {
        this(in, getCharset(charsetName));
    }

    public TInputStreamReader(TInputStream in, TCharset charset) {
        this(in, charset.newDecoder()
                .onMalformedInput(TCodingErrorAction.REPLACE)
                .onUnmappableCharacter(TCodingErrorAction.REPLACE));
    }

    public TInputStreamReader(TInputStream in) {
        this(in, TUTF8Charset.INSTANCE);
    }

    public TInputStreamReader(TInputStream in, TCharsetDecoder decoder) {
        this.stream = in;
        this.decoder = decoder;
        outBuffer.position(outBuffer.limit());
        inBuffer.position(inBuffer.limit());
    }

    private static TCharset getCharset(String charsetName) throws TUnsupportedEncodingException  {
        try {
            return TCharset.forName(charsetName.toString());
        } catch (TUnsupportedCharsetException e) {
            throw new TUnsupportedEncodingException(charsetName);
        }
    }

    public String getEncoding() {
        return decoder.charset().name();
    }

    @Override
    public void close() throws IOException {
        stream.close();
    }

    @Override
    public int read() throws IOException {
        if (eof && !outBuffer.hasRemaining()) {
            return -1;
        }
        if (outBuffer.hasRemaining()) {
            return outBuffer.get();
        }
        return fillBuffer() ? outBuffer.get() : -1;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        if (eof && !outBuffer.hasRemaining()) {
            return -1;
        }
        int bytesRead = 0;
        while (len > 0) {
            int sz = Math.min(len, outBuffer.remaining());
            outBuffer.get(cbuf, off + bytesRead, sz);
            len -= sz;
            bytesRead += sz;
            if (!outBuffer.hasRemaining() && !fillBuffer()) {
                break;
            }
        }
        return bytesRead;
    }

    private boolean fillBuffer() throws IOException {
        if (eof) {
            return false;
        }
        outBuffer.compact();
        while (true) {
            if (!inBuffer.hasRemaining() && !fillReadBuffer()) {
                break;
            }
            var result = decoder.decode(inBuffer, outBuffer, streamEof);
            if (result.isOverflow()) {
                break;
            } else if (result.isUnderflow()) {
                fillReadBuffer();
            }
        }
        if (!inBuffer.hasRemaining() && streamEof && decoder.flush(outBuffer).isUnderflow()) {
            eof = true;
        }
        outBuffer.flip();
        return true;
    }

    private boolean fillReadBuffer() throws IOException {
        if (streamEof) {
            return false;
        }
        inBuffer.compact();
        while (inBuffer.hasRemaining()) {
            byte[] array = inBuffer.array();
            int bytesRead = stream.read(array, inBuffer.position(), inBuffer.remaining());
            // This is the only part that was changed. We update the array back to js array
            inBuffer.putArray(array);
            if (bytesRead == -1) {
                streamEof = true;
                break;
            } else {
                inBuffer.position(inBuffer.position() + bytesRead);
                if (bytesRead == 0) {
                    break;
                }
            }
        }
        inBuffer.flip();
        return true;
    }

    @Override
    public boolean ready() throws IOException {
        return outBuffer.hasRemaining() || inBuffer.hasRemaining();
    }
}
