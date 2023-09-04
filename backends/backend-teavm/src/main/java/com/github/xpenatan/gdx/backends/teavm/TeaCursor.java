package com.github.xpenatan.gdx.backends.teavm;

import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.utils.PNG;

import java.nio.ByteBuffer;

/**
 * Cursor functionality ported for libGDX/GWT. This implementation also supports dynamically created cursor
 * images.
 */
public class TeaCursor implements Cursor {

  String cssCursorProperty;

  public TeaCursor(Pixmap pixmap, int xHotspot, int yHotspot) {
    if (pixmap == null) {
      this.cssCursorProperty = "auto";
      return;
    }

    if (pixmap.getFormat() != com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888) {
      throw new GdxRuntimeException("Cursor image pixmap is not in RGBA8888 format.");
    }

    if ((pixmap.getWidth() & (pixmap.getWidth() - 1)) != 0) {
      throw new GdxRuntimeException(
          "Cursor image pixmap width of " + pixmap.getWidth() + " is not a power-of-two greater than zero.");
    }

    if ((pixmap.getHeight() & (pixmap.getHeight() - 1)) != 0) {
      throw new GdxRuntimeException(
          "Cursor image pixmap height of " + pixmap.getHeight() + " is not a power-of-two greater than zero.");
    }

    if (xHotspot < 0 || xHotspot >= pixmap.getWidth()) {
      throw new GdxRuntimeException(
         "xHotspot coordinate of " + xHotspot + " is not within image width bounds: [0, " + pixmap.getWidth() + ").");
    }

    if (yHotspot < 0 || yHotspot >= pixmap.getHeight()) {
      throw new GdxRuntimeException(
         "yHotspot coordinate of " + yHotspot + " is not within image height bounds: [0, " + pixmap.getHeight() + ").");
    }

    int index = ((ByteBuffer)pixmap.getPixels()).getInt(0);
    Pixmap pixmapEmu = Pixmap.pixmaps.get(index);
    String url;
    if (pixmapEmu != null) {
      // referenced cursor image via URL
      url = "'" + pixmapEmu.getCanvasElement().toDataURL("image/png") + "'";
    }
    else {
      // create custom cursor image from raw-data, e.g. when a cursor was created via Pixmap.drawPixels(...) or so
      int w = pixmap.getWidth();
      int h = pixmap.getHeight();
      byte[] rawBytes = new byte[4 * w * h];
      for (int y = 0; y < h; y++) {
        int offLine = 4 * y * w;
        for (int x = 0; x < w; x++) {
          int pixel = pixmap.getPixel(x, y);
          int off = offLine + (4 * x);
          rawBytes[off    ] = (byte)((pixel >>> 24) & 0xff);
          rawBytes[off + 1] = (byte)((pixel >>> 16) & 0xff);
          rawBytes[off + 2] = (byte)((pixel >>>  8) & 0xff);
          rawBytes[off + 3] = (byte)((pixel      ) & 0xff);
        }
      }

      // convert to uncompressed PNG byte-array
      byte[] pngBytes;
      try {
        pngBytes = PNG.encode(w, h, rawBytes);
      }
      catch (Exception e) {
        throw new GdxRuntimeException("Error encoding bytes as PNG.", e);
      }

      // we pass the images as base64 via CSS
      String base64 = String.valueOf(Base64Coder.encode(pngBytes));
      url = "data:image/png;base64," + base64;
    }
    cssCursorProperty = "url(";
    cssCursorProperty += url;
    cssCursorProperty += ")";
    cssCursorProperty += xHotspot;
    cssCursorProperty += " ";
    cssCursorProperty += yHotspot;
    cssCursorProperty += ",auto";
  }

  static String getNameForSystemCursor (SystemCursor systemCursor) {
    if (systemCursor == SystemCursor.Arrow) {
      return "default";
    } else if (systemCursor == SystemCursor.Crosshair) {
      return "crosshair";
    } else if (systemCursor == SystemCursor.Hand) {
      return "pointer"; // Don't change to 'hand'; 'hand' is non-standard holdover from IE5
    } else if (systemCursor == SystemCursor.HorizontalResize) {
      return "ew-resize";
    } else if (systemCursor == SystemCursor.VerticalResize) {
      return "ns-resize";
    } else if (systemCursor == SystemCursor.Ibeam) {
      return "text";
    } else if (systemCursor == SystemCursor.NWSEResize) {
      return "nwse-resize";
    } else if (systemCursor == SystemCursor.NESWResize) {
      return "nesw-resize";
    } else if (systemCursor == SystemCursor.AllResize) {
      return "move";
    } else if (systemCursor == SystemCursor.NotAllowed) {
      return "not-allowed";
    } else if (systemCursor == SystemCursor.None) {
      return "none";
    } else {
      throw new GdxRuntimeException("Unknown system cursor " + systemCursor);
    }
  }

  @Override
  public void dispose () {
  }
}
