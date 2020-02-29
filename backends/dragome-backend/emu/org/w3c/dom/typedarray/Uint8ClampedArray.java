package org.w3c.dom.typedarray;

/** @author xpenatan */
public interface Uint8ClampedArray extends Uint8Array
{
	Uint8ClampedArray subarray(int begin);
	Uint8ClampedArray subarray(int begin, int end);
}