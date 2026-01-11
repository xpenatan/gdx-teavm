package com.github.xpenatan.gdx.teavm.backend.web;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.teavm.backend.web.filesystem.HEXCoder;
import java.util.HashMap;
import java.util.Map;
import org.teavm.jso.browser.Storage;

/**
 * @author xpenatan
 */
public class TeaPreferences implements Preferences {

    /**
     * Prefix for preferences, so we don't interfere with file keys.
     */
    private static final String ID_FOR_PREF = "pref:";

    final String prefix;
    ObjectMap<String, Object> values = new ObjectMap<String, Object>();

    private Storage storage;

    private boolean shouldEncode;

    public TeaPreferences(Storage storage, String prefix, boolean shouldEncode) {
        this.storage = storage;
        this.prefix = ID_FOR_PREF + prefix + ":";
        this.shouldEncode = shouldEncode;
        int prefixLength = this.prefix.length();
        try {
            for(int i = 0; i < storage.getLength(); i++) {
                String keyEncoded = storage.key(i);
                String key = keyEncoded;
                if(shouldEncode) {
                    key = new String(HEXCoder.decode(keyEncoded));
                }
                boolean flag = key.startsWith(this.prefix);
                if(flag) {
                    String value = storage.getItem(keyEncoded);
                    String keyStr = key.substring(prefixLength, key.length() - 1);
                    Object object;
                    if(shouldEncode) {
                        object = toObject(key, new String(HEXCoder.decode(value)));
                    }
                    else {
                        object = toObject(key, value);
                    }
                    values.put(keyStr, object);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
            values.clear();
        }
    }

    private Object toObject(String key, String value) {
        if(key.endsWith("b")) return Boolean.valueOf(value);
        if(key.endsWith("i")) return Integer.valueOf(value);
        if(key.endsWith("l")) return Long.valueOf(value);
        if(key.endsWith("f")) return Float.valueOf(value);
        return value;
    }

    private String toStorageKey(String key, Object value) {
        if(value instanceof Boolean) return prefix + key + "b";
        if(value instanceof Integer) return prefix + key + "i";
        if(value instanceof Long) return prefix + key + "l";
        if(value instanceof Float) return prefix + key + "f";
        return prefix + key + "s";
    }

    @Override
    public void flush() {
        try {
            // remove all old values
            for(int i = 0; i < storage.getLength(); i++) {
                String keyEncoded = storage.key(i);
                String key = keyEncoded;
                if(shouldEncode) {
                    key = new String(HEXCoder.decode(keyEncoded));
                }
                if(key.startsWith(prefix)) {
                    storage.removeItem(keyEncoded);
                }
            }

            // push new values to LocalStorage
            for(String key : values.keys()) {
                String storageKey = toStorageKey(key, values.get(key));
                String storageValue = "" + values.get(key).toString();
                if(shouldEncode) {
                    storage.setItem(HEXCoder.encode(storageKey.getBytes()), HEXCoder.encode(storageValue.getBytes()));
                }
                else {
                    storage.setItem(storageKey, storageValue);
                }
            }
        }
        catch(Exception e) {
            throw new GdxRuntimeException("Couldn't flush preferences");
        }
    }

    @Override
    public Preferences putBoolean(String key, boolean val) {
        values.put(key, val);
        return this;
    }

    @Override
    public Preferences putInteger(String key, int val) {
        values.put(key, val);
        return this;
    }

    @Override
    public Preferences putLong(String key, long val) {
        values.put(key, val);
        return this;
    }

    @Override
    public Preferences putFloat(String key, float val) {
        values.put(key, val);
        return this;
    }

    @Override
    public Preferences putString(String key, String val) {
        values.put(key, val);
        return this;
    }

    @Override
    public Preferences put(Map<String, ?> vals) {
        for(String key : vals.keySet()) {
            values.put(key, vals.get(key));
        }
        return this;
    }

    @Override
    public boolean getBoolean(String key) {
        Boolean v = (Boolean)values.get(key);
        return v == null ? false : v;
    }

    @Override
    public int getInteger(String key) {
        Integer v = (Integer)values.get(key);
        return v == null ? 0 : v;
    }

    @Override
    public long getLong(String key) {
        Long v = (Long)values.get(key);
        return v == null ? 0 : v;
    }

    @Override
    public float getFloat(String key) {
        Float v = (Float)values.get(key);
        return v == null ? 0 : v;
    }

    @Override
    public String getString(String key) {
        String v = (String)values.get(key);
        return v == null ? "" : v;
    }

    @Override
    public boolean getBoolean(String key, boolean defValue) {
        Boolean res = (Boolean)values.get(key);
        return res == null ? defValue : res;
    }

    @Override
    public int getInteger(String key, int defValue) {
        Integer res = (Integer)values.get(key);
        return res == null ? defValue : res;
    }

    @Override
    public long getLong(String key, long defValue) {
        Long res = (Long)values.get(key);
        return res == null ? defValue : res;
    }

    @Override
    public float getFloat(String key, float defValue) {
        Float res = (Float)values.get(key);
        return res == null ? defValue : res;
    }

    @Override
    public String getString(String key, String defValue) {
        String res = (String)values.get(key);
        return res == null ? defValue : res;
    }

    @Override
    public Map<String, ?> get() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        for(String key : values.keys()) {
            map.put(key, values.get(key));
        }
        return map;
    }

    @Override
    public boolean contains(String key) {
        return values.containsKey(key);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public void remove(String key) {
        values.remove(key);
    }
}
