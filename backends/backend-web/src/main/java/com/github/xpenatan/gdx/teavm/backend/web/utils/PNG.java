package com.github.xpenatan.gdx.teavm.backend.web.utils;

import java.io.ByteArrayOutputStream;

/*
 * Minimal PNG encoder to create PNG streams (and MIDP images) from RGBA arrays.
 *
 * Copyright 2006-2009 Christian Fröschlin
 *
 * www.chrfr.de
 *
 *
 * Changelog:
 *
 * 09/22/08: Fixed Adler checksum calculation and byte order
 *           for storing length of zlib deflate block. Thanks
 *           to Miloslav Ruzicka for noting this.
 *
 * 05/12/09: Split PNG and ZLIB functionality into separate classes.
 *           Added support for images > 64K by splitting the data into
 *           multiple uncompressed deflate blocks.
 *
 * Terms of Use:
 *
 * You may use the PNG encoder free of charge for any purpose you desire, as long
 * as you do not claim credit for the original sources and agree not to hold me
 * responsible for any damage arising out of its use.
 *
 * If you have a suitable location in GUI or documentation for giving credit,
 * I'd appreciate a mention of
 *
 *  PNG encoder (C) 2006-2009 by Christian Fröschlin, www.chrfr.de
 *
 * but that's not mandatory.
 *
 * @author Christian Fröschlin
 * @see <a href="http://www.chrfr.de/software/PNG.java">Christian Fröschlin's PNG.java</a>
 */
public final class PNG {

  private static final byte[] ZERO_BYTE = new byte[0];
  private static final byte[] SIGNATURE = new byte[] {(byte) 137, (byte) 80, (byte) 78, (byte) 71,
                                                      (byte)  13, (byte) 10, (byte) 26, (byte) 10};

  private PNG() {
    // prevents instantiation
  }

  /**
   * Generate an uncompressed PNG image from a byte-array.  The pixel array must contain (width * height) pixels.
   *
   * @param w            The width of image, in pixels
   * @param h            The height of image, in pixels
   * @param rgba         Color information in RGBA-format.
   * @return PNG data as byte-array.
   */
  public static byte[] encode(int w, int h, byte[] rgba) throws Exception {
    // use default non-compressed method
    byte[] ihdr = createIHDRChunk(w, h);
    byte[] idat = createIDATChunk(w, h, rgba);
    byte[] iend = createIENDChunk();

    ByteArrayOutputStream output = new ByteArrayOutputStream(SIGNATURE.length + ihdr.length + idat.length + iend.length);
    output.write(SIGNATURE);
    output.write(ihdr);
    output.write(idat);
    output.write(iend);

    return output.toByteArray();
  }

  private static byte[] createIHDRChunk(int w, int h) throws Exception {
    ByteArrayOutputStream chunk = new ByteArrayOutputStream(13);
    chunk.write((byte) ((w >>> 24) & 0xFF));
    chunk.write((byte) ((w >>> 16) & 0xFF));
    chunk.write((byte) ((w >>>  8) & 0xFF));
    chunk.write((byte) ((w       ) & 0xFF));
    chunk.write((byte) ((h >>> 24) & 0xFF));
    chunk.write((byte) ((h >>> 16) & 0xFF));
    chunk.write((byte) ((h >>>  8) & 0xFF));
    chunk.write((byte) ((h       ) & 0xFF));
    chunk.write((byte) 8); // Bitdepth
    chunk.write((byte) 6); // Colortype ARGB
    chunk.write((byte) 0); // Compression
    chunk.write((byte) 0); // Filter
    chunk.write((byte) 0); // Interlace
    return toChunk("IHDR", chunk.toByteArray());
  }

  private static byte[] createIDATChunk(int w, int h, byte[] rgba) throws Exception {
    int source = 0;
    int dest = 0;
    byte[] raw = new byte[4 * (w * h) + h];
    for (int y = 0; y < h; y++) {
      raw[dest++] = 0; // No filter
      for (int x = 0; x < w; x++) {
        raw[dest++] = rgba[source    ];   // red
        raw[dest++] = rgba[source + 1];   // green
        raw[dest++] = rgba[source + 2];   // blue
        raw[dest++] = rgba[source + 3];   // alpha
        source += 4;
      }
    }
    return toChunk("IDAT", toZLIB(raw));
  }

  private static byte[] createIENDChunk() throws Exception {
    return toChunk("IEND", ZERO_BYTE);
  }

  private static byte[] toChunk(String id, byte[] raw) throws Exception {
    ByteArrayOutputStream chunk = new ByteArrayOutputStream(raw.length + 12);

    chunk.write((byte) ((raw.length >>> 24) & 0xFF));
    chunk.write((byte) ((raw.length >>> 16) & 0xFF));
    chunk.write((byte) ((raw.length >>>  8) & 0xFF));
    chunk.write((byte) ((raw.length       ) & 0xFF));

    byte[] bid = new byte[4];
    for (int i = 0; i < 4; i++) {
      bid[i] = (byte) id.charAt(i);
    }

    chunk.write(bid);

    chunk.write(raw);

    int crc = 0xFFFFFFFF;
    crc = updateCRC(crc, bid);
    crc = updateCRC(crc, raw);
    int ncrs = ~crc;
    chunk.write((byte) ((ncrs >>> 24) & 0xFF));
    chunk.write((byte) ((ncrs >>> 16) & 0xFF));
    chunk.write((byte) ((ncrs >>>  8) & 0xFF));
    chunk.write((byte) ((ncrs       ) & 0xFF));

    return chunk.toByteArray();
  }

  private static int[] crcTable = null;

  private static void createCRCTable() {
    crcTable = new int[256];

    for (int i = 0; i < 256; i++) {
      int c = i;
      for (int k = 0; k < 8; k++) {
        c = ((c & 1) > 0) ? 0xedb88320 ^ (c >>> 1) : c >>> 1;
      }
      crcTable[i] = c;
    }
  }

  private static int updateCRC(int crc, byte[] raw) {
    if (crcTable == null) {
      createCRCTable();
    }
    for (int i = 0; i < raw.length; i++) {
      crc = crcTable[(crc ^ raw[i]) & 0xFF] ^ (crc >>> 8);
    }
    return crc;
  }

  /**
   * This method is called to encode the image data as a zlib block as required by the PNG specification. This file
   * comes with a minimal ZLIB encoder which uses uncompressed deflate blocks (fast, short, easy, but no compression).
   * If you want compression, call another encoder (such as JZLib?) here.
   */
  private static byte[] toZLIB(byte[] raw) throws Exception {
    return ZLIB.toZLIB(raw);
  }

  /**
   * ZLIB utility.
   */
  private static final class ZLIB {

    static final int BLOCK_SIZE = 32000;

    private ZLIB() {
      // not used
    }

    public static byte[] toZLIB(byte[] raw) throws Exception {
      ByteArrayOutputStream zlib = new ByteArrayOutputStream(raw.length + 6 + (raw.length / BLOCK_SIZE) * 5);

      byte tmp = (byte) 8;
      zlib.write(tmp); // CM = 8, CMINFO = 0
      zlib.write((byte) ((31 - ((tmp << 8) % 31)) % 31)); // FCHECK(FDICT/FLEVEL=0)

      int pos = 0;
      while (raw.length - pos > BLOCK_SIZE) {
        writeUncompressedDeflateBlock(zlib, false, raw, pos, (char) BLOCK_SIZE);
        pos += BLOCK_SIZE;
      }
      writeUncompressedDeflateBlock(zlib, true, raw, pos, (char) (raw.length - pos));

      // zlib check sum of uncompressed data
      int alder32 = calcADLER32(raw);
      zlib.write((byte) ((alder32 >>> 24) & 0xFF));
      zlib.write((byte) ((alder32 >>> 16) & 0xFF));
      zlib.write((byte) ((alder32 >>>  8) & 0xFF));
      zlib.write((byte) ((alder32       ) & 0xFF));

      return zlib.toByteArray();
    }

    private static void writeUncompressedDeflateBlock(ByteArrayOutputStream zlib, boolean last, byte[] raw, int off, char len) throws Exception {
      zlib.write((byte) (last ? 1 : 0)); // Final flag, Compression type 0
      zlib.write((byte) (len & 0xFF)); // Length LSB
      zlib.write((byte) ((len & 0xFF00) >> 8)); // Length MSB
      zlib.write((byte) (~len & 0xFF)); // Length 1st complement LSB
      zlib.write((byte) ((~len & 0xFF00) >> 8)); // Length 1st complement MSB
      zlib.write(raw, off, len); // Data
    }

    private static int calcADLER32(byte[] raw) {
      int s1 = 1;
      int s2 = 0;
      for (int i = 0; i < raw.length; i++) {
        int abs = raw[i] >= 0 ? raw[i] : (raw[i] + 256);
        s1 = (s1 + abs) % 65521;
        s2 = (s2 + s1) % 65521;
      }
      return (s2 << 16) + s1;
    }
  }
}