package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.Comment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParserHelper;
import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.JParserItem;
import com.github.xpenatan.tools.jparser.idl.IDLAttribute;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLMethod;
import com.github.xpenatan.tools.jparser.idl.IDLParameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** @author xpenatan */
public abstract class IDLDefaultCodeParser extends DefaultCodeParser {

    protected final IDLFile idlFile;

    protected boolean enableAttributeParsing = true;

    public IDLDefaultCodeParser(String headerCMD, IDLFile idlFile) {
        super(headerCMD);
        this.idlFile = idlFile;
    }

    @Override
    protected void setJavaBodyNativeCMD(String content, MethodDeclaration methodDeclaration) {
    }

    @Override
    public void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        if(idlFile != null) {
            SimpleName name = classOrInterfaceDeclaration.getName();
            String nameStr = name.asString();
            IDLClass idlClass = idlFile.getClass(nameStr);
            if(idlClass != null) {
                ArrayList<IDLMethod> methods = idlClass.methods;
                for(int i = 0; i < methods.size(); i++) {
                    IDLMethod idlMethod = methods.get(i);
                    generateMethods(jParser, unit, classOrInterfaceDeclaration, idlClass, idlMethod);
                }
                if(enableAttributeParsing) {
                    ArrayList<IDLAttribute> attributes = idlClass.attributes;
                    for(int i = 0; i < attributes.size(); i++) {
                        IDLAttribute idlAttribute = attributes.get(i);
                        generateAttribute(jParser, unit, classOrInterfaceDeclaration, idlClass, idlAttribute);
                    }
                }
            }
        }
    }

    /**
     * true to accept the idl method
     */
    public boolean filterIDLMethod(IDLClass idlClass, IDLMethod idlMethod) {
        return true;
    }

    private void generateAttribute(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration, IDLClass idlClass, IDLAttribute idlAttribute) {
        if(idlAttribute.skip) {
            return;
        }

        String attributeName = idlAttribute.name;
        String attributeType = idlAttribute.type;

        Type type = null;
        try {
            type = StaticJavaParser.parseType(attributeType);
        }
        catch(Exception e) {
            e.printStackTrace();
            return;
        }

        JParserItem parserUnitItem = jParser.getParserUnitItem(type.toString());
        if(parserUnitItem != null) {
            if(parserUnitItem.notAllowed) {
                // skip generating set/get for class that is not allowed to have get and set. Ex Enum
                return;
            }
        }

        boolean addGet = true;
        boolean addSet = true;
        MethodDeclaration getMethodDeclaration = null;
        List<MethodDeclaration> getMethods = classOrInterfaceDeclaration.getMethodsBySignature(attributeName);
        if(getMethods.size() > 0) {
            getMethodDeclaration = getMethods.get(0);
            Optional<Comment> optionalComment = getMethodDeclaration.getComment();
            if(optionalComment.isPresent()) {
                Comment comment = optionalComment.get();
                if(comment instanceof BlockComment) {
                    BlockComment blockComment = (BlockComment)optionalComment.get();
                    String headerCommands = CodeParserItem.obtainHeaderCommands(blockComment);
                    // Skip if method already exist with header code
                    if(headerCommands != null && headerCommands.startsWith(CMD_HEADER_START + headerCMD)) {
                        addGet = false;
                    }
                }
            }
        }
        MethodDeclaration setMethodDeclaration = null;
        List<MethodDeclaration> setMethods = classOrInterfaceDeclaration.getMethodsBySignature(attributeName, attributeType);
        if(setMethods.size() > 0) {
            setMethodDeclaration = setMethods.get(0);
            Optional<Comment> optionalComment = setMethodDeclaration.getComment();
            if(optionalComment.isPresent()) {
                Comment comment = optionalComment.get();
                if(comment instanceof BlockComment) {
                    BlockComment blockComment = (BlockComment)optionalComment.get();
                    String headerCommands = CodeParserItem.obtainHeaderCommands(blockComment);
                    // Skip if method already exist with header code
                    if(headerCommands != null && headerCommands.startsWith(CMD_HEADER_START + headerCMD)) {
                        addSet = false;
                    }
                }
            }
        }
        if(addGet) {
            if(getMethodDeclaration != null) {
                getMethodDeclaration.remove();
            }
            getMethodDeclaration = classOrInterfaceDeclaration.addMethod(attributeName, Keyword.PUBLIC);
            getMethodDeclaration.setType(type);
            JParserHelper.addMissingImportType(jParser, unit, type);
            setDefaultReturnValues(jParser, unit, type, getMethodDeclaration);
            onIDLMethodGenerated(jParser, unit, classOrInterfaceDeclaration, getMethodDeclaration, true);
        }
        if(addSet) {
            if(setMethodDeclaration != null) {
                setMethodDeclaration.remove();
            }
            setMethodDeclaration = classOrInterfaceDeclaration.addMethod(attributeName, Keyword.PUBLIC);
            Parameter parameter = setMethodDeclaration.addAndGetParameter(type, attributeName);
            Type paramType = parameter.getType();
            JParserHelper.addMissingImportType(jParser, unit, paramType);
            onIDLMethodGenerated(jParser, unit, classOrInterfaceDeclaration, setMethodDeclaration, true);
        }
    }

    private void generateMethods(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration, IDLClass idlClass, IDLMethod idlMethod) {
        if(idlMethod.skip) {
            return;
        }

        String methodName = idlMethod.name;

        MethodDeclaration containsMethod = containsMethod(classOrInterfaceDeclaration, idlMethod);
        if(containsMethod != null) {
            Optional<Comment> optionalComment = containsMethod.getComment();
            if(optionalComment.isPresent()) {
                Comment comment = optionalComment.get();
                if(comment instanceof BlockComment) {
                    BlockComment blockComment = (BlockComment)optionalComment.get();
                    String headerCommands = CodeParserItem.obtainHeaderCommands(blockComment);
                    // Skip if method already exist with header code
                    if(headerCommands != null && headerCommands.startsWith(CMD_HEADER_START + headerCMD)) {
                        return;
                    }
                }
            }
            containsMethod.remove();
        }

        if(!filterIDLMethod(idlClass, idlMethod)) {
            return;
        }

        ArrayList<IDLParameter> parameters = idlMethod.parameters;
        MethodDeclaration methodDeclaration = classOrInterfaceDeclaration.addMethod(methodName, Keyword.PUBLIC);
        for(int i = 0; i < parameters.size(); i++) {
            IDLParameter idlParameter = parameters.get(i);
            String paramType = idlParameter.type;
            String paramName = idlParameter.name;
            Parameter parameter = methodDeclaration.addAndGetParameter(paramType, paramName);
            Type type = parameter.getType();
            JParserHelper.addMissingImportType(jParser, unit, type);
        }

        Type returnType = StaticJavaParser.parseType(idlMethod.returnType);
        methodDeclaration.setType(returnType);
        setDefaultReturnValues(jParser, unit, returnType, methodDeclaration);
        onIDLMethodGenerated(jParser, unit, classOrInterfaceDeclaration, methodDeclaration, false);
    }

    private void setDefaultReturnValues(JParser jParser, CompilationUnit unit, Type returnType, MethodDeclaration idlMethodDeclaration) {
        if(!returnType.isVoidType()) {
            BlockStmt blockStmt = idlMethodDeclaration.getBody().get();
            ReturnStmt returnStmt = new ReturnStmt();
            if(returnType.isPrimitiveType()) {
                if(JParserHelper.isLong(returnType) || JParserHelper.isInt(returnType) || JParserHelper.isFloat(returnType) || JParserHelper.isDouble(returnType)) {
                    NameExpr returnNameExpr = new NameExpr();
                    returnNameExpr.setName("0");
                    returnStmt.setExpression(returnNameExpr);
                }
                else if(JParserHelper.isBoolean(returnType)) {
                    NameExpr returnNameExpr = new NameExpr();
                    returnNameExpr.setName("false");
                    returnStmt.setExpression(returnNameExpr);
                }
            }
            else {
                JParserHelper.addMissingImportType(jParser, unit, returnType);
                NameExpr returnNameExpr = new NameExpr();
                returnNameExpr.setName("null");
                returnStmt.setExpression(returnNameExpr);
            }
            blockStmt.addStatement(returnStmt);
        }
    }

    protected abstract void onIDLMethodGenerated(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, boolean isAttribute);

    private MethodDeclaration containsMethod(ClassOrInterfaceDeclaration classOrInterfaceDeclaration, IDLMethod idlMethod) {
        ArrayList<IDLParameter> parameters = idlMethod.parameters;
        String [] paramTypes = new String[parameters.size()];

        for(int i = 0; i < parameters.size(); i++) {
            IDLParameter parameter = parameters.get(i);
            String paramType = parameter.type;
            paramTypes[i] = paramType;
        }
        List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethodsBySignature(idlMethod.name, paramTypes);

        if(methods.size() > 0) {
            return methods.get(0);
        }
        return null;
    }
}