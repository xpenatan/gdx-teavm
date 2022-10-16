package com.github.xpenatan.tools.jparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import com.github.javaparser.ast.body.TypeDeclaration;
import java.util.List;
import java.util.Optional;

/** @author xpenatan */
public class JParserItem {
    public CompilationUnit unit;
    public String destinationPath;
    public String inputPath;
    public String className = "";
    public boolean notAllowed;

    public JParserItem(CompilationUnit unit, String inputPath, String destinationPath) {
        this.unit = unit;
        this.inputPath = inputPath;
        this.destinationPath = destinationPath;

        List<ClassOrInterfaceDeclaration> all = unit.findAll(ClassOrInterfaceDeclaration.class);
        if(all.size() > 0) {
            className = all.get(0).getNameAsString();
        }
        else {
            notAllowed = true;
            List<CompilationUnit> compiAll = unit.findAll(CompilationUnit.class);
            if(compiAll.size() > 0) {
                CompilationUnit compilationUnit = compiAll.get(0);
                NodeList<TypeDeclaration<?>> types = compilationUnit.getTypes();
                if(types.size() > 0) {
                    className = types.get(0).getNameAsString();
                }
            }
        }
    }

    public PackageDeclaration getPackage() {
        Optional<PackageDeclaration> optionalPackageDeclaration = unit.getPackageDeclaration();
        return optionalPackageDeclaration.orElse(null);
    }

    public ClassOrInterfaceDeclaration getClassDeclaration() {
        return JParserHelper.getClassDeclaration(unit);
    }

    @Override
    public String toString() {
        String name = "";
        if(!className.isEmpty()) {
            name = " " + className;
        }
        return super.toString() + name;
    }
}