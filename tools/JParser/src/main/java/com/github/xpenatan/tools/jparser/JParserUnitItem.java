package com.github.xpenatan.tools.jparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.util.CustomFileDescriptor;

import java.util.Optional;

public class JParserUnitItem {
    public CompilationUnit unit;
    public String destinationPath;
    public String inputPath;

    public JParserUnitItem(CompilationUnit unit, String inputPath, String destinationPath) {
        this.unit = unit;
        this.inputPath = inputPath;
        this.destinationPath = destinationPath;
    }

    public PackageDeclaration getPackage() {
        Optional<PackageDeclaration> optionalPackageDeclaration = unit.getPackageDeclaration();
        return optionalPackageDeclaration.orElse(null);
    }

    public ClassOrInterfaceDeclaration getClassDeclaration() {
        Optional<ClassOrInterfaceDeclaration> first = unit.findFirst(ClassOrInterfaceDeclaration.class);
        return first.orElse(null);
    }

    public static void addMissingImportType(JParserUnit jParserUnit, CompilationUnit unit, Type type) {
        String s = type.asString();
        JParserUnitItem parserUnitItem = jParserUnit.getParserUnitItem(s);
        if(parserUnitItem != null) {
            ClassOrInterfaceDeclaration classDeclaration = parserUnitItem.getClassDeclaration();
            Optional<String> optionalFullyQualifiedName = classDeclaration.getFullyQualifiedName();
            if(optionalFullyQualifiedName.isPresent()) {
                String fullyQualifiedName = optionalFullyQualifiedName.get();
                if(!JParserUnitItem.containsImport(unit, fullyQualifiedName)) {
                    unit.addImport(fullyQualifiedName);
                }
            }
        }
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
}
