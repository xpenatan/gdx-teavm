package com.github.xpenatan.tools.jparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.SimpleName;

import java.util.ArrayList;
import java.util.Optional;

public class JParserUnit {

    public ArrayList<JParserUnitItem> unitArray = new ArrayList<>();

    public JParserUnitItem getParserUnitItem(String className) {
        for(int i = 0; i < unitArray.size(); i++) {
            JParserUnitItem jParserUnitItem = unitArray.get(i);
            CompilationUnit unit = jParserUnitItem.unit;
            Optional<PackageDeclaration> optionalPackageDeclaration = unit.getPackageDeclaration();
            if(optionalPackageDeclaration.isPresent()) {
                Optional<ClassOrInterfaceDeclaration> first = unit.findFirst(ClassOrInterfaceDeclaration.class);
                if(first.isPresent()) {
                    ClassOrInterfaceDeclaration classOrInterfaceDeclaration = first.get();
                    String name = classOrInterfaceDeclaration.getName().asString();
                    if(className.equals(name)) {
                        return jParserUnitItem;
                    }
                }
            }
        }
        return null;
    }
}
