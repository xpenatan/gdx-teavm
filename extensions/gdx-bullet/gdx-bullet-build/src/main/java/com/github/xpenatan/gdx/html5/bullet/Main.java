package com.github.xpenatan.gdx.html5.bullet;

import com.github.xpenatan.jparser.core.JParser;
import com.github.xpenatan.jparser.core.idl.IDLFile;
import com.github.xpenatan.jparser.core.idl.IDLParser;
import com.github.xpenatan.jparser.cpp.CPPBuildHelper;
import com.github.xpenatan.jparser.cpp.CppCodeParser;
import com.github.xpenatan.jparser.cpp.FileCopyHelper;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "..\\..\\gdx-bullet\\gdx-bullet-build\\jni\\bullet.idl";
        IDLFile idlFile = IDLParser.parseFile(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new BulletCodeParser(idlFile), basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", null);


        String libName = "gdx-bullet";
        String bulletPath = new File("../gdx-bullet/").getCanonicalPath();
        String cppPath = new File("./jni/").getCanonicalPath();
        String buildPath = cppPath + "/build/c++/";
        FileCopyHelper.copyDir(cppPath + "/bullet/src/", buildPath + "/src");

        String sourceDir = "../gdx-bullet-base/src/main/java/";
        BulletCppParser cppParser = new BulletCppParser(idlFile, CppCodeParser.getClassPath("imgui-core"), buildPath);
        JParser.generate(cppParser, sourceDir, bulletPath + "/src");

        String [] flags = new String[1];
        flags[0] = " -DBT_USE_INVERSE_DYNAMICS_WITH_BULLET2";
        CPPBuildHelper.build(libName, buildPath, flags);
    }
}