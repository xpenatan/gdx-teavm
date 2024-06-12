package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MemoryFileStorage extends FileDB {
    private final OrderedMap<String, FileData> fileMap;

    private final Array<String> tmpPaths = new Array<>();

    protected boolean debug = false;

    public MemoryFileStorage() {
        fileMap = new OrderedMap<>();
        setupIndexedDB();
    }

    private void setupIndexedDB() {
        TeaApplication teaApplication = (TeaApplication)Gdx.app;
        TeaApplicationConfiguration config = teaApplication.getConfig();
    }

    @Override
    public InputStream read(TeaFileHandle file) {
        String path = fixPath(file.path());
        FileData data = get(path);
        byte[] byteArray = data.getBytes();
        try {
            return new ByteArrayInputStream(byteArray);
        }
        catch(RuntimeException e) {
            // Something corrupted: we remove it & re-throw the error
            remove(path);
            removeFile(path);
            throw e;
        }
    }

    @Override
    public byte[] readBytes(TeaFileHandle file) {
        String path = fixPath(file.path());
        FileData data = get(path);
        if(data != null) {
            byte[] byteArray = data.getBytes();
            return byteArray;
        }
        return new byte[0];
    }

    @Override
    protected void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        String path = fixPath(file.path());

        FileData fileData = new FileData(path, data);
        put(path, fileData);
        putFile(path, fileData);

        FileHandle cur = file.parent();
        while(!isRootFolder(cur)) {
            String parentPath = fixPath(cur.path());
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
        String path = fixPath(file.path());

        if(isRootFolder(file)) {
            return true;
        }

        FileData data = get(path);
        if(data != null) {
            return data.isDirectory();
        }
        return false;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        String path = fixPath(file.path());
        putFolder(path);
        FileHandle cur = file.parent();
        while(!isRootFolder(cur)) {
            String parentPath = fixPath(cur.path());
            putFolder(parentPath);
            cur = cur.parent();
        }
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        String path = fixPath(file.path());
        if(isRootFolder(file)) {
            return true;
        }
        return containsKey(path);
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        String path = fixPath(file.path());
        if(isRootFolder(file)) {
            return false;
        }

        FileData data = get(path);
        if(data != null) {
            if(data.isDirectory()) {
                return false;
            }
            if(remove(path) != null) {
                removeFile(path);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean deleteDirectory(TeaFileHandle file) {
        String path = fixPath(file.path());
        if(isRootFolder(file)) {
            return false;
        }
        FileData data = get(path);
        if(data != null) {
            if(!data.isDirectory()) {
                return false;
            }

            if(remove(path) != null) {
                removeFile(path);
                FileHandle[] list = file.list();
                // Get all children paths and delete them all
                String[] paths = getAllChildrenAndSiblings(file);
                for(int i = 0; i < paths.length; i++) {
                    String childOrSiblingPath = fixPath(paths[i]);
                    boolean rem = remove(childOrSiblingPath) != null;
                    if(rem) {
                        removeFile(childOrSiblingPath);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public long length(TeaFileHandle file) {
        String path = fixPath(file.path());
        FileData data = get(path);
        if(data != null && !data.isDirectory()) {
            byte[] bytes = data.getBytes();
            return bytes.length;
        }
        return 0;
    }

    @Override
    public void rename(TeaFileHandle source, TeaFileHandle target) {
        String sourcePath = fixPath(source.path());
        String targetPath = fixPath(target.path());
        FileData data = remove(sourcePath);
        if(data != null) {
            put(targetPath, data);
            renameFile(sourcePath, targetPath, data);
        }
    }

    @Override
    public String getPath() {
        return "";
    }

    private boolean isRootFolder(FileHandle cur) {
        String path = fixPath(cur.path());
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
        String dirPath = fixPath(file.path());

        boolean isRoot = isRootFolder(file);

        String dir = fixPath(dirPath);
        ObjectMap.Entries<String, FileData> it = fileMap.iterator();
        while(it.hasNext) {
            ObjectMap.Entry<String, FileData> next = it.next();
            String path = fixPath(next.key);
            FileHandle parent = Gdx.files.local(path).parent();
            String parentPath = fixPath(parent.path());

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
        FileData fileData = new FileData(path);
        put(path, fileData);
        putFile(path, fileData);
    }

    protected void renameFile(String sourcePath, String targetPath, FileData data) {
        removeFile(sourcePath);
        putFile(targetPath, data);
    }

    protected void putFile(String key, FileData data) {
    }

    protected void removeFile(String key) {
    }

    final protected FileData get(String path) {
        FileData data = fileMap.get(path);
        if(debug) {
            System.out.println("file get: " + (data != null) + " Path: " + path);
        }
        return data;
    }

    final protected void put(String path, FileData fileData) {
        if(debug) {
            System.out.println("file put: " + path);
        }
        if(path.isEmpty()  || path.equals(".") || path.equals("/") || path.equals("./")) {
            throw new GdxRuntimeException("Cannot put an empty folder name");
        }
        fileMap.put(path, fileData);
    }

    final protected FileData remove(String path) {
        FileData data = fileMap.remove(path);
        if(debug) {
            System.out.println("file remove: " + (data != null) + " Path: " + path);
        }
        return data;
    }

    final protected boolean containsKey(String path) {
        boolean flag = fileMap.containsKey(path);
        if(debug) {
            System.out.println("file containsKey: " + flag + " Path: " + path);
        }
        return flag;
    }

    final protected String fixPath(String path) {
        path = path.trim();
        if(path.startsWith("./")) {
            path = path.replace("./", "");
        }

        if(path.startsWith(".")) {
            path = path.replaceFirst(".", "");
        }

        if(!path.startsWith("/")) {
            path = "/" + path;
        }
        return path;
    }
}