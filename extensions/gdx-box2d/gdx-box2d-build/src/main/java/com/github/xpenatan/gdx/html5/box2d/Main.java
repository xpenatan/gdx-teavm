package com.github.xpenatan.gdx.html5.box2d;

import com.github.xpenatan.jparser.core.JParser;
import com.github.xpenatan.jparser.idl.IDLReader;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "..\\..\\gdx-box2d\\gdx-box2d-build\\jni\\box2D.idl";
        IDLReader idlReader = IDLReader.readIDL(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new Box2dParser(idlReader), basePath + "./gdx-box2d-base/src/main/java", "../gdx-box2d-teavm/src/main/java", null);
    }
}