package emu.com.badlogic.gdx.graphics;

import com.badlogic.gdx.utils.GdxRuntimeException;
import org.junit.Test;

import java.nio.ByteBuffer;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class PixmapByteBufferTest {

    @Test
    public void copyEncodedDataUsesBufferBaseOffsetAndPreservesPosition() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(8);
        buffer.put(new byte[]{10, 11, 12, 13, 14, 15, 16, 17});
        buffer.position(2);
        buffer.limit(6);

        byte[] actual = Pixmap.copyEncodedData(buffer, buffer.position(), buffer.remaining());

        assertArrayEquals(new byte[]{12, 13, 14, 15}, actual);
        assertEquals(2, buffer.position());
        assertEquals(6, buffer.limit());
    }

    @Test
    public void copyEncodedDataRejectsRangesOutsideTheBufferLimit() {
        ByteBuffer buffer = ByteBuffer.allocateDirect(4);

        IndexOutOfBoundsException error = assertThrows(IndexOutOfBoundsException.class,
                () -> Pixmap.copyEncodedData(buffer, 3, 2));

        assertThat(error).hasMessageThat().contains("offset=3, length=2, limit=4");
    }

    @Test
    public void pixmapRejectsNonDirectByteBufferLikeLibgdx() {
        GdxRuntimeException error = assertThrows(GdxRuntimeException.class,
                () -> new Pixmap(ByteBuffer.allocate(4)));

        assertThat(error).hasMessageThat().contains("non-direct ByteBuffer");
    }
}
