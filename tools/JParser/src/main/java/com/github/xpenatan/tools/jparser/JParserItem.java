package com.github.xpenatan.tools.jparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.type.Type;
import com.github.xpenatan.tools.jparser.codeparser.CodeParserHelper;

import java.util.Optional;

public class JParserItem {
    public CompilationUnit unit;
    public String destinationPath;
    public String inputPath;

    public JParserItem(CompilationUnit unit, String inputPath, String destinationPath) {
        this.unit = unit;
        this.inputPath = inputPath;
        this.destinationPath = destinationPath;
    }

    public PackageDeclaration getPackage() {
        Optional<PackageDeclaration> optionalPackageDeclaration = unit.getPackageDeclaration();
        return optionalPackageDeclaration.orElse(null);
    }

    public ClassOrInterfaceDeclaration getClassDeclaration() {
        return CodeParserHelper.getClassDeclaration(unit);
    }
}
