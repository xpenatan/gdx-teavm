package com.github.xpenatan.gdx.html5.box2d;



import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLParser;

import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "..\\..\\gdx-bullet\\gdx-box2d-build\\jni\\bullet.idl";
        IDLFile idlFile = IDLParser.parseFile(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new TeaVMCodeParser(idlFile), basePath + "./gdx-box2d-base/src", "../gdx-box2d-teavm/src", null);
    }
}