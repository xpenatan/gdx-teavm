package com.github.xpenatan.gdx.backends.teavm.filesystem;

/**
 * @author noblemaster
 */
public interface Store {

  int getLength() ;

  String key(int i);

  String getItem(String key);

  void setItem(String key, String item);

  void removeItem(String key);

  void clear();
}
