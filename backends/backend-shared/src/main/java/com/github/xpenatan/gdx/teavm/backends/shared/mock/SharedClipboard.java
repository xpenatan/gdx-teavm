package com.github.xpenatan.gdx.teavm.backends.shared.mock;

import com.badlogic.gdx.utils.Clipboard;

public class SharedClipboard implements Clipboard {
    private String contents = "";

    @Override
    public boolean hasContents() {
        return contents != null && !contents.isEmpty();
    }

    @Override
    public String getContents() {
        return contents;
    }

    @Override
    public void setContents(String contents) {
        this.contents = contents == null ? "" : contents;
    }
}
