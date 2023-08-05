package com.github.xpenatan.gdx.html5.bullet;

import com.github.xpenatan.jparser.core.JParser;
import com.github.xpenatan.jparser.core.util.FileHelper;
import com.github.xpenatan.jparser.cpp.CppCodeParserV2;
import com.github.xpenatan.jparser.cpp.CppGenerator;
import com.github.xpenatan.jparser.cpp.NativeCPPGeneratorV2;
import com.github.xpenatan.jparser.idl.IDLReader;
import com.github.xpenatan.jparser.cpp.CPPBuildHelper;
import com.github.xpenatan.jparser.cpp.CppCodeParser;
import com.github.xpenatan.jparser.idl.parser.IDLDefaultCodeParser;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {

//        generateOld();
        generateNew();
    }

    public static void generateNew() throws Exception {
        String basePackage = "com.badlogic.gdx.physics.bullet";
        String libName = "bullet";
        String idlPath = "jni\\bullet.idl";
        String baseJavaDir = new File(".").getAbsolutePath() + "./gdx-bullet-base/src/main/java";
        String cppSourceDir = new File("./jni/bullet/src/").getCanonicalPath();

        IDLReader idlReader = IDLReader.readIDL(idlPath, cppSourceDir);

//        buildClassOnly(idlReader, basePackage, baseJavaDir);
        buildBulletCPP(idlReader, libName, basePackage, baseJavaDir, cppSourceDir);
    }

    private static void buildClassOnly(
            IDLReader idlReader,
            String basePackage,
            String baseJavaDir
    ) throws Exception {
        IDLDefaultCodeParser idlParser = new IDLDefaultCodeParser(basePackage, "C++", idlReader);
        idlParser.generateClass = true;
        String genDir = "../gdx-bullet/src/main/java";
        JParser.generate(idlParser, baseJavaDir, genDir);
    }

    private static void buildBulletCPP(
            IDLReader idlReader,
            String libName,
            String basePackage,
            String baseJavaDir,
            String cppSourceDir
    ) throws Exception {
        String libsDir = new File("./build/c++/desktop/").getCanonicalPath();
        String genDir = "../gdx-bullet/src/main/java";
        String libBuildPath = new File("./build/c++/").getCanonicalPath();
        String cppDestinationPath = libBuildPath + "/src";

        CppGenerator cppGenerator = new NativeCPPGeneratorV2(cppSourceDir, cppDestinationPath);
        CppCodeParserV2 cppParser = new CppCodeParserV2(cppGenerator, idlReader, basePackage);
        cppParser.generateClass = true;
        JParser.generate(cppParser, baseJavaDir, genDir);
        String [] flags = new String[1];
        flags[0] = " -DBT_USE_INVERSE_DYNAMICS_WITH_BULLET2";
        CPPBuildHelper.DEBUG_BUILD = true;
        CPPBuildHelper.build(libName, libBuildPath, flags);
    }

    private static void generateOld() throws Exception {
        String path = "..\\..\\gdx-bullet\\gdx-bullet-build\\jni\\bullet.idl";
        IDLReader idlReader = IDLReader.readIDL(path);

        String basePath = new File(".").getAbsolutePath();
        JParser.generate(new BulletCodeParser(idlReader), basePath + "./gdx-bullet-base/src/main/java", "../gdx-bullet-teavm/src/main/java", null);

        buildBulletCPP(idlReader);
    }

    private static void buildBulletCPP(IDLReader idlReader) throws Exception {
        String libName = "gdx-bullet";
        String bulletPath = new File("../gdx-bullet/").getCanonicalPath();
        String genDir = bulletPath + "/src/main/java";
        String cppPath = new File("./jni/").getCanonicalPath();
        String buildPath = cppPath + "/build/c++/";
        FileHelper.copyDir(cppPath + "/bullet/src/", buildPath + "/src");

        String sourceDir = "../gdx-bullet-base/src/main/java/";
        String classPath = CppCodeParser.getClassPath("bullet-base", "gdx-1", "gdx-jnigen-loader", "jParser");
        BulletCppParser cppParser = new BulletCppParser(idlReader, classPath, buildPath);
        JParser.generate(cppParser, sourceDir, genDir);

        String [] flags = new String[1];
        flags[0] = " -DBT_USE_INVERSE_DYNAMICS_WITH_BULLET2";
//        CPPBuildHelper.DEBUG_BUILD = true;
        CPPBuildHelper.build(libName, buildPath, flags);
    }
}