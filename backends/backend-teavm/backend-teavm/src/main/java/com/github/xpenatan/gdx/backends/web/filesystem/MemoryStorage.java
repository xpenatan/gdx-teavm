package com.github.xpenatan.gdx.backends.web.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.backends.web.dom.StorageWrapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage for data in memory.
 *
 * @author noblemaster
 */
class MemoryStorage implements StorageWrapper {

  /** Contains all the data. */
  private final List<String> keys;
  private final ObjectMap<String, String> map;


  MemoryStorage() {
    keys = new ArrayList<String>(16);
    map = new ObjectMap<String, String>(16);
  }

  /** Removes large files as needed to prevent out of memory problems. */
  synchronized void cleanup() {
    // remove large files if we are above max. bytes
    long maxChars = 10000000;
    boolean cleaned = true;
    while  (cleaned) {
      // calculate new total
      long total = 0;
      for (String key: keys) {
        String val = map.get(key);
        if (val != null) {
          total += val.length();
        }
      }

      // over max?
      if (total > maxChars) {
        // remove the next largest file
        String largeKey = null;
        long largeKeyLength = -1;
        for (String key: keys) {
          String val = map.get(key);
          if (val != null) {
            long length = val.length();
            if (length > largeKeyLength) {
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
  public synchronized int getLength() {
    return keys.size();
  }

  @Override
  public synchronized String key(int i) {
    return map.get(keys.get(i));
  }

  @Override
  public synchronized String getItem(String key) {
    return map.get(key);
  }

  @Override
  public synchronized void setItem(String key, String item) {
    if (!map.containsKey(key)) {
      keys.add(key);
    }
    map.put(key, item);
  }

  @Override
  public synchronized void removeItem(String key) {
    keys.remove(key);
    map.remove(key);
  }

  @Override
  public synchronized void clear() {
    keys.clear();
    map.clear();
  }
}
