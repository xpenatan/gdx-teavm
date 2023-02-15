package com.github.xpenatan.gdx.backends.teavm.filesystem;

/**
 * @author noblemaster
 */
public interface Store {

  public int getLength() ;

  public String key(int i);

  public String getItem(String key);

  public void setItem(String key, String item);

  public void removeItem(String key);

  public void clear();
}
