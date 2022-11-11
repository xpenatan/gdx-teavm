package com.badlogic.gdx.graphics;

import java.nio.ByteBuffer;
import java.nio.DirectReadWriteByteBuffer;
import java.nio.FreeTypeUtil;
import java.nio.HasArrayBufferView;

import org.w3c.dom.html.CanvasPixelArray;
import org.w3c.dom.html.CanvasRenderingContext2D;
import org.w3c.dom.typedarray.ArrayBuffer;
import org.w3c.dom.typedarray.ArrayBufferView;
import org.w3c.dom.typedarray.Uint8ClampedArray;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;

/**
 * @author Simon Gerst
 */
public class FreeTypePixmap extends Pixmap {

    ByteBuffer buffer;

    public FreeTypePixmap(int width, int height, Format format) {
        super(width, height, format);
    }

    public void setPixelsNull() {
        pixels = null;
    }

    public ByteBuffer getRealPixels() {
        if(getWidth() == 0 || getHeight() == 0) {
            return new DirectReadWriteByteBuffer(0);
        }
        if(pixels == null) {
            pixels = getContext().getImageData(0, 0, getWidth(), getHeight()).getData();
            ScriptHelper.put("pix", pixels, this);
            Object obj = ScriptHelper.eval("pix.node.buffer", this);
            ArrayBuffer buff = JsCast.castTo(obj, ArrayBuffer.class);
            buffer = FreeTypeUtil.newDirectReadWriteByteBuffer(buff);
            return buffer;
        }
        return buffer;
    }

    public void putPixelsBack(ByteBuffer pixels) {
        if(getWidth() == 0 || getHeight() == 0) return;
        putPixelsBack(((HasArrayBufferView)pixels).getTypedArray(), getWidth(), getHeight(), getContext());
    }

    private void putPixelsBack(ArrayBufferView pixels, int width, int height, CanvasRenderingContext2D ctx) {
        ScriptHelper.put("width", width, this);
        ScriptHelper.put("height", height, this);
        ScriptHelper.put("ctx", ctx, this);
        ScriptHelper.evalNoResult("ctx=ctx.node", this);
        ScriptHelper.put("pixels", pixels, this);
        ScriptHelper.evalNoResult("pixels=pixels.node", this);
        ScriptHelper.evalNoResult("var imgData = ctx.createImageData(width, height);", this);
        ScriptHelper.evalNoResult("var data = imgData.data;", this);
        ScriptHelper.evalNoResult("for (var i = 0, len = width * height * 4; i < len; i++) { data[i] = pixels[i] & 0xff; }", this);
        ScriptHelper.evalNoResult("ctx.putImageData(imgData, 0, 0);", this);
    }
}