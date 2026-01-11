package com.github.xpenatan.gdx.teavm.backend.web.filesystem.types;

import com.github.xpenatan.gdx.teavm.backend.web.TeaApplication;
import com.github.xpenatan.gdx.teavm.backend.web.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.teavm.backend.web.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.teavm.backend.web.filesystem.FileData;
import com.github.xpenatan.gdx.teavm.backend.web.filesystem.MemoryFileStorage;
import com.github.xpenatan.gdx.teavm.backend.web.filesystem.indexeddb.IndexedDBFileData;
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
import org.teavm.jso.typedarrays.Int8Array;

public class LocalDBStorage extends MemoryFileStorage {
    private final static String KEY_OBJECT_STORE = "FILE_DATA";
    private IDBDatabase dataBase = null;

    public LocalDBStorage(TeaApplication teaApplication) {
        setupIndexedDB(teaApplication);
    }

    private void setupIndexedDB(TeaApplication teaApplication) {
        if(teaApplication == null) {
            return;
        }
        TeaApplicationConfiguration config = teaApplication.getConfig();
        teaApplication.addInitQueue();

        IDBFactory instance = IDBFactory.getInstance();
        String databaseName = config.localStoragePrefix;
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
            System.err.println("IndexedDB" + " Error opening database: " + databaseName);
            teaApplication.subtractInitQueue();
        });
    }

    @Override
    protected void putFile(String key, FileData fileData) {
        if(debug) {
            String type = fileData != null && fileData.isDirectory() ? "PUT FOLDER DB: " : "PUT FILE DB: ";
            System.out.println(type + key);
        }
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readwrite");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);

        IndexedDBFileData dbFileData = IndexedDBFileData.create(fileData.getType(), new JSDate());
        if(!fileData.isDirectory()) {
            dbFileData.setContents(fileData.getBytes());
        }
        IDBRequest request = objectStore.put(dbFileData, getJSString(key));
        request.setOnError(() -> {
            System.err.println("IndexedDB" + " Error putting file: " + key);
        });
    }

    @Override
    protected void removeFile(String key) {
        if(debug) {
            System.out.println("REMOVE FILE DB: " + key);
        }
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readwrite");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);
        IDBRequest request = objectStore.delete(getJSString(key));
        request.setOnError(() -> {
            System.err.println("IndexedDB" + " Error removing file: " + key);
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

                if(type == FileData.TYPE_DIRECTORY) {
                    putFolderInternal(key, false);
                }
                else {
                    Int8Array contents = dbFileData.getContents();
                    byte[] bytes = TypedArrays.toByteArray(contents);
                    putFileInternal(key, bytes, false);
                }

                cursor.doContinue();
            }
        });

        transaction.setOnComplete(() -> {
            teaApplication.subtractInitQueue();
        });

        cursorRequest.setOnError(() -> {
            System.err.println("IndexedDB" + " Error cursor");
            teaApplication.subtractInitQueue();
        });
    }

    @JSBody(params = { "data" }, script = "return data;")
    private static native JSObject getJSString(String data);

    @JSBody(params = { "data" }, script = "return data;")
    private static native String getJavaString(JSObject data);

    @JSBody(params = { "data" }, script = "return data;")
    private static native IndexedDBFileData getFileData(JSObject data);
}