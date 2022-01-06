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
        int endIndex = getLastIndex(leftSide);
        if(endIndex != -1) {
            leftSide = line.substring(endIndex + 1, index).trim();
        }

        String[] s = leftSide.split(" ");
        returnType = s[0];
        name = s[1];
    }

    private int getLastIndex(String leftSide) {
        int startIndex = leftSide.indexOf("[");
        if(startIndex != -1) {
            int count = 0;
            for(int i = startIndex; i < leftSide.length(); i++) {
                char c = leftSide.charAt(i);
                if(c == '[') {
                    count++;
                }
                else if(c == ']') {
                    count--;
                }
                if(count == 0) {
                    return i;
                }
            }
        }
        return -1;
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