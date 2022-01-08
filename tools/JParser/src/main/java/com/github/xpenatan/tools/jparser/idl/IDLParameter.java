package com.github.xpenatan.tools.jparser.idl;

import java.util.ArrayList;

/** @author xpenatan */
public class IDLParameter {
    public String line;
    public String type;
    public String name;
    public final ArrayList<String> tags = new ArrayList<>();

    public boolean optional;

    public void initParameter(String line) {
        this.line = line;
        String tmpLine = line;

        optional = line.contains("optional");

        int startIndex = line.indexOf("[");
        int endIndex = line.indexOf("]");
        if(startIndex != -1 && endIndex != -1) {
            String substring1 = line.substring(startIndex, endIndex+1);
            String substring2 = substring1.replace("[", "").replace("]", "");
            String[] s = substring2.split(" ");
            for(int i = 0; i < s.length; i++) {
                String tag = s[i];
                tags.add(tag);
            }
            tmpLine = tmpLine.replace(substring1, "").trim();
        }
        String[] s1 = tmpLine.split(" ");
        type = s1[s1.length-2];

        if(type.equals("long")) {
            type = "int";
        }

        name = s1[s1.length-1];
    }
}