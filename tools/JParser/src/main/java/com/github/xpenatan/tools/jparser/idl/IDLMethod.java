package com.github.xpenatan.tools.jparser.idl;

import java.util.ArrayList;

/** @author xpenatan */
public class IDLMethod {
    public String line;
    public String paramsLine;
    public String returnType;
    public String name;

    public final ArrayList<IDLParameter> parameters = new ArrayList<>();

    public void initMethod(String line) {
        this.line = line;
        paramsLine = IDLMethod.setParameters(line, parameters);
        int index = line.indexOf("(");
        String leftSide = line.substring(0, index).trim();
        int startIndex = leftSide.indexOf("[");
        int endIndex = leftSide.indexOf("]");
        if(startIndex != -1 && endIndex != -1) {
            String tags = line.substring(startIndex, endIndex + 1);
            leftSide = leftSide.replace(tags, "").trim();
        }

        String[] s = leftSide.split(" ");
        returnType = s[0];
        name = s[1];
    }

    static String setParameters(String line, ArrayList<IDLParameter> out) {
        int firstIdx = line.indexOf("(");
        int lastIdx = line.indexOf(")");
        String params = line.substring(firstIdx, lastIdx+1);
        params = params.replace("(", "").replace(")", "");
        if(!params.isEmpty()) {
            String[] paramSplit = params.split(",");
            String cur = "";
            for(int i = 0; i < paramSplit.length; i++) {
                String text = paramSplit[i];
                cur = cur + text;

                boolean containsTags = text.contains("[") || text.contains("]");

                if(!containsTags) {
                    IDLParameter parameter = new IDLParameter();
                    parameter.initParameter(cur.trim());
                    out.add(parameter);
                    cur = "";
                }
                else {
                    if(text.contains("]")) {
                        IDLParameter parameter = new IDLParameter();
                        parameter.initParameter(cur.trim());
                        out.add(parameter);
                        cur = "";
                    }
                }
            }
            return params;
        }
        else {
            return null;
        }
    }
}