package com.github.xpenatan.gdx.html5.bullet;


import com.github.xpenatan.gdx.html5.bullet.codegen.CodeGen;
import com.github.xpenatan.gdx.html5.bullet.codegen.CodeGenWrapper;
import com.github.xpenatan.gdx.html5.bullet.codegen.TeaVMCodeWrapper;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String basePath = new File(".").getAbsolutePath();
        generate(basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", new TeaVMCodeWrapper());
    }

    public static void generate(String sourceDir, String genDir, CodeGenWrapper wrapper)  throws Exception {
        new CodeGen().generate(sourceDir, genDir, wrapper, null);
    }
}