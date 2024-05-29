package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import java.io.InputStream;
import org.teavm.jso.indexeddb.IDBDatabase;
import org.teavm.jso.indexeddb.IDBFactory;
import org.teavm.jso.indexeddb.IDBOpenDBRequest;

public class IndexedDBStorage extends FileDB {

    private IDBDatabase dataBase = null;

    public IndexedDBStorage() {
        TeaApplication teaApplication = (TeaApplication)Gdx.app;
        teaApplication.delayInitCount++;
        IDBFactory instance = IDBFactory.getInstance();
        IDBOpenDBRequest request = instance.open("TeaVM", 1);
        request.setOnSuccess(() -> {
            dataBase = request.getResult();
            System.out.println("SUCCESS");
            teaApplication.delayInitCount--;
        });

        request.setOnError(() -> {
            System.out.println("ERROR");
            teaApplication.delayInitCount--;
        });
    }

    @Override
    public InputStream read(TeaFileHandle file) {

        System.out.println("1111");
        return null;
    }

    @Override
    protected void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        System.out.println("2222");
    }

    @Override
    protected String[] paths(TeaFileHandle file) {

        System.out.println("3333");
        return new String[0];
    }

    @Override
    public boolean isDirectory(TeaFileHandle file) {
        System.out.println("4444");
        return false;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        System.out.println("5555");
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        System.out.println("6666");
        return false;
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        System.out.println("7777");
        return false;
    }

    @Override
    public long length(TeaFileHandle file) {
        System.out.println("8888");
        return 0;
    }

    @Override
    public void rename(TeaFileHandle source, TeaFileHandle target) {
        System.out.println("9999");
    }
}
