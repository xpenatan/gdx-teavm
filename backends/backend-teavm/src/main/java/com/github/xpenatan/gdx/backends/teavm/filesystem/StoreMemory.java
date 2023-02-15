package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;

/**
 * Storage for data in memory.
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

    /**
     * Removes large files as needed to prevent out of memory problems.
     */
    void cleanup() {
        // remove large files if we are above max. bytes
        long maxChars = 10000000;
        boolean cleaned = true;
        while(cleaned) {
            // calculate new total
            long total = 0;
            for(String key : keys) {
                String val = map.get(key);
                if(val != null) {
                    total += val.length();
                }
            }

            // over max?
            if(total > maxChars) {
                // remove the next largest file
                String largeKey = null;
                long largeKeyLength = -1;
                for(String key : keys) {
                    String val = map.get(key);
                    if(val != null) {
                        long length = val.length();
                        if(length > largeKeyLength) {
                            largeKey = key;
                            largeKeyLength = length;
                        }
                    }
                }

                // and clean it..
                Gdx.app.debug("File System", "Cleanup of " + largeKey);
                removeItem(largeKey);
            }
            else {
                // we are done cleaning...
                cleaned = false;
            }
        }
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
