package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.xpenatan.tools.jparser.JParser;

/** @author xpenatan */
public interface CodeParser {
    void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration);
    void onParseClassEnd(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration);
    void onParseConstructor(ConstructorDeclaration constructorDeclaration);
    void onParseMethod(MethodDeclaration methodDeclaration);
    void onParseField(FieldDeclaration fieldDeclaration);
    void parseCodeBlock(boolean isHeader, CodeParserItem parserItem);
}