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
import com.github.xpenatan.tools.jparser.JParserUnit;
import com.github.xpenatan.tools.jparser.JParserUnitItem;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLMethod;
import com.github.xpenatan.tools.jparser.idl.IDLParameter;

import java.util.ArrayList;
import java.util.List;

/** @author xpenatan */
public class IBLDefaultCodeParser extends DefaultCodeParser {

    private IDLFile idlFile;

    public IBLDefaultCodeParser(String headerCMD, IDLFile idlFile) {
        super(headerCMD);
        this.idlFile = idlFile;
    }

    @Override
    protected void setJavaBodyNativeCMD(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
    }

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