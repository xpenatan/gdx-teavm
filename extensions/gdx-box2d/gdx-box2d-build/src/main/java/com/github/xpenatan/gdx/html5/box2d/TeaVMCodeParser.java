package com.github.xpenatan.gdx.html5.box2d;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.codeparser.IDLDefaultCodeParser;
import com.github.xpenatan.tools.jparser.idl.IDLFile;

public class TeaVMCodeParser extends IDLDefaultCodeParser {

    private static final String HEADER_CMD = "teaVM";

    public TeaVMCodeParser(IDLFile idlFile) {
        super(HEADER_CMD, idlFile);
    }

    @Override
    protected void onIDLMethodGenerated(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration) {
    }
}