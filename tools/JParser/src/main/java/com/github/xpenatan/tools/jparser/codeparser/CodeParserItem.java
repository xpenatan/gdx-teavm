package com.github.xpenatan.tools.jparser.codeparser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.comments.BlockComment;
import java.util.Scanner;

/** @author xpenatan */
public class CodeParserItem {
    public CompilationUnit unit;

    public Node node;

    public static String obtainHeaderCommands(BlockComment blockComment) {
        String content = blockComment.getContent();
        Scanner scanner = new Scanner(content);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if(line.startsWith(DefaultCodeParser.CMD_HEADER_START) && line.endsWith(DefaultCodeParser.CMD_HEADER_END)) {
                scanner.close();
                return line;
            }
        }
        scanner.close();
        return null;
    }

    public static String obtainContent(String header, BlockComment blockComment) {
        if(header == null)
            return null;
        String content = blockComment.getContent();
        content = content.replace(header, "").trim();
        return content;
    }
}