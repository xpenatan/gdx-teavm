package com.github.xpenatan.gdx.backends.teavm.filesystem;

import org.teavm.jso.browser.Storage;

/**
 * Storage for data in browser's local storage (limited to a max. size of ~2.5MB).
 *
 * @author noblemaster
 */
class StoreLocal implements Store {

    /**
     * Contains all the data.
     */
    private final Storage storage;

    /**
     * Prefix that makes sure multiple applications on the same server don't use the same paths.
     */
    private final String prefix;

    StoreLocal(String prefix) {
        storage = Storage.getLocalStorage();
        this.prefix = prefix;
    }

    @Override
    public int getLength() {
        return storage.getLength();
    }

    @Override
    public String key(int i) {
        String key = storage.key(i);
        if (key.startsWith(prefix)) {
            return key.substring(prefix.length());
        }
        else {
            return null;
        }
    }

    @Override
    public String getItem(String key) {
        return storage.getItem(prefix + key);
    }

    @Override
    public void setItem(String key, String item) {
        storage.setItem(prefix + key, item);
    }

    @Override
    public void removeItem(String key) {
        storage.removeItem(prefix + key);
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
