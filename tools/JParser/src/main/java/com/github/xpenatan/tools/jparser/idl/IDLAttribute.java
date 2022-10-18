package com.github.xpenatan.tools.jparser.idl;

/** @author xpenatan */
public class IDLAttribute {
    public String line;
    public String type;
    public String name;

    public void initAttribute(String line) {
        this.line = line;
        String text = line.replace("attribute", "")
                .replace("unsigned", "")
                .replace(";", "")
                .replace("[Value]", "")
                .replace("[Const]", "")
                .trim();
        String[] s = text.split(" ");
        type = s[0];
        name = s[1];

        if(name.equals("long")) {
            name = "int";
        }
    }
}