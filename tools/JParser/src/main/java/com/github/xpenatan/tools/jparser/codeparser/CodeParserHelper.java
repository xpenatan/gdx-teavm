package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import java.util.List;

public class CodeParserHelper {

    public static boolean isType(Type type, String typeStr) {
        if(type.isPrimitiveType()) {
            PrimitiveType primitiveType = type.asPrimitiveType();
            String name = primitiveType.getType().name();
            return name.contains(typeStr.toUpperCase());
        }
        return false;
    }

    public static boolean isLong(Type type) {
        return CodeParserHelper.isType(type, "long");
    }

    public static boolean isInt(Type type) {
        return CodeParserHelper.isType(type, "int");
    }

    public static boolean isFloat(Type type) {
        return CodeParserHelper.isType(type, "float");
    }

    public static boolean isDouble(Type type) {
        return CodeParserHelper.isType(type, "double");
    }

    public static boolean isBoolean(Type type) {
        return CodeParserHelper.isType(type, "boolean");
    }

    public static boolean containsMethod(ClassOrInterfaceDeclaration classDeclaration, MethodDeclaration methodDeclaration) {
        String nameAsString = methodDeclaration.getNameAsString();
        NodeList<Parameter> parameters = methodDeclaration.getParameters();
        List<MethodDeclaration> methodsByName = classDeclaration.getMethodsByName(nameAsString);
        for(int i = 0; i < methodsByName.size(); i++) {
            MethodDeclaration otherMethod = methodsByName.get(i);
            NodeList<Parameter> otherParameters = otherMethod.getParameters();
            boolean containsParameters = containsParameters(parameters, otherParameters);
            if(containsParameters) {
                return true;
            }
        }
        return false;
    }

    public static boolean containsParameters(NodeList<Parameter> parameters, NodeList<Parameter> otherParameters) {
        if(parameters.size() == otherParameters.size()) {
            boolean isValid = true;
            for(int j = 0; j < otherParameters.size(); j++) {
                Type type = parameters.get(j).getType();
                Type otherType = otherParameters.get(j).getType();
                boolean equals = type.equals(otherType);
                if(!equals) {
                    isValid = false;
                    break;
                }
            }
            return isValid;
        }
        return false;
    }
}
