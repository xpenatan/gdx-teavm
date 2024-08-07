package com.github.xpenatan.gdx.examples.tests;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.StreamUtils;

public class FilesTest implements ApplicationListener {
    String message = "";
    boolean success;
    BitmapFont font;
    SpriteBatch batch;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        font = new BitmapFont(Gdx.files.internal("custom/lsans-15.fnt"), false);
        batch = new SpriteBatch();

        if(Gdx.files.isExternalStorageAvailable()) {
            message += "External storage available\n";
            message += "External storage path: " + Gdx.files.getExternalStoragePath() + "\n";

            try {
                InputStream in = Gdx.files.internal("custom/cube.obj").read();
                StreamUtils.closeQuietly(in);
                message += "Open internal success\n";
            } catch(Throwable e) {
                message += "Couldn't open internal custom/cube.obj\n" + e.getMessage() + "\n";
            }

            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(Gdx.files.external("test.txt").write(false)));
                out.write("test");
                message += "Write external success\n";
            } catch(GdxRuntimeException ex) {
                message += "Couldn't open externalstorage/test.txt\n";
            } catch(IOException e) {
                message += "Couldn't write externalstorage/test.txt\n";
            } finally {
                StreamUtils.closeQuietly(out);
            }

            try {
                InputStream in = Gdx.files.external("test.txt").read();
                StreamUtils.closeQuietly(in);
                message += "Open external success\n";
            } catch(Throwable e) {
                message += "Couldn't open internal externalstorage/test.txt\n" + e.getMessage() + "\n";
            }

            BufferedReader in = null;
            try {
                in = new BufferedReader(new InputStreamReader(Gdx.files.external("test.txt").read()));
                if(!in.readLine().equals("test"))
                    message += "Read result wrong\n";
                else
                    message += "Read external success\n";
            } catch(GdxRuntimeException ex) {
                message += "Couldn't open externalstorage/test.txt\n";
            } catch(IOException e) {
                message += "Couldn't read externalstorage/test.txt\n";
            } finally {
                StreamUtils.closeQuietly(in);
            }

            if(!Gdx.files.external("test.txt").delete()) message += "Couldn't delete externalstorage/test.txt";
        }
        else {
            message += "External storage not available";
        }
        testLocalPath();
        try {
            testClasspath();
            testInternal();
            if(!(Gdx.app.getType() == ApplicationType.WebGL)) {
                testExternal();
                testAbsolute();
                testLocal();
            }
        } catch(IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void resize(int width, int height) {
    }

    private void testLocalPath() {
        if(Gdx.files.isLocalStorageAvailable()) {
            message += "\nLocal storage available\n";
            message += "Local storage path: " + Gdx.files.getLocalStoragePath() + "\n\n";

            {
                // Test multiple subfolder
                FileHandle subFolder = Gdx.files.local("rootFolder/childFolder/subFolder/");
                FileHandle childFolder1 = Gdx.files.local("rootFolder/childFolder/");
                FileHandle childFolder2 = Gdx.files.local("rootFolder/childFolder");
                FileHandle rootFolder1 = Gdx.files.local("rootFolder/");
                FileHandle rootFolder2 = Gdx.files.local("rootFolder");

                boolean exists = rootFolder1.exists();

                message += "subFolder: " + subFolder +  " exists: " + subFolder.exists() + "\n";
                message += "childFolder1: " + childFolder1 +  " exists: " + childFolder1.exists() + "\n";
                message += "childFolder2: " + childFolder2 +  " exists: " + childFolder2.exists() + "\n";
                message += "rootFolder1: " + rootFolder1 +  " exists: " + exists + "\n";
                message += "rootFolder2: " + rootFolder2 +  " exists: " + rootFolder2.exists() + "\n";

                subFolder.mkdirs();

                if(exists) {
                    FileHandle[] list = rootFolder1.list();
                    for(int i = 0; i < list.length; i++) {
                        FileHandle fileHandle = list[i];
                        System.out.println("FOLDER LIST: " + fileHandle);
                    }
                    boolean delete = rootFolder1.deleteDirectory();
                    System.out.println("Deleted: " + delete + " " + rootFolder1);
                }

            }
            message += "\n";
            {
                // Test file in subfolder
                FileHandle subFile = Gdx.files.local("rootFileFolder/childFileFolder/subFile.txt");
                FileHandle childFileFolder1 = Gdx.files.local("rootFileFolder/childFileFolder/");
                FileHandle childFileFolder2 = Gdx.files.local("rootFileFolder/childFileFolder");
                FileHandle rootFileFolder1 = Gdx.files.local("rootFileFolder/");
                FileHandle rootFileFolder2 = Gdx.files.local("rootFileFolder");

                boolean exists = rootFileFolder1.exists();

                message += "subFile: " + subFile +  " exists: " + subFile.exists() + "\n";
                message += "childFileFolder1: " + childFileFolder1 +  " exists: " + childFileFolder1.exists() + "\n";
                message += "childFileFolder2: " + childFileFolder2 +  " exists: " + childFileFolder2.exists() + "\n";
                message += "rootFileFolder1: " + rootFileFolder1 +  " exists: " + exists + "\n";
                message += "rootFileFolder2: " + rootFileFolder2 +  " exists: " + rootFileFolder2.exists() + "\n";

                FileHandle helloFolder = Gdx.files.local("HELLO");
                if(exists) {
                    System.out.println("================");
                    rootFileFolder1.moveTo(helloFolder);
                }
                else {
                    subFile.writeString("HELLO", false);
                    helloFolder.deleteDirectory();
                }
            }
            message += "\n";

            BufferedWriter out = null;
            boolean canDelete = false;
            try {
                FileHandle testFile = Gdx.files.local("test.txt");
                boolean exists = testFile.exists();
                canDelete = exists;
                message += "text.txt exists: " + exists + "\n";

                testFile.writeString("test", false);

            } catch(GdxRuntimeException ex) {
                message += "Couldn't open localstorage/test.txt\n";
            } finally {
                StreamUtils.closeQuietly(out);
            }

            try {
                String s = Gdx.files.local("test.txt").readString();
                message += "Open local success\n";
            } catch(Throwable e) {
                message += "Couldn't open localstorage/test.txt\n" + e.getMessage() + "\n";
            }

            BufferedReader in = null;
            try {
                FileHandle testFile = Gdx.files.local("test.txt");
                InputStream read = testFile.read();
                in = new BufferedReader(new InputStreamReader(read));
                if(!in.readLine().equals("test"))
                    message += "Read result wrong\n";
                else
                    message += "Read local success\n";
            } catch(GdxRuntimeException ex) {
                message += "Couldn't open localstorage/test.txt\n";
            } catch(Throwable e) {
                message += "Couldn't read localstorage/test.txt\n";
            } finally {
                StreamUtils.closeQuietly(in);
            }

            try {
                byte[] testBytes = Gdx.files.local("test.txt").readBytes();
                if(Arrays.equals("test".getBytes(), testBytes))
                    message += "Read into byte array success\n";
                else
                    fail();
            } catch(Throwable e) {
                message += "Couldn't read localstorage/test.txt\n" + e.getMessage() + "\n";
            }

            if(canDelete) {
                if(!Gdx.files.local("test.txt").delete())  {
                    message += "Couldn't delete localstorage/test.txt";
                }
            }
            message += "\n";
            {
                FileHandle root1 = Gdx.files.local("");
                FileHandle root2 = Gdx.files.local("./");
                FileHandle root3 = Gdx.files.local(".");

                message += "root1: " + root1 +  " exists: " + root1.exists() + " Root size: " + root1.list().length + "\n";
                message += "root2: " + root2 +  " exists: " + root2.exists() + " Root size: " + root2.list().length + "\n";
                message += "root3: " + root3 +  " exists: " + root3.exists() + " Root size: " + root3.list().length + "\n";
                message += "root2 length: " + root2.length() + "\n";
                FileHandle[] list = root1.list();
                for(int i = 0; i < list.length; i++) {
                    System.out.println("Files: " + list[i]);
                }
            }
        }
    }

    private void testClasspath() throws IOException {
        // no classpath support on ios
        if(Gdx.app.getType() == ApplicationType.iOS) return;
        FileHandle handle = Gdx.files.classpath("com/badlogic/gdx/utils/lsans-15.png");
        if(!handle.exists()) fail();
        if(handle.isDirectory()) fail();
        try {
            handle.delete();
            fail();
        } catch(Exception expected) {
        }
        try {
            handle.list();
            fail();
        } catch(Exception expected) {
        }
        try {
            handle.read().close();
            fail();
        } catch(Exception ignored) {
        }
        FileHandle dir = Gdx.files.classpath("com/badlogic/gdx/utils");
        if(dir.isDirectory()) fail();
        FileHandle child = dir.child("lsans-15.fnt");
        if(!child.name().equals("lsans-15.fnt")) fail();
        if(!child.nameWithoutExtension().equals("lsans-15")) fail();
        if(!child.extension().equals("fnt")) fail();
        handle.read().close();
        if(handle.readBytes().length != handle.length()) fail();
    }

    private void testInternal() throws IOException {
        FileHandle handle = Gdx.files.internal("custom/badlogic.jpg");
        if(!handle.exists()) fail("Couldn't find internal file");
        if(handle.isDirectory()) fail("Internal file shouldn't be a directory");
        try {
            handle.delete();
            fail("Shouldn't be able to delete internal file");
        } catch(Exception expected) {
        }
        if(handle.list().length != 0) fail("File length shouldn't be 0");
        if(Gdx.app.getType() != ApplicationType.Android) {
            FileHandle parent = handle.parent();
            boolean exists = parent.exists();
            if(!exists) fail("Parent doesn't exist");
        }
        try {
            handle.read().close();
            fail();
        } catch(Exception ignored) {
        }
        FileHandle dir = Gdx.files.internal("custom");
        if(Gdx.app.getType() != ApplicationType.Android) {
            if(!dir.exists()) fail();
        }
//        printInternalFiles();
        if(!dir.isDirectory()) fail();
        if(dir.list().length == 0) fail();
        Gdx.app.log("FilesTest", "Files in data: " + Arrays.toString(dir.list()) + " (" + dir.list().length + ")");
        FileHandle child = dir.child("badlogic.jpg");
        if(!child.name().equals("badlogic.jpg")) fail();
        if(!child.nameWithoutExtension().equals("badlogic")) fail();
        if(!child.extension().equals("jpg")) fail();
        if(Gdx.app.getType() != ApplicationType.Android) {
            if(!child.parent().exists()) fail();
        }
        if(!(Gdx.app.getType() == ApplicationType.WebGL)) {
            FileHandle copy = Gdx.files.external("badlogic.jpg-copy");
            copy.delete();
            if(copy.exists()) fail();
            handle.copyTo(copy);
            if(!copy.exists()) fail();
            if(copy.length() != 68465) fail();
            copy.delete();
            if(copy.exists()) fail();
        }
        handle.read().close();
        if(handle.readBytes().length != handle.length()) fail();
    }

    private void testExternal() throws IOException {
        String path = "meow";
        FileHandle handle = Gdx.files.external(path);
        handle.delete();
        if(handle.exists()) fail();
        if(handle.isDirectory()) fail();
        if(handle.delete()) fail();
        if(handle.list().length != 0) fail();
        if(handle.child("meow").exists()) fail();
        if(!handle.parent().exists()) fail();
        try {
            handle.read().close();
            fail();
        } catch(Exception ignored) {
        }
        handle.mkdirs();
        if(!handle.exists()) fail();
        if(!handle.isDirectory()) fail();
        if(handle.list().length != 0) fail();
        handle.child("meow").mkdirs();
        if(handle.list().length != 1) fail();
        FileHandle child = handle.list()[0];
        if(!child.name().equals("meow")) fail();
        if(!child.parent().exists()) fail();
        if(!handle.deleteDirectory()) fail();
        if(handle.exists()) fail();
        OutputStream output = handle.write(false);
        output.write("moo".getBytes());
        output.close();
        if(!handle.exists()) fail();
        if(handle.length() != 3) fail();
        FileHandle copy = Gdx.files.external(path + "-copy");
        copy.delete();
        if(copy.exists()) fail();
        handle.copyTo(copy);
        if(!copy.exists()) fail();
        if(copy.length() != 3) fail();
        FileHandle move = Gdx.files.external(path + "-move");
        move.delete();
        if(move.exists()) fail();
        copy.moveTo(move);
        if(!move.exists()) fail();
        if(move.length() != 3) fail();
        move.deleteDirectory();
        if(move.exists()) fail();
        InputStream input = handle.read();
        byte[] bytes = new byte[6];
        if(input.read(bytes) != 3) fail();
        input.close();
        if(!new String(bytes, 0, 3).equals("moo")) fail();
        output = handle.write(true);
        output.write("cow".getBytes());
        output.close();
        if(handle.length() != 6) fail();
        input = handle.read();
        if(input.read(bytes) != 6) fail();
        input.close();
        if(!new String(bytes, 0, 6).equals("moocow")) fail();
        if(handle.isDirectory()) fail();
        if(handle.list().length != 0) fail();
        if(!handle.name().equals("meow")) fail();
        if(!handle.nameWithoutExtension().equals("meow")) fail();
        if(!handle.extension().equals("")) fail();
        handle.deleteDirectory();
        if(handle.exists()) fail();
        if(handle.isDirectory()) fail();
        handle.delete();
        handle.deleteDirectory();
    }

    private void testAbsolute() throws IOException {
        String path = new File(Gdx.files.getExternalStoragePath(), "meow").getAbsolutePath();
        FileHandle handle = Gdx.files.absolute(path);
        handle.delete();
        if(handle.exists()) fail();
        if(handle.isDirectory()) fail();
        if(handle.delete()) fail();
        if(handle.list().length != 0) fail();
        if(handle.child("meow").exists()) fail();
        if(!handle.parent().exists()) fail();
        try {
            handle.read().close();
            fail();
        } catch(Exception ignored) {
        }
        handle.mkdirs();
        if(!handle.exists()) fail();
        if(!handle.isDirectory()) fail();
        if(handle.list().length != 0) fail();
        handle.child("meow").mkdirs();
        if(handle.list().length != 1) fail();
        FileHandle child = handle.list()[0];
        if(!child.name().equals("meow")) fail();
        if(!child.parent().exists()) fail();
        if(!handle.deleteDirectory()) fail();
        if(handle.exists()) fail();
        OutputStream output = handle.write(false);
        output.write("moo".getBytes());
        output.close();
        if(!handle.exists()) fail();
        if(handle.length() != 3) fail();
        FileHandle copy = Gdx.files.absolute(path + "-copy");
        copy.delete();
        if(copy.exists()) fail();
        handle.copyTo(copy);
        if(!copy.exists()) fail();
        if(copy.length() != 3) fail();
        FileHandle move = Gdx.files.absolute(path + "-move");
        move.delete();
        if(move.exists()) fail();
        copy.moveTo(move);
        if(!move.exists()) fail();
        if(move.length() != 3) fail();
        move.deleteDirectory();
        if(move.exists()) fail();
        InputStream input = handle.read();
        byte[] bytes = new byte[6];
        if(input.read(bytes) != 3) fail();
        input.close();
        if(!new String(bytes, 0, 3).equals("moo")) fail();
        output = handle.write(true);
        output.write("cow".getBytes());
        output.close();
        if(handle.length() != 6) fail();
        input = handle.read();
        if(input.read(bytes) != 6) fail();
        input.close();
        if(!new String(bytes, 0, 6).equals("moocow")) fail();
        if(handle.isDirectory()) fail();
        if(handle.list().length != 0) fail();
        if(!handle.name().equals("meow")) fail();
        if(!handle.nameWithoutExtension().equals("meow")) fail();
        if(!handle.extension().equals("")) fail();
        handle.deleteDirectory();
        if(handle.exists()) fail();
        if(handle.isDirectory()) fail();
        handle.delete();
        handle.deleteDirectory();
    }

    private void testLocal() throws IOException {
        String path = "meow";
        FileHandle handle = Gdx.files.local(path);
        handle.delete();
        if(handle.exists()) fail();
        if(handle.isDirectory()) fail();
        if(handle.delete()) fail();
        if(handle.list().length != 0) fail();
        if(handle.child("meow").exists()) fail();
        if(!handle.parent().exists()) fail();
        try {
            handle.read().close();
            fail();
        } catch(Exception ignored) {
        }
        handle.mkdirs();
        if(!handle.exists()) fail();
        if(!handle.isDirectory()) fail();
        if(handle.list().length != 0) fail();
        handle.child("meow").mkdirs();
        if(handle.list().length != 1) fail();
        FileHandle child = handle.list()[0];
        if(!child.name().equals("meow")) fail();
        if(!child.parent().exists()) fail();
        if(!handle.deleteDirectory()) fail();
        if(handle.exists()) fail();
        OutputStream output = handle.write(false);
        output.write("moo".getBytes());
        output.close();
        if(!handle.exists()) fail();
        if(handle.length() != 3) fail();
        FileHandle copy = Gdx.files.local(path + "-copy");
        copy.delete();
        if(copy.exists()) fail();
        handle.copyTo(copy);
        if(!copy.exists()) fail();
        if(copy.length() != 3) fail();
        FileHandle move = Gdx.files.local(path + "-move");
        move.delete();
        if(move.exists()) fail();
        copy.moveTo(move);
        if(!move.exists()) fail();
        if(move.length() != 3) fail();
        move.deleteDirectory();
        if(move.exists()) fail();
        InputStream input = handle.read();
        byte[] bytes = new byte[6];
        if(input.read(bytes) != 3) fail();
        input.close();
        if(!new String(bytes, 0, 3).equals("moo")) fail();
        output = handle.write(true);
        output.write("cow".getBytes());
        output.close();
        if(handle.length() != 6) fail();
        input = handle.read();
        if(input.read(bytes) != 6) fail();
        input.close();
        if(!new String(bytes, 0, 6).equals("moocow")) fail();
        if(handle.isDirectory()) fail();
        if(handle.list().length != 0) fail();
        if(!handle.name().equals("meow")) fail();
        if(!handle.nameWithoutExtension().equals("meow")) fail();
        if(!handle.extension().equals("")) fail();
        handle.deleteDirectory();
        if(handle.exists()) fail();
        if(handle.isDirectory()) fail();
        handle.delete();
        handle.deleteDirectory();
    }

    private void fail() {
        throw new RuntimeException();
    }

    private void fail(String msg) {
        throw new RuntimeException(msg);
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        font.draw(batch, message, 20, Gdx.graphics.getHeight() - 20);
        batch.end();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    public void printInternalFiles() {

    }
}
