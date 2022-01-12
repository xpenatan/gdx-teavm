package com.github.xpenatan.gdx.backends.web.preloader;

import java.io.IOException;
import java.io.InputStream;

import com.github.xpenatan.gdx.backends.web.dom.typedarray.ArrayBufferWrapper;
import com.github.xpenatan.gdx.backends.web.dom.typedarray.Int8ArrayWrapper;

/**
 * @author xpenatan
 */
public final class Blob {

	private ArrayBufferWrapper response;
	private final Int8ArrayWrapper data;

	public Blob (ArrayBufferWrapper response, Int8ArrayWrapper data) {
		this.data = data;
		this.response = response;
	}

	public Int8ArrayWrapper getData() {
		return data;
	}

	public ArrayBufferWrapper getResponse() {
		return response;
	}

	public int length () {
		return data.getLength();
	}

	public byte get (int i) {
		return data.get(i);
	}

	public InputStream read () {
		return new InputStream() {

			@Override
			public int read () throws IOException {
				if (pos == length()) return -1;
				return get(pos++) & 0xff;
			}

			@Override
			public int available () {
				return length() - pos;
			}

			int pos;
		};
	}

	public String toBase64 () {
		int length = data.getLength();
		String base64code = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
		StringBuilder encoded = new StringBuilder(length * 4 / 3 + 2);
		for (int i = 0; i < length; i += 3) {
			if (length - i >= 3) {
				int j = ((data.get(i) & 0xff) << 16) + ((data.get(i + 1) & 0xff) << 8) + (data.get(i + 2) & 0xff);
				encoded.append(base64code.charAt((j >> 18) & 0x3f));
				encoded.append(base64code.charAt((j >> 12) & 0x3f));
				encoded.append(base64code.charAt((j >> 6) & 0x3f));
				encoded.append(base64code.charAt(j & 0x3f));
			} else if (length - i >= 2) {
				int j = ((data.get(i) & 0xff) << 16) + ((data.get(i + 1) & 0xff) << 8);
				encoded.append(base64code.charAt((j >> 18) & 0x3f));
				encoded.append(base64code.charAt((j >> 12) & 0x3f));
				encoded.append(base64code.charAt((j >> 6) & 0x3f));
				encoded.append("=");
			} else {
				int j = ((data.get(i) & 0xff) << 16);
				encoded.append(base64code.charAt((j >> 18) & 0x3f));
				encoded.append(base64code.charAt((j >> 12) & 0x3f));
				encoded.append("==");
			}
		}
		return encoded.toString();
	}
}
