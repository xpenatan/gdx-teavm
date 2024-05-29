package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import java.io.InputStream;
import org.teavm.jso.indexeddb.IDBDatabase;
import org.teavm.jso.indexeddb.IDBFactory;
import org.teavm.jso.indexeddb.IDBOpenDBRequest;

public class IndexedDBStorage extends FileDB {

    private IDBDatabase result = null;

    public IndexedDBStorage() {

        IDBFactory instance = IDBFactory.getInstance();

        IDBOpenDBRequest request = instance.open("TeaVM", 1);
        request.setOnSuccess(() -> {
            result = request.getResult();
            System.out.println("SUCCESS");
        });

        request.setOnError(() -> {
            System.out.println("ERROR");
        });
    }

    @Override
    public InputStream read(TeaFileHandle file) {
        return null;
    }

    @Override
    protected void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {

    }

    @Override
    protected String[] paths(TeaFileHandle file) {
        return new String[0];
    }

    @Override
    public boolean isDirectory(TeaFileHandle file) {
        return false;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {

    }

    @Override
    public boolean exists(TeaFileHandle file) {
        return false;
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        return false;
    }

    @Override
    public long length(TeaFileHandle file) {
        return 0;
    }

    @Override
    public void rename(TeaFileHandle source, TeaFileHandle target) {

    }
}
