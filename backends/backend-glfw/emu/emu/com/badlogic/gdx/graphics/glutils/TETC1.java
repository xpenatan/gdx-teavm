package emu.com.badlogic.gdx.graphics.glutils;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;
import com.github.xpenatan.gdx.teavm.backends.glfw.GLFWGL32;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import org.teavm.interop.Address;
import org.teavm.interop.Import;
import org.teavm.interop.c.Include;

/**
 * Class for encoding and decoding ETC1 compressed images. Also provides methods to add a PKM header.
 *
 * @author mzechner
 */
@Include("etc1_utils.h")
public class TETC1 {
    /**
     * The PKM header size in bytes
     **/
    public static int PKM_HEADER_SIZE = 16;
    public static int ETC1_RGB8_OES = 0x00008d64;

    /**
     * Class for storing ETC1 compressed image data.
     *
     * @author mzechner
     */
    public final static class ETC1Data implements Disposable {
        /**
         * the width in pixels
         **/
        public final int width;
        /**
         * the height in pixels
         **/
        public final int height;
        /**
         * the optional PKM header and compressed image data
         **/
        public final ByteBuffer compressedData;
        /**
         * the offset in bytes to the actual compressed data. Might be 16 if this contains a PKM header, 0 otherwise
         **/
        public final int dataOffset;

        public ETC1Data(int width, int height, ByteBuffer compressedData, int dataOffset) {
            this.width = width;
            this.height = height;
            this.compressedData = compressedData;
            this.dataOffset = dataOffset;
            checkNPOT();
        }

        public ETC1Data(FileHandle pkmFile) {
            byte[] buffer = new byte[1024 * 10];
            DataInputStream in = null;
            try {
                in = new DataInputStream(new BufferedInputStream(new GZIPInputStream(pkmFile.read())));
                int fileSize = in.readInt();
                compressedData = BufferUtils.newUnsafeByteBuffer(fileSize);
                int readBytes = 0;
                while((readBytes = in.read(buffer)) != -1) {
                    compressedData.put(buffer, 0, readBytes);
                }
                ((Buffer)compressedData).position(0);
                ((Buffer)compressedData).limit(compressedData.capacity());
            } catch(Exception e) {
                throw new GdxRuntimeException("Couldn't load pkm file '" + pkmFile + "'", e);
            } finally {
                StreamUtils.closeQuietly(in);
            }

            width = getWidthPKM(compressedData, 0);
            height = getHeightPKM(compressedData, 0);
            dataOffset = PKM_HEADER_SIZE;
            ((Buffer)compressedData).position(dataOffset);
            checkNPOT();
        }

        private void checkNPOT() {
            if(!MathUtils.isPowerOfTwo(width) || !MathUtils.isPowerOfTwo(height)) {
                System.out.println("ETC1Data " + "warning: non-power-of-two ETC1 textures may crash the driver of PowerVR GPUs");
            }
        }

        /**
         * @return whether this ETC1Data has a PKM header
         */
        public boolean hasPKMHeader() {
            return dataOffset == 16;
        }

        /**
         * Writes the ETC1Data with a PKM header to the given file.
         *
         * @param file the file.
         */
        public void write(FileHandle file) {
            DataOutputStream write = null;
            byte[] buffer = new byte[10 * 1024];
            int writtenBytes = 0;
            ((Buffer)compressedData).position(0);
            ((Buffer)compressedData).limit(compressedData.capacity());
            try {
                write = new DataOutputStream(new GZIPOutputStream(file.write(false)));
                write.writeInt(compressedData.capacity());
                while(writtenBytes != compressedData.capacity()) {
                    int bytesToWrite = Math.min(compressedData.remaining(), buffer.length);
                    compressedData.get(buffer, 0, bytesToWrite);
                    write.write(buffer, 0, bytesToWrite);
                    writtenBytes += bytesToWrite;
                }
            } catch(Exception e) {
                throw new GdxRuntimeException("Couldn't write PKM file to '" + file + "'", e);
            } finally {
                StreamUtils.closeQuietly(write);
            }
            ((Buffer)compressedData).position(dataOffset);
            ((Buffer)compressedData).limit(compressedData.capacity());
        }

        /**
         * Releases the native resources of the ETC1Data instance.
         */
        public void dispose() {
            BufferUtils.disposeUnsafeByteBuffer(compressedData);
        }

        public String toString() {
            if(hasPKMHeader()) {
                return (TETC1.isValidPKM(compressedData, 0) ? "valid" : "invalid") + " pkm [" + TETC1.getWidthPKM(compressedData, 0)
                        + "x" + TETC1.getHeightPKM(compressedData, 0) + "], compressed: "
                        + (compressedData.capacity() - TETC1.PKM_HEADER_SIZE);
            }
            else {
                return "raw [" + width + "x" + height + "], compressed: " + (compressedData.capacity() - TETC1.PKM_HEADER_SIZE);
            }
        }
    }

    private static int getPixelSize(Format format) {
        if(format == Format.RGB565) return 2;
        if(format == Format.RGB888) return 3;
        throw new GdxRuntimeException("Can only handle RGB565 or RGB888 images");
    }

    /**
     * Encodes the image via the ETC1 compression scheme. Only {@link Format#RGB565} and {@link Format#RGB888} are supported.
     *
     * @param pixmap the {@link Pixmap}
     * @return the {@link ETC1Data}
     */
    public static ETC1Data encodeImage(Pixmap pixmap) {
        int pixelSize = getPixelSize(pixmap.getFormat());
        ByteBuffer compressedData = encodeImage(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), pixelSize);
        BufferUtils.newUnsafeByteBuffer(compressedData);
        return new ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 0);
    }

    /**
     * Encodes the image via the ETC1 compression scheme. Only {@link Format#RGB565} and {@link Format#RGB888} are supported. Adds
     * a PKM header in front of the compressed image data.
     *
     * @param pixmap the {@link Pixmap}
     * @return the {@link ETC1Data}
     */
    public static ETC1Data encodeImagePKM(Pixmap pixmap) {
        int pixelSize = getPixelSize(pixmap.getFormat());
        ByteBuffer compressedData = encodeImagePKM(pixmap.getPixels(), 0, pixmap.getWidth(), pixmap.getHeight(), pixelSize);
        BufferUtils.newUnsafeByteBuffer(compressedData);
        return new ETC1Data(pixmap.getWidth(), pixmap.getHeight(), compressedData, 16);
    }

    /**
     * Takes ETC1 compressed image data and converts it to a {@link Format#RGB565} or {@link Format#RGB888} {@link Pixmap}. Does
     * not modify the ByteBuffer's position or limit.
     *
     * @param etc1Data the {@link ETC1Data} instance
     * @param format   either {@link Format#RGB565} or {@link Format#RGB888}
     * @return the Pixmap
     */
    public static Pixmap decodeImage(ETC1Data etc1Data, Format format) {
        int dataOffset = 0;
        int width = 0;
        int height = 0;

        if(etc1Data.hasPKMHeader()) {
            dataOffset = 16;
            width = TETC1.getWidthPKM(etc1Data.compressedData, 0);
            height = TETC1.getHeightPKM(etc1Data.compressedData, 0);
        }
        else {
            dataOffset = 0;
            width = etc1Data.width;
            height = etc1Data.height;
        }

        int pixelSize = getPixelSize(format);
        Pixmap pixmap = new Pixmap(width, height, format);
        decodeImage(etc1Data.compressedData, dataOffset, pixmap.getPixels(), 0, width, height, pixelSize);
        return pixmap;
    }

    // @off
    /*JNI
    #include <etc1/etc1_utils.h>
    #include <stdlib.h>
     */

    /**
     * @param width  the width in pixels
     * @param height the height in pixels
     * @return the number of bytes needed to store the compressed data
     */
    public static int getCompressedDataSize(int width, int height) {
        return etc1_get_encoded_data_size(width, height);
    }
    /*
        return etc1_get_encoded_data_size(width, height);
    */

    /**
     * Writes a PKM header to the {@link ByteBuffer}. Does not modify the position or limit of the ByteBuffer.
     *
     * @param header the direct native order {@link ByteBuffer}
     * @param offset the offset to the header in bytes
     * @param width  the width in pixels
     * @param height the height in pixels
     */
    public static void formatHeader(ByteBuffer header, int offset, int width, int height) {
        Address address = GLFWGL32.AddressUtils.ofInternal(header).add(offset);
        etc1_pkm_format_header(address, width, height);
    }
    /*
        etc1_pkm_format_header((etc1_byte*)header + offset, width, height);
    */

    /**
     * @param header direct native order {@link ByteBuffer} holding the PKM header
     * @param offset the offset in bytes to the PKM header from the ByteBuffer's start
     * @return the width stored in the PKM header
     */
    static int getWidthPKM(ByteBuffer header, int offset) {
//        Address address = GLFWGL32.AddressUtils.ofInternal(header).add(offset);
//        return etc1_pkm_get_width(address);
        return 0;
    }
    /*
        return etc1_pkm_get_width((etc1_byte*)header + offset);
    */

    /**
     * @param header direct native order {@link ByteBuffer} holding the PKM header
     * @param offset the offset in bytes to the PKM header from the ByteBuffer's start
     * @return the height stored in the PKM header
     */
    static int getHeightPKM(ByteBuffer header, int offset) {
//        Address address = GLFWGL32.AddressUtils.ofInternal(header).add(offset);
//        return etc1_pkm_get_height(address);
        return 0;
    }
    /*
        return etc1_pkm_get_height((etc1_byte*)header + offset);
    */

    /**
     * @param header direct native order {@link ByteBuffer} holding the PKM header
     * @param offset the offset in bytes to the PKM header from the ByteBuffer's start
     * @return the width stored in the PKM header
     */
    static boolean isValidPKM(ByteBuffer header, int offset) {
        Address address = GLFWGL32.AddressUtils.ofInternal(header).add(offset);
        return etc1_pkm_is_valid(address);
    }
    /*
        return etc1_pkm_is_valid((etc1_byte*)header + offset) != 0?true:false;
    */

    /**
     * Decodes the compressed image data to RGB565 or RGB888 pixel data. Does not modify the position or limit of the
     * {@link ByteBuffer} instances.
     *
     * @param compressedData the compressed image data in a direct native order {@link ByteBuffer}
     * @param offset         the offset in bytes to the image data from the start of the buffer
     * @param decodedData    the decoded data in a direct native order ByteBuffer, must hold width * height * pixelSize bytes.
     * @param offsetDec      the offset in bytes to the decoded image data.
     * @param width          the width in pixels
     * @param height         the height in pixels
     * @param pixelSize      the pixel size, either 2 (RBG565) or 3 (RGB888)
     */
    private static void decodeImage(ByteBuffer compressedData, int offset, ByteBuffer decodedData, int offsetDec,
                                           int width, int height, int pixelSize) {
//        Address compressedDataAddress = GLFWGL32.AddressUtils.ofInternal(compressedData).add(offset);
//        Address decodedDataAddress = GLFWGL32.AddressUtils.ofInternal(decodedData).add(offsetDec);
//        etc1_decode_image(compressedDataAddress, decodedDataAddress, width, height, pixelSize, width * pixelSize);
    }
    /*
        etc1_decode_image((etc1_byte*)compressedData + offset, (etc1_byte*)decodedData + offsetDec, width, height, pixelSize, width * pixelSize);
    */

    /**
     * Encodes the image data given as RGB565 or RGB888. Does not modify the position or limit of the {@link ByteBuffer}.
     *
     * @param imageData the image data in a direct native order {@link ByteBuffer}
     * @param offset    the offset in bytes to the image data from the start of the buffer
     * @param width     the width in pixels
     * @param height    the height in pixels
     * @param pixelSize the pixel size, either 2 (RGB565) or 3 (RGB888)
     * @return a new direct native order ByteBuffer containing the compressed image data
     */
    private static ByteBuffer encodeImage(ByteBuffer imageData, int offset, int width, int height, int pixelSize) {
        int compressedSize = etc1_get_encoded_data_size(width, height);
        ByteBuffer compressedData = ByteBuffer.allocate(compressedSize);
        Address imageDataAddress = GLFWGL32.AddressUtils.ofInternal(imageData).add(offset);
        etc1_encode_image(imageDataAddress, width, height, pixelSize, width * pixelSize, compressedData);
        return compressedData;
    }
    /*
        int compressedSize = etc1_get_encoded_data_size(width, height);
        etc1_byte* compressedData = (etc1_byte*)malloc(compressedSize);
        etc1_encode_image((etc1_byte*)imageData + offset, width, height, pixelSize, width * pixelSize, compressedData);
        return env->NewDirectByteBuffer(compressedData, compressedSize);
    */

    /**
     * Encodes the image data given as RGB565 or RGB888. Does not modify the position or limit of the {@link ByteBuffer}.
     *
     * @param imageData the image data in a direct native order {@link ByteBuffer}
     * @param offset    the offset in bytes to the image data from the start of the buffer
     * @param width     the width in pixels
     * @param height    the height in pixels
     * @param pixelSize the pixel size, either 2 (RGB565) or 3 (RGB888)
     * @return a new direct native order ByteBuffer containing the compressed image data
     */
    private static ByteBuffer encodeImagePKM(ByteBuffer imageData, int offset, int width, int height, int pixelSize) {
        int ETC_PKM_HEADER_SIZE = 16;
        int compressedSize = etc1_get_encoded_data_size(width, height);
        ByteBuffer compressedData = ByteBuffer.allocate(compressedSize + ETC_PKM_HEADER_SIZE);
        Address compressedDataAddress = GLFWGL32.AddressUtils.of(compressedData);
        etc1_pkm_format_header(compressedDataAddress, width, height);
        Address imageDataAddress = GLFWGL32.AddressUtils.ofInternal(imageData).add(offset);
        etc1_encode_image(imageDataAddress, width, height, pixelSize, width * pixelSize, compressedData); // TODO FIX
        return compressedData;
    }
    /*
        int compressedSize = etc1_get_encoded_data_size(width, height);
        etc1_byte* compressed = (etc1_byte*)malloc(compressedSize + ETC_PKM_HEADER_SIZE);
        etc1_pkm_format_header(compressed, width, height);
        etc1_encode_image((etc1_byte*)imageData + offset, width, height, pixelSize, width * pixelSize, compressed + ETC_PKM_HEADER_SIZE);
        return env->NewDirectByteBuffer(compressed, compressedSize + ETC_PKM_HEADER_SIZE);
    */


    @Import(name = "etc1_get_encoded_data_size")
    private static native int etc1_get_encoded_data_size(int width, int height);

    @Import(name = "etc1_pkm_format_header")
    private static native void etc1_pkm_format_header(Address header, int width, int height);

    // TODO fix

//    @Import(name = "etc1_pkm_get_width")
//    private static native int etc1_pkm_get_width(Address header);
//
//    @Import(name = "etc1_pkm_get_height")
//    private static native int etc1_pkm_get_height(Address header);

    @Import(name = "etc1_pkm_is_valid")
    private static native boolean etc1_pkm_is_valid(Address header);

//    @Import(name = "etc1_decode_image")
//    private static native int etc1_decode_image(Address pIn, Address pOut,
//                                                    int width, int height,
//                                                    int pixelSize, int stride);

    @Import(name = "etc1_encode_image")
    private static native int etc1_encode_image(Address pIn, int width, int height,
                                                int pixelSize, int stride, Buffer pOut);
}
