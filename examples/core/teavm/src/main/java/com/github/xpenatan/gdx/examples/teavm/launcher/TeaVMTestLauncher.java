package com.github.xpenatan.gdx.examples.teavm.launcher;

import com.badlogic.gdx.Gdx;
import com.github.xpenatan.gdx.backends.teavm.TeaApplication;
import com.github.xpenatan.gdx.backends.teavm.TeaApplicationConfiguration;
import com.github.xpenatan.gdx.backends.teavm.TeaFiles;
import com.github.xpenatan.gdx.examples.tests.FilesTest;
import com.github.xpenatan.gdx.examples.tests.PixelTest;
import com.github.xpenatan.gdx.examples.tests.ReadPixelsTest;

public class TeaVMTestLauncher {

    public static void main(String[] args) {
        TeaApplicationConfiguration config = new TeaApplicationConfiguration("canvas");
        config.width = 0;
        config.height = 0;
        config.showDownloadLogs = true;
//        new TeaApplication(new GearsDemo(), config);
        new TeaApplication(new FilesTest() {
            @Override
            public void create() {
                TeaFiles files  = (TeaFiles)Gdx.files;
                files.localStorage.debug = true;
                super.create();
            }

            @Override
            public void printInternalFiles() {
                TeaFiles files  = (TeaFiles)Gdx.files;
                files.internalStorage.printAllFiles();
            }
        }, config);
//        new TeaApplication(new PixelTest(), config);
    }
}