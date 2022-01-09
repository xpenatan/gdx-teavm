package com.github.xpenatan.gdx.html5.bullet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Modifier.Keyword;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.JParser;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserHelper;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserItem;
import com.github.xpenatan.tools.jparser.codeparser.IDLDefaultCodeParser;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;

import java.util.List;
import java.util.Optional;

/** @author xpenatan */
public class TeaVMCodeParser extends IDLDefaultCodeParser {

    private static final String HEADER_CMD = "teaVM";

    private static final String TEMPLATE_TAG_TYPE = "[TYPE]";
    private static final String TEMPLATE_TAG_METHOD = "[METHOD]";

    private static final String CONVERT_TO_GDX_TEMPLATE = "" +
            "{\n" +
            "    int pointer = [METHOD];\n" +
            "    [TYPE].WRAPPER_GEN_01.setPointer(pointer);\n" +
            "    [TYPE].convert([TYPE].WRAPPER_GEN_01, [TYPE].TEMP_GDX_01);\n" +
            "    return [TYPE].TEMP_GDX_01;\n" +
            "}";

    private static final String OBJECT_CREATION_TEMPLATE = "" +
            "public static [TYPE] WRAPPER_GEN_01 = new [TYPE](false);";

    private static final String GET_OBJECT_TEMPLATE = "" +
            "{\n" +
            "    int pointer = [METHOD];\n" +
            "    [TYPE].WRAPPER_GEN_01.setPointer(pointer);\n" +
            "    return [TYPE].WRAPPER_GEN_01;\n" +
            "}";

    private static final String GET_JS_POINTER_TEMPLATE = "" +
            "var jsObj = Bullet.wrapPointer(addr, Bullet.[TYPE]);\n" +
            "var otherJSObj = jsObj.[METHOD];\n" +
            "return Bullet.getPointer(otherJSObj);";

    public TeaVMCodeParser(IDLFile idlFile) {
        super(HEADER_CMD, idlFile);
    }

    @Override
    public void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        String nameAsString = classOrInterfaceDeclaration.getNameAsString();
        IDLClass idlClass = idlFile.getClass(nameAsString);

        if(idlClass != null) {
            // Create a static temp object for every bullet class so any generated method can use to store a pointer.
            // Also generate a boolean constructor if it's not in the original source code.
            List<ConstructorDeclaration> constructors = classOrInterfaceDeclaration.getConstructors();
            String replace = OBJECT_CREATION_TEMPLATE.replace(TEMPLATE_TAG_TYPE, nameAsString);
            FieldDeclaration bodyDeclaration = (FieldDeclaration)StaticJavaParser.parseBodyDeclaration(replace);
            classOrInterfaceDeclaration.getMembers().add(0, bodyDeclaration);

            boolean containsConstructor = false;
            boolean containsZeroParamConstructor = false;
            for(int i = 0; i < constructors.size(); i++) {
                ConstructorDeclaration constructorDeclaration = constructors.get(i);
                NodeList<Parameter> parameters = constructorDeclaration.getParameters();
                if(parameters.size() == 1 && CodeParserHelper.isBoolean(parameters.get(0).getType())) {
                    containsConstructor = true;
                }
                else if(parameters.size() == 0) {
                    containsZeroParamConstructor = true;
                }
            }

            if(!containsConstructor) {
                ConstructorDeclaration constructorDeclaration = classOrInterfaceDeclaration.addConstructor(Keyword.PUBLIC);
                constructorDeclaration.addParameter("boolean", "cMemoryOwn");
            }
            if(!containsZeroParamConstructor) {
                classOrInterfaceDeclaration.addConstructor(Keyword.PUBLIC);
            }
        }

        super.onParseClassStart(jParser, unit, classOrInterfaceDeclaration);
    }

    @Override
    public void onParseClassEnd(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        super.onParseClassEnd(jParser, unit, classOrInterfaceDeclaration);

        // Convert everything in this class from long to int. teaVM does not support long
        List<MethodDeclaration> methods = classOrInterfaceDeclaration.getMethods();
        for(int i = 0; i < methods.size(); i++) {
            MethodDeclaration methodDeclaration = methods.get(i);
            convertMethodParamsAndReturn(methodDeclaration);
        }
        List<FieldDeclaration> fields = classOrInterfaceDeclaration.getFields();
        for(int i = 0; i < fields.size(); i++) {
            FieldDeclaration fieldDeclaration = fields.get(i);
            convertFieldsToInt(fieldDeclaration);
        }
        List<ConstructorDeclaration> constructors = classOrInterfaceDeclaration.getConstructors();
        for(int i = 0; i < constructors.size(); i++) {
            ConstructorDeclaration constructorDeclaration = constructors.get(i);
            convertLongToIntParameters(constructorDeclaration.getParameters());
            BlockStmt body = constructorDeclaration.getBody();
            convertBodyLongToInt(body);
        }
    }

    @Override
    protected void setJavaBodyNativeCMD(String headerCommands, BlockComment blockComment, MethodDeclaration methodDeclaration) {
        // Generate teaVM binding when using comment blocks
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
        // If you send an array to bullet and it writes to it, the JSbyRef annotation is required.
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
    protected void onIDLMethodGenerated(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration) {
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
            nativeMethod.setModifiers(Modifier.createModifierList(Keyword.PRIVATE, Keyword.STATIC, Keyword.NATIVE));
            nativeMethod.removeBody();
            nativeMethod.addAndGetParameter("long", "addr");

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
                    Type type = StaticJavaParser.parseType(long.class.getSimpleName());
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
                generateObjectPointerReturnType(unit, idlMethodDeclaration, caller);
            }
            else {
                // Should be a primitive return type.
                ReturnStmt returnStmt = getReturnStmt(idlMethodDeclaration);
                returnStmt.setExpression(caller);
            }
            generateNativeMethodAnnotation(classDeclaration, idlMethodDeclaration, nativeMethod);
            classDeclaration.getMembers().add(nativeMethod);
        }
    }

    private ReturnStmt getReturnStmt(MethodDeclaration idlMethodDeclaration) {
        BlockStmt blockStmt = idlMethodDeclaration.getBody().get();
        NodeList<Statement> statements = blockStmt.getStatements();
        if(statements.size() > 0) {
            // Find the return block and add the caller
            Statement statement = blockStmt.getStatement(0);
            return (ReturnStmt) statement;
        }
        else {
            // should not go here
            throw new RuntimeException("Should not go here");
        }
    }

    private void generateNativeMethodAnnotation(ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodDeclaration nativeMethod) {
        String idlMethodName = idlMethodDeclaration.getNameAsString();
        NodeList<Parameter> idlMethodParameters = idlMethodDeclaration.getParameters();
        MethodCallExpr caller = new MethodCallExpr();
        caller.setName(idlMethodName);
        for(int i = 0; i < idlMethodParameters.size(); i++) {
            Parameter parameter = idlMethodParameters.get(i);
            String paramName = parameter.getNameAsString();
            caller.addArgument(paramName);
        }

        NodeList<Parameter> parameters = nativeMethod.getParameters();
        int size = parameters.size();
        Type type = nativeMethod.getType();
        String returnTypeName = classDeclaration.getNameAsString();
        String methodCaller = caller.toString();
        String content = GET_JS_POINTER_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);

        String param = "";

        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            SimpleName name = parameter.getName();
            param += name;
            if(i < size - 1) {
                param += "\", \"";
            }
        }

        if(content != null) {
            content = content.replace("\n", "");
            content = content.trim();

            if(!content.isEmpty()) {
                NormalAnnotationExpr normalAnnotationExpr = nativeMethod.addAndGetAnnotation("org.teavm.jso.JSBody");
                if(!param.isEmpty()) {
                    normalAnnotationExpr.addPair("params", "{\"" + param + "\"}");
                }
                normalAnnotationExpr.addPair("script", "\"" + content + "\"");
            }
        }
    }

    private void generateObjectPointerReturnType(CompilationUnit unit, MethodDeclaration idlMethodDeclaration, MethodCallExpr caller) {
        //  if return type is an object we need to get the method pointer, add it do a temp object and return this object
        BlockStmt blockStmt = idlMethodDeclaration.getBody().get();

        Type type = idlMethodDeclaration.getType();

        String returnTypeName = type.toString();
        String methodCaller = caller.toString();
        String newBody = GET_OBJECT_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);

        {
            // Convert native return object to Gdx object
            if(returnTypeName.equals("btVector3")) {
                newBody = CONVERT_TO_GDX_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);
                idlMethodDeclaration.setType("Vector3");
                CodeParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Vector3");
            }
            else if(returnTypeName.equals("btTransform")) {
                newBody = CONVERT_TO_GDX_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);
                idlMethodDeclaration.setType("Matrix4");
                CodeParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Matrix4");
            }
        }

        BodyDeclaration<?> bodyDeclaration = StaticJavaParser.parseBodyDeclaration(newBody);
        InitializerDeclaration initializerDeclaration = (InitializerDeclaration) bodyDeclaration;
        BlockStmt body = initializerDeclaration.getBody();
        idlMethodDeclaration.setBody(body);
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
        Optional<BlockStmt> body = methodDeclaration.getBody();
        if(body.isPresent()) {
            convertBodyLongToInt(body.get());
        }
        convertLongToIntParameters(methodDeclaration.getParameters());
    }

    private static void convertBodyLongToInt(BlockStmt blockStmt) {
        // Convert local variables
        NodeList<Statement> statements = blockStmt.getStatements();
        for(int i = 0; i < statements.size(); i++) {
            Statement statement = statements.get(i);
            if(statement.isExpressionStmt()) {
                ExpressionStmt expressionStmt = statement.asExpressionStmt();
                Expression expression = expressionStmt.getExpression();
                if(expression.isVariableDeclarationExpr()) {
                    VariableDeclarationExpr variableDeclarationExpr = expression.asVariableDeclarationExpr();
                    NodeList<VariableDeclarator> variables = variableDeclarationExpr.getVariables();
                    for(int j = 0; j < variables.size(); j++) {
                        VariableDeclarator variableDeclarator = variables.get(j);
                        if(CodeParserHelper.isLong(variableDeclarator.getType())) {
                            Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                            variableDeclarator.setType(intType);
                        }
                    }
                }
            }
        }
    }

    private static void convertLongToIntParameters(NodeList<Parameter> parameters) {
        // teaVM does not support Long so convert the return type to int
        int size = parameters.size();
        for(int i = 0; i < size; i++) {
            Parameter parameter = parameters.get(i);
            if(CodeParserHelper.isLong(parameter.getType())) {
                Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                parameter.setType(intType);
            }
        }
    }

}