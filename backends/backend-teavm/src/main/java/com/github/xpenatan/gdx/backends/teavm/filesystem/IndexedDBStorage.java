package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.filesystem.indexeddb.IndexedDBFileData;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSDate;
import org.teavm.jso.indexeddb.IDBCursor;
import org.teavm.jso.indexeddb.IDBCursorRequest;
import org.teavm.jso.indexeddb.IDBDatabase;
import org.teavm.jso.indexeddb.IDBFactory;
import org.teavm.jso.indexeddb.IDBObjectStore;
import org.teavm.jso.indexeddb.IDBObjectStoreParameters;
import org.teavm.jso.indexeddb.IDBOpenDBRequest;
import org.teavm.jso.indexeddb.IDBRequest;
import org.teavm.jso.indexeddb.IDBTransaction;

public class IndexedDBStorage extends MemoryFileStorage {
    private final static String KEY_OBJECT_STORE = "FILE_DATA";
    private final static String ROOT_PATH = "db/assets";
    private IDBDatabase dataBase = null;

    private String databaseName;

    public IndexedDBStorage() {
        setupIndexedDB();
    }

    private void setupIndexedDB() {
        TeaApplication teaApplication = (TeaApplication)Gdx.app;
        TeaApplicationConfiguration config = teaApplication.getConfig();
        teaApplication.delayInitCount++;

        IDBFactory instance = IDBFactory.getInstance();
        databaseName = getDatabaseName(config);
        IDBOpenDBRequest request = instance.open(databaseName, 1);

        request.setOnUpgradeNeeded(evt -> {
            IDBDatabase result = request.getResult();
            IDBObjectStoreParameters op = IDBObjectStoreParameters.create();
            result.createObjectStore(KEY_OBJECT_STORE, op);
        });

        request.setOnSuccess(() -> {
            dataBase = request.getResult();
            readAllFilesAsync(teaApplication);
        });

        request.setOnError(() -> {
            Gdx.app.error("IndexedDB", "Error opening database: " + databaseName);
            teaApplication.delayInitCount--;
        });
    }

    private String getDatabaseName(TeaApplicationConfiguration config) {
        String path = ROOT_PATH;
        String storagePrefix = config.storagePrefix.trim();
        if(storagePrefix.endsWith("/")) {
            path = storagePrefix + ROOT_PATH;
        }
        else {
            if(storagePrefix.isEmpty()) {
                path = ROOT_PATH;
            }
            else {
                path = storagePrefix + "/" + ROOT_PATH;
            }
        }
        return path;
    }

    @Override
    public String getPath() {
        return databaseName;
    }

    @Override
    protected void renameFile(String sourcePath, String targetPath, FileData data) {
        removeFile(sourcePath);
        putFile(targetPath, data);
    }

    @Override
    protected void putFile(String key, FileData data) {
        if(debug) {
            System.out.println("putFileAsync: " + key);
        }
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readwrite");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);

        IndexedDBFileData dbFileData = IndexedDBFileData.create(data.getType(), new JSDate());
        if(!data.isDirectory()) {
            dbFileData.setContents(data.getBytes());
        }
        IDBRequest request = objectStore.put(dbFileData, getJSString(key));
        request.setOnError(() -> {
            Gdx.app.error("IndexedDB", "Error putting file: " + key);
        });
    }

    @Override
    protected void removeFile(String key) {
        if(debug) {
            System.out.println("removeFileAsync: " + key);
        }
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readwrite");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);
        IDBRequest request = objectStore.delete(getJSString(key));
        request.setOnError(() -> {
            Gdx.app.error("IndexedDB", "Error removing file: " + key);
        });
    }

    private void readAllFilesAsync(TeaApplication teaApplication) {
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readonly");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);
        IDBCursorRequest cursorRequest = objectStore.openCursor();
        cursorRequest.setOnSuccess(() -> {
            IDBCursor cursor = cursorRequest.getResult();
            if(cursor != null) {
                String key = fixPath(getJavaString(cursor.getKey()));
                JSObject value = cursor.getValue();
                IndexedDBFileData dbFileData = getFileData(value);
                int type = dbFileData.getType();
                FileData fileData = null;

                if(type == FileData.TYPE_DIRECTORY) {
                    fileData = new FileData(key);
                }
                else {
                    Int8ArrayWrapper contents = dbFileData.getContents();
                    byte[] bytes = TypedArrays.toByteArray(contents);
                    fileData = new FileData(key, type, bytes);
                }
                put(key, fileData);
                cursor.doContinue();
            }
            teaApplication.delayInitCount--;
        });
        cursorRequest.setOnError(() -> {
            teaApplication.delayInitCount--;
        });
    }

    @JSBody(params = { "data" }, script = "return data;")
    private static native JSObject getJSString(String data);

    @JSBody(params = { "data" }, script = "return data;")
    private static native String getJavaString(JSObject data);

    @JSBody(params = { "data" }, script = "return data;")
    private static native IndexedDBFileData getFileData(JSObject data);
}