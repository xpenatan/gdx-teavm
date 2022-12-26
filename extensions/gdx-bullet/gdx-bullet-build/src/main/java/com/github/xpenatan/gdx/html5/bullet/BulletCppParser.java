package com.github.xpenatan.gdx.html5.bullet;

import com.github.xpenatan.jparser.core.idl.IDLFile;
import com.github.xpenatan.jparser.cpp.CppCodeParser;

public class BulletCppParser extends CppCodeParser {

    public BulletCppParser(String classpath, String jniDir) {
        this(null, classpath, jniDir);
    }

    public BulletCppParser(IDLFile idlFile, String classpath, String jniDir) {
        super(null, classpath, jniDir);
    }
}