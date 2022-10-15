package com.github.xpenatan.gdx.html5.box2d;

import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.teavm.TeaVMCodeParser;

public class Box2dParser extends TeaVMCodeParser {
    public Box2dParser(IDLFile idlFile) {
        super("Box2D", idlFile);
    }
}
