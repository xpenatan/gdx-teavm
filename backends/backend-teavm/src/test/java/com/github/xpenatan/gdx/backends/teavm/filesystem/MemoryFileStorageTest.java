package com.github.xpenatan.gdx.backends.teavm.filesystem;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaFileHandle;
import com.github.xpenatan.gdx.backends.teavm.TeaFiles;
import com.github.xpenatan.gdx.backends.teavm.filesystem.types.InternalDBStorage;
import com.google.common.truth.Truth;
import org.junit.Before;
import org.junit.Test;

public class MemoryFileStorageTest {

    private InternalDBStorage internalStorage;

    @Before
    public void setUp() {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("");
        TeaFiles teaFiles = new TeaFiles(config, null) {
            @Override
            public FileHandle local(String path) {
                return new TeaFileHandle(null, internalStorage, path, FileType.Local);
            }
        };
        Gdx.files = teaFiles;
        internalStorage = teaFiles.internalStorage;
        internalStorage.debug = false;
    }

    @Test
    public void test_add_remove_01() {
        // Have 2 folders and another as a child of the second folder.
        // Delete the first folder.

        FileHandle A = Gdx.files.local("New folder");
        FileHandle B = Gdx.files.local("New folder(2)");
        FileHandle BA = B.child("New folder(2)/New folder");

        A.mkdirs();
        B.mkdirs();
        BA.mkdirs();

        System.out.println("FILES BEFORE: ");
        internalStorage.printAllFiles();

        boolean b = A.deleteDirectory();

        System.out.println("FILES AFTER: ");
        internalStorage.printAllFiles();

        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(BA.exists()).isTrue();
    }

    @Test
    public void test_add_remove_02() {
        // Have 2 folders and another as a child of the second folder.
        // Delete the first folder.

        FileHandle A = Gdx.files.local("A");
        FileHandle B = Gdx.files.local("B");
        FileHandle BA = B.child("A");

        A.mkdirs();
        B.mkdirs();
        BA.mkdirs();

        boolean b = A.deleteDirectory();

        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(BA.exists()).isTrue();
    }

    @Test
    public void test_move_from_A_to_B() {
        // Have 2 folders and another as a child of the second folder.
        // Delete the first folder.

        FileHandle A = Gdx.files.local("A");
        FileHandle AA = A.child("AA");
        FileHandle AB = A.child("AB");
        FileHandle ABA = AB.child("ABA");
        FileHandle ABAX = ABA.child("file.txt");
        FileHandle AC = A.child("AC");
        FileHandle B = Gdx.files.local("B");

        A.mkdirs();
        AA.mkdirs();
        AB.mkdirs();
        ABA.mkdirs();
        AC.mkdirs();
        B.mkdirs();

        ABAX.writeString("Text", false);
        ABAX.writeString("One", true);

        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(AA.exists()).isTrue();
        Truth.assertThat(AB.exists()).isTrue();
        Truth.assertThat(ABA.exists()).isTrue();
        Truth.assertThat(ABAX.exists()).isTrue();
        Truth.assertThat(AC.exists()).isTrue();

        System.out.println("FILES BEFORE: ");
        internalStorage.printAllFiles();

        A.moveTo(B);

        System.out.println("FILES AFTER: ");
        internalStorage.printAllFiles();

        Truth.assertThat(A.exists()).isFalse();
        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(AA.exists()).isFalse();
        Truth.assertThat(AB.exists()).isFalse();
        Truth.assertThat(ABA.exists()).isFalse();
        Truth.assertThat(ABAX.exists()).isFalse();
        Truth.assertThat(AC.exists()).isFalse();
    }
}