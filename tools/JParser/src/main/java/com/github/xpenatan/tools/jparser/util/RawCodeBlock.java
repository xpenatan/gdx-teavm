package com.github.xpenatan.tools.jparser.util;

import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;

public class RawCodeBlock extends TypeDeclaration<RawCodeBlock> {

    private String content;

    public RawCodeBlock() {
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    @Override
    public ResolvedReferenceTypeDeclaration resolve() {
        return null;
    }

    public <R, A> R accept(final GenericVisitor<R, A> v, final A arg) {
        return null;
    }

    @Override
    public String getNameAsString() {
        return super.getNameAsString();
    }

    public <A> void accept(final VoidVisitor<A> v, final A arg) {
        CustomDefaultPrettyPrinterVisitor visitor = (CustomDefaultPrettyPrinterVisitor) v;
        visitor.visit(this);
    }
}