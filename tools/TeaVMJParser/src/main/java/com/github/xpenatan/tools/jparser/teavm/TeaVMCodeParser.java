package com.github.xpenatan.tools.jparser.teavm;

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
import com.github.xpenatan.tools.jparser.idl.IDLMethod;

import java.util.List;
import java.util.Optional;

public class TeaVMCodeParser extends IDLDefaultCodeParser {

    private static final String HEADER_CMD = "teaVM";

    protected static final String TEMPLATE_TAG_TYPE = "[TYPE]";
    protected static final String TEMPLATE_TAG_METHOD = "[METHOD]";
    protected static final String TEMPLATE_TAG_WRAPPER = "[WRAPPER]";
    protected static final String TEMPLATE_TAG_PARAM = "[PARAM]";
    protected static final String TEMPLATE_TAG_NAME = "[NAME]";
    protected static final String TEMPLATE_TAG_MODULE = "[MODULE]";

    private static final String CAST_TO_INT_METHOD = "getCPointer";
    private static final String CPOINTER = "cPointer";
    private static final String ENDS_WITH_POINTER = "Pointer";

    protected static final String CONVERT_TO_GDX_TEMPLATE = "" +
            "{\n" +
            "    int pointer = [METHOD];\n" +
            "    [TYPE].WRAPPER_GEN_01.setPointer(pointer);\n" +
            "    [TYPE].convert([TYPE].WRAPPER_GEN_01, [TYPE].TEMP_GDX_01);\n" +
            "    return [TYPE].TEMP_GDX_01;\n" +
            "}";

    protected static final String OBJECT_CREATION_TEMPLATE = "" +
            "public static [TYPE] WRAPPER_GEN_01 = new [TYPE](false);";

    protected static final String GET_OBJECT_TEMPLATE = "" +
            "{\n" +
            "    int pointer = [METHOD];\n" +
            "    [TYPE].WRAPPER_GEN_01.setPointer(pointer);\n" +
            "    return [TYPE].WRAPPER_GEN_01;\n" +
            "}";

    /** When a js method returns a js object, we need get its pointer.  */
    protected static final String GET_JS_METHOD_OBJ_POINTER_TEMPLATE = "" +
            "var jsObj = [MODULE].wrapPointer(addr, [MODULE].[TYPE]);\n" +
            "var returnedJSObj = jsObj.[METHOD];\n" +
            "return [MODULE].getPointer(returnedJSObj);";

    protected static final String GET_JS_METHOD_PRIMITIVE_TEMPLATE = "" +
            "var jsObj = [MODULE].wrapPointer(addr, [MODULE].[TYPE]);\n" +
            "var returnedJSObj = jsObj.[METHOD];\n" +
            "return returnedJSObj;";

    protected static final String GET_JS_METHOD_VOID_TEMPLATE = "" +
            "var jsObj = [MODULE].wrapPointer(addr, [MODULE].[TYPE]);\n" +
            "jsObj.[METHOD];";

    protected static final String GDX_OBJECT_TEMPLATE = "" +
            "{" +
            "[TYPE].convert([PARAM], [TYPE].[WRAPPER]);\n" +
            "[TYPE] [NAME] = [TYPE].[WRAPPER];" +
            "}";

    private final String module;

    public TeaVMCodeParser(String module, IDLFile idlFile) {
        super(HEADER_CMD, idlFile);
        this.module = module;
    }

    @Override
    public void onParseClassStart(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        String nameAsString = classOrInterfaceDeclaration.getNameAsString();
        IDLClass idlClass = idlFile.getClass(nameAsString);

        if(idlClass != null) {
            // Create a static temp object for every module class so any generated method can use to store a pointer.
            // Also generate a boolean constructor if it's not in the original source code.
            List<ConstructorDeclaration> constructors = classOrInterfaceDeclaration.getConstructors();

            if(!classOrInterfaceDeclaration.isAbstract()) {
                String replace = OBJECT_CREATION_TEMPLATE.replace(TEMPLATE_TAG_TYPE, nameAsString);
                FieldDeclaration bodyDeclaration = (FieldDeclaration)StaticJavaParser.parseBodyDeclaration(replace);
                classOrInterfaceDeclaration.getMembers().add(0, bodyDeclaration);
            }

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
            convertMethodParamsAndReturn(methodDeclaration, classOrInterfaceDeclaration);
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
            convertBodyLongToInt(body, classOrInterfaceDeclaration);
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
            content = content.replace("\n", "").replace("\r", "").replaceAll("[ ]+", " ");;
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
        // If you send an array to module and it writes to it, the JSbyRef annotation is required.
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
    protected void onIDLMethodGenerated(JParser jParser, CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, boolean isAttribute) {
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
            generateNativeMethodAnnotation(classDeclaration, idlMethodDeclaration, nativeMethod, isAttribute);
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

    protected void generateGdxMethod(CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodDeclaration nativeMethod, MethodCallExpr caller) {
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

    private void generateNativeMethodAnnotation(ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration,  MethodDeclaration nativeMethod, boolean isAttribute) {
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
            if(isAttribute) {
                Expression expression = caller.getArguments().get(0);
                methodCaller = methodName + " = " + expression.toString();
            }
            content = GET_JS_METHOD_VOID_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName).replace(TEMPLATE_TAG_MODULE, module);
        }
        else if(returnType.isClassOrInterfaceType()) {
            if(isAttribute) {
                methodCaller = methodName;
            }
            content = GET_JS_METHOD_OBJ_POINTER_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName).replace(TEMPLATE_TAG_MODULE, module);
        }
        else {
            if(isAttribute) {
                methodCaller = methodName;
            }
            content = GET_JS_METHOD_PRIMITIVE_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName).replace(TEMPLATE_TAG_MODULE, module);
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

    protected BlockStmt generateObjectPointerReturnType(CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodCallExpr caller) {
        //  if return type is an object we need to get the method pointer, add it do a temp object and return this object
        Type type = idlMethodDeclaration.getType();

        String returnTypeName = type.toString();
        String methodCaller = caller.toString();
        String newBody = GET_OBJECT_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, returnTypeName);

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

    private static void convertMethodParamsAndReturn(MethodDeclaration methodDeclaration, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        if(!methodDeclaration.isNative()) {
            Optional<BlockStmt> body = methodDeclaration.getBody();
            if(body.isPresent()) {
                convertBodyLongToInt(body.get(), classOrInterfaceDeclaration);
            }
        }
        else {
            if(JParserHelper.isLong(methodDeclaration.getType())) {
                methodDeclaration.setType(int.class);
            }
            convertLongToIntParameters(methodDeclaration.getParameters());
        }
    }

    private static void convertBodyLongToInt(BlockStmt blockStmt, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
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
                                convertNativeMethodCall(expr.asMethodCallExpr(), classOrInterfaceDeclaration);
                            }
                            else if(expr.isCastExpr()) {
                                CastExpr castExpr = expr.asCastExpr();
                                Expression expr2 = castExpr.getExpression();
                                if(expr2.isMethodCallExpr()) {
                                    convertNativeMethodCall(expr2.asMethodCallExpr(), classOrInterfaceDeclaration);
                                }
                            }
                        }
                    }
                }
                else if(expression.isMethodCallExpr()) {
                    convertNativeMethodCall(expression.asMethodCallExpr(), classOrInterfaceDeclaration);
                }
            }
            else if(statement.isReturnStmt()) {
                ReturnStmt returnStmt = statement.asReturnStmt();
                Optional<Expression> expressionOptional = returnStmt.getExpression();
                if(expressionOptional.isPresent()) {
                    Expression expression = expressionOptional.get();
                    if(expression.isMethodCallExpr()) {
                        convertNativeMethodCall(expression.asMethodCallExpr(), classOrInterfaceDeclaration);
                    }
                    //Testing code to cast to int inside code block body
//                    else {
//                        Optional<Node> parentNodeOptional = blockStmt.getParentNode();
//                        if(parentNodeOptional.isPresent()) {
//                            Node node = parentNodeOptional.get();
//                            if(node instanceof MethodDeclaration) {
//                                MethodDeclaration method = (MethodDeclaration)node;
//                                Type type = method.getType();
//                                if(JParserHelper.isLong(type)) {
//                                    Type intType = StaticJavaParser.parseType(int.class.getSimpleName());
//                                    CastExpr intCast = new CastExpr(intType, expression);
//                                    returnStmt.setExpression(intCast);
//                                }
//                            }
//                        }
//                    }
                }
            }
        }
    }

    private static void convertNativeMethodCall(MethodCallExpr methodCallExpr, ClassOrInterfaceDeclaration classOrInterfaceDeclaration) {
        String methodName = methodCallExpr.getNameAsString();
        MethodDeclaration callerMethod = null;
        List<MethodDeclaration> methodsByName = classOrInterfaceDeclaration.getMethodsByName(methodName);
        if(methodsByName.size() > 0) {
            // Testing
            callerMethod = methodsByName.get(0);
        }

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
                    convertNativeMethodCall(methodCallExpr1, classOrInterfaceDeclaration);
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