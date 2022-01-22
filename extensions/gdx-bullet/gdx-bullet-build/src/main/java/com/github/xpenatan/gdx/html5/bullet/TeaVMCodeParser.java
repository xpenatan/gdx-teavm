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
import com.github.xpenatan.tools.jparser.JParserHelper;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserItem;
import com.github.xpenatan.tools.jparser.codeparser.IDLDefaultCodeParser;
import com.github.xpenatan.tools.jparser.idl.IDLClass;
import com.github.xpenatan.tools.jparser.idl.IDLFile;

import java.util.List;
import java.util.Optional;

/**
 * A Bullet parser which attempt to use the generated idl java methods and bind it to teaVM api.
 *
 * It also adds additional methods to use Matrix4 and Vector3
 *
 * @author xpenatan */
public class TeaVMCodeParser extends IDLDefaultCodeParser {

    private static final String HEADER_CMD = "teaVM";

    private static final String TEMPLATE_TAG_TYPE = "[TYPE]";
    private static final String TEMPLATE_TAG_METHOD = "[METHOD]";
    private static final String TEMPLATE_TAG_WRAPPER = "[WRAPPER]";
    private static final String TEMPLATE_TAG_PARAM = "[PARAM]";
    private static final String TEMPLATE_TAG_NAME = "[NAME]";

    private static final String CAST_TO_INT_METHOD = "getCPointer";
    private static final String CPOINTER = "cPointer";
    private static final String ENDS_WITH_POINTER = "Pointer";

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

    /** When a js method returns a js object, we need get its pointer.  */
    private static final String GET_JS_METHOD_OBJ_POINTER_TEMPLATE = "" +
            "var jsObj = Bullet.wrapPointer(addr, Bullet.[TYPE]);\n" +
            "var returnedJSObj = jsObj.[METHOD];\n" +
            "return Bullet.getPointer(returnedJSObj);";

    private static final String GET_JS_METHOD_PRIMITIVE_TEMPLATE = "" +
            "var jsObj = Bullet.wrapPointer(addr, Bullet.[TYPE]);\n" +
            "var returnedJSObj = jsObj.[METHOD];\n" +
            "return returnedJSObj;";

    private static final String GET_JS_METHOD_VOID_TEMPLATE = "" +
            "var jsObj = Bullet.wrapPointer(addr, Bullet.[TYPE]);\n" +
            "jsObj.[METHOD];";

    private static final String GDX_OBJECT_TEMPLATE = "" +
            "{" +
            "[TYPE].convert([PARAM], [TYPE].[WRAPPER]);\n" +
            "[TYPE] [NAME] = [TYPE].[WRAPPER];" +
            "}";

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
                if(parameters.size() == 1 && JParserHelper.isBoolean(parameters.get(0).getType())) {
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
//        List<FieldDeclaration> fields = classOrInterfaceDeclaration.getFields();
//        for(int i = 0; i < fields.size(); i++) {
//            FieldDeclaration fieldDeclaration = fields.get(i);
//            convertFieldsToInt(fieldDeclaration);
//        }
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
            nativeMethod.setName(idlMethodName + "NATIVE");
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
            // If the return type is an object we need to return a pointer.
            if(idlMethodReturnType.isClassOrInterfaceType()) {
                // Class Object needs to return a pointer
                Type type = StaticJavaParser.parseType(long.class.getSimpleName());
                nativeMethod.setType(type);
            }
            else {
                nativeMethod.setType(idlMethodReturnType);
            }
            //Generate teaVM Annotation
            generateNativeMethodAnnotation(classDeclaration, idlMethodDeclaration, nativeMethod);
        }

        // Check if the generated method does not exist in the original class
        if(!JParserHelper.containsMethod(classDeclaration, nativeMethod)) {
            MethodCallExpr caller = null;
            {
                // Generate the method caller
                caller = new MethodCallExpr();
                caller.setName(nativeMethod.getNameAsString());
                caller.addArgument(CPOINTER);
                for(int i = 0; i < idlMethodParameters.size(); i++) {
                    Parameter parameter = idlMethodParameters.get(i);
                    Type type = parameter.getType();
                    String paramName = parameter.getNameAsString();
                    if(type.isClassOrInterfaceType()) {
                        String typeName = parameter.getType().toString();
                        paramName = "(int)" + paramName + ".getCPointer()";
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
                BlockStmt blockStmt = generateObjectPointerReturnType(unit, classDeclaration, idlMethodDeclaration, caller);
                idlMethodDeclaration.setBody(blockStmt);
            }
            else {
                // Should be a primitive return type.
                ReturnStmt returnStmt = getReturnStmt(idlMethodDeclaration);
                returnStmt.setExpression(caller);
            }
            classDeclaration.getMembers().add(nativeMethod);
            generateGdxMethod(unit, classDeclaration, idlMethodDeclaration, nativeMethod, caller);
        }
    }

    private void generateGdxMethod(CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodDeclaration nativeMethod, MethodCallExpr caller) {
        // Generate a new method if the parameters contains btVector3 or btTransform
        boolean containsVector3 = idlMethodDeclaration.getParameterByType("btVector3").isPresent();
        boolean containsTransform = idlMethodDeclaration.getParameterByType("btTransform").isPresent();

        if(containsVector3 || containsTransform) {
            MethodDeclaration gdxMethod = new MethodDeclaration();
            gdxMethod.setName(idlMethodDeclaration.getNameAsString());
            gdxMethod.setModifiers(Modifier.createModifierList(Keyword.PUBLIC));
            Type returnType = idlMethodDeclaration.getType().clone();
            gdxMethod.setType(returnType);
            BlockStmt body = gdxMethod.createBody();
            NodeList<Parameter> idlParameters = idlMethodDeclaration.getParameters();

            int btVec3Used = 0;
            int btTransformUsed = 0;
            for(int i = 0; i < idlParameters.size(); i++) {
                Parameter parameter = idlParameters.get(i);
                String paramName = parameter.getNameAsString();
                Type paramType = parameter.getType().clone();
                String paramTypeStr = paramType.toString();
                if(paramTypeStr.equals("btVector3")) {
                    String newParam = paramName + "GDX";
                    gdxMethod.addParameter("Vector3", newParam);
                    convertGdxToNative(body, btVec3Used, paramName, paramTypeStr, newParam);
                    btVec3Used++;
                }
                else if(paramTypeStr.equals("btTransform")) {
                    String newParam = paramName + "GDX";
                    gdxMethod.addParameter("Matrix4", newParam);
                    convertGdxToNative(body, btTransformUsed, paramName, paramTypeStr, newParam);
                    btTransformUsed++;
                }
                else {
                    gdxMethod.addParameter(paramType, paramName);
                }
            }

            if(returnType.isVoidType()) {
                body.addStatement(caller);
            }
            else if(returnType.isClassOrInterfaceType()) {
                BlockStmt blockStmt = generateObjectPointerReturnType(unit, classDeclaration, idlMethodDeclaration, caller);
                NodeList<Statement> statements = blockStmt.getStatements();
                for(int i = 0; i < statements.size(); i++) {
                    Statement statement = statements.get(i);
                    body.addStatement(statement);
                }
            }
            else {
                ReturnStmt returnStmt = new ReturnStmt();
                returnStmt.setExpression(caller);
                body.addStatement(returnStmt);
            }

            if(!JParserHelper.containsMethod(classDeclaration, gdxMethod)) {
                if(containsVector3) {
                    JParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Vector3");
                }
                if(containsTransform) {
                    JParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Matrix4");
                }
                classDeclaration.getMembers().add(gdxMethod);
            }
        }
    }

    private void convertGdxToNative(BlockStmt body, int btTransformUsed, String paramName, String paramTypeStr, String newParam) {
        String wrapperName = "TEMP_" + btTransformUsed;
        String gdxCode = GDX_OBJECT_TEMPLATE.replace(TEMPLATE_TAG_TYPE, paramTypeStr)
                .replace(TEMPLATE_TAG_NAME, paramName)
                .replace(TEMPLATE_TAG_PARAM, newParam)
                .replace(TEMPLATE_TAG_WRAPPER, wrapperName);
        btTransformUsed++;

        BodyDeclaration<?> bodyDeclaration = StaticJavaParser.parseBodyDeclaration(gdxCode);
        InitializerDeclaration initializerDeclaration = (InitializerDeclaration) bodyDeclaration;
        NodeList<Statement> statements = initializerDeclaration.getBody().getStatements();
        for(int j = 0; j < statements.size(); j++) {
            Statement statement = statements.get(j);
            body.addStatement(statement);
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

    private void generateNativeMethodAnnotation(ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration,  MethodDeclaration nativeMethod) {
        NodeList<Parameter> nativeParameters = nativeMethod.getParameters();
        Type returnType = idlMethodDeclaration.getType();
        String methodName = idlMethodDeclaration.getNameAsString();
        MethodCallExpr caller = new MethodCallExpr();
        caller.setName(methodName);

        String param = "";
        int size = nativeParameters.size();
        for(int i = 0; i < size; i++) {
            Parameter parameter = nativeParameters.get(i);
            String paramName = parameter.getNameAsString();
            if(i > 0) {
                caller.addArgument(paramName);
            }

            param += paramName;
            if(i < size - 1) {
                param += "\", \"";
            }
        }

        String returnTypeName = classDeclaration.getNameAsString();
        String methodCaller = caller.toString();

        String content = null;
        if(returnType.isVoidType()) {
            content = GET_JS_METHOD_VOID_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);
        }
        else if(returnType.isClassOrInterfaceType()) {
            content = GET_JS_METHOD_OBJ_POINTER_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);
        }
        else {
            content = GET_JS_METHOD_PRIMITIVE_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);
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

    private BlockStmt generateObjectPointerReturnType(CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodCallExpr caller) {
        //  if return type is an object we need to get the method pointer, add it do a temp object and return this object
        Type type = idlMethodDeclaration.getType();

        String returnTypeName = type.toString();
        String methodCaller = caller.toString();
        String newBody = GET_OBJECT_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);

        {
            // Convert native return object to Gdx object
            if(returnTypeName.equals("btVector3") || returnTypeName.equals("Vector3")) {
                newBody = CONVERT_TO_GDX_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, "btVector3");
                idlMethodDeclaration.setType("Vector3");
                JParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Vector3");
            }
            else if(returnTypeName.equals("btTransform") || returnTypeName.equals("Matrix4")) {
                newBody = CONVERT_TO_GDX_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, "btTransform");
                idlMethodDeclaration.setType("Matrix4");
                JParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Matrix4");
            }
        }

        BlockStmt body = null;
        try {
            BodyDeclaration<?> bodyDeclaration = StaticJavaParser.parseBodyDeclaration(newBody);
            InitializerDeclaration initializerDeclaration = (InitializerDeclaration) bodyDeclaration;
            body = initializerDeclaration.getBody();
        }
        catch(Throwable t) {
            String className = classDeclaration.getNameAsString();
            System.err.println("Error Class: " + className + "\n" + newBody);
            throw t;
        }
        return body;
    }

    private static void convertFieldsToInt(FieldDeclaration fieldDeclaration) {
        Type elementType = fieldDeclaration.getElementType();
        if(JParserHelper.isLong(elementType)) {
            Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
            fieldDeclaration.setAllTypes(intType);
        }
    }

    private static void convertMethodParamsAndReturn(MethodDeclaration methodDeclaration) {
        if(!methodDeclaration.isNative()) {
            Optional<BlockStmt> body = methodDeclaration.getBody();
            if(body.isPresent()) {
                convertBodyLongToInt(body.get());
            }
            return;
        }

        if(JParserHelper.isLong(methodDeclaration.getType())) {
            methodDeclaration.setType(int.class);
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
                        if(JParserHelper.isLong(variableDeclarator.getType())) {
                            // Commented because its not needed anymore. Only needs to cast
//                            Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
//                            variableDeclarator.setType(intType);
                        }
                        Optional<Expression> expressionOptional = variableDeclarator.getInitializer();
                        if(expressionOptional.isPresent()) {
                            Expression expr = expressionOptional.get();
                            if(expr.isMethodCallExpr()) {
                                convertNativeMethodCall(expr.asMethodCallExpr());
                            }
                        }
                    }
                }
                else if(expression.isMethodCallExpr()) {
                    convertNativeMethodCall(expression.asMethodCallExpr());
                }
            }
            else if(statement.isReturnStmt()) {
                ReturnStmt returnStmt = statement.asReturnStmt();
                Optional<Expression> expressionOptional = returnStmt.getExpression();
                if(expressionOptional.isPresent()) {
                    Expression expression = expressionOptional.get();
                    if(expression.isMethodCallExpr()) {
                        convertNativeMethodCall(expression.asMethodCallExpr());
                    }
                }
            }
        }
    }

    private static void convertNativeMethodCall(MethodCallExpr methodCallExpr) {
        NodeList<Expression> arguments = methodCallExpr.getArguments();
        for(int i = 0; i < arguments.size(); i++) {
            Expression expression = arguments.get(i);
            if(expression.isMethodCallExpr()) {
                MethodCallExpr methodCallExpr1 = expression.asMethodCallExpr();
                String nameAsString = methodCallExpr1.getNameAsString();
                if(nameAsString.equals(CAST_TO_INT_METHOD)) {
                    Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                    CastExpr intCast = new CastExpr(intType, expression);
                    methodCallExpr.setArgument(i, intCast);
                }
                else {
                    convertNativeMethodCall(methodCallExpr1);
                }
            }
            else if(expression.isNameExpr()) {
                NameExpr nameExpr = expression.asNameExpr();
                String nameAsString = nameExpr.getNameAsString();
                if(nameAsString.equals(CPOINTER) || nameAsString.endsWith(ENDS_WITH_POINTER)) {
                    Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                    CastExpr intCast = new CastExpr(intType, expression);
                    methodCallExpr.setArgument(i, intCast);
                }
            }
            else if(expression.isConditionalExpr()) {
                ConditionalExpr conditionalExpr = expression.asConditionalExpr();
                Expression thenExpr = conditionalExpr.getThenExpr();
                if(thenExpr.isMethodCallExpr()) {
                    MethodCallExpr methodCall = thenExpr.asMethodCallExpr();
                    String nameAsString = methodCall.getNameAsString();
                    if(nameAsString.equals(CAST_TO_INT_METHOD)) {
                        Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                        CastExpr intCast = new CastExpr(intType, thenExpr);
                        conditionalExpr.setThenExpr(intCast);
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
            if(JParserHelper.isLong(parameter.getType())) {
                Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
                parameter.setType(intType);
            }
        }
    }
}