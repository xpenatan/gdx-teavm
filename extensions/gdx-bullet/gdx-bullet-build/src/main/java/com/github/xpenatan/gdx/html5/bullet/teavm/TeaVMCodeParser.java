package com.github.xpenatan.gdx.html5.bullet.teavm;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.xpenatan.gdx.html5.bullet.codegen.CodeGenParser;
import com.github.xpenatan.gdx.html5.bullet.codegen.CodeGenParserItem;

/** @author xpenatan */
public class TeaVMCodeParser implements CodeGenParser {

    public static final String CMD_HEADER = "[-teaVM";
    public static final String CMD_DELETE = "Delete";
    public static final String CMD_NATIVE = "Native";
    public static final String CMD_REPLACE = "Replace";

    @Override
    public String parseCodeBlock(CodeGenParserItem parserItem) {
        for(int i = 0; i < parserItem.rawComments.size(); i++) {
            BlockComment rawBlockComment = parserItem.rawComments.get(i);
            String headerCommands = CodeGenParserItem.obtainHeaderCommands(rawBlockComment);
            // Remove comment block if its not part of this parser
            if(headerCommands == null || !headerCommands.startsWith(CMD_HEADER)) {
                rawBlockComment.remove();
                parserItem.rawComments.remove(i);
                i--;
            }
        }

        if(parserItem.rawComments.size() == 0) {
            return null;
        }
        // should only work with 1 block comment
        BlockComment blockComment = parserItem.rawComments.get(0);

        // Remove raw block comment from source
        blockComment.remove();

        if(parserItem.isFieldBlock()) {
            FieldDeclaration fieldDeclaration = parserItem.fieldDeclaration;
        }
        else if(parserItem.isMethodBlock()) {
            MethodDeclaration methodDeclaration = parserItem.methodDeclaration;
            String headerCommands = CodeGenParserItem.obtainHeaderCommands(blockComment);

            if(methodDeclaration.isNative()) {
                if(headerCommands.contains(CMD_NATIVE)) {
                    addJSBody(headerCommands, blockComment, methodDeclaration);
                }
                else if(headerCommands.contains(CMD_REPLACE)) {
                    replaceJSBody(parserItem, headerCommands, blockComment, methodDeclaration);
                }
            }
        }
        else {
            // Block comments without field or method

        }

        return null;
    }

    private void addJSBody(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        int size = parameters.size();

        String content = CodeGenParserItem.obtainContent(headerCommands, blockComment);

        String param = "";

        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            SimpleName name = parameter.getName();
            param += name;
            if(i < size - 1) {
                param += "\", \"";
            }
        }

        if(content != null) {
            content = content.replace("\n", "");
            content = content.trim();

            if(!content.isEmpty()) {
                NormalAnnotationExpr normalAnnotationExpr = methodDeclaration.addAndGetAnnotation("org.teavm.jso.JSBody");
                if(!param.isEmpty()) {
                    normalAnnotationExpr.addPair("params", "{\"" + param + "\"}");
                }
                normalAnnotationExpr.addPair("script", "\"" + content + "\"");
            }
        }
    }

    private void replaceJSBody(CodeGenParserItem parserItem, String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
        ClassOrInterfaceDeclaration classInterface = parserItem.classInterface;

    }
}