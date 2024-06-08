package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
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
    private final static String ROOT_PATH = "db/assets";
    private IDBDatabase dataBase = null;

    private static final int TYPE_FILE = 1;
    private static final int TYPE_DIRECTORY = 2;

    private final OrderedMap<String, IndexedDBFileData> fileMap;

    private Array<String> tmpPaths = new Array<>();

    private String databaseName;

    private boolean debug = false;

    public IndexedDBStorage() {
        fileMap = new OrderedMap<>();
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
    public InputStream read(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData data = get(path);
        Int8ArrayWrapper array = data.getContents();
        byte[] byteArray = TypedArrays.toByteArray(array);
        try {
            return new ByteArrayInputStream(byteArray);
        }
        catch(RuntimeException e) {
            // Something corrupted: we remove it & re-throw the error
            remove(path);
            removeFileAsync(path);
            throw e;
        }
    }

    @Override
    protected void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        String path = file.path();
        IndexedDBFileData fileData = IndexedDBFileData.create(TYPE_FILE, new JSDate());
        fileData.setContents(data);
        put(path, fileData);
        putFileAsync(path, fileData);

        FileHandle cur = file.parent();
        while(!isRootFolder(cur)) {
            String parentPath = cur.path();
            putFolder(parentPath);
            cur = cur.parent();
        }
    }

    @Override
    protected String[] paths(TeaFileHandle file) {
        return getAllChildren(file);
    }

    @Override
    public boolean isDirectory(TeaFileHandle file) {
        String path = file.path();

        if(isRootFolder(file)) {
            return true;
        }

        IndexedDBFileData data = get(path);
        if(data != null) {
            int type = data.getType();
            return type == TYPE_DIRECTORY;
        }
        return false;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        String path = file.path();
        if(path.startsWith(".")) {
            path = path.replaceFirst(".", "");
        }
        if(path.startsWith("/")) {
            path = path.replaceFirst("/", "");
        }
        putFolder(path);
        FileHandle cur = file.parent();
        while(!isRootFolder(cur)) {
            String parentPath = cur.path();
            putFolder(parentPath);
            cur = cur.parent();
        }
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        String path = file.path();
        if(isRootFolder(file)) {
            return true;
        }
        return containsKey(path);
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        String path = file.path();
        if(isRootFolder(file)) {
            return false;
        }

        IndexedDBFileData data = get(path);
        if(data != null) {
            if(data.getType() != TYPE_FILE) {
                return false;
            }
            if(remove(path) != null) {
                removeFileAsync(path);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteDirectory(TeaFileHandle file) {
        String path = file.path();
        if(isRootFolder(file)) {
            return false;
        }
        IndexedDBFileData data = get(path);
        if(data != null) {
            if(data.getType() != TYPE_DIRECTORY) {
                return false;
            }

            if(remove(path) != null) {
                removeFileAsync(path);
                FileHandle[] list = file.list();
                // Get all children paths and delete them all
                String[] paths = getAllChildrenAndSiblings(file);
                for(int i = 0; i < paths.length; i++) {
                    String childOrSiblingPath = paths[i];
                    boolean rem = remove(childOrSiblingPath) != null;
                    if(rem) {
                        removeFileAsync(childOrSiblingPath);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public long length(TeaFileHandle file) {
        String path = file.path();
        IndexedDBFileData data = get(path);
        if(data != null && data.getType() == TYPE_FILE) {
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
        IndexedDBFileData data = remove(sourcePath);
        if(data != null) {
            put(targetPath, data);
            removeFileAsync(sourcePath);
            putFileAsync(targetPath, data);
        }
    }

    @Override
    public String getLocalStoragePath() {
        return databaseName;
    }

    private boolean isRootFolder(FileHandle cur) {
        String path = cur.path();
        if(path.isEmpty() || path.equals(".") || path.equals("/") || path.equals("./")) {
            return true;
        }
        else {
            return false;
        }
    }

    private String[] getAllChildrenAndSiblings(FileHandle file) {
        return list(file, false);
    }

    private String[] getAllChildren(FileHandle file) {
        return list(file, true);
    }

    private String[] list(FileHandle file, boolean equals) {
        String dirPath = file.path();

        boolean isRoot = isRootFolder(file);

        String dir = dirPath;
        ObjectMap.Entries<String, IndexedDBFileData> it = fileMap.iterator();
        while(it.hasNext) {
            ObjectMap.Entry<String, IndexedDBFileData> next = it.next();
            String path = next.key;
            FileHandle parent = Gdx.files.local(path).parent();
            String parentPath = parent.path();

            boolean isChildParentRoot = isRootFolder(parent);

            // Only add path if parent is dir
            if(isChildParentRoot && isRoot) {
                tmpPaths.add(path);
            }
            else {
                if(equals) {
                    if(parentPath.equals(dir)) {
                        tmpPaths.add(path);
                    }
                }
                else {
                    if(parentPath.startsWith(dir)) {
                        tmpPaths.add(path);
                    }
                }
            }
        }
        tmpPaths.sort();
        String[] str = new String[tmpPaths.size];
        for(int i = 0; i < tmpPaths.size; i++) {
            String s = tmpPaths.get(i);
            if(debug) {
                System.out.println("Listh[" + i + "]: " + s);
            }
            str[i] = s;
        }
        tmpPaths.clear();
        return str;
    }

    private void putFolder(String path) {
        IndexedDBFileData fileData = IndexedDBFileData.create(TYPE_DIRECTORY, new JSDate());
        put(path, fileData);
        putFileAsync(path, fileData);
    }

    private void putFileAsync(String key1, IndexedDBFileData data) {
        String key = getPath(key1);
        if(debug) {
            System.out.println("putFileAsync: " + key);
        }
        IDBTransaction transaction = dataBase.transaction(KEY_OBJECT_STORE, "readwrite");
        IDBObjectStore objectStore = transaction.objectStore(KEY_OBJECT_STORE);
        IDBRequest request = objectStore.put(data, getJSString(key));
        request.setOnError(() -> {
            Gdx.app.error("IndexedDB", "Error putting file: " + key);
        });
    }

    private void removeFileAsync(String key1) {
        String key = getPath(key1);
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
                String key = getJavaString(cursor.getKey());
                if(key.startsWith("/")) {
                    key = key.replaceFirst("/", "");
                }
                JSObject value = cursor.getValue();
                IndexedDBFileData fileData = getFileData(value);
                put(key, fileData);
                cursor.doContinue();
            }
            teaApplication.delayInitCount--;
        });
        cursorRequest.setOnError(() -> {
            teaApplication.delayInitCount--;
        });
    }

    private IndexedDBFileData get(String path) {
        return fileMap.get(path);
    }

    private void put(String path, IndexedDBFileData fileData) {
        fileMap.put(path, fileData);
    }

    private IndexedDBFileData remove(String path) {
        return fileMap.remove(path);
    }

    private boolean containsKey(String path) {
        return fileMap.containsKey(path);
    }

    private String getPath(String path) {
        if(path.startsWith("./")) {
            path = path.replace("./", "");
        }

        if(!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }

    @JSBody(params = { "data" }, script = "return data;")
    private static native JSObject getJSString(String data);

    @JSBody(params = { "data" }, script = "return data;")
    private static native String getJavaString(JSObject data);

    @JSBody(params = { "data" }, script = "return data;")
    private static native IndexedDBFileData getFileData(JSObject data);
}