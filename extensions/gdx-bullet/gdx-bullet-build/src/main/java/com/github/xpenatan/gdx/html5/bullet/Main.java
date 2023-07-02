package com.github.xpenatan.gdx.html5.bullet;

import com.github.xpenatan.jparser.core.JParser;
import com.github.xpenatan.jparser.idl.IDLFile;
import com.github.xpenatan.jparser.idl.IDLReader;
import com.github.xpenatan.jparser.cpp.CPPBuildHelper;
import com.github.xpenatan.jparser.cpp.CppCodeParser;
import com.github.xpenatan.jparser.cpp.FileCopyHelper;
import java.io.File;

public class Main {
    public static void main(String[] args) throws Exception {
        String path = "..\\..\\gdx-bullet\\gdx-bullet-build\\jni\\bullet.idl";
        IDLFile idlFile = IDLReader.parseFile(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new BulletCodeParser(idlFile), basePath + "./gdx-bullet-base/src", "../gdx-bullet-teavm/src", null);

        buildBulletCPP(idlFile);
    }

    private static void buildBulletCPP(IDLFile idlFile) throws Exception {
        String libName = "gdx-bullet";
        String bulletPath = new File("../gdx-bullet/").getCanonicalPath();
        String genDir = bulletPath + "/src/main/java";
        String cppPath = new File("./jni/").getCanonicalPath();
        String buildPath = cppPath + "/build/c++/";
        FileCopyHelper.copyDir(cppPath + "/bullet/src/", buildPath + "/src");

        String sourceDir = "../gdx-bullet-base/src/main/java/";
        String classPath = CppCodeParser.getClassPath("bullet-base", "gdx-1", "gdx-jnigen-loader", "jParser-loader");
        BulletCppParser cppParser = new BulletCppParser(idlFile, classPath, buildPath);
        JParser.generate(cppParser, sourceDir, genDir);

        String [] flags = new String[1];
        flags[0] = " -DBT_USE_INVERSE_DYNAMICS_WITH_BULLET2";
//        CPPBuildHelper.DEBUG_BUILD = true;
        CPPBuildHelper.build(libName, buildPath, flags);
    }
}