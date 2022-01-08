package com.github.xpenatan.gdx.html5.bullet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParserUnit;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserHelper;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserItem;
import com.github.xpenatan.tools.jparser.codeparser.IDLDefaultCodeParser;
import com.github.xpenatan.tools.jparser.idl.IDLFile;

import java.util.List;

import static com.github.javaparser.ast.Modifier.createModifierList;

/** @author xpenatan */
public class TeaVMCodeParser extends IDLDefaultCodeParser {

    private static final String HEADER_CMD = "teaVM";

    public TeaVMCodeParser(IDLFile idlFile) {
        super(HEADER_CMD, idlFile);
    }

    @Override
    public void onParseClass(JParserUnit jParserUnit, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        // First convert everything in this class from long to int. teaVM does not support long

        List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
        for(int i = 0; i < methods.size(); i++) {
            MethodDeclaration methodDeclaration = methods.get(i);
            convertMethodParamsAndReturn(methodDeclaration);
        }
        List<ConstructorDeclaration> constructors = classOrInterfaceDeclaration.getConstructors();
        for(int i = 0; i < constructors.size(); i++) {
            ConstructorDeclaration constructorDeclaration = constructors.get(i);
            convertLongToIntParameters(constructorDeclaration.getParameters());
        }

        super.onParseClass(jParserUnit, unit, classOrInterfaceDeclaration);
    }

    @Override
    public void onParseField(FieldDeclaration fieldDeclaration) {
        convertFieldsToInt(fieldDeclaration);
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
        // primitive arrays need to have JSbyRef annotation
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
    protected void onIDLMethodGenerated(JParserUnit jParserUni, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration) {
        // IDL parser generated our empty methods with default return values.
        // We now modify it to match teaVM api calls

        String idlMethodName = idlMethodDeclaration.getNameAsString();
        NodeList<Parameter> idlMethodParameters = idlMethodDeclaration.getParameters();
        Type idlMethodReturnType = idlMethodDeclaration.getType();
        MethodDeclaration nativeMethod = null;

        {
            // Clone some generated idl method settings
            nativeMethod = new MethodDeclaration();
            nativeMethod.setName(idlMethodName);
            nativeMethod.setModifiers(createModifierList(Keyword.PRIVATE, Keyword.NATIVE));
            nativeMethod.removeBody();
            nativeMethod.addAndGetParameter("int", "addr");

            for(int i = 0; i < idlMethodParameters.size(); i++) {
                Parameter parameter = idlMethodParameters.get(i);
                String nameAsString = parameter.getNameAsString();
                Type type = parameter.getType();
                if(type.isPrimitiveType()) {
                    nativeMethod.addParameter(type.clone(), nameAsString);
                }
                else {
                    String pointerMethod = nameAsString + "Addr";
                    nativeMethod.addParameter("long", pointerMethod);
                }
            }
        }

        // Check if the generated method does not exist in the original class
        if(!CodeParserHelper.containsMethod(classDeclaration, nativeMethod)) {
            {
                // If the return type is an object we need to return a pointer.
                if(idlMethodReturnType.isClassOrInterfaceType()) {
                    // Class Object needs to return a pointer
                    Type type = StaticJavaParser.parseType(int.class.getSimpleName());
                    nativeMethod.setType(type);
                }
                else {
                    nativeMethod.setType(idlMethodReturnType);
                }
            }
            MethodCallExpr caller = null;
            {
                // Generate the method caller
                caller = new MethodCallExpr();
                caller.setName(idlMethodName);
                caller.addArgument("cPointer");
                for(int i = 0; i < idlMethodParameters.size(); i++) {
                    Parameter parameter = idlMethodParameters.get(i);
                    Type type = parameter.getType();
                    String paramName = parameter.getNameAsString();
                    if(type.isClassOrInterfaceType()) {
                        String typeName = parameter.getType().toString();
                        paramName = paramName + ".getCPointer()";
                    }
                    caller.addArgument(paramName);
                }
            }

            if(idlMethodReturnType.isVoidType()) {
                // void types just call the method.
                BlockStmt blockStmt = idlMethodDeclaration.getBody().get();
                blockStmt.addStatement(caller);
            }
            else if(idlMethodReturnType.isClassOrInterfaceType()) {
                // Class object needs to generate some code.
                generateObjectPointerReturnType(idlMethodDeclaration, caller);
            }
            else {
                // Should be a primitive return type.
                ReturnStmt returnStmt = getReturnStmt(idlMethodDeclaration);
                returnStmt.setExpression(caller);
            }
            generateNativeMethodAnnotation(nativeMethod);
            classDeclaration.getMembers().add(nativeMethod);
        }
    }

    private ReturnStmt getReturnStmt(MethodDeclaration idlMethodDeclaration) {
        BlockStmt blockStmt = idlMethodDeclaration.getBody().get();
        NodeList<Statement> statements = blockStmt.getStatements();
        if(statements.size() > 0) {
            // Find the return block and add the caller
            Statement statement = blockStmt.getStatement(0);
            ReturnStmt returnStmt = (ReturnStmt) statement;
            return returnStmt;
        }
        else {
            // should not go here
            throw new RuntimeException("Should not go here");
        }
    }

    private void generateNativeMethodAnnotation(MethodDeclaration nativeMethod) {
        // TODO add teaVM annotation
    }

    private void generateObjectPointerReturnType(MethodDeclaration idlMethodDeclaration, MethodCallExpr caller) {
        //  if return type is an object we need to get the method pointer, add it do a temp object and return this object

        BlockStmt blockStmt = idlMethodDeclaration.getBody().get();

        blockStmt.addStatement(0, caller);
        // TODO implement return temp object
    }

    private static void convertFieldsToInt(FieldDeclaration fieldDeclaration) {
        Type elementType = fieldDeclaration.getElementType();
        if(CodeParserHelper.isLong(elementType)) {
            Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
            fieldDeclaration.setAllTypes(intType);
        }
    }

    private static void convertMethodParamsAndReturn(MethodDeclaration methodDeclaration) {
        if(CodeParserHelper.isLong(methodDeclaration.getType())) {
            methodDeclaration.setType(int.class);
        }
        convertLongToIntParameters(methodDeclaration.getParameters());
    }

    private static void convertLongToIntParameters(NodeList<Parameter> parameters) {
        // teaVM does not support Long so convert the return type to int
        int size = parameters.size();
        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            if(CodeParserHelper.isLong(parameter.getType())){
                Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                parameter.setType(intType);
            }
        }
    }

}