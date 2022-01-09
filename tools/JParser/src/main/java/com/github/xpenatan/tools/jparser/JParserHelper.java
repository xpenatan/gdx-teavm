package com.github.xpenatan.tools.jparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.Type;

import java.util.List;
import java.util.Optional;

/** @author xpenatan */
public class JParserHelper {

    public static boolean isType(Type type, String typeStr) {
        if(type.isPrimitiveType()) {
            PrimitiveType primitiveType = type.asPrimitiveType();
            String name = primitiveType.getType().name();
            return name.contains(typeStr.toUpperCase());
        }
        return false;
    }

    public static boolean isLong(Type type) {
        return JParserHelper.isType(type, "long");
    }

    public static boolean isInt(Type type) {
        return JParserHelper.isType(type, "int");
    }

    public static boolean isFloat(Type type) {
        return JParserHelper.isType(type, "float");
    }

    public static boolean isDouble(Type type) {
        return JParserHelper.isType(type, "double");
    }

    public static boolean isBoolean(Type type) {
        return JParserHelper.isType(type, "boolean");
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

    public static boolean containsImport(CompilationUnit unit, String fullyQualifiedName) {
        NodeList<ImportDeclaration> imports = unit.getImports();
        for(int i = 0; i < imports.size(); i++) {
            ImportDeclaration importDeclaration = imports.get(i);
            String importName = importDeclaration.getName().asString();
            if(fullyQualifiedName.equals(importName))
                return true;
        }
        return false;
    }

    public static void addMissingImportType(JParser jParser, CompilationUnit unit, Type type) {
        String s = type.asString();
        JParserItem parserUnitItem = jParser.getParserUnitItem(s);
        if(parserUnitItem != null) {
            ClassOrInterfaceDeclaration classDeclaration = parserUnitItem.getClassDeclaration();
            Optional<String> optionalFullyQualifiedName = classDeclaration.getFullyQualifiedName();
            if(optionalFullyQualifiedName.isPresent()) {
                String fullyQualifiedName = optionalFullyQualifiedName.get();
                if(!JParserHelper.containsImport(unit, fullyQualifiedName)) {
                    unit.addImport(fullyQualifiedName);
                }
            }
        }
    }

    public static ClassOrInterfaceDeclaration getClassDeclaration(CompilationUnit unit) {
        Optional<ClassOrInterfaceDeclaration> first = unit.findFirst(ClassOrInterfaceDeclaration.class);
        return first.orElse(null);
    }

    public static void addMissingImportType(CompilationUnit unit, String importName) {
        boolean found = false;
        NodeList<ImportDeclaration> imports = unit.getImports();
        for(int i = 0; i < imports.size(); i++) {
            ImportDeclaration importDeclaration = imports.get(i);
            Name name = importDeclaration.getName();
            String s = name.asString();
            if(importName.equals(s)) {
                found = true;
                break;
            }
        }
        if(!found) {
            unit.addImport(importName);
        }
    }
}