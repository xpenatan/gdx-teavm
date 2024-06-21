package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MemoryFileStorage extends FileDB {
    private final OrderedMap<String, FileData> fileMap;

    private final Array<String> tmpPaths = new Array<>();

    public boolean debug = false;

    public MemoryFileStorage() {
        fileMap = new OrderedMap<>();
    }

    @Override
    public InputStream read(TeaFileHandle file) {
        String path = fixPath(file.path());
        FileData data = getInternal(path);
        byte[] byteArray = data.getBytes();
        try {
            return new ByteArrayInputStream(byteArray);
        }
        catch(RuntimeException e) {
            // Something corrupted: we remove it & re-throw the error
            removeInternal(path);
            throw e;
        }
    }

    @Override
    public byte[] readBytes(TeaFileHandle file) {
        String path = fixPath(file.path());
        FileData data = getInternal(path);
        if(data != null) {
            byte[] byteArray = data.getBytes();
            return byteArray;
        }
        return new byte[0];
    }

    @Override
    protected void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        String path = fixPath(file.path());

        byte[] newBytes = null;
        FileData oldData = fileMap.get(path);
        if(append) {
            if(oldData == null) {
                newBytes = data;
            }
            else {
                byte[] oldBytes = oldData.getBytes();
                int newSize = data.length + oldBytes.length;
                newBytes = new byte[newSize];
                for(int i = 0; i < oldBytes.length; i++) {
                    newBytes[i] = oldBytes[i];
                }
                for(int i = oldBytes.length, j = 0; i < newSize; i++, j++) {
                    newBytes[i] = data[j];
                }
            }
        }
        else {
            newBytes = data;
        }

        putFileInternal(path, newBytes);

        FileHandle cur = file.parent();
        while(!isRootFolder(cur)) {
            String parentPath = fixPath(cur.path());
            if(!fileMap.containsKey(parentPath)) {
                putFolderInternal(parentPath);
            }
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

        FileData data = getInternal(path);
        if(data != null) {
            return data.isDirectory();
        }
        return false;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        String path = fixPath(file.path());
        putFolderInternal(path);
        FileHandle cur = file.parent();
        while(!isRootFolder(cur)) {
            String parentPath = fixPath(cur.path());
            if(!fileMap.containsKey(parentPath)) {
                putFolderInternal(parentPath);
            }
            cur = cur.parent();
        }
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        String path = fixPath(file.path());
        if(isRootFolder(file)) {
            return true;
        }
        return containsKeyInternal(path);
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        String path = fixPath(file.path());
        if(isRootFolder(file)) {
            return false;
        }

        FileData data = getInternal(path);
        if(data != null) {
            if(data.isDirectory()) {
                return false;
            }
            if(removeInternal(path) != null) {
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
        FileData data = getInternal(path);
        if(data != null) {
            if(!data.isDirectory()) {
                return false;
            }

            if(removeInternal(path) != null) {
                // Get all children paths and delete them all
                String[] paths = getAllChildrenAndSiblings(file);
                for(int i = 0; i < paths.length; i++) {
                    String childOrSiblingPath = fixPath(paths[i]);
                    boolean rem = removeInternal(childOrSiblingPath) != null;
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public long length(TeaFileHandle file) {
        String path = fixPath(file.path());
        FileData data = getInternal(path);
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

        if(source.isDirectory()) {
            target.mkdirs();
            FileHandle[] list = source.list();
            for(int i = 0; i < list.length; i++) {
                FileHandle fileHandle = list[i];
                fileHandle.moveTo(target);
            }
            source.deleteDirectory();
        }
        else {
            byte[] bytes = source.readBytes();
            target.writeBytes(bytes, false);
            source.delete();
        }
    }

    @Override
    public String getPath() {
        return "";
    }

    public String debugAllFiles() {
        String text = "";
        text += println("####### START DEBUG FILE: " + fileMap.size + "\n");
        ObjectMap.Entries<String, FileData> it = fileMap.iterator();
        while(it.hasNext) {
            ObjectMap.Entry<String, FileData> next = it.next();
            FileData value = next.value;
            String name = "";
            String key = next.key;
            key = key + "\"";
            if(!value.getPath().equals(next.key)) {
                name = " Path: \"" + value.getPath() + "\"";
            }
            text += println("Key: \"" + key + name + " Type: " + value.getType() + " Bytes: " + value.getBytesSize()+ "\n");
        }
        text += println("####### END DEBUG FILE");
        return text;
    }

    public void printAllFiles() {
        System.out.println(debugAllFiles());
    }

    private String println(String value) {
        return value;
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
        String dir = fixPath(file.path());
        boolean isRoot = isRootFolder(file);
        if(debug) {
            System.out.println("########## START LIST ### isRoot: " + isRoot + " DIR: " + dir);
        }
        ObjectMap.Entries<String, FileData> it = fileMap.iterator();
        while(it.hasNext) {
            ObjectMap.Entry<String, FileData> next = it.next();
            String path = fixPath(next.key);

            FileHandle parent = getFilePath(path).parent();
            String parentPath = fixPath(parent.path());

            boolean isChildParentRoot = isRootFolder(parent);

            // Only add path if parent is dir
            if(isRoot) {
                if(isChildParentRoot) {
                    if(debug) {
                        System.out.println("LIST ROOD ADD: " + path);
                    }
                    tmpPaths.add(path);
                }
            }
            else {
                if(equals) {
                    if(parentPath.equals(dir)) {
                        if(debug) {
                            System.out.println("LIST EQUAL ADD: PATH: " + path + " --- PARENT: " + parentPath);
                        }
                        tmpPaths.add(path);
                    }
                }
                else {
                    if(parentPath.startsWith(dir)) {
                        if(debug) {
                            System.out.println("LIST STARTWITH ADD: PATH: " + path + " --- PARENT: " + parentPath);
                        }
                        tmpPaths.add(path);
                    }
                }
            }
        }
        if(debug) {
            System.out.println("########## END LIST ###");
        }
        String[] str = new String[tmpPaths.size];
        for(int i = 0; i < tmpPaths.size; i++) {
            String s = tmpPaths.get(i);
            if(debug) {
                System.out.println(getClass().getSimpleName() + " LIST[" + i + "]: " + s);
            }
            str[i] = s;
        }
        tmpPaths.clear();
        return str;
    }

    protected FileHandle getFilePath(String path) {
        return Gdx.files.internal(path);
    }

    protected void putFile(String key, FileData data) {
    }

    protected void removeFile(String key) {
    }

    final protected FileData getInternal(String path) {
        FileData fileData = fileMap.get(path);
        if(debug) {
            path = "\"" + path + "\"";
            String type = fileData != null && fileData.isDirectory() ? " GET FOLDER: " : " GET FILE: ";
            System.out.println(getClass().getSimpleName() + type + (fileData != null) + " Size: " + fileData.getBytesSize() + " Path: " + path);
        }
        return fileData;
    }

    final public void putFileInternal(String path, byte[] bytes) {
        putFileInternal(path, bytes, true);
    }

    final public void putFileInternal(String path, byte[] bytes, boolean callMethod) {
        if(debug) {
            String pathStr = "\"" + path + "\"";
            System.out.println(getClass().getSimpleName() + " PUT FILE: " + pathStr + " Bytes: " + bytes.length);
        }
        if(path.isEmpty()  || path.equals(".") || path.equals("/") || path.equals("./")) {
            throw new GdxRuntimeException("Cannot put an empty folder name");
        }
        FileData fileData = new FileData(path, bytes);
        fileMap.put(path, fileData);
        if(callMethod) {
            putFile(path, fileData);
        }
    }

    final public void putFolderInternal(String path) {
        putFolderInternal(path, true);
    }

    final public void putFolderInternal(String path, boolean callMethod) {
        if(debug) {
            String pathStr = "\"" + path + "\"";
            System.out.println(getClass().getSimpleName() + " PUT FOLDER: " + pathStr);
        }
        if(path.isEmpty()  || path.equals(".") || path.equals("/") || path.equals("./")) {
            throw new GdxRuntimeException("Cannot put an empty folder name");
        }
        FileData fileData = new FileData(path);
        fileMap.put(path, new FileData(path));
        if(callMethod) {
            putFile(path, fileData);
        }
    }

    final public FileData removeInternal(String path) {
        return removeInternal(path, true);
    }

    final public FileData removeInternal(String path, boolean callMethod) {
        FileData fileData = fileMap.remove(path);
        if(debug) {
            String pathStr = "\"" + path + "\"";
            String type = fileData != null && fileData.isDirectory() ? " REMOVE FOLDER: " : " REMOVE FILE: ";
            System.out.println(getClass().getSimpleName() + type + (fileData != null) + " Path: " + pathStr);
        }
        if(fileData != null && callMethod) {
            removeFile(path);
        }
        return fileData;
    }

    final public boolean containsKeyInternal(String path) {
        FileData fileData = fileMap.get(path);
        boolean flag = fileData != null;
        if(debug) {
            String type = fileData != null && fileData.isDirectory() ? " CONTAINS FOLDER: " : " CONTAINS FILE: ";
            System.out.println(getClass().getSimpleName() + type + flag + " Path: " + path);
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
        if(!path.endsWith("/")) {
            path = path +  "/";
        }
        return path;
    }
}