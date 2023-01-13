package com.github.xpenatan.gdx.html5.bullet;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.jparser.core.JParserHelper;
import com.github.xpenatan.jparser.core.idl.IDLFile;
import com.github.xpenatan.jparser.cpp.CppCodeParser;

public class BulletCppParser extends CppCodeParser {

    protected static final String GDX_OBJECT_TEMPLATE = "" +
            "{" +
            "[TYPE].convert([PARAM], [TYPE].[WRAPPER]);\n" +
            "[TYPE] [NAME] = [TYPE].[WRAPPER];" +
            "}";

    protected static final String TEMPLATE_TAG_WRAPPER = "[WRAPPER]";
    protected static final String TEMPLATE_TAG_PARAM = "[PARAM]";
    protected static final String TEMPLATE_TAG_NAME = "[NAME]";

    public BulletCppParser(String classpath, String jniDir) {
        this(null, classpath, jniDir);
    }

    public BulletCppParser(IDLFile idlFile, String classpath, String jniDir) {
        super(idlFile, classpath, jniDir);
    }

    protected void generateGdxMethod(CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodDeclaration nativeMethod, MethodCallExpr caller) {
//         Generate a new method if the parameters contains btVector3 or btTransform
        boolean containsVector3 = idlMethodDeclaration.getParameterByType("btVector3").isPresent();
        boolean containsTransform = idlMethodDeclaration.getParameterByType("btTransform").isPresent();
        boolean containsQuaternion = idlMethodDeclaration.getParameterByType("btQuaternion").isPresent();

        if(containsVector3 || containsTransform || containsQuaternion) {
            MethodDeclaration gdxMethod = new MethodDeclaration();
            gdxMethod.setName(idlMethodDeclaration.getNameAsString());
            gdxMethod.setModifiers(Modifier.createModifierList(Modifier.Keyword.PUBLIC));
            Type returnType = idlMethodDeclaration.getType().clone();
            gdxMethod.setType(returnType);
            BlockStmt body = gdxMethod.createBody();
            NodeList<Parameter> idlParameters = idlMethodDeclaration.getParameters();

            int btVec3Used = 0;
            int btTransformUsed = 0;
            int btQuaternionUsed = 0;
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
                else if(paramTypeStr.equals("btQuaternion")) {
                    String newParam = paramName + "GDX";
                    gdxMethod.addParameter("Quaternion", newParam);
                    convertGdxToNative(body, btQuaternionUsed, paramName, paramTypeStr, newParam);
                    btQuaternionUsed++;
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
                if(containsQuaternion) {
                    JParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Quaternion");
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
        InitializerDeclaration initializerDeclaration = (InitializerDeclaration)bodyDeclaration;
        NodeList<Statement> statements = initializerDeclaration.getBody().getStatements();
        for(int j = 0; j < statements.size(); j++) {
            Statement statement = statements.get(j);
            body.addStatement(statement);
        }
    }
}