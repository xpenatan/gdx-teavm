package com.github.xpenatan.gdx.html5.box2d;

import com.github.xpenatan.jparser.core.JParser;
import com.github.xpenatan.jparser.core.idl.IDLFile;
import com.github.xpenatan.jparser.core.idl.IDLParser;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "..\\..\\gdx-box2d\\gdx-box2d-build\\jni\\box2D.idl";
        IDLFile idlFile = IDLParser.parseFile(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new Box2dParser(idlFile), basePath + "./gdx-box2d-base/src", "../gdx-box2d-teavm/src", null);
    }
}