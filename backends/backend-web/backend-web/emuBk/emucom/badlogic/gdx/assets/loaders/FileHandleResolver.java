package emucom.badlogic.gdx.assets.loaders;

import emucom.badlogic.gdx.files.FileHandle;

public interface FileHandleResolver {
    public FileHandle resolve (String fileName);
}