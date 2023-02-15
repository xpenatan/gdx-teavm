package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.dom.StorageWrapper;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Local storage based file system. Stays persistent but is limited to about 2.5-5MB in general.
 *
 * @author noblemaster
 */
final class FileDBStorage extends FileDB {

    /**
     * Our files stored and encoded as Base64. Directories with "d:..." and regular files stored as "f:...".
     */
    private final StorageWrapper storage;

    private static final String ID_FOR_FILE = "file-f:";
    private static final String ID_FOR_DIR = "file-d:";
    /**
     * The ID length should match both for files and directories.
     */
    private static final int ID_LENGTH = ID_FOR_FILE.length();

    FileDBStorage(StorageWrapper storage) {
        this.storage = storage;
    }

    StorageWrapper storage() {
        return storage;
    }

    @Override
    public InputStream read(TeaFileHandle file) {
        // we obtain the stored byte array
        String data = storage.getItem(ID_FOR_FILE + file.path());
        try {
            return new ByteArrayInputStream(HEXCoder.decode(data));
        }
        catch(RuntimeException e) {
            // something corrupted: we remove it & re-throw the error
            storage.removeItem(ID_FOR_FILE + file.path());
            throw e;
        }
    }

    @Override
    public void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        String value = HEXCoder.encode(data);

        // append to existing as needed
        if(append) {
            String dataCurrent = storage.getItem(ID_FOR_FILE + file.path());
            if(dataCurrent != null) {
                value = dataCurrent + value;
            }
        }
        storage.setItem(ID_FOR_FILE + file.path(), value);
    }

    @Override
    public String[] paths(TeaFileHandle file) {
        String dir = file.path() + "/";
        List<String> paths = new ArrayList<String>(storage.getLength());
        for(int i = 0; i < storage.getLength(); i++) {
            // cut the identifier for files and directories and add to path list
            String key = storage.key(i);
            if (key.startsWith(ID_FOR_DIR) || key.startsWith(ID_FOR_FILE)) {
                String path = key.substring(ID_LENGTH);
                if(path.startsWith(dir)) {
                    paths.add(path);
                }
            }
        }
        return paths.toArray(new String[paths.size()]);
    }

    @Override
    public boolean isDirectory(TeaFileHandle file) {
        return storage.getItem(ID_FOR_DIR + file.path()) != null;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        // add for current path if not already a file!
        if(storage.getItem(ID_FOR_FILE + file.path()) == null) {
            storage.setItem(ID_FOR_DIR + file.path(), "");
        }

        // do for parent directories also
        file = (TeaFileHandle)file.parent();
        while(file.path().length() > 0) {
            storage.setItem(ID_FOR_DIR + file.path(), "");
            file = (TeaFileHandle)file.parent();
        }
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        // check if either a file or directory entry exists
        return (storage.getItem(ID_FOR_DIR + file.path()) != null) ||
                (storage.getItem(ID_FOR_FILE + file.path()) != null);
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        storage.removeItem(ID_FOR_DIR + file.path());
        storage.removeItem(ID_FOR_FILE + file.path());
        return true;
    }

    @Override
    public long length(TeaFileHandle file) {
        // this is somewhat slow
        String data = storage.getItem(ID_FOR_FILE + file.path());
        try {
            return HEXCoder.decode(data).length;
        }
        catch(RuntimeException e) {
            // something corrupted: we report 'null'
            Gdx.app.error("File System", "Error Decoding Data", e);
            storage.removeItem(ID_FOR_FILE + file.path());
            return 0L;
        }
    }

    @Override
    public void rename(TeaFileHandle source, TeaFileHandle target) {
        // assuming file (not directory)
        String data = storage.getItem(ID_FOR_FILE + source.path());
        storage.removeItem(ID_FOR_FILE + source.path());
        storage.setItem(ID_FOR_FILE + target.path(), data);
    }
}
