package com.github.xpenatan.tools.jparser.idl;

import java.util.ArrayList;

/** @author xpenatan */
public class IDLMethod {
    public String line;
    public String paramsLine;
    public String returnType;
    public String name;
    public boolean returnIsArray;

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

        if(leftSide.contains("[]")) {
            leftSide = leftSide.replace("[]", "");
            returnIsArray = true;
        }
        leftSide = leftSide.trim().replaceAll(" +", " ");
        String[] s = leftSide.split(" ");
        returnType = s[0];
        if(returnType.equals("long")) {
            returnType = "int";
        }
        name = s[1];
    }

    public int getTotalOptionalParams() {
        int count = 0;
        for(int i = 0; i < parameters.size(); i++) {
            IDLParameter parameter = parameters.get(i);
            if(parameter.line.contains("optional")) {
                count++;
            }
        }
        return count;
    }

    public void removeLastParam(int count) {
        for(int i = 0; i < count; i++) {
            parameters.remove(parameters.size()-1);
        }
    }

    public IDLMethod clone() {
        IDLMethod clonedMethod = new IDLMethod();
        clonedMethod.line = line;
        clonedMethod.paramsLine = paramsLine;
        clonedMethod.returnType = returnType;
        clonedMethod.name = name;

        for(int i = 0; i < parameters.size(); i++) {
            IDLParameter parameter = parameters.get(i);
            IDLParameter clonedParam = parameter.clone();
            clonedMethod.parameters.add(clonedParam);
        }
        return clonedMethod;
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