package com.github.xpenatan.gdx.html5.bullet;



import com.github.xpenatan.tools.jparser.JParser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new TeaVMCodeParser(), basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", null);
    }
}