package com.github.xpenatan.gdx.html5.bullet.codegen;

import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.comments.BlockComment;

import java.util.ArrayList;

/** @author xpenatan */
public class CodeGenParserItem {
    public final ArrayList<BlockComment> rawComments = new ArrayList<>();

    public FieldDeclaration fieldDeclaration;

    public MethodDeclaration methodDeclaration;

    public boolean isFieldBlock() {
        return fieldDeclaration != null;
    }

    public boolean isMethodBlock() {
        return methodDeclaration != null;
    }

    public void reset() {
        rawComments.clear();
        fieldDeclaration = null;
        methodDeclaration = null;
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
}
