package com.github.xpenatan.gdx.html5.bullet;



import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLParser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "..\\..\\gdx-bullet-old\\gdx-bullet-build\\jni\\emscripten\\bullet.idl";
        IDLFile idlFile = IDLParser.parseFile(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new TeaVMCodeParser(idlFile), basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", null);
    }


}