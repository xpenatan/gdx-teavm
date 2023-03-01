package com.github.xpenatan.gdx.backends.teavm.filesystem;

/**
 * Definition for a key-based file store.
 * 
 * @author noblemaster
 */
public interface Store {

  int getLength();

  /** The key or 'null' if it's not related to this application. */
  String key(int i);

  String getItem(String key);

  void setItem(String key, String item);

  void removeItem(String key);

  void clear();
}
