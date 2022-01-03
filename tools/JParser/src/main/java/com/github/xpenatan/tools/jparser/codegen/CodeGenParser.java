package com.github.xpenatan.tools.jparser.codegen;

/** @author xpenatan */
public interface CodeGenParser {
    void parseCodeBlock(CodeGenParserItem parserItem);
    void parseHeaderBlock(CodeGenParserItem parserItem);
}