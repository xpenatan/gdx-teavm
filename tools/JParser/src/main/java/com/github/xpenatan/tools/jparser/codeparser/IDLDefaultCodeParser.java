package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParserHelper;
import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLMethod;
import com.github.xpenatan.tools.jparser.idl.IDLParameter;

import java.util.ArrayList;
import java.util.List;

/** @author xpenatan */
public abstract class IDLDefaultCodeParser extends DefaultCodeParser {

    protected final IDLFile idlFile;

    public IDLDefaultCodeParser(String headerCMD, IDLFile idlFile) {
        super(headerCMD);
        this.idlFile = idlFile;
    }

    @Override
    protected void setJavaBodyNativeCMD(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
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
                    generateMethods(jParser, unit, classOrInterfaceDeclaration, idlMethod);
                }
            }
        }
    }

    private void generateMethods(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration, IDLMethod idlMethod) {
        String methodName = idlMethod.name;
        if(containsMethod(classOrInterfaceDeclaration, idlMethod)) {
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
        onIDLMethodGenerated(jParser, unit, classOrInterfaceDeclaration, methodDeclaration);
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

    protected abstract void onIDLMethodGenerated(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration);

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
}