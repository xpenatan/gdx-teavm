package com.github.xpenatan.gdx.backends.teavm.filesystem.indexeddb;

import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSDate;
import org.teavm.jso.typedarrays.Int8Array;

public abstract class IndexedDBFileData implements JSObject {
    @JSProperty
    public abstract void setContents(byte[] contents);

    @JSProperty
    public abstract Int8Array getContents();

    @JSProperty
    public abstract int getType();

    @JSProperty
    public abstract JSDate getTimestamp();

    @JSBody(params = { "type", "timestamp" }, script = "return {type: type, date: timestamp};")
    public static native IndexedDBFileData create(int type, JSDate timestamp);
}