package com.github.xpenatan.gdx.html5.bullet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParserUnit;
import com.github.xpenatan.tools.jparser.JParserUnitItem;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserItem;
import com.github.xpenatan.tools.jparser.codeparser.DefaultCodeParser;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLMethod;
import com.github.xpenatan.tools.jparser.idl.IDLParameter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeaVMCodeParser extends DefaultCodeParser {

    private IDLFile idlFile;
    private static final String HEADER_CMD = "teaVM";

    public TeaVMCodeParser(IDLFile idlFile) {
        super(HEADER_CMD);
        this.idlFile = idlFile;
    }

    @Override
    protected void setJavaBodyNativeCMD(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        int size = parameters.size();

        String content = CodeParserItem.obtainContent(headerCommands, blockComment);

        String param = "";

        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            SimpleName name = parameter.getName();
            param += name;
            if(i < size - 1) {
                param += "\", \"";
            }
        }

        convertJavaPrimitiveArrayToJavaScriptReferenceArray(parameters);

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

    private void convertJavaPrimitiveArrayToJavaScriptReferenceArray(NodeList<Parameter> parameters) {
        int size = parameters.size();
        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            Type type = parameter.getType();
            if(type.isArrayType()) {
                ArrayType arrayType = (ArrayType) type;
                if(arrayType.getComponentType().isPrimitiveType()) {
                    parameter.addAndGetAnnotation("org.teavm.jso.JSByRef");
                }
            }
        }
    }

    @Override
    public void onParseConstructor(ConstructorDeclaration constructorDeclaration) {
        convertLongToIntParameters(constructorDeclaration.getParameters());
    }

    @Override
    public void onParseField(FieldDeclaration fieldDeclaration) {
        Type elementType = fieldDeclaration.getElementType();
        if(isTypeLong(elementType)) {
            Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
            fieldDeclaration.setAllTypes(intType);
        }
    }

    @Override
    public void onParseMethod(MethodDeclaration methodDeclaration) {
        {
            // teaVM does not support Long so we convert the return type to int
            if(isTypeLong(methodDeclaration.getType())) {
                methodDeclaration.setType(int.class);
            }
            convertLongToIntParameters(methodDeclaration.getParameters());
        }
    }

    private void convertLongToIntParameters(NodeList<Parameter> parameters) {
        int size = parameters.size();
        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            if(isTypeLong(parameter.getType())){
                Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                parameter.setType(intType);
            }
        }
    }

    private boolean isTypeLong(Type type) {
        return isType(type, "long");
    }

    private boolean isType(Type type, String typeStr) {
        if(type.isPrimitiveType()) {
            PrimitiveType primitiveType = type.asPrimitiveType();
            String name = primitiveType.getType().name();
            return name.contains(typeStr.toUpperCase());
        }
        return false;
    }

    @Override
    public void onParseClass(JParserUnit jParserUnit, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
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
