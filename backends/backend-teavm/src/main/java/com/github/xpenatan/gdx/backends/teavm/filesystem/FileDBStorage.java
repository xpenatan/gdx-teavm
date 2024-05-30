package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Local storage based file system. Stays persistent but is limited to about 2.5-5MB in general.
 *
 * @author noblemaster
 */
final class FileDBStorage extends FileDB {

    /**
     * Our files stored and encoded as Base64. Directories with "d:..." and regular files stored as "f:...".
     */
    private final Store store;

    private static final String ID_FOR_FILE = "file-f:";
    private static final String ID_FOR_DIR = "file-d:";
    /**
     * The ID length should match both for files and directories.
     */
    private static final int ID_LENGTH = ID_FOR_FILE.length();

    FileDBStorage(Store store) {
        this.store = store;
    }

    @Override
    public InputStream read(TeaFileHandle file) {
        // we obtain the stored byte array
        String data = store.getItem(ID_FOR_FILE + file.path());
        try {
            return new ByteArrayInputStream(HEXCoder.decode(data));
        }
        catch(RuntimeException e) {
            // something corrupted: we remove it & re-throw the error
            store.removeItem(ID_FOR_FILE + file.path());
            throw e;
        }
    }

    @Override
    public void writeInternal(TeaFileHandle file, byte[] data, boolean append, int expectedLength) {
        String value = HEXCoder.encode(data);

        // append to existing as needed
        if(append) {
            String dataCurrent = store.getItem(ID_FOR_FILE + file.path());
            if(dataCurrent != null) {
                value = dataCurrent + value;
            }
        }
        store.setItem(ID_FOR_FILE + file.path(), value);
    }

    @Override
    public String[] paths(TeaFileHandle file) {
        String dir = file.path() + "/";
        int length = store.getLength();
        Array<String> paths = new Array<String>(length);
        for(int i = 0; i < length; i++) {
            // cut the identifier for files and directories and add to path list
            String key = store.key(i);
            if ((key != null) && ((key.startsWith(ID_FOR_DIR) || key.startsWith(ID_FOR_FILE)))) {
                String path = key.substring(ID_LENGTH);
                if(path.startsWith(dir)) {
                    paths.add(path);
                }
            }
        }
        return paths.toArray(String.class);
    }

    @Override
    public boolean isDirectory(TeaFileHandle file) {
        return store.getItem(ID_FOR_DIR + file.path()) != null;
    }

    @Override
    public void mkdirs(TeaFileHandle file) {
        // add for current path if not already a file!
        if(store.getItem(ID_FOR_FILE + file.path()) == null) {
            store.setItem(ID_FOR_DIR + file.path(), "");
        }

        // do for parent directories also
        file = (TeaFileHandle)file.parent();
        while(file.path().length() > 0) {
            store.setItem(ID_FOR_DIR + file.path(), "");
            file = (TeaFileHandle)file.parent();
        }
    }

    @Override
    public boolean exists(TeaFileHandle file) {
        // check if either a file or directory entry exists
        return (store.getItem(ID_FOR_DIR + file.path()) != null) ||
                (store.getItem(ID_FOR_FILE + file.path()) != null);
    }

    @Override
    public boolean delete(TeaFileHandle file) {
        store.removeItem(ID_FOR_DIR + file.path());
        store.removeItem(ID_FOR_FILE + file.path());
        return true;
    }

    @Override
    public boolean deleteDirectory(TeaFileHandle file) {
        throw new GdxRuntimeException("Cannot delete directory (missing implementation): " + file);
    }

    @Override
    public long length(TeaFileHandle file) {
        // this is somewhat slow
        String data = store.getItem(ID_FOR_FILE + file.path());
        if (data != null) {
            // 2 HEX characters == 1 byte
            return data.length() / 2;
        }
        else {
            // no data available
            return 0L;
        }
    }

    @Override
    public void rename(TeaFileHandle source, TeaFileHandle target) {
        // assuming file (not directory)
        String data = store.getItem(ID_FOR_FILE + source.path());
        store.removeItem(ID_FOR_FILE + source.path());
        store.setItem(ID_FOR_FILE + target.path(), data);
    }

    @Override
    public String getLocalStoragePath() {
        return null;
    }
}
