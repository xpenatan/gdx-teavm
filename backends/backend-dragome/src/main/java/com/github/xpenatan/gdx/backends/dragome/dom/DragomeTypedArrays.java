package com.github.xpenatan.gdx.backends.dragome.dom;

import com.dragome.commons.javascript.ScriptHelper;
import com.dragome.web.enhancers.jsdelegate.JsCast;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Float32ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Float64ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int16ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int32ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Uint8ArrayWrapper;

/**
 * @author xpenatan
 */
public class DragomeTypedArrays extends TypedArrays {

    public DragomeTypedArrays() {
        TypedArrays.setInstance(this);
    }

    @Override
    public Float32ArrayWrapper createFloat32Array(int length) {
        ScriptHelper.put("length", length, this);
        Object eval = ScriptHelper.eval("new Float32Array(length)", this);
        return JsCast.castTo(eval, Float32ArrayWrapper.class, this);
    }

    @Override
    public Int32ArrayWrapper createInt32Array(int length) {
        ScriptHelper.put("length", length, this);
        Object eval = ScriptHelper.eval("new Int32Array(length)", this);
        return JsCast.castTo(eval, Int32ArrayWrapper.class);
    }

    @Override
    public Int16ArrayWrapper createInt16Array(int length) {
        ScriptHelper.put("length", length, this);
        Object eval = ScriptHelper.eval("new Int16Array(length)", this);
        return JsCast.castTo(eval, Int16ArrayWrapper.class);
    }

    @Override
    public Uint8ArrayWrapper createUint8Array(int length) {
        ScriptHelper.put("length", length, this);
        Object eval = ScriptHelper.eval("new Uint8Array(length)", this);
        return JsCast.castTo(eval, Uint8ArrayWrapper.class);
    }

    @Override
    public Float64ArrayWrapper createFloat64Array(int length) {
        ScriptHelper.put("length", length, this);
        Object eval = ScriptHelper.eval("new Float64Array(length)", this);
        return JsCast.castTo(eval, Float64ArrayWrapper.class);
    }

    @Override
    public Int8ArrayWrapper createInt8Array(int length) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Int8ArrayWrapper createInt8Array(ArrayBufferWrapper buffer) {
        // TODO Auto-generated method stub
        return null;
    }
}
