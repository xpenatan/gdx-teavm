package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;

/**
 * Storage for data in memory (RAM). The limit is on how much memory the browser will allow the JavaScript
 * application.
 *
 * @author noblemaster
 */
class StoreMemory implements Store {

    /**
     * Contains all the data.
     */
    private final Array<String> keys;
    private final ObjectMap<String, String> map;

    StoreMemory() {
        keys = new Array<String>(16);
        map = new ObjectMap<String, String>(16);
    }

    @Override
    public int getLength() {
        return keys.size;
    }

    @Override
    public String key(int i) {
        return keys.get(i);
    }

    @Override
    public String getItem(String key) {
        return map.get(key);
    }

    @Override
    public void setItem(String key, String item) {
        if(!map.containsKey(key)) {
            keys.add(key);
        }
        map.put(key, item);
    }

    @Override
    public void removeItem(String key) {
        keys.removeValue(key, false);
        map.remove(key);
    }

    @Override
    public void clear() {
        keys.clear();
        map.clear();
    }
}
