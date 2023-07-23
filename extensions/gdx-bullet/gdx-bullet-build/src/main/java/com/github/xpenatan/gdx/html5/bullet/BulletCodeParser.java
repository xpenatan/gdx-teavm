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
import com.github.xpenatan.jparser.idl.IDLClass;
import com.github.xpenatan.jparser.idl.IDLMethod;
import com.github.xpenatan.jparser.idl.IDLReader;
import com.github.xpenatan.jparser.teavm.TeaVMCodeParser;

/**
 * A Bullet parser which attempt to use the generated idl java methods and bind it to teaVM api.
 * <p>
 * It also adds additional methods to use Matrix4 and Vector3
 *
 * @author xpenatan
 */
public class BulletCodeParser extends TeaVMCodeParser {

    public BulletCodeParser(IDLReader idlReader) {
        super("Bullet", idlReader);
        enableAttributeParsing = false;
    }

    @Override
    public boolean filterIDLMethod(IDLClass idlClass, IDLMethod idlMethod) {
        if(idlClass.name.equals("btCollisionObject")) {
            if(idlMethod.name.equals("getUserPointer") || idlMethod.name.equals("setUserPointer")) {
                return false;
            }
        }
        else if(idlClass.name.equals("btShapeHull")) {
            if(idlMethod.name.equals("getVertexPointer") || idlMethod.name.equals("getIndexPointer")) {
                return false;
            }
        }
        else if(idlClass.name.equals("btTriangleIndexVertexArray")) {
            if(idlMethod.name.equals("addIndexedMesh")) {
                return false;
            }
        }
        return true;
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

    protected BlockStmt generateObjectPointerReturnType(CompilationUnit unit, ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration idlMethodDeclaration, MethodCallExpr caller) {
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
            else if(returnTypeName.equals("btQuaternion") || returnTypeName.equals("Quaternion")) {
                newBody = CONVERT_TO_GDX_TEMPLATE.replace(TEMPLATE_TAG_METHOD, methodCaller).replace(TEMPLATE_TAG_TYPE, "btQuaternion");
                idlMethodDeclaration.setType("Quaternion");
                JParserHelper.addMissingImportType(unit, "com.badlogic.gdx.math.Quaternion");
            }
        }

        BlockStmt body = null;
        try {
            BodyDeclaration<?> bodyDeclaration = StaticJavaParser.parseBodyDeclaration(newBody);
            InitializerDeclaration initializerDeclaration = (InitializerDeclaration)bodyDeclaration;
            body = initializerDeclaration.getBody();
        }
        catch(Throwable t) {
            String className = classDeclaration.getNameAsString();
            System.err.println("Error Class: " + className + "\n" + newBody);
            throw t;
        }
        return body;
    }
}