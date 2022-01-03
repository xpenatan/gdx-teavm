package com.github.xpenatan.tools.jparser.codeparser;

/** @author xpenatan */
public interface CodeParser {
    void parseCodeBlock(CodeParserItem parserItem);
    void parseHeaderBlock(CodeParserItem parserItem);
}