package com.github.xpenatan.gdx.html5.bullet;



import com.github.xpenatan.tools.jparser.JParser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String basePath = new File(".").getAbsolutePath();
        generate(basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", new TeaVMCodeParser());
    }

    public static void generate(String sourceDir, String genDir, TeaVMCodeParser wrapper)  throws Exception {
        new JParser().generate(sourceDir, genDir, wrapper, null);
    }
}