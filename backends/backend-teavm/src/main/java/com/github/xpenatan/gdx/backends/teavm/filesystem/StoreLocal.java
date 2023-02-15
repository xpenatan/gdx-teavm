package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;
import org.teavm.jso.browser.Storage;

/**
 * Storage for data in memory.
 *
 * @author noblemaster
 */
class StoreLocal implements Store {

    /**
     * Contains all the data.
     */
    private final StorageWrapper storage;

    StoreLocal() {
        storage = (StorageWrapper) Storage.getLocalStorage();
    }

    @Override
    public int getLength() {
        return storage.getLength();
    }

    @Override
    public String key(int i) {
        return storage.key(i);
    }

    @Override
    public String getItem(String key) {
        return storage.getItem(key);
    }

    @Override
    public void setItem(String key, String item) {
        storage.setItem(key, item);
    }

    @Override
    public void removeItem(String key) {
        storage.removeItem(key);
    }

    @Override
    public void clear() {
        storage.clear();
    }
}
