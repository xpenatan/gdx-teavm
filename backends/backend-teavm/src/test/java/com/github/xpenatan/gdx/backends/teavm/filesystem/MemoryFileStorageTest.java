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
        TeaFiles teaFiles = new TeaFiles(config, null, null) {
            @Override
            public FileHandle local(String path) {
                return new TeaFileHandle(null, internalStorage, path, FileType.Local);
            }
        };
        Gdx.files = teaFiles;
        internalStorage = teaFiles.internalStorage;
//        internalStorage.debug = true;
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

        FileHandle[] list = Gdx.files.local(".").list();

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

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        A.moveTo(B);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(A.exists()).isFalse();
        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(AA.exists()).isFalse();
        Truth.assertThat(AB.exists()).isFalse();
        Truth.assertThat(ABA.exists()).isFalse();
        Truth.assertThat(ABAX.exists()).isFalse();
        Truth.assertThat(AC.exists()).isFalse();
    }

    @Test
    public void test_rename_A_to_B_from_root() {

        FileHandle A = Gdx.files.local("A");
        FileHandle AA = A.child("AA");
        FileHandle AB = A.child("AB");
        FileHandle AX = A.child("file.txt");
        FileHandle B = A.sibling("B");

        A.mkdirs();
        AA.mkdirs();
        AB.mkdirs();
        AX.writeString("Hello", false);

        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(AA.exists()).isTrue();
        Truth.assertThat(AB.exists()).isTrue();
        Truth.assertThat(AX.exists()).isTrue();
        Truth.assertThat(B.exists()).isFalse();

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        A.moveTo(B);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(A.exists()).isFalse();
        Truth.assertThat(AA.exists()).isFalse();
        Truth.assertThat(AB.exists()).isFalse();
        Truth.assertThat(AX.exists()).isFalse();
    }

    @Test
    public void test_rename_A_to_B_from_child() {

        FileHandle Root = Gdx.files.local("root");
        FileHandle A = Root.child("A");
        FileHandle AA = A.child("AA");
        FileHandle AB = A.child("AB");
        FileHandle AX = A.child("file.txt");
        FileHandle B = A.sibling("B");

        A.mkdirs();
        AA.mkdirs();
        AB.mkdirs();
        AX.writeString("Hello", false);

        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(AA.exists()).isTrue();
        Truth.assertThat(AB.exists()).isTrue();
        Truth.assertThat(AX.exists()).isTrue();
        Truth.assertThat(B.exists()).isFalse();

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        A.moveTo(B);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(A.exists()).isFalse();
        Truth.assertThat(AA.exists()).isFalse();
        Truth.assertThat(AB.exists()).isFalse();
        Truth.assertThat(AX.exists()).isFalse();
    }

    @Test
    public void test_move_folder_A_to_B() {

        FileHandle Root = Gdx.files.local("root");
        FileHandle A = Root.child("A");
        FileHandle B = A.sibling("B");
        FileHandle BA = B.child("A");

        A.mkdirs();
        B.mkdirs();

        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(BA.exists()).isFalse();

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        A.moveTo(B);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(A.exists()).isFalse();
        Truth.assertThat(BA.exists()).isTrue();
    }

    @Test
    public void test_move_file_A_to_B() {

        FileHandle Root = Gdx.files.local("root");
        FileHandle A = Root.child("A.txt");
        FileHandle B = A.sibling("B");
        FileHandle BA = B.child("A.txt");

        A.writeString("Hello", false);
        B.mkdirs();

        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(BA.exists()).isFalse();

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        A.moveTo(B);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(B.exists()).isTrue();
        Truth.assertThat(A.exists()).isFalse();
        Truth.assertThat(BA.exists()).isTrue();
    }

    @Test
    public void test_rename_folder_child() {

        FileHandle Root = Gdx.files.local("root");
        FileHandle A = Root.child("A");
        FileHandle AA = A.child("AA");

        FileHandle AB = AA.sibling("AB");

        Root.mkdirs();
        A.mkdirs();
        AA.mkdirs();

        Truth.assertThat(Root.exists()).isTrue();
        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(AA.exists()).isTrue();

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        AA.moveTo(AB);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(Root.exists()).isTrue();
        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(AA.exists()).isFalse();
        Truth.assertThat(AB.exists()).isTrue();
    }

    @Test
    public void test_rename_file_child() {

        FileHandle Root = Gdx.files.local("root");
        FileHandle A = Root.child("A");
        FileHandle AA = A.child("AA.txt");

        FileHandle AB = AA.sibling("AB.txt");

        Root.mkdirs();
        A.mkdirs();
        AA.writeString("Hello", false);

        Truth.assertThat(Root.exists()).isTrue();
        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(AA.exists()).isTrue();

        System.out.println("\n####################### FILES BEFORE: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES BEFORE END");

        System.out.println("####################### MOVE TO: \n");
        AA.moveTo(AB);

        System.out.println("\n####################### FILES AFTER: ");
        internalStorage.printAllFiles();
        System.out.println("####################### FILES AFTER END");

        Truth.assertThat(Root.exists()).isTrue();
        Truth.assertThat(A.exists()).isTrue();
        Truth.assertThat(AA.exists()).isFalse();
        Truth.assertThat(AB.exists()).isTrue();
    }
}