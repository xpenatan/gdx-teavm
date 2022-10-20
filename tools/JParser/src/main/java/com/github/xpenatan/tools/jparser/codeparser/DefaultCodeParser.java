package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.util.RawCodeBlock;
import java.util.Optional;

/** @author xpenatan */
public abstract class DefaultCodeParser implements CodeParser {

    static final String CMD_HEADER_START = "[-";
    static final String CMD_HEADER_END = "]";
    public static final String CMD_ADD = "-ADD";
    public static final String CMD_REMOVE = "-REMOVE";
    public static final String CMD_REPLACE = "-REPLACE";
    public static final String CMD_NATIVE = "-NATIVE";

    public final String headerCMD;

    public DefaultCodeParser(String headerCMD) {
        this.headerCMD = headerCMD;
    }

    @Override
    public void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {}

    @Override
    public void onParseClassEnd(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {}

    @Override
    public void parseCode(CodeParserItem parserItem) {
        BlockComment blockComment = null;
        Node node = parserItem.node;
        Optional<Comment> commentOptional = node.getComment();
        if(commentOptional.isPresent()) {
            Comment comment = commentOptional.get();
            if(comment.isBlockComment()) {
                blockComment = comment.asBlockComment();
            }
        }
        else if(node instanceof BlockComment) {
            blockComment = (BlockComment)node;
        }
        if(blockComment != null) {
            String headerCommands = CodeParserItem.obtainHeaderCommands(blockComment);
            if(headerCommands != null && (headerCommands.startsWith(CMD_HEADER_START + headerCMD) && headerCommands.endsWith(CMD_HEADER_END))) {
                String content = CodeParserItem.obtainContent(headerCommands, blockComment);
                parseCodeBlock(parserItem, headerCommands, content);
                blockComment.remove();
            }
        }
    }

    public void parseCodeBlock(CodeParserItem parserItem, String headerCommands, String content) {
        Node node = parserItem.node;

        if(headerCommands.contains(CMD_ADD)) {
            setAddReplaceCMD(parserItem, content, false);
        }
        else if(headerCommands.contains(CMD_REMOVE)) {
            node.remove();
        }
        else if(headerCommands.contains(CMD_REPLACE)) {
            setAddReplaceCMD(parserItem, content, true);
        }
        else if(headerCommands.contains(CMD_NATIVE)) {
            if(node instanceof MethodDeclaration) {
                MethodDeclaration methodDeclaration = (MethodDeclaration)node;
                setJavaBodyNativeCMD(content, methodDeclaration);
            }
        }
    }

    private void setAddReplaceCMD(CodeParserItem parserItem, String content, boolean replace) {
        Node node = parserItem.node;
        Node parentNode = null;
        Optional<Node> parentNodeOptional = node.getParentNode();
        if(parentNodeOptional.isPresent()) {
            parentNode = parentNodeOptional.get();
        }
        if(parentNode != null) {
            if(parentNode instanceof TypeDeclaration) {
                TypeDeclaration<?> typeDeclaration = (TypeDeclaration<?>)parentNode;
                try {
                    BodyDeclaration<?> newCode = StaticJavaParser.parseBodyDeclaration(content);
                    typeDeclaration.getMembers().add(newCode);
                }
                catch(Throwable t) {
                    String className = typeDeclaration.getNameAsString();
                    System.err.println("Error Class: " + className + "\n" + content);
                    throw t;
                }
            }
            else if(parentNode instanceof CompilationUnit) {
                CompilationUnit unit = (CompilationUnit)parentNode;
                RawCodeBlock newblockComment = new RawCodeBlock();
                newblockComment.setContent(content);
                Optional<TokenRange> tokenRange = node.getTokenRange();
                TokenRange javaTokens = tokenRange.get();
                newblockComment.setTokenRange(javaTokens);
                unit.addType(newblockComment);
            }
        }
        if(replace) {
            node.remove();
        }
    }

    protected abstract void setJavaBodyNativeCMD(String content, MethodDeclaration methodDeclaration);

}