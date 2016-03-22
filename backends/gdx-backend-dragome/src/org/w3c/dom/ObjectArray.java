package org.w3c.dom;

public interface ObjectArray<E> {
  int getLength();
  void setLength(int length);
  E getElement(int index);
  void setElement(int index, E value);
}
