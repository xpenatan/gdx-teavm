package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
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

    private final String headerCMD;

    public DefaultCodeParser(String headerCMD) {
        this.headerCMD = headerCMD;
    }

    @Override
    public void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {}

    @Override
    public void onParseClassEnd(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {}

    @Override
    public void onParseConstructor(ConstructorDeclaration constructorDeclaration) {}

    @Override
    public void onParseField(FieldDeclaration fieldDeclaration) {}

    @Override
    public void onParseMethod(MethodDeclaration methodDeclaration) {}

    @Override
    public void parseCodeBlock(boolean isHeader, CodeParserItem parserItem) {
        removeCodeBlock(parserItem);
        if(isHeader) {
            parseHeaderBlock(parserItem);
        }
        else {
            parseCodeBlock(parserItem);
        }
    }

    private void removeCodeBlock(CodeParserItem parserItem) {
        // Remove comment block if it's not part of this parser
        for(int i = 0; i < parserItem.rawComments.size(); i++) {
            BlockComment rawBlockComment = parserItem.rawComments.get(i);
            String headerCommands = CodeParserItem.obtainHeaderCommands(rawBlockComment);
            if(headerCommands == null || !(headerCommands.startsWith(CMD_HEADER_START + headerCMD) && headerCommands.endsWith(CMD_HEADER_END))) {
                rawBlockComment.remove();
                parserItem.rawComments.remove(i);
                i--;
            }
        }
    }

    public void parseCodeBlock(CodeParserItem parserItem) {
        if(parserItem.rawComments.size() == 0) {
            return;
        }
        // REPLACE, DELETE and NATIVE is action taken. ADD is not.
        boolean actionTaken = false;

        for(int i = 0; i < parserItem.rawComments.size(); i++) {
            BlockComment blockComment = parserItem.rawComments.get(i);
            // Remove raw block comment from source
            blockComment.remove();
            String headerCommands = CodeParserItem.obtainHeaderCommands(blockComment);

            if(headerCommands == null)
                continue;

            if(parserItem.isFieldBlock()) {
                FieldDeclaration fieldDeclaration = parserItem.fieldDeclaration;
                if(headerCommands.contains(CMD_REPLACE) && !actionTaken) {
                    actionTaken = true;
                    setJavaBodyReplaceCMD(parserItem, headerCommands, blockComment, fieldDeclaration);
                }
            }
            else if(parserItem.isMethodBlock()) {
                MethodDeclaration methodDeclaration = parserItem.methodDeclaration;

                if(methodDeclaration.isNative()) {
                    if(headerCommands.contains(CMD_NATIVE) && !actionTaken) {
                        actionTaken = true;
                        setJavaBodyNativeCMD(headerCommands, blockComment, methodDeclaration);
                    }
                }
                if(headerCommands.contains(CMD_REPLACE) && !actionTaken) {
                    actionTaken = true;
                    setJavaBodyReplaceCMD(parserItem, headerCommands, blockComment, methodDeclaration);
                }
            }
            // Block comments without field or method
            if(headerCommands.contains(CMD_ADD)) {
                setJavaBodyAddCMD(parserItem, headerCommands, blockComment);
            }
            else if(headerCommands.contains(CMD_REMOVE) && !actionTaken) {
                actionTaken = true;
                setJavaBodyRemoveCMD(parserItem);
            }
        }
    }

    public void parseHeaderBlock(CodeParserItem parserItem) {
        if(parserItem.rawComments.size() == 0) {
            return;
        }
        // should only work with 1 block comment
        BlockComment blockComment = parserItem.rawComments.get(0);
        // Remove raw block comment from source
        blockComment.remove();

        String headerCommands = CodeParserItem.obtainHeaderCommands(blockComment);

        if(parserItem.isImportBlock()) {
            if(headerCommands.contains(CMD_REPLACE)) {
                setJavaHeaderReplaceCMD(parserItem, headerCommands, blockComment, parserItem.importDeclaration);
            }
        }

        if(headerCommands.contains(CMD_ADD)) {
            setJavaHeaderAddCMD(parserItem, headerCommands, blockComment);
        }
        else if(headerCommands.contains(CMD_REMOVE)) {
            setJavaHeaderRemoveCMD(parserItem);
        }
    }

    private void setJavaHeaderReplaceCMD(CodeParserItem parserItem, String headerCommands, BlockComment blockComment, ImportDeclaration importDeclaration) {
        importDeclaration.remove();
        String content = CodeParserItem.obtainContent(headerCommands, blockComment);
        RawCodeBlock newblockComment = new RawCodeBlock();
        newblockComment.setContent(content);
        Optional<TokenRange> tokenRange = importDeclaration.getTokenRange();
        TokenRange javaTokens = tokenRange.get();
        newblockComment.setTokenRange(javaTokens);
        parserItem.unit.addType(newblockComment);
    }

    private void setJavaHeaderAddCMD(CodeParserItem parserItem, String headerCommands, BlockComment blockComment) {
        String content = CodeParserItem.obtainContent(headerCommands, blockComment);
        RawCodeBlock newblockComment = new RawCodeBlock();
        newblockComment.setContent(content);
        Optional<TokenRange> tokenRange = blockComment.getTokenRange();
        TokenRange javaTokens = tokenRange.get();
        newblockComment.setTokenRange(javaTokens);
        parserItem.unit.addType(newblockComment);
    }

    private void setJavaBodyAddCMD(CodeParserItem parserItem, String headerCommands, BlockComment blockComment) {
        String content = CodeParserItem.obtainContent(headerCommands, blockComment);
        RawCodeBlock newblockComment = new RawCodeBlock();
        ClassOrInterfaceDeclaration classInterface = parserItem.classInterface;
        newblockComment.setContent(content);
        Optional<TokenRange> tokenRange = blockComment.getTokenRange();
        TokenRange javaTokens = tokenRange.get();
        newblockComment.setTokenRange(javaTokens);
        classInterface.getMembers().add(newblockComment);
    }

    private void setJavaHeaderRemoveCMD(CodeParserItem parserItem) {
        setJavaBodyRemoveCMD(parserItem);
    }

    private void setJavaBodyRemoveCMD(CodeParserItem parserItem) {
        parserItem.removeAll();
    }

    private void setJavaBodyReplaceCMD(CodeParserItem parserItem, String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
        methodDeclaration.remove();
        ClassOrInterfaceDeclaration classInterface = parserItem.classInterface;
        String content = CodeParserItem.obtainContent(headerCommands, blockComment);
        RawCodeBlock newblockComment = new RawCodeBlock();
        newblockComment.setContent(content);
        Optional<TokenRange> tokenRange = methodDeclaration.getTokenRange();
        TokenRange javaTokens = tokenRange.get();
        newblockComment.setTokenRange(javaTokens);
        classInterface.getMembers().add(newblockComment);
    }

    private void setJavaBodyReplaceCMD(CodeParserItem parserItem, String headerCommands, BlockComment blockComment, FieldDeclaration fieldDeclaration) {
        fieldDeclaration.remove();
        ClassOrInterfaceDeclaration classInterface = parserItem.classInterface;
        String content = CodeParserItem.obtainContent(headerCommands, blockComment);
        RawCodeBlock newblockComment = new RawCodeBlock();
        newblockComment.setContent(content);
        Optional<TokenRange> tokenRange = fieldDeclaration.getTokenRange();
        TokenRange javaTokens = tokenRange.get();
        newblockComment.setTokenRange(javaTokens);
        classInterface.getMembers().add(newblockComment);
    }

    protected abstract void setJavaBodyNativeCMD(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration);

}