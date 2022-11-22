package emucom.badlogic.gdx;

import emucom.badlogic.gdx.files.FileHandle;

public interface Files {
    public enum FileType {
        Classpath,
        Internal,
        External,
        Absolute,
        Local;
    }

    public FileHandle getFileHandle (String path, com.badlogic.gdx.Files.FileType type);

    public FileHandle classpath (String path);

    public FileHandle internal (String path);

    public FileHandle external (String path);

    public FileHandle absolute (String path);

    public FileHandle local (String path);

    public String getExternalStoragePath ();

    public boolean isExternalStorageAvailable ();

    public String getLocalStoragePath ();

    public boolean isLocalStorageAvailable ();
}
