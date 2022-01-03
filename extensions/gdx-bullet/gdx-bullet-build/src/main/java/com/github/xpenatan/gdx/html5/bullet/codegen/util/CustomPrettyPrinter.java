package com.github.xpenatan.gdx.html5.bullet.codegen.util;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.printer.DefaultPrettyPrinter;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration;
import com.github.javaparser.printer.configuration.PrinterConfiguration;

import java.util.function.Function;

public class CustomPrettyPrinter extends DefaultPrettyPrinter {

    private static Function<PrinterConfiguration, VoidVisitor<Void>> createDefaultVisitor() {
        PrinterConfiguration configuration = createDefaultConfiguration();
        return createDefaultVisitor(configuration);
    }

    private static PrinterConfiguration createDefaultConfiguration() {
        return new DefaultPrinterConfiguration();
    }

    private static Function<PrinterConfiguration, VoidVisitor<Void>> createDefaultVisitor(PrinterConfiguration configuration) {
        return (config) -> new CustomDefaultPrettyPrinterVisitor(config, new CustomSourcePrinter(config));
    }

    public CustomPrettyPrinter() {
        super(createDefaultVisitor(), createDefaultConfiguration());
    }

    @Override
    public String print(Node node) {
        String print = super.print(node);

        if(node instanceof RawCodeBlock) {

            System.out.println("");
        }

        return print;
    }
}