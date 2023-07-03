package com.github.xpenatan.gdx.html5.box2d;

import com.github.xpenatan.jparser.idl.IDLReader;
import com.github.xpenatan.jparser.teavm.TeaVMCodeParser;

public class Box2dParser extends TeaVMCodeParser {
    public Box2dParser(IDLReader idlReader) {
        super("Box2D", idlReader);
    }
}
