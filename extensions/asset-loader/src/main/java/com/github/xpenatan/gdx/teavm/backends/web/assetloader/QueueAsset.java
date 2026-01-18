package com.github.xpenatan.gdx.teavm.backends.web.assetloader;

import com.badlogic.gdx.files.FileHandle;

public class QueueAsset {
    public String assetUrl;
    public FileHandle fileHandle;
    public AssetLoaderListener<TeaBlob> listener;
}
