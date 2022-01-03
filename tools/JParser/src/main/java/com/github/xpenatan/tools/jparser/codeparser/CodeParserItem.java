package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;

import java.util.ArrayList;
import java.util.Scanner;

/** @author xpenatan */
public class CodeParserItem {
    public final ArrayList<BlockComment> rawComments = new ArrayList<>();

    public CompilationUnit unit;

    public ImportDeclaration importDeclaration;

    public ClassOrInterfaceDeclaration classInterface;

    public FieldDeclaration fieldDeclaration;

    public MethodDeclaration methodDeclaration;

    public boolean isFieldBlock() {
        return fieldDeclaration != null;
    }

    public boolean isMethodBlock() {
        return methodDeclaration != null;
    }

    public boolean isImportBlock() {
        return importDeclaration != null;
    }

    public void reset() {
        rawComments.clear();
        unit = null;
        classInterface = null;
        fieldDeclaration = null;
        methodDeclaration = null;
        importDeclaration = null;
    }

    @Override
    public String toString() {
        String ret = "";
        if(fieldDeclaration != null) {
            ret = fieldDeclaration.getTokenRange().get().toString();
        }
        else if(methodDeclaration != null) {
            ret = methodDeclaration.getTokenRange().get().toString();
        }
        else if(importDeclaration != null) {
            ret = importDeclaration.getTokenRange().get().toString();
        }
        else {
            for(int i = 0; i < rawComments.size(); i++) {
                BlockComment blockComment = rawComments.get(i);
                ret += blockComment.getContent();
                if(i < rawComments.size() -1) {
                    ret += "\n";
                }
            }
        }
        return ret;
    }

    public void removeAll() {
        removeCodeBlocks();
        removeDeclaration();
    }

    public void removeDeclaration() {
        if(fieldDeclaration != null) {
            fieldDeclaration.remove();
        }
        if(methodDeclaration != null) {
            methodDeclaration.remove();
        }
        if(importDeclaration != null) {
            importDeclaration.remove();
        }
    }

    public void removeCodeBlocks() {
        for(int i = 0; i < rawComments.size(); i++) {
            BlockComment blockComment = rawComments.get(i);
            blockComment.remove();
        }
    }

    public static String obtainHeaderCommands(BlockComment blockComment) {
        String content = blockComment.getContent();
        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(line.startsWith(DefaultCodeParser.CMD_HEADER_START) && line.endsWith(DefaultCodeParser.CMD_HEADER_END)) {
                scanner.close();
                return line;
            }
        }
        scanner.close();
        return null;
    }

    public static String obtainContent(String header, BlockComment blockComment) {
        if(header == null)
            return null;
        String content = blockComment.getContent();
        content = content.replace(header, "").trim();
        return content;
    }
}
