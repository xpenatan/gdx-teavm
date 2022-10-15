package com.github.xpenatan.tools.jparser.idl;

/** @author xpenatan */
public class IDLAttribute {
    public String line;
    public String type;
    public String name;

    public void initAttribute(String line) {
        this.line = line;
        String[] s = line.replace("attribute", "").replace(";", "").trim().split(" ");
        type = s[0];
        name = s[1];
    }
}