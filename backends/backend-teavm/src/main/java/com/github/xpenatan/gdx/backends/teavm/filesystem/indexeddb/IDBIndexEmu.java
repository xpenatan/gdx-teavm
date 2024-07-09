package com.github.xpenatan.gdx.backends.teavm.filesystem.indexeddb;

import com.github.xpenatan.gdx.backends.teavm.gen.Emulate;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSByRef;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSString;
import org.teavm.jso.indexeddb.IDBCountRequest;
import org.teavm.jso.indexeddb.IDBCursorRequest;
import org.teavm.jso.indexeddb.IDBCursorSource;
import org.teavm.jso.indexeddb.IDBGetRequest;
import org.teavm.jso.indexeddb.IDBIndex;
import org.teavm.jso.indexeddb.IDBKeyRange;

@Emulate(IDBIndex.class)
public abstract class IDBIndexEmu implements JSObject, IDBCursorSource {
    @JSProperty
    public abstract String getName();

    @JSProperty("keyPath")
    abstract JSObject getKeyPathImpl();

    public final String[] getKeyPath() {
        JSObject result = getKeyPathImpl();
        if (JSString.isInstance(result)) {
            return new String[] { result.<JSString>cast().stringValue() };
        } else {
            return unwrapStringArray(result);
        }
    }

    @JSBody(params = "obj", script = "return obj;")
    private static native String[] unwrapStringArray(JSObject obj);

    @JSProperty
    public abstract boolean isMultiEntry();

    @JSProperty
    public abstract boolean isUnique();

    public abstract IDBCursorRequest openCursor();

    public abstract IDBCursorRequest openCursor(IDBKeyRange range);

    public abstract IDBCursorRequest openKeyCursor();

    public abstract IDBGetRequest get(JSObject key);

    public abstract IDBGetRequest getKey(JSObject key);

    public abstract IDBCountRequest count(JSObject key);

    public abstract IDBCountRequest count();
}
