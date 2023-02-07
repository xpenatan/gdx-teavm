package com.github.xpenatan.gdx.backends.web.filesystem;

/**
 * Utility class to encode and decode from HEX.
 *
 * @author noblemaster
 */
public class HEXCoder {

  /** HEX chars. */
  private static final char[] HEX_CHARS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
  /** HEX lookup. */
  private static final int[]  HEX_BYTES = new int[128];

  static {
    // init byte lookup
    for (int i = 0; i < HEX_CHARS.length; i++) {
      HEX_BYTES[HEX_CHARS[i]] = i;
      if (HEX_CHARS[i] >= 'A') {
        HEX_BYTES[HEX_CHARS[i] - 'A' + 'a'] = i;  // lowercase also!
      }
    }
  }

  public static byte[] decode(String string) {
    byte[] bytes = new byte[string.length() / 2];
    for (int i = 0; i < bytes.length; i++) {
      char c0 = string.charAt((i * 2)    );
      char c1 = string.charAt((i * 2) + 1);
      bytes[i] = (byte)((HEX_BYTES[c0] << 4) + (HEX_BYTES[c1]));
    }
    return bytes;
  }

  public static String encode(byte[] bytes) {
    char[] chars = new char[bytes.length * 2];
    for (int i = 0; i < bytes.length; i++) {
      byte b = bytes[i];
      chars[(i * 2)]     = HEX_CHARS[(b & 0xFF) >>> 4];
      chars[(i * 2) + 1] = HEX_CHARS[(b & 0x0F)];
    }
    return String.valueOf(chars);
  }
}
