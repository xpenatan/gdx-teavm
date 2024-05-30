package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.Int8ArrayWrapper;
import com.github.xpenatan.gdx.backends.teavm.dom.typedarray.TypedArrays;
import com.github.xpenatan.gdx.backends.teavm.filesystem.indexeddb.IndexedDBFileData;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
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

public class IndexedDBStorage extends FileDB {
    private final static String KEY_OBJECT_STORE = "FILE_DATA";
    private IDBDatabase dataBase = null;

    private static final int TYPE_FILE = 1;
    private static final int TYPE_DIRECTORY = 2;

    private final ObjectMap<String, IndexedDBFileData> fileMap;

    public IndexedDBStorage() {
        fileMap = new ObjectMap<>();
        setupIndexedDB();
    }

    private void setupIndexedDB() {
        TeaApplication teaApplication = (TeaApplication)Gdx.app;
        TeaApplicationConfiguration config = teaApplication.getConfig();
        teaApplication.delayInitCount++;

        IDBFactory instance = IDBFactory.getInstance();
        String databaseName = getDatabaseName(config);
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
        String name = "home/assets";
        if(config.storagePrefix.endsWith("/")) {
            name = config.storagePrefix + name;
        }
        else {
            name = config.storagePrefix + "/" + name;
        }
        return name;
    }

    @Override
    public InputStream read(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData data = fileMap.get(path);
        Int8ArrayWrapper array = data.getContents();
        byte[] byteArray = TypedArrays.toByteArray(array);
        try {
            return new ByteArrayInputStream(byteArray);
        }
        catch(RuntimeException e) {
            // Something corrupted: we remove it & re-throw the error
            fileMap.remove(path);
            removeFileAsync(path);
            throw e;
        }
    }

    @Override
    protected void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        String path = file.path();
        IndexedDBFileData fileData = IndexedDBFileData.create(TYPE_FILE, new JSDate());
        fileData.setContents(data);
        fileMap.put(path, fileData);
        putFileAsync(path, fileData);
    }

    @Override
    protected String[] paths(TeaFileHandle file) {
        Array<String> paths = new Array<>(fileMap.size);
        String dir = file.path() + "/";
        ObjectMap.Entries<String, IndexedDBFileData> it = fileMap.iterator();
        while(it.hasNext) {
            ObjectMap.Entry<String, IndexedDBFileData> next = it.next();
            String path = next.key;
            if(path.startsWith(dir)) {
                paths.add(path);
            }
        }
        paths.sort();
        return paths.items;
    }

    @Override
    public boolean isDirectory(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData data = fileMap.get(path);
        if(data != null) {
            int type = data.getType();
            return type == TYPE_DIRECTORY;
        }
        return false;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData fileData = IndexedDBFileData.create(TYPE_DIRECTORY, new JSDate());
        fileMap.put(path, fileData);
        putFileAsync(path, fileData);
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        String path = file.path();
        return fileMap.containsKey(path);
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData data = fileMap.remove(path);
        if(data != null) {
            removeFileAsync(path);
            return true;
        }
        return false;
    }

    @Override
    public long length(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData data = fileMap.get(path);
        if(data.getType() == TYPE_FILE) {
            Int8ArrayWrapper contents = data.getContents();
            byte[] bytes = TypedArrays.toByteArray(contents);
            return bytes.length;
        }
        return 0;
    }

    @Override
    public void rename(TeaFileHandle source, TeaFileHandle target) {
        String sourcePath = source.path();
        String targetPath = target.path();
        IndexedDBFileData data = fileMap.remove(sourcePath);
        if(data != null) {
            fileMap.put(targetPath, data);
            removeFileAsync(sourcePath);
            putFileAsync(targetPath, data);
        }
    }

    private void putFileAsync(String key, IndexedDBFileData data) {
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readwrite");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);
        IDBRequest request = objectStore.put(data, getJSString(key));
        request.setOnError(() -> {
            Gdx.app.error("IndexedDB", "Error putting file: " + key);
        });
    }

    private void removeFileAsync(String key) {
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
                String key = getJavaString(cursor.getKey());
                JSObject value = cursor.getValue();
                IndexedDBFileData fileData = getFileData(value);
                fileMap.put(key, fileData);
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