package com.github.xpenatan.gdx.html5.bullet;


import com.github.xpenatan.gdx.html5.bullet.web.JSCodeGen;
import com.github.xpenatan.gdx.html5.bullet.web.JSCodeWrapper;
import com.github.xpenatan.gdx.html5.bullet.web.TeaVMCodeWrapper;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String basePath = new File(".").getAbsolutePath();
        generate(basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", new TeaVMCodeWrapper());
    }

    public static void generate(String sourceDir, String genDir, JSCodeWrapper wrapper)  throws Exception {
        new JSCodeGen().generate(sourceDir, genDir, wrapper, null);
    }
}