
package com.github.xpenatan.gdx.backend.web.dom;

/**
 * @author xpenatan
 */
public interface StorageWrapper {
	public int getLength();

	public String key(int index);

	public String getItem(String key);

	public void setItem(String key, String value);

	public void removeItem(String key);

	public void clear();
}
