package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.TokenRange;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParserUnit;
import com.github.xpenatan.tools.jparser.JParserUnitItem;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLMethod;
import com.github.xpenatan.tools.jparser.idl.IDLParameter;
import com.github.xpenatan.tools.jparser.util.RawCodeBlock;

import java.util.ArrayList;
import java.util.List;
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

    private IDLFile idlFile;

    public DefaultCodeParser(String headerCMD) {
        this(headerCMD, null);
    }

    public DefaultCodeParser(String headerCMD, IDLFile idlFile) {
        this.headerCMD = headerCMD;
        this.idlFile = idlFile;
    }

    private void updateBlock(CodeParserItem parserItem) {
        for(int i = 0; i < parserItem.rawComments.size(); i++) {
            BlockComment rawBlockComment = parserItem.rawComments.get(i);
            String headerCommands = CodeParserItem.obtainHeaderCommands(rawBlockComment);
            // Remove comment block if its not part of this parser
            if(headerCommands == null || !(headerCommands.startsWith(CMD_HEADER_START + headerCMD) && headerCommands.endsWith(CMD_HEADER_END))) {
                rawBlockComment.remove();
                parserItem.rawComments.remove(i);
                i--;
            }
        }
    }

    @Override
    public void parseCodeBlock(CodeParserItem parserItem) {
        updateBlock(parserItem);

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

    @Override
    public void parseHeaderBlock(CodeParserItem parserItem) {
        updateBlock(parserItem);

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

    protected abstract void setJavaBodyNativeCMD(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration);

    @Override
    public void onParseClass(JParserUnit jParserUnit, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        if(idlFile != null) {
            SimpleName name = classOrInterfaceDeclaration.getName();
            String nameStr = name.asString();
            IDLClass idlClass = idlFile.getClass(nameStr);
            if(idlClass != null) {
                ArrayList<IDLMethod> methods = idlClass.methods;
                for(int i = 0; i < methods.size(); i++) {
                    IDLMethod idlMethod = methods.get(i);
                    generateMethods(jParserUnit, unit, classOrInterfaceDeclaration, idlMethod);
                }
            }
        }
    }

    private void generateMethods(JParserUnit jParserUnit, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration, IDLMethod idlMethod) {
        if(containsMethod(classOrInterfaceDeclaration, idlMethod)) {
            return;
        }
        String methodName = idlMethod.name;

        ArrayList<IDLParameter> parameters = idlMethod.parameters;

        MethodDeclaration methodDeclaration = classOrInterfaceDeclaration.addMethod(methodName, Keyword.PUBLIC);
        BlockStmt blockStmt = methodDeclaration.getBody().get();

        for(int i = 0; i < parameters.size(); i++) {
            IDLParameter idlParameter = parameters.get(i);
            String paramType = idlParameter.type;
            String paramName = idlParameter.name;
            Parameter parameter = methodDeclaration.addAndGetParameter(paramType, paramName);
            Type type = parameter.getType();
            JParserUnitItem.addMissingImportType(jParserUnit, unit, type);
        }

        Type returnType = StaticJavaParser.parseType(idlMethod.returnType);
        methodDeclaration.setType(returnType);
        if(!returnType.isVoidType()) {
            ReturnStmt returnStmt = new ReturnStmt();
            if(returnType.isPrimitiveType()) {
                if(isType(returnType, "long") || isType(returnType, "int") || isType(returnType, "float") || isType(returnType, "double")) {
                    NameExpr returnNameExpr = new NameExpr();
                    returnNameExpr.setName("0");
                    returnStmt.setExpression(returnNameExpr);
                }
                else if(isType(returnType, "boolean")) {
                    NameExpr returnNameExpr = new NameExpr();
                    returnNameExpr.setName("false");
                    returnStmt.setExpression(returnNameExpr);
                }
            }
            else {
                JParserUnitItem.addMissingImportType(jParserUnit, unit, returnType);

                NameExpr returnNameExpr = new NameExpr();
                returnNameExpr.setName("null");
                returnStmt.setExpression(returnNameExpr);
            }
            blockStmt.addStatement(returnStmt);
        }
    }

    public boolean isType(Type type, String typeStr) {
        if(type.isPrimitiveType()) {
            PrimitiveType primitiveType = type.asPrimitiveType();
            String name = primitiveType.getType().name();
            return name.contains(typeStr.toUpperCase());
        }
        return false;
    }

    private boolean containsMethod(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, IDLMethod idlMethod) {
        ArrayList<IDLParameter> parameters = idlMethod.parameters;
        String [] paramTypes = new String[parameters.size()];

        for(int i = 0; i < parameters.size(); i++) {
            IDLParameter parameter = parameters.get(i);
            String paramType = parameter.type;
            paramTypes[i] = paramType;
        }
        List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethodsBySignature(idlMethod.name, paramTypes);

        return methods.size() > 0;
    }

    @Override
    public void onParseConstructor(ConstructorDeclaration constructorDeclaration) {}

    @Override
    public void onParseField(FieldDeclaration fieldDeclaration) {}

    @Override
    public void onParseMethod(MethodDeclaration methodDeclaration) {}

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

    private void setJavaBodyRemoveCMD(CodeParserItem parserItem) {
        parserItem.removeAll();
    }
}