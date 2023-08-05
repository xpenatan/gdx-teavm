package com.github.xpenatan.gdx.html5.bullet;

import com.github.xpenatan.jparser.core.JParser;
import com.github.xpenatan.jparser.cpp.CppCodeParserV2;
import com.github.xpenatan.jparser.cpp.CppGenerator;
import com.github.xpenatan.jparser.cpp.NativeCPPGeneratorV2;
import com.github.xpenatan.jparser.idl.IDLReader;
import com.github.xpenatan.jparser.cpp.CPPBuildHelper;
import com.github.xpenatan.jparser.idl.parser.IDLDefaultCodeParser;
import com.github.xpenatan.jparser.teavm.TeaVMCodeParserV2;
import java.io.File;

public class Main {

    public static void main(String[] args) throws Exception {
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
        generateTeaVM(idlReader, libName, basePackage, baseJavaDir);
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
        CPPBuildHelper.build(libName, libBuildPath, libsDir, flags);
    }

    public static void generateTeaVM(IDLReader idlReader, String libName, String basePackage, String baseJavaDir) throws Exception {
        String genDir = "../gdx-bullet-teavm/src/main/java/";
        TeaVMCodeParserV2 teavmParser = new TeaVMCodeParserV2(idlReader, libName, basePackage);
        JParser.generate(teavmParser, baseJavaDir, genDir);
    }
}