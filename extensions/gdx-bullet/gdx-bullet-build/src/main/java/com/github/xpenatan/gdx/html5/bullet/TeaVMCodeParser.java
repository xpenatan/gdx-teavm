package com.github.xpenatan.gdx.html5.bullet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParserUnit;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserItem;
import com.github.xpenatan.tools.jparser.codeparser.IBLDefaultCodeParser;
import com.github.xpenatan.tools.jparser.idl.IDLFile;
import com.github.xpenatan.tools.jparser.idl.IDLMethod;

/** @author xpenatan */
public class TeaVMCodeParser extends IBLDefaultCodeParser {

    private static final String HEADER_CMD = "teaVM";

    public TeaVMCodeParser(IDLFile idlFile) {
        super(HEADER_CMD, idlFile);
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

    @Override
    protected void onMethodGenerated(JParserUnit jParserUni, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration methodDeclaration, IDLMethod idlMethod) {

//        if(methodDeclaration.getType().isVoidType()) {
            MethodDeclaration clonedMethod = methodDeclaration.clone();
            Parameter parameter = clonedMethod.addAndGetParameter("int", "addr");
            NodeList<Parameter> parameters = clonedMethod.getParameters();
            parameters.remove(parameter);
            parameters.add(0, parameter);
            clonedMethod.setNative(true);
            clonedMethod.removeBody();
            clonedMethod.removeModifier(Keyword.PUBLIC);
            clonedMethod.setPrivate(true);
            classDeclaration.getMembers().add(clonedMethod);
//        }
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
}