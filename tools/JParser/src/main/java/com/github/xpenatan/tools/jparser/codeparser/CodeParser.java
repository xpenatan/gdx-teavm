package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.xpenatan.tools.jparser.JParser;

/** @author xpenatan */
public interface CodeParser {
    void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration);
    void onParseClassEnd(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration);
    void parseCode(CodeParserItem parserItem);
    default void onParseCodeEnd() {}
}